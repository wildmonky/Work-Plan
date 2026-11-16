/**
 * com.zhoufan.core.common
 *
 *
 *   ver     date           author
 * 日志打印
 *           2015-05-18      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */
package com.zhuofan.core.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName:CommonLogger
 *
 * @author fuxiaodong
 * @Date 2015-05-18
 */
public class CommonLogger {
    private static Logger logger;

    private static CommonLogger instance = null;

    private static synchronized void syncInit() {
        if(instance == null) {
            instance = new CommonLogger();
        }
    }

    // 获取实例
    public static CommonLogger getInstance() {
        if(instance == null) {
            syncInit();
        }
        return instance;
    }

    private CommonLogger() {
        logger = LoggerFactory.getLogger(CommonLogger.class);
    }

    /*
     * debug
     *
     * @param Object
     *
     * @param methodName
     *
     * @param msg
     */
    public void debug(Object o, String methodName, String msg) {
        logger.debug(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":"+o.getClass().getName() + "." + methodName + ":" + msg);
    }

    /*
     * error
     *
     * @param Object
     *
     * @param methodName
     *
     * @param msg
     */
    public void error(Object o, String methodName, String msg) {
        logger.error(o.getClass().getName() + "." + methodName + ":" + msg);
    }

    public void error(String msg) {
        logger.error(msg);
    }
    /*
     * warn
     *
     * @param msg
     */
    public void warn(String msg, Object o) {
        logger.warn(msg);
    }

    /*
     * warn
     *
     * @param msg
     */
    public void info(String msg) {
        if (StringUtils.length(msg) >= 10 && !CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd")
                .equals(msg.substring(0,10))) {
            logger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":" + msg);
        }else{
            logger.info(msg);
        }
    }

    public void info(Object o, String methodName, String msg) {
        if (StringUtils.length(msg) >= 10 && !CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd")
                .equals(msg.substring(0,10))) {
            logger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+ ":"
                    +o.getClass().getName() + "." + methodName + ":" + msg);
        }else{
            logger.info(o.getClass().getName() + "." + methodName + ":" + msg);
        }

    }
}
