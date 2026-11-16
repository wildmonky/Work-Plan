package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName = "SYS_FLOW")
public class SysFlow extends BaseModel {
    @TableField(name = "FLOW_ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
    private String flowId;

    @TableField(name = "FLOW_NAME", type = TableField.TF_VARCHAR)
    private String flowName;

    @TableField(name = "TABLE_ID", type = TableField.TF_VARCHAR)
    private String tableId;

    @TableField(name = "CREATE_USER", type = TableField.TF_VARCHAR)
    private String createUser;

    @TableField(name = "CREATE_TIME", type = TableField.TF_VARCHAR)
    private String createTime;

    @TableField(name = "REMARK", type = TableField.TF_VARCHAR)
    private String remark;

    @TableField(name = "FLOW_CODE", type = TableField.TF_VARCHAR)
    private String flowCode;

    @TableField(name = "LIST_ORDERBY", type = TableField.TF_VARCHAR)
    private String listOrderby;

    @TableField(name = "NOTICE_FORMAT", type = TableField.TF_VARCHAR)
    private String noticeFormat;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {

        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowName = flowName;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.tableId = tableId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.createUser = createUser;
    }

    public void setCreateTime(String createTime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowCode = flowCode;
    }

    public String getListOrderby() {
        return listOrderby;
    }

    public void setListOrderby(String listOrderby) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.listOrderby = listOrderby;
    }

    public String getNoticeFormat() {
        return noticeFormat;
    }

    public void setNoticeFormat(String noticeFormat) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.noticeFormat = noticeFormat;
    }
}
