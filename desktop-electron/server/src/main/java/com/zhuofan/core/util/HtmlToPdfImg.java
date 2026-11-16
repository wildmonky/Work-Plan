package com.zhuofan.core.util;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class HtmlToPdfImg {

    /**
     * @Author fuxiaodong
     * @Description //将HTML文件（URL）转换成PDF文件，需第三方命令支持
     * @Version: v1.8
     * @Date 10:13 2025/7/31
     * @Param
     * @return
     */
    public static boolean htmlToPdf(String url,String fileNameFull,String fileType){
        return htmlToPdfImage(url,fileNameFull,"pdf");
    }

    /**
     * @Author fuxiaodong
     * @Description //将HTML文件（URL）转换成IMAGE文件，需第三方命令支持
     * @Version: v1.8
     * @Date 10:13 2025/7/31
     * @Param
     * @return
     */
    public static boolean htmlToImage(String url,String fileNameFull,String fileType){
        return htmlToPdfImage(url,fileNameFull,"image");
    }

    private static boolean htmlToPdfImage(String url,String fileNameFull,String fileType){
        CommonLogger commonLogger=CommonLogger.getInstance();
        commonLogger.info(CommonFunc.unixTimeStr()+":htmlToPdfImage begin");
        commonLogger.info(CommonFunc.unixTimeStr()+":fileType:"+fileType);

        String commandPath=CommonFunc.getConfigByKey("sys.html2"+fileType+".command");
        commonLogger.info(CommonFunc.unixTimeStr()+":htmlToPdfImage url:"+url);

        String command="";
        String osName=System.getProperty("os.name").toLowerCase();
        List<String> cmds = new ArrayList<>();

        // /usr/local/bin/wkhtmltopdf
        // "https://www.fenfei.cc:9443/kemp/ex_web/jbcsd/jbcsd-server/CsdViewByPdf?csd_id=254195"
        // /home/apps/kemp/ex_web/jbcsd/app/test.pdf
        if (osName.contains("win")){
            if ("pdf".equals(fileType)) {
                command = String.format("%s \"%s\" \"%s\"", commandPath, url, fileNameFull);
            }else{
                command = String.format("%s --debug-javascript --enable-local-file-access --no-stop-slow-scripts --javascript-delay 1000 \"%s\" \"%s\"", commandPath, url, fileNameFull);
            }
            cmds.add(command);
        }else {
            if ("pdf".equals(fileType)) {
                command = String.format("source /etc/profile && %s \"%s\" \"%s\"",
                        commandPath, url, fileNameFull);
            }else{
                command = String.format("source /etc/profile && %s --debug-javascript --enable-local-file-access --no-stop-slow-scripts --javascript-delay 1000 \"%s\" \"%s\"", commandPath, url, fileNameFull);
            }
            cmds.add("/bin/bash");
            cmds.add("-c");
            cmds.add(command);
        }

        commonLogger.info(CommonFunc.unixTimeStr()+":htmlToPdf,command:"+command);
        System.out.println(CommonFunc.unixTimeStr()+":htmlToPdf,command:"+command);

        File file=new File(fileNameFull);
        if (file.isFile()){
            file.delete();
        }

        //执行命令
        Runtime runtime=Runtime.getRuntime();
        Process process=null;
        BufferedReader reader=null;
        InputStream inputStream=null;

        try{
            process= osName.contains("win")
                    ? runtime.exec(command,null,
                    new File(CommonFunc.getConfigByKey("sys.tempfile_path")))
                    : runtime.exec(cmds.toArray(new String[0]),null,
                    new File(CommonFunc.getConfigByKey("sys.tempfile_path")));
            inputStream=process.getInputStream();
            reader=new BufferedReader(new InputStreamReader(inputStream));
            String cmdResult="",cmdItem;

            while ((cmdItem=reader.readLine())!=null){
                cmdResult+=cmdItem;
            }

            System.out.println("result:"+cmdResult);
            commonLogger.info("result:"+cmdResult);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process!=null){
                process.destroy();
            }
        }

        return new File(fileNameFull).exists();
    }

    /**
     * @Author fuxiaodong
     * @Description //将HTML文件（URL）转换成IMAGE文件，需第三方命令支持
     * @Version: v1.8
     * @Date 10:13 2025/7/31
     * @Param
     * @return
     */
    public static void main (String[] args){
        htmlToPdfImage("http://localhost:8443/kemp/ex_web/oms/omsserver/Zdh/LogViewDefect?days=7",
                "d:/1.jpg","image");
    }
}
