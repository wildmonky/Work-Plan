package com.zhuofan.core.config;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class ConfigFileInfo {
    public static void main(String[] args) {
        String config = getConfigPath("config,xml");
        System.out.println(config);
    }

    public static String getConfigPath(String fileName) {
        boolean debug = ConfigFileInfo.isDebug();
        ConfigFileInfo thisClass = new ConfigFileInfo();//当前代码所在的类为ParamUtil,可以根据自己创建的类设置
        String configPath;
        if (debug) {
            URL resource = thisClass.getClass().getClassLoader().getResource("");//ParamUtil使用当前创建的类就行
            String path = resource.getPath();
            File file = new File(path);
            String parent = file.getParentFile().getParent();
            configPath = parent + "/src/main/resources/" + fileName;
        } else {//获取当前jar包所在的路径
            String path = thisClass.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();//必须要用到new
            path = dirNameEncode(path);
            if (System.getProperty("os.name").contains("dows")) {
                path = path.substring(1);
            }
            if (path.contains("jar")) {
                path = path.substring(0, path.lastIndexOf("."));
                path = path.substring(0, path.lastIndexOf("/"));
            } else {
                path = path.replace("target/classes/", "");
            }
            configPath = path + "/" + fileName;
        }
        try {
            configPath = URLDecoder.decode(configPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return configPath;
    }

    //处理文件路径转码异常问题
    private static String dirNameEncode(String dir) {
        String[] split = dir.split("/");
        StringBuffer sbr = new StringBuffer();
        for (String unit : split) {
            try {
                String dirName = URLDecoder.decode(unit, "UTF-8");
                if ("".equals(dirName)) continue;
                sbr.append("/").append(dirName);
            } catch (UnsupportedEncodingException e) {

                return null;
            }
        }
        return sbr.toString();
    }

    /**
     * 校验是否是调试启动
     *
     * @return
     */
    public static boolean isDebug() {
        try {
            Class.forName("com.intellij.rt.execution.application.AppMainV2");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
