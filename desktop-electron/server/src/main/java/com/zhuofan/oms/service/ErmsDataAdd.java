package com.zhuofan.oms.service;

import com.zhuofan.core.common.CommonFunc;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.util.UUID;

public class ErmsDataAdd {

    /*
     * 数据录入时,上传文件处理
     * 参数:
     * req :带文件对象的request
     * fieldName:要保存到数据库的字段名,以及文件上传的变量名
     * savePath:要保存到的根目录
     * entry:如果为对象,则设置相关的值
     *
     * 返回数据:
     *{
            result:0/1,
            msg:提示消息
        };
     */
    public void uploadFile(HttpServletRequest req,String fieldName,String savePath,Object entry){
        MultipartHttpServletRequest mulReq = (MultipartHttpServletRequest) req;
        MultipartFile mulFile = mulReq.getFile(fieldName);

        String yearMonth=CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()),"yyyyMM");
        String uuid = UUID.randomUUID().toString();

        if (mulFile!=null){
            String fileName=mulFile.getOriginalFilename();
            String fileFormat = fileName.substring(fileName.lastIndexOf("."));

            String saveFileName = uuid+fileFormat;
            String dir=savePath+"/"+yearMonth;
            String saveFullName = savePath + "/" + yearMonth + "/" + saveFileName;

            try {
                File fileD = new File(dir);
                if (!fileD.exists()) {
                    fileD.mkdirs();
                }

                if (fileD.exists()) {

                    File targetFile = new File(saveFullName);
                    System.out.println("targetFile:"+targetFile);
                    FileUtils.writeByteArrayToFile(targetFile, mulFile.getBytes());
                }

                //保存至相关对象
                if (entry!=null){
                    long size=mulFile.getSize();
                    String fileType=fileFormat.substring(1);
                    String fieldNameSize= fieldName.substring(0,fieldName.length()-4)+"SIZE";
                    String fieldNameType=fieldNameSize.substring(0,fieldName.length()-4)+"TYPE";
                    String fieldNameSrc=fieldNameSize.substring(0,fieldName.length()-4)+"NAME";
                    String methodNameSize="set"+CommonFunc.changeWord(fieldNameSize);
                    String methodNameType="set"+CommonFunc.changeWord(fieldNameType);
                    String methodNamePath="set"+CommonFunc.changeWord(fieldName);
                    String methodNameSrc="set"+CommonFunc.changeWord(fieldNameSrc);

                    try{
                        Method methodPath=entry.getClass().getMethod(methodNamePath,new Class[]{String.class});
                        methodPath.invoke(entry,new Object[]{saveFullName});
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try{
                        Method methodSize=entry.getClass().getMethod(methodNameSize,new Class[]{Long.class});
                        methodSize.invoke(entry,new Object[]{size});
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try {
                        Method methodType = entry.getClass().getMethod(methodNameType, new Class[]{String.class});
                        methodType.invoke(entry,new Object[]{fileType});
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try {
                        Method methodSrc = entry.getClass().getMethod(methodNameSrc, new Class[]{String.class});
                        methodSrc.invoke(entry,new Object[]{fileName});
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
