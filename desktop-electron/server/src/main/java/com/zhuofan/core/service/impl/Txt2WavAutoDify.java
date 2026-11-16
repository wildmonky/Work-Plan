package com.zhuofan.core.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.service.Txt2WavService;
import com.zhuofan.oms.api.AutoDifyApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("Txt2WavAutoDify")
public class Txt2WavAutoDify  implements Txt2WavService {
    @Override
    public String txt2wav(String text, String outputFilename) {
        String url= CommonFunc.getConfigByKey("api.txt2wav.url.execflow");
        String authorization=CommonFunc.getConfigByKey("api.txt2wav.token");
        String user=CommonFunc.getConfigByKey("api.ddbht.user.upload");

        String wavFile= null;
        try {
            wavFile = AutoDifyApi.txt2wavRunWorkflow(url,authorization,
                    text,user,600000,600000);
            if (wavFile.isEmpty()){
                return "";
            }

            JSONArray jsonArray=JSONArray.parseArray(wavFile);
            String wavUrl=jsonArray.getJSONObject(0).getString("url");
            if (StringUtils.isNotEmpty(wavUrl)){
                String  prefix=CommonFunc.getConfigByKey("api.txt2war.downurl.prefix");
                String walFullUrl=prefix+wavUrl;
                //进行HTTP下载
                
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return "";
    }

    public static void main(String[] args){
        Txt2WavAutoDify t=new Txt2WavAutoDify();

        String s=t.txt2wav("this is a test","");
        System.out.println(s);
    }
}
