package com.zhuofan.oms.controller.sys;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.kafka.KafkaClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/*
 */
@RestController
@CrossOrigin
public class ElinkController {
    /*
     * SendMessage:下载附件
     * 功能:发送ELINK信息
     * 参数：
     * 类型：sms/msg/file/image
     * 接收对象：sendto:三者合一：ELINk人员名单user_list，chatId,phohelist
     * 消息内容：message
     * 文件名：filename
     * 文件内容：filecontent
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Elink/SendMessage",method = RequestMethod.POST)
    public Map<String, Object> SaveData(@RequestParam Map<String,Object> params, HttpServletRequest req) throws Exception {
        String msgType= CommonFunc.getNotNullString(params.get("msgType"));
        String userList=CommonFunc.getNotNullString(params.get("userList"));
        String chatId=CommonFunc.getNotNullString(params.get("chatId"));
        String phoneList=CommonFunc.getNotNullString(params.get("phoneList"));
        String message=CommonFunc.getNotNullString(params.get("message"));
        String fileName=CommonFunc.getNotNullString(params.get("fileName"));
        String fileContent=CommonFunc.getNotNullString(params.get("fileContent"));
        String appName=CommonFunc.getNotNullString(params.get("appName"));
        boolean sendFlag=false;

        KafkaClient kafkaClient=new KafkaClient(appName);

        if ("sms".equals(msgType)){
            sendFlag=kafkaClient.sendSms(message,phoneList);
        }else if ("msg".equals(msgType)){
            sendFlag=kafkaClient.sendElinkMsg(message,userList,chatId);
        }else if (("file".equals(msgType)) || ("image".equals(msgType))) {
            //上传文件到EL指定目录
            String descPath=("file".equals(msgType))?CommonFunc.getConfigByKey("sys.kafka_path_file"):CommonFunc.getConfigByKey("sys.kafka_path_image");
            String fileNameDesc=descPath+"/"+fileName;

            byte[] arrByte=Base64.getDecoder().decode(fileContent);
            try {
                // 将数据写入文件
                FileOutputStream fos = new FileOutputStream(fileNameDesc);
                fos.write(arrByte);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if ("file".equals(msgType)) {
                kafkaClient.sendElinkFile(fileNameDesc, userList, chatId);
            }else {
                kafkaClient.sendElinkImage(fileNameDesc, userList, chatId);
            }
        }else{
            //上传图片到指定目录
            return CommonFunc.makeErrResult("800002");
        }

        return null;
    }


}
