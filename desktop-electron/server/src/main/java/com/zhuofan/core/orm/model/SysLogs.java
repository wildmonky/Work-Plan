/** 
 * com.zhuofan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_LOGS 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * SysLogs 系统操作日志表
 * 
 * @author fuxiaodong
 * @Date 2015-05-19
 * 
 */
@Table(
    tableName = "SYS_LOGS",
    comment = "日志信息")
public class SysLogs extends BaseModel {
    @TableField(
        name = "LOGID",
        isPrimaryKey = true)
    private String logId;
    
    @TableField(
        name = "USERID")
    private String userId;
    
    @TableField(
        name = "LOGINIP")
    private String loginIp;
    
    @TableField(
        name = "EXETIME")
    private String exeTime;
    
    @TableField(
        name = "EXETYPE")
    private String exeType;
    
    @TableField(
        name = "EXECONTENT")
    private String exeContent;
    
    @TableField(
        name = "LOGTYPE")
    private String logType;
    
    @TableField(
        name = "USERNAME")
    private String userName;

    public String getLogId() {
        return this.logId;
    }

    public void setLogId(String logId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.logId = logId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void getUserId(String userId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.userId = userId;
    }

    public String getLoginIp() {
        return this.loginIp;
    }

    public void setLoginIp(String loginIp) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.loginIp = loginIp;
    }

    public String getExeTime() {
        return this.exeTime;
    }

    public void setExeTime(String exeTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.exeTime = exeTime;
    }

    public String getExeType() {
        return this.exeType;
    }

    public void setExeType(String exeType) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.exeType = exeType;
    }

    public String getExeContent() {
        return this.exeContent;
    }

    public void setExeContent(String exeContent) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.exeContent = exeContent;
    }

    public String getLogType() {
        return this.logType;
    }

    public void setLogType(String logType) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.logType = logType;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.userName = userName;
    }

}
