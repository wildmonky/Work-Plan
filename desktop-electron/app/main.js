// process.env.TZ = 'Asia/Shanghai';
const { app, BrowserWindow, protocol, net } = require('electron')
const path = require('node:path');
const { pathToFileURL } = require('node:url');
const fs  = require('node:fs').promises;
const { spawn } = require('child_process');
const util  = require('util');

const isDev = !app.isPackaged;
// 保持你原来的日志路径，不要改
const logPath = path.join(isDev ? __dirname + "/logs" : process.resourcesPath, 'main.log');

async function writeLog(...args) {
    const line = `[${formatDate(new Date())}] ${util.format(...args)}\n`;
    try {
        return fs.appendFile(logPath, line, 'utf8');
    } catch (e) {
        console.error(e)
        // 写日志失败不影响主流程
    }
}

// 重定向 console.log
const originalLog = console.log;
console.log = (...args) => {
    originalLog.apply(console, args);
    writeLog(...args);
};

// 自定义协议配置
protocol.registerSchemesAsPrivileged([
    {
        scheme: 'app',
        privileges: {
            standard: true,        // 允许正确解析相对路径
            secure: true,          // 视为安全来源
            supportFetchAPI: true, // 关键：允许 Fetch API 访问
            corsEnabled: true,     // 允许 CORS（Axios 可能需要）
            stream: true           // 支持流式响应（如视频）
        }
    }
]);

let win;
let javaProcess;
const server = 'http://127.0.0.1:8181';

app.whenReady().then(() => {
    // 1. 注册协议处理器，返回本地资源文件, 后端请求转发到本地java程序，默认8181端口
    protocol.handle('app', async (request) => {
        const url = new URL(request.url);
        // 安全处理：防止路径遍历攻击
        let request_path = url.pathname
        // 转发后端请求
        if (request_path.includes("omsserver")) {
            // 后端请求
            const redirectUrl = new URL(url.pathname + url.search + url.hash, server);

            win.webContents.session.cookies.get({}).then(d => {
                console.log("转发请求, originUrl: %s, redirectToUrl: %s, session: %s", request.url, redirectUrl.toString(), JSON.stringify(d))
            })

            if (request.method === 'POST' && request.body) {
                request.clone().text().then(body => {
                    console.log("转发请求, originUrl: %s, redirectToUrl: %s, body: %s", request.url, redirectUrl.toString(), body)
                })
                return net.fetch(redirectUrl.toString(), {
                    method: request.method,
                    headers: request.headers,
                    body: request.body,
                    duplex: 'half',
                    useSessionCookies: true,
                    bypassCustomProtocolHandlers: true
                });
            }
            return net.fetch(redirectUrl.toString(), {
                bypassCustomProtocolHandlers: true,
                useSessionCookies: true
            }).then(res => {
                if (res.headers.get('content-type')) {
                    let r = res.clone()
                    r = new Response(r.body, {
                        headers: { 'Content-Type': 'application/json; charset=utf-8' }
                    });
                    r.json().then(data => {
                        console.log("转发请求, originUrl: %s, redirectToUrl: %s, 响应: %s", request.url, redirectUrl.toString(), JSON.stringify(data))
                    })
                }
                return res;
            })
        }

        // 加载本地页面
        const resourcesPath = isDev ? path.join(__dirname) : path.join(process.resourcesPath, "app.asar");
        let filePath = path.join(resourcesPath, url.pathname);
        filePath = pathToFileURL(filePath).toString()
        // console.log("跳转本地页面", filePath)
        return net.fetch(filePath).catch((error) => {
            console.error('Failed to load resource:', filePath, error);
            return new Response(null, {
                status: 404,
                statusText: 'Not Found',
                headers: new Headers({
                    'Content-Type': 'text/plain'
                })
            });
        });
    });

    // 2.启动java后台程序
    // if (!isDev) {
    //     javaProcess = startJavaApp(() => {
    //         showMainWindow();
    //     });
    // }

    win = new BrowserWindow({
        width: 1200,
        height: 800,
        show: false,
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true
        }
    });

    win.loadFile('loading.html')
    win.show()

    if (isDev) {
        showMainWindow();
    }
});

// 应用退出前触发
app.on('before-quit', async (event) => {
    if (javaProcess && !javaProcess.killed) {
        event.preventDefault();  // 阻止立即退出

        await shutdownJavaApp();

        // 等待 Java 进程真正退出，最多等 5 秒
        const timeout = setTimeout(() => {
            console.warn('Java 进程未在 10 秒内退出，强制结束');
            javaProcess.kill('SIGKILL');
            app.exit(0);
        }, 10000);

        javaProcess.removeAllListeners('close');
        javaProcess.once('close', (code) => {
            clearTimeout(timeout);
            console.log(`Java 进程已退出，退出码: ${code}`);
            app.exit(0);
        });
    }
});

function showMainWindow() {
    // 后端启动成功后再用自定义协议加载页面
    win.loadURL("app://workplan.com/login.html");
    // win.loadFile("login.html");
    win.webContents.once('did-finish-load', () => {
        console.log('页面完全加载完成');
        // 页面跳转完毕后展示
        win.once('ready-to-show', () => {
            win.show();
        });
    });
}

// 启动java后端程序
function startJavaApp(doOnSuccess) {
    // 动态获取资源路径
    // 开发环境：项目根目录/resources；生产环境：进程资源目录
    const isDev = !app.isPackaged;
    const resourcesPath = isDev
        ? path.join(__dirname, 'resources')
        : process.resourcesPath;

    const jrePath = path.join(resourcesPath, 'jre1.8.0_411', 'bin', 'java.exe'); // linux 下是 java
    const jarPath = path.join(resourcesPath, 'app.jar');

    // 启动 Java 进程
    const javaProcess = spawn(jrePath, ['-Dfile.encoding=utf-8', '-jar', jarPath])

    javaProcess.stdout.on('data', (data) => {
        // console.log(`Java stdout: ${data}`);
        let mes = Buffer.from(data).toString('utf-8')
        if (mes.includes('Started OmsServerApplication in')) {
            console.log('Java 后端已启动')
            doOnSuccess();
        }
    });

    javaProcess.stderr.on('data', (data) => {
        console.error(`Java stderr: ${data}`);
    });

    javaProcess.once('close', (code) => {
        console.log(`Java 进程已退出，退出码： ${code}`);
        app.exit(0);
    });

    return javaProcess;
}

// 优雅关闭
function shutdownJavaApp() {
    return new Promise((resolve) => {
        if (!javaProcess || javaProcess.killed) {
            return resolve();
        }

        const serverpath = "/kemp/ex_web/oms/omsserver";
        // 1. 先尝试通过 HTTP 接口通知关闭
        const req = net.fetch(server + serverpath + '/actuator/shutdown', {
            method: 'POST',
            timeout: 3000,
        }).then((res) => {
            res.text().then(code => {
                console.log(`Java 关闭请求已发送，状态码: ${code}`);
                resolve();
            })
        }).catch(err => {
            console.warn('HTTP 关闭失败，回退到 kill:', err.message);
            resolve();
        });
    });
}

function formatDate(date, timeZone = 'Asia/Shanghai') {
    const parts = new Intl.DateTimeFormat('en-CA', {
        timeZone,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
    }).formatToParts(date);

    const get = (type) => parts.find(p => p.type === type)?.value;
    return `${get('year')}-${get('month')}-${get('day')} ${get('hour')}:${get('minute')}:${get('second')}`;
}