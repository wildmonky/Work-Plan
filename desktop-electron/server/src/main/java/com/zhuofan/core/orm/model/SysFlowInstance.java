package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName = "SYS_FLOW_INSTANCE")
public class SysFlowInstance  extends BaseModel {
    @TableField(name = "FLOW_INSTANCE_ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
    private String flowInstanceId;

    @TableField(name = "FLOW_ID", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String flowId;

    @TableField(name = "DATA_ID", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String dataId;

    @TableField(name = "CREATE_TIME", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String createTime;

    @TableField(name = "USERID", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String userid;

    @TableField(name = "DEPARTMENTID", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String departmentid;

    @TableField(name = "FLOW_NODE_ID", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String flowNodeId;

    @TableField(name = "STATUS", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String status;

    @TableField(name = "END_TIME", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String endTime;

    @TableField(name = "FLAG_RETURN", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String flagReturn;

    @TableField(name = "SENDTO_USER", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String sendtoUser;

    @TableField(name = "NOTICE_CONTENT", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String noticeContent;

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowInstanceId = flowInstanceId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowId = flowId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.dataId = dataId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.createTime = createTime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.userid = userid;
    }

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.departmentid = departmentid;
    }

    public String getFlowNodeId() {
        return flowNodeId;
    }

    public void setFlowNodeId(String flowNodeId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowNodeId = flowNodeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.endTime = endTime;
    }

    public String getFlagReturn() {
        return flagReturn;
    }

    public void setFlagReturn(String flagReturn) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flagReturn = flagReturn;
    }

    public String getSendtoUser() {
        return sendtoUser;
    }

    public void setSendtoUser(String sendtoUser) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sendtoUser = sendtoUser;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.noticeContent = noticeContent;
    }
}
