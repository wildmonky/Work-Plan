/** 
 * com.zhuofan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_MODULES 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * SYS_MODULES 用户角色表
 * 
 * @author fuxiaodong
 * @Date 2015-05-19
 * 
 */
@Table(
    tableName = "SYS_MODULES",
    comment = "角色权限信息")
public class SysModules extends BaseModel {
    @TableField(
        name = "MODULEID",
        isPrimaryKey = true)
    private String moduleid;
    @TableField(
        name = "MODULENAME",
        type = TableField.TF_VARCHAR)
    private String modulename;
    @TableField(
        name = "REMARK",
        type = TableField.TF_VARCHAR)
    private String remark;
    @TableField(
        name = "PARENTID",
        type = TableField.TF_VARCHAR)
    private String parentid;
    @TableField(
        name = "URL",
        type = TableField.TF_VARCHAR)
    private String url;
    @TableField(
        name = "SORTINDEX",
        type = TableField.TF_VARCHAR)
    private String sortindex;
    @TableField(
        name = "TYPE",
        type = TableField.TF_VARCHAR)
    private String type;
    @TableField(
        name = "ICOCLASS",
        type = TableField.TF_VARCHAR)
    private String icoclass;
    @TableField(
        name = "ISSHOW",
        type = TableField.TF_VARCHAR)
    private String isshow;

    public String getModuleid() {
        return this.moduleid;
    }

    public void setModuleid(String moduleid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.moduleid = moduleid;
    }

    public String getModulename() {
        return this.modulename;
    }

    public void setModulename(String modulename) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.modulename = modulename;
    }

    public String getParentid() {
        return this.parentid;
    }

    public void setParentid(String parentid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.parentid = parentid;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.url = url;
    }

    public String getSortindex() {
        return this.sortindex;
    }

    public void setSortindex(String sortindex) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sortindex = sortindex;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.type = type;
    }

    public String getIcoclass() {
        return icoclass;
    }

    public void setIcoclass(String icoclass) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.icoclass = icoclass;
    }

    public String getIsshow() {
        return isshow;
    }

    public void setIsshow(String isshow) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.isshow = isshow;
    }
}
