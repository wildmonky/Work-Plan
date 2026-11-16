package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName = "SYS_FLOW_EXEC")
public class SysFlowExec extends BaseModel {
    @TableField(name = "FLOW_EXEC_ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
    private String flowExecId;

    @TableField(name = "FLOW_ID", type = TableField.TF_VARCHAR)
    private String flowId;

    @TableField(name = "FLOW_NODE_ID", type = TableField.TF_VARCHAR)
    private String flowNodeId;

    @TableField(name = "EXEC_TIME", type = TableField.TF_VARCHAR)
    private String execTime;

    @TableField(name = "USERID", type = TableField.TF_VARCHAR)
    private String userid;

    @TableField(name = "USERNAME", type = TableField.TF_VARCHAR)
    private String username;

    @TableField(name = "EXEC_ACTION", type = TableField.TF_VARCHAR)
    private String execAction;

    @TableField(name = "PREV_FLOW_NODE_ID", type = TableField.TF_VARCHAR)
    private String prevFlowNodeId;

    @TableField(name = "FLOW_INSTANCE_ID", type = TableField.TF_VARCHAR)
    private String flowInstanceId;

    @TableField(name = "DEPARTMENTID", type = TableField.TF_VARCHAR)
    private String departmentid;

    @TableField(name = "CREATE_TIME", type = TableField.TF_VARCHAR)
    private String createTime;

    @TableField(name = "PREV_FLOW_EXEC_ID", type = TableField.TF_VARCHAR)
    private String prevFlowExecId;

    @TableField(name = "FLAG_RETURN", type = TableField.TF_VARCHAR)
    private String flagReturn;

    @TableField(name = "SENDTO_USER", type = TableField.TF_VARCHAR)
    private String sendtoUser;

    @TableField(name = "AUDIT_CONTENT", type = TableField.TF_VARCHAR)
    private String auditContent;

    @TableField(name = "LAST_UPDATE_TIME", type = TableField.TF_VARCHAR)
    private String lastUpdateTime;

    public String getFlowExecId() {
        return flowExecId;
    }

    public void setFlowExecId(String flowExecId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowExecId = flowExecId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowId = flowId;
    }

    public String getFlowNodeId() {
        return flowNodeId;
    }

    public void setFlowNodeId(String flowNodeId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowNodeId = flowNodeId;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.execTime = execTime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.username = username;
    }

    public String getExecAction() {
        return execAction;
    }

    public void setExecAction(String execAction) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.execAction = execAction;
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowInstanceId = flowInstanceId;
    }

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.departmentid = departmentid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.createTime = createTime;
    }

    public String getPrevFlowExecId() {
        return prevFlowExecId;
    }

    public void setPrevFlowExecId(String prevFlowExecId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.prevFlowExecId = prevFlowExecId;
    }

    public String getPrevFlowNodeId() {
        return prevFlowNodeId;
    }

    public void setPrevFlowNodeId(String prevFlowNodeId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.prevFlowNodeId = prevFlowNodeId;
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

    public String getAuditContent() {
        return auditContent;
    }

    public void setAuditContent(String auditContent) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.auditContent = auditContent;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.lastUpdateTime = lastUpdateTime;
    }
}
