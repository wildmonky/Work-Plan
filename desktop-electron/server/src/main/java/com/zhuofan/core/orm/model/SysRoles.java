/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_ROLES 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * SysRoles 用户角色表
 * 
 * @author fuxiaodong
 * @Date 2015-05-19
 * 
 */
@Table(
    tableName = "SYS_ROLES",
    comment = "角色信息")
public class SysRoles extends BaseModel {
    @TableField(
        name = "ROLEID",
        isPrimaryKey = true)
    private String roleid;

    @TableField(
        name = "ROLENAME")
    private String rolename;

    @TableField(
        name = "REMARK")
    private String remark;

    @TableField(
        name = "DEPT_TYPE")
    private String deptType;

    @TableField(
        name = "M_TYPE")
    private String mType;

    @TableField(
        name = "IS_ADMIN")
    private String isAdmin;

    public String getRoleid() {
        return this.roleid;
    }

    public void setRoleid(String roleid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.roleid = roleid;
    }

    public String getRolename() {
        return this.rolename;
    }

    public void setRolename(String rolename) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.rolename = rolename;
    }

    public String getDeptType() {
        return this.deptType;
    }

    public void setDeptType(String deptType) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.deptType = deptType;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public String getMType() {
        return this.mType;
    }

    public void setMType(String mType) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.mType = mType;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.mType = mType;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.isAdmin = isAdmin;
    }
}
