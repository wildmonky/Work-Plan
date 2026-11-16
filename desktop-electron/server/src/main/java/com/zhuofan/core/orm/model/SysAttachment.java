package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(
        tableName = "SYS_ATTACHMENT")
public class SysAttachment extends BaseModel {
    @TableField(
            name = "ATTA_ID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attaId;

    @TableField(
            name = "ATTA_SRCTYPE",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attaSrctype;

    @TableField(
            name = "ATTA_SRCID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attaSrcid;

    @TableField(
            name = "ATTACHMENT_NAME",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attachmentName;

    @TableField(
            name = "ATTACHMENT_DESCR",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attachmentDescr;

    @TableField(
            name = "CONTENT",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String content;

    @TableField(
            name = "ATTACHMENT_DIR",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attachmentDir;

    @TableField(
            name = "UPLOAD_USERNAME",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String uploadUsername;

    @TableField(
            name = "UPLOAD_USERID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String uploadUserid;

    @TableField(
            name = "UPLOAD_USERDEPT",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String uploadUserdept;

    @TableField(
            name = "UPLOAD_USERDEPTID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String uploadUserdeptid;

    @TableField(
            name = "UPLOAD_TIME",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String uploadTime;

    @TableField(
            name = "ATTACHMENT_SIZE",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attachmentSize;

    @TableField(
            name = "PMSID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String pmsid;

    @TableField(
            name = "ATTACHMENT_SUM",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String attachmentSum;

    @TableField(
            name = "REMARK",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String remark;

    @TableField(
            name = "GUID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String guid;

    @TableField(
            name = "EDIT_TIME",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String editTime;

    public String getAttaId() {
        return attaId;
    }

    public void setAttaId(String attaId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attaId = attaId;
    }

    public String getAttaSrctype() {
        return attaSrctype;
    }

    public void setAttaSrctype(String attaSrctype) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attaSrctype = attaSrctype;
    }

    public String getAttachmentDescr() {
        return attachmentDescr;
    }

    public void setAttachmentDescr(String attachmentDescr) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attachmentDescr = attachmentDescr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.content = content;
    }

    public String getAttachmentDir() {
        return attachmentDir;
    }

    public void setAttachmentDir(String attachmentDir) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attachmentDir = attachmentDir;
    }

    public String getUploadUsername() {
        return uploadUsername;
    }

    public void setUploadUsername(String uploadUsername) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.uploadUsername = uploadUsername;
    }

    public String getUploadUserid() {
        return uploadUserid;
    }

    public void setUploadUserid(String uploadUserid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.uploadUserid = uploadUserid;
    }

    public String getUploadUserdept() {
        return uploadUserdept;
    }

    public void setUploadUserdept(String uploadUserdept) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.uploadUserdept = uploadUserdept;
    }

    public String getUploadUserdeptid() {
        return uploadUserdeptid;
    }

    public void setUploadUserdeptid(String uploadUserdeptid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.uploadUserdeptid = uploadUserdeptid;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.uploadTime = uploadTime;
    }

    public String getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(String attachmentSize) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attachmentSize = attachmentSize;
    }

    public String getPmsid() {
        return pmsid;
    }

    public void setPmsid(String pmsid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.pmsid = pmsid;
    }

    public String getAttachmentSum() {
        return attachmentSum;
    }

    public void setAttachmentSum(String attachmentSum) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attachmentSum = attachmentSum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.guid = guid;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.editTime = editTime;
    }

    public String getAttaSrcid() {
        return attaSrcid;
    }

    public void setAttaSrcid(String attaSrcid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attaSrcid = attaSrcid;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.attachmentName = attachmentName;
    }
}
