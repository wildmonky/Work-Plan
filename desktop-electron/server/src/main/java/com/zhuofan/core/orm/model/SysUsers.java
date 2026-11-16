/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_USERS 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */

package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * 用户角色表
 * 
 * @author fuxiaodong
 * @Date 2015-05-20
 * 
 */
@Table(
    tableName = "SYS_USERS")
public class SysUsers extends BaseModel {
    @TableField(
        name = "USERID",
        type = TableField.TF_VARCHAR,
        isPrimaryKey = true)
    private String userid;

    @TableField(
        name = "USERNAME",
        type = TableField.TF_VARCHAR,
        visible = true)
    private String username;

    @TableField(
        name = "SEX",
        type = TableField.TF_VARCHAR,
        visible = true)
    private String sex;

    @TableField(
        name = "USERPASSWORD",
        type = TableField.TF_VARCHAR,
        inputType = TableField.TFInpytType.password)
    private String userpassword;

    @TableField(
        name = "DEPARTMENTID",
        type = TableField.TF_VARCHAR,
        visible = true)
    private String departmentid;
    
    @TableField(
            name = "ISTEST",
            type = TableField.TF_VARCHAR,
            visible = false)
        private String istest;

    @TableField(
            name = "USERNAME_DFDZ",
            type = TableField.TF_VARCHAR,
            visible = false)
    private String usernameDfdz;



    @TableField(
            name = "ELINK_ID",
            type = TableField.TF_VARCHAR,
            visible = false)
    private String elinkId;

    /**
     * 0禁用，1可用
     */
    @TableField(
        name = "ACTIVED",
        type = TableField.TF_VARCHAR,
        visible = true)
    private String actived;

    @TableField(
        name = "REMARK",
        type = TableField.TF_VARCHAR,
        visible = true)
    private String remark;

    @TableField(
        name = "ROLEID",
        type = TableField.TF_VARCHAR,
        visible = true)
    private String roleid;

    @TableField(
            name = "MOBILE",
            type = TableField.TF_VARCHAR,
            visible = true)
    private String mobile;

    @TableField(
            name = "FLAG_LEADER",
            type = TableField.TF_VARCHAR,
            visible = true)
    private String flagLeader;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.userid = userid;
    }


    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.userpassword = userpassword;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sex = sex;
    }

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.departmentid = departmentid;
    }

    public String getActived() {
        return actived;
    }

    public void setActived(String actived) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.actived = actived;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.roleid = roleid;
    }

	public String getIstest() {
		return istest;
	}

	public void setIstest(String istest) {
		this.istest = istest;
	}

    public String getUsernameDfdz() {
        return usernameDfdz;
    }

    public void setUsernameDfdz(String usernameDfdz) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.usernameDfdz = usernameDfdz;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.username = username;
    }

    public String getElinkId() {
        return elinkId;
    }

    public void setElinkId(String elinkId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.elinkId = elinkId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.mobile = mobile;
    }

    public String getFlagLeader() {
        return flagLeader;
    }

    public void setFlagLeader(String flagLeader) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flagLeader = flagLeader;
    }
}
