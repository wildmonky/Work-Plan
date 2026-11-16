package com.zhuofan.oms.service;

import com.zhuofan.core.config.SysConfig;

import java.util.Map;

/**
 * @ClassName: DataEditService
 * @Description: 数据编辑服务的前置,后置处理器
 * @author 广州卓凡扶小冬
 * @date Jun 15, 2022 8:19:02 PM
 */

public class DataEditService {
    private Map<String,String> params=null;
    private Map<String,Object> paramsPost=null;
    private Object objectData=null;
    private SysConfig sysConfig;

    public DataEditService(Map<String,String> params, SysConfig sysConfig){
        this.params=params;
        this.sysConfig=sysConfig;
    }

    public DataEditService(Map<String,Object> paramsPost, Object objectData, SysConfig sysConfig){
        this.paramsPost=paramsPost;
        this.objectData=objectData;
        this.sysConfig=sysConfig;
    }
}
