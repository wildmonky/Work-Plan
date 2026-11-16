package com.zhuofan.oms.controller.sys;

import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/*
 */
@RestController
@CrossOrigin
public class BaseController {
    @Autowired(required = false)
    protected HttpServletRequest request;

    @Autowired(required = false)
    protected HttpServletResponse response;

    @Autowired
    protected Environment env;

    @Autowired
    protected SysConfig sysConfig;

    @Autowired
    protected DatasourceConfig datasourceConfig;


    public void setResponseHeader(HttpServletResponse response,String fileName){
        try{
            fileName=URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition","attachment;filename="+fileName);
            response.addHeader("Pargam","no-cache");
            response.addHeader("Cache-control","no-cache");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
