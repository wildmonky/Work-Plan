package com.zhuofan.core.util;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;

public class SpringUtils {

    public static ConfigurableApplicationContext applicationContext;

    private static final ApplicationHome applicationHome = new ApplicationHome(SpringUtils.class);

    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        CommonLogger.getInstance().info("SpringUtils.setApplicationContext:applicationContext:"+applicationContext);
        SpringUtils.applicationContext = applicationContext;
    }

    public static String getProperty(String propertyName) {
        CommonLogger.getInstance().info("SpringUtils.applicationContext:"+applicationContext);
        if (applicationContext==null){
            return CommonFunc.getConfigByKey(propertyName);
        }
        String value = applicationContext.getEnvironment().getProperty(propertyName);
        // properties 配置文件使用iso-8859-1编码读取，存在中文乱码现象
        // 建议换成yaml配置文件，UTF-8编码格式，支持中文
        return value == null ? null : new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public static boolean containsProperty(String propertyName) {
        return applicationContext != null && applicationContext.getEnvironment().containsProperty(propertyName);
    }

    /**
     * 根据启动类位置判断是否以jar方式启动
     * @return true-以jar包方式启动
     */
    public static boolean isRunAsJar() {
//        String protocol = Objects.requireNonNull(application.getMainApplicationClass().getResource("")).getProtocol();
        String protocol = getAppHome();
        return  protocol != null && protocol.contains("jar");
    }

    public static String getCurrentProfile() {
        if (applicationContext==null){
            return "dev";
        }
        Environment env = applicationContext.getEnvironment();
        return env.getActiveProfiles().length > 0 ?
                env.getActiveProfiles()[0] : "dev";
    }

    public static String getAppHome() {
        return applicationHome.getSource().toString();
    }
}
