package com.zhuofan.core.dameng;


import com.dameng.floader.Instance;
import com.dameng.floader.Properties;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;

import java.io.File;

public class FldrJniImport {

    public static void main(String[] args){
        long rowCount=FldrJniImport.ImportFromCsv("E:\\www\\OMS\\app\\data\\alarm_202405.csv",
                "ALARM_ALLEVENTLOG",
                "EVENT_SEQ,CONFIRM_FLAG,EVENT_LEVEL,ACTING_TIME,ALARM_CONTENT,ALARM_GROUP,ACTING_DESC,CONFIRM_MAN,CONFIRM_TIME,TABLE_NAME",1,1);

        System.out.println(rowCount);
    }

    /*
    fileName：文件完整路径
    tableName：表名
    fields：字段列表
    threadNum:线路数
    skip：跳过行数
     */
    public static long ImportFromCsv(String fileName,String tableName,String fields,int threadNum,int skip){
        CommonLogger commonLogger=CommonLogger.getInstance();

        Instance instance=new Instance();

        FldrJniImport fldrJniImport=new FldrJniImport();

        boolean success = fldrJniImport.init(instance, threadNum, tableName);

        if (!success){
            String message = instance.getErrorMsg();
            commonLogger.info("ImportFromCsv error :" + message);
            System.out.println("ImportFromCsv error :" + message);
            instance.free();
            return -1;
        }

        String ctl = "options(skip="+String.valueOf(skip)+")\r\n"+
                "LOAD DATA\r\n" +
                "INFILE '"+fileName+"'\r\n" +
                "INTO TABLE "+tableName+"\r\n" +
                "FIELDS ','\r\n" +
                "("+fields+")";
        commonLogger.info("ctrl内容进行数据载入"+ctl+"==========================================");
        //根据ctrl内容进行数据载入
        long rowCount=instance.setControl(ctl);
        commonLogger.info("ctrl内容进行数据载入"+rowCount+"==========================================");
        return rowCount;
    }

    /**********自定义init函数，设置属性信息，并初始化当前快速装载实例**********/
    private static boolean init(Instance instance, int threadNum, String tableName) {
        CommonLogger commonLogger=CommonLogger.getInstance();
        // 分配句柄
        instance.allocInstance();
        // 设置必要的属性信息
        String host =  CommonFunc.getConfigByKey("spring.datasource.server");
        String port = CommonFunc.getConfigByKey("spring.datasource.port");
        String userName = CommonFunc.getConfigByKey("spring.datasource.username");
        String password = CommonFunc.getConfigByKey("spring.datasource.password");

        instance.setAttribute(Properties.FLDR_ATTR_SERVER, host);
        instance.setAttribute(Properties.FLDR_ATTR_PORT, port);
        instance.setAttribute(Properties.FLDR_ATTR_UID, userName);
        instance.setAttribute(Properties.FLDR_ATTR_PWD, password);

        // 其余属性用户均可根据实际情况选择性设置
        // 设置编码
        //instance.setAttribute(Properties.FLDR_ATTR_DATA_CHAR_SET,System.getProperty("file.encoding"));
        // 设置是否插入自增列
        //instance.setAttribute(Properties.FLDR_ATTR_SET_INDENTITY, "0");
        // fldr task线程默认为cpu个数，如果客户端线程数大于cpu个数，则必须设置该参数
        //instance.setAttribute(Properties.FLDR_ATTR_TASK_THREAD_NUM,String.valueOf(threadNum));

        // 记录出错的信息
        String dmHome = System.getProperty("DM_HOME");
        if(dmHome != null && !dmHome.equals("")){
            commonLogger.info("dmHome===="+dmHome+"==========================================");
            if(dmHome.endsWith("/") || dmHome.endsWith("\\")){
                instance.setAttribute(Properties.FLDR_ATTR_BAD_FILE, dmHome +
                        "BADFILE_1.TXT");
            }else{
                instance.setAttribute(Properties.FLDR_ATTR_BAD_FILE, dmHome +
                        File.separator + "BADFILE_1.TXT");
            }
        }

        // 装载日志
        if (dmHome != null && !dmHome.equals("")){
            if (dmHome.endsWith("/") || dmHome.endsWith("\\")){
                instance.setAttribute(Properties.FLDR_ATTR_LOG_FILE, dmHome +
                        "FLDRLOG_1.TXT");
            }else{
                instance.setAttribute(Properties.FLDR_ATTR_LOG_FILE, dmHome +
                        File.separator + "FLDRLOG_1.TXT");
            }
        }

        //初始化当前快速装载实例
        boolean success = instance.initializeInstance(null, null, null, tableName);

        return success;

    }
}
