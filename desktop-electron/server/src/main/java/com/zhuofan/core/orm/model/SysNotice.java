package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * @author Administrator
 *
 */
@Table(
        tableName = "SYS_NOTICE")
public class SysNotice extends BaseModel {

    //通知ID
    @TableField(
            name = "NOTICE_ID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String noticeId;

    //创建时间
    @TableField(
            name = "CREATETIME",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String createtime;

    //创建人
    @TableField(
            name = "CREATEUSER",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String createuser;

    //通知标题
    @TableField(
            name = "BIAOTI",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String biaoti;

    //通知内容
    @TableField(
            name = "NEIRONG",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String neirong;

    //添加时间
    @TableField(
            name = "SHIJIAN",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String shijian;

    //附件
    @TableField(
            name = "FUJIAN",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String fujian;

    //通知类型
    @TableField(
            name = "LEIXING",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String leixing;

    //通知对像
    @TableField(
            name = "TONGZHIDUIXIANG",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String tongzhiduixiang;

    //被通知人
    @TableField(
            name = "MAN_NOTICE_TO",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String manNoticeTo;

    //URL
    @TableField(
            name = "NOTICE_INFO_URL",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String noticeInfoUrl;

    //已读用户
    @TableField(
            name = "USER_HAVEREAD",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
    private String userHaveread;

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.noticeId = noticeId;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.createtime = createtime;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.createuser = createuser;
    }

    public String getBiaoti() {
        return biaoti;
    }

    public void setBiaoti(String biaoti) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.biaoti = biaoti;
    }

    public String getNeirong() {
        return neirong;
    }

    public void setNeirong(String neirong) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.neirong = neirong;
    }

    public String getShijian() {
        return shijian;
    }

    public void setShijian(String shijian) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.shijian = shijian;
    }

    public String getFujian() {
        return fujian;
    }

    public void setFujian(String fujian) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.fujian = fujian;
    }

    public String getLeixing() {
        return leixing;
    }

    public void setLeixing(String leixing) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.leixing = leixing;
    }

    public String getTongzhiduixiang() {
        return tongzhiduixiang;
    }

    public void setTongzhiduixiang(String tongzhiduixiang) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.tongzhiduixiang = tongzhiduixiang;
    }

    public String getManNoticeTo() {
        return manNoticeTo;
    }

    public void setManNoticeTo(String manNoticeTo) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.manNoticeTo = manNoticeTo;
    }

    public String getNoticeInfoUrl() {
        return noticeInfoUrl;
    }

    public void setNoticeInfoUrl(String noticeInfoUrl) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.noticeInfoUrl = noticeInfoUrl;
    }

    public String getUserHaveread() {
        return userHaveread;
    }

    public void setUserHaveread(String userHaveread) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.userHaveread = userHaveread;
    }
}
