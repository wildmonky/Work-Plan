package com.zhuofan.core.service.impl;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.kafka.KafkaClient;
import com.zhuofan.core.service.Txt2WavService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * XunfeiTTS
 * 科大讯飞语音合成TTS类
 */
@Service("XunfeiTTS")
public class XunfeiTTS implements Txt2WavService {
    /**
     * @return boolean：操作结果是否成功。
     * @Author fuxiaodong
     * @Description 调用科大讯飞的离线语音包工具。工具用命令行参数完成，输出为wav文件
     * @Version: v1.8
     * @Date 10:19 2024/12/27
     * @Param [text, outputFilename]
     * text：需要合成语音的文本内容
     * outputFilename：完整输出文件名
     */
    @Override
    public String txt2wav(String text, String outputFilename) {
        //String ttsDir= SpringBeanUtils.mapConfig.get("sys.xunfei_tts_dir");
        //String ttsCmd=SpringBeanUtils.mapConfig.get("sys.xunfei_tts_cmd");

        String ttsDir = CommonFunc.getConfigByKey("sys.xunfei_tts_dir");
        String ttsCmd = CommonFunc.getConfigByKey("sys.xunfei_tts_cmd");

        String cmd;
        String osName = System.getProperty("os.name").toLowerCase();

        List<String> cmds = new ArrayList<>();

        if (osName.contains("win")) {
            cmd = String.format("%s/%s \"-t%s\" -o%s",
                    ttsDir, ttsCmd, text.replaceAll("\n", "").replace("\r", ""),
                    outputFilename);
            cmds.add(cmd);
        } else {
            cmd = String.format("source /etc/profile && cd %s && %s \"-t%s\" -o%s",
                    ttsDir, ttsCmd, text, outputFilename);
            cmds.add("/bin/bash");
            cmds.add("-c");
            cmds.add(cmd);
        }
//cmd="ping www.baidu.com && ping www.sina.com";
        CommonLogger commonLogger = CommonLogger.getInstance();
        System.out.println("XunfeiTTS.txt2wav cmd:" + cmd);
        commonLogger.info("XunfeiTTS.txt2wav cmd:" + cmd);

        //判断文件是否存在
        File file = new File(outputFilename);

        //先删除文件
        if (file.exists()) {
            file.delete();
        }

        //执行命令
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        BufferedReader reader = null;
        InputStream inputStream = null;

        try {
            process = osName.contains("win") ? runtime.exec(cmd, null, new File(ttsDir)) : runtime.exec(cmds.toArray(new String[0]), null, new File(ttsDir));
            inputStream = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String cmdResult = "", cmdItem;

            while ((cmdItem = reader.readLine()) != null) {
                cmdResult += cmdItem;
            }

            System.out.println("result:" + cmdResult);
            commonLogger.info("result:" + cmdResult);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean result = false;

        if (file.exists() && file.isFile() && file.length() > 0) {
            result = true;
        }

        return result ? outputFilename : "";
    }

    public static void main(String[] args) throws Exception {
        //合成语音测试
        String wavfile = "d:/xunfei/test.wav";
        XunfeiTTS xunfeiTTS = new XunfeiTTS();
        xunfeiTTS.txt2wav("1.110kV裕鹿线、古樟乙线限流按710A控制，预测重载（85%）。\n" +
                "2.社贝站#2#3变低红线值均按3464A控制，预测1M、2甲M重载（90%），2乙M、3M负荷满足（71%）；\n" +
                "  鸡啼岗站#1#3变低红线值均按2749A控制，预测1M、2甲M负荷满足（74%）,2乙M、3M负荷满足（68%）；\n" +
                "  樟木头站#1#2变低红线值均按2749A控制，预测负荷满足；\n" +
                "  鹿窝站#2#3变低红线值均按3464A控制，预测负荷满足；\n" +
                "2.若裕元站110kV#2母线、110kV裕鹿线、古樟乙线发生故障，造成110kV樟木头站10kV#2乙M、3M、鸡啼岗站10kV#1M、2甲M、鹿窝站10kV#1M、2甲M失压，损失负荷约80MW，损失用户数约2.6万户，四级事件风险。(请东莞配调组织区域调整配网运行方式，控制樟木头站10kV#2乙M、3M、鸡啼岗站10kV#1M、2甲M、鹿窝站10kV#1M、2甲M，需提前转移2.6万户)\n" +
                "  若道岭站110kV#2母线、110kV道啼线发生故障，造成110kV鸡啼岗站10kV#2乙M、3M、社贝站10kV#1M、2甲M失压，损失负荷约90MW，损失用户数约2.3万户，四级事件风险。\n" +
                "  若社贝站#2主变（道啼线）或#3主变（道社线），鸡啼岗站#1主变（裕鹿线）或#3主变（道啼线），樟木头站#2主变（裕鹿线、古樟乙线），鹿窝站#2主变（裕鹿线、古樟乙线）发生故障，存在负荷损失的风险；\n" +
                "  请通知输电所、巡维中心对上述设备加强巡维。1.110kV裕鹿线、古樟乙线限流按710A控制，预测重载（85%）。\n" +
                "2.社贝站#2#3变低红线值均按3464A控制，预测1M、2甲M重载（90%），2乙M、3M负荷满足（71%）；\n" +
                "  鸡啼岗站#1#3变低红线值均按2749A控制，预测1M、2甲M负荷满足（74%）,2乙M、3M负荷满足（68%）；\n" +
                "  樟木头站#1#2变低红线值均按2749A控制，预测负荷满足；\n" +
                "  鹿窝站#2#3变低红线值均按3464A控制，预测负荷满足；\n" +
                "2.若裕元站110kV#2母线、110kV裕鹿线、古樟乙线发生故障，造成110kV樟木头站10kV#2乙M、3M、鸡啼岗站10kV#1M、2甲M、鹿窝站10kV#1M、2甲M失压，损失负荷约80MW，损失用户数约2.6万户，四级事件风险。(请东莞配调组织区域调整配网运行方式，控制樟木头站10kV#2乙M、3M、鸡啼岗站10kV#1M、2甲M、鹿窝站10kV#1M、2甲M，需提前转移2.6万户)\n" +
                "  若道岭站110kV#2母线、110kV道啼线发生故障，造成110kV鸡啼岗站10kV#2乙M、3M、社贝站10kV#1M、2甲M失压，损失负荷约90MW，损失用户数约2.3万户，四级事件风险。\n" +
                "  若社贝站#2主变（道啼线）或#3主变（道社线），鸡啼岗站#1主变（裕鹿线）或#3主变（道啼线），樟木头站#2主变（裕鹿线、古樟乙线），鹿窝站#2主变（裕鹿线、古樟乙线）发生故障，存在负荷损失的风险；\n" +
                "  请通知输电所、巡维中心对上述设备加强巡维。", wavfile);


        /////////////////////////////////////////////////////////////////////////
        //发送语音测试
        //1.此处初始化对象，照抄
        KafkaClient kafkaClient = new KafkaClient("ZFOMS_测试");

        //2.此处参数定义，按实际情况传参
        String[] arrPhoneList = new String[]{"6130", "18899998888"};
        String taskId = "1111111";

        //3.此处特别注意：发送时，须将文件复制到固定目录:/sys.kafka_path_tel/sys.kafka_appname/
        File file = new File(wavfile);
        String telDir = CommonFunc.getConfigByKey("sys.kafka_path_tel");
        String descFilename = telDir + "/" + CommonFunc.getConfigByKey("sys.kafka_appname") + "/" + file.getName();
        FileUtils.copyFile(file, new File(descFilename));

        //4.开始调用语音电话。注意：此接口不会返回状态值。状态值需开个服务轮询。（建议结合表做个视图）
        kafkaClient.callTel(arrPhoneList, "test.wav", "3", taskId);
    }
}
