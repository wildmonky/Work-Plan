package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(
    tableName = "SYS_DEPARTMENTS")
public class SysDepartments extends BaseModel {
    @TableField(
        name = "DEPARTMENTID",
        type = TableField.TF_VARCHAR,
        isPrimaryKey = true)
    private String departmentid;

    @TableField(
        name = "DEPARTMENTNAME",
        type = TableField.TF_VARCHAR)
    private String departmentname;

    @TableField(
        name = "REMARK",
        type = TableField.TF_VARCHAR)
    private String remark;

    @TableField(
        name = "PARENTID",
        type = TableField.TF_VARCHAR)
    private String parentid;

    @TableField(
            name = "ELINK_CHAT_ID",
            type = TableField.TF_VARCHAR)
    private String elinkChatId;

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.departmentid = departmentid;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.departmentname = departmentname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.parentid = parentid;
    }

    public String getElinkChatId() {
        return elinkChatId;
    }

    public void setElinkChatId(String elinkChatId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.elinkChatId = elinkChatId;
    }
}
