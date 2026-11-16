/**
         * com.zhuofan.core.orm.model
         *
         *
         *   ver     date           author 
         * 流程节点配置表：SYS_FLOW_NODE 
         *           2015-05-20      fuxiaodong 
         *
         * Copyright (c) 2015, TNT All Rights Reserved. 
**/

package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * SYS_FLOW_NODE 流程节点配置表
 *
 * @author admin
 * @Date 2025-10-28 09:06:55
 *
 */

@Table(
    tableName = "SYS_FLOW_NODE",
    comment = "流程节点配置表")

public class SysFlowNode extends BaseModel {

            @TableField(
                    name = "FLOW_NODE_ID",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = true)
            private String flowNodeId;

            @TableField(
                    name = "FLOW_ID",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String flowId;

            @TableField(
                    name = "NODE_NAME",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String nodeName;

            @TableField(
                    name = "NODE_CODE",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String nodeCode;

            @TableField(
                    name = "GRANTS",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String grants;

            @TableField(
                    name = "TABLE_COLUMN_GROUP_ID",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String tableColumnGroupId;

            @TableField(
                    name = "TABLE_SUBINFO",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String tableSubinfo;

            @TableField(
                    name = "GRANTS_FIELD",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String grantsField;

            @TableField(
                    name = "NODE_TYPE",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String nodeType;

            @TableField(
                    name = "EVENT_BEFORE",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String eventBefore;

            @TableField(
                    name = "EVENT_AFTER",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String eventAfter;

            @TableField(
                    name = "TEMPLATE_NAME",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String templateName;

            @TableField(
                    name = "REMARK",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String remark;

            @TableField(
                    name = "SORTINDEX",
                    type = TableField.TF_NUMBER,
                    isPrimaryKey = false)
            private Long sortindex;

            @TableField(
                    name = "IS_CHILDNODE",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String isChildnode;

            @TableField(
                    name = "PARENT_NODE_ID",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String parentNodeId;

            @TableField(
                    name = "CHILD_SORTINDEX",
                    type = TableField.TF_NUMBER,
                    isPrimaryKey = false)
            private Long childSortindex;

            @TableField(
                    name = "SWITCH_CODE",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String switchCode;

            @TableField(
                    name = "LOG_ACCEPT",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String logAccept;

            @TableField(
                    name = "LOG_RETURN",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String logReturn;

            @TableField(
                    name = "IS_EMPTYNODE",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String isEmptynode;

            @TableField(
                    name = "IS_SELECTUSER",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String isSelectuser;

            @TableField(
                    name = "ELINK_ACCEPT",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String elinkAccept;

            @TableField(
                    name = "ELINK_RETURN",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String elinkReturn;

            @TableField(
                    name = "FLAG_RETURN_FIRST",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String flagReturnFirst;

            @TableField(
                    name = "FLDN_AUDIT",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String fldnAudit;

            @TableField(
                    name = "AUDIT_CONTENT",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String auditContent;

            @TableField(
                    name = "SUBMIT_DEFUSER",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String submitDefuser;

            @TableField(
                    name = "WHERE_EX",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String whereEx;

            @TableField(
                    name = "BY1",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String by1;

            @TableField(
                    name = "BY2",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String by2;

            @TableField(
                    name = "BY3",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String by3;

            @TableField(
                    name = "BY4",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String by4;

            @TableField(
                    name = "BY5",
                    type = TableField.TF_VARCHAR,
                    isPrimaryKey = false)
            private String by5;

    public String getFlowNodeId() {
        return this.flowNodeId;
    }

    public void setFlowNodeId(String flowNodeId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowNodeId = flowNodeId;
    }

    public String getFlowId() {
        return this.flowId;
    }

    public void setFlowId(String flowId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flowId = flowId;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public void setNodeName(String nodeName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.nodeName = nodeName;
    }

    public String getNodeCode() {
        return this.nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.nodeCode = nodeCode;
    }

    public String getGrants() {
        return this.grants;
    }

    public void setGrants(String grants) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.grants = grants;
    }

    public String getTableColumnGroupId() {
        return this.tableColumnGroupId;
    }

    public void setTableColumnGroupId(String tableColumnGroupId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.tableColumnGroupId = tableColumnGroupId;
    }

    public String getTableSubinfo() {
        return this.tableSubinfo;
    }

    public void setTableSubinfo(String tableSubinfo) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.tableSubinfo = tableSubinfo;
    }

    public String getGrantsField() {
        return this.grantsField;
    }

    public void setGrantsField(String grantsField) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.grantsField = grantsField;
    }

    public String getNodeType() {
        return this.nodeType;
    }

    public void setNodeType(String nodeType) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.nodeType = nodeType;
    }

    public String getEventBefore() {
        return this.eventBefore;
    }

    public void setEventBefore(String eventBefore) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.eventBefore = eventBefore;
    }

    public String getEventAfter() {
        return this.eventAfter;
    }

    public void setEventAfter(String eventAfter) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.eventAfter = eventAfter;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(String templateName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.templateName = templateName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public Long getSortindex() {
        return this.sortindex;
    }

    public void setSortindex(Long sortindex) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sortindex = sortindex;
    }

    public String getIsChildnode() {
        return this.isChildnode;
    }

    public void setIsChildnode(String isChildnode) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.isChildnode = isChildnode;
    }

    public String getParentNodeId() {
        return this.parentNodeId;
    }

    public void setParentNodeId(String parentNodeId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.parentNodeId = parentNodeId;
    }

    public Long getChildSortindex() {
        return this.childSortindex;
    }

    public void setChildSortindex(Long childSortindex) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.childSortindex = childSortindex;
    }

    public String getSwitchCode() {
        return this.switchCode;
    }

    public void setSwitchCode(String switchCode) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.switchCode = switchCode;
    }

    public String getLogAccept() {
        return this.logAccept;
    }

    public void setLogAccept(String logAccept) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.logAccept = logAccept;
    }

    public String getLogReturn() {
        return this.logReturn;
    }

    public void setLogReturn(String logReturn) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.logReturn = logReturn;
    }

    public String getIsEmptynode() {
        return this.isEmptynode;
    }

    public void setIsEmptynode(String isEmptynode) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.isEmptynode = isEmptynode;
    }

    public String getIsSelectuser() {
        return this.isSelectuser;
    }

    public void setIsSelectuser(String isSelectuser) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.isSelectuser = isSelectuser;
    }

    public String getElinkAccept() {
        return this.elinkAccept;
    }

    public void setElinkAccept(String elinkAccept) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.elinkAccept = elinkAccept;
    }

    public String getElinkReturn() {
        return this.elinkReturn;
    }

    public void setElinkReturn(String elinkReturn) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.elinkReturn = elinkReturn;
    }

    public String getFlagReturnFirst() {
        return this.flagReturnFirst;
    }

    public void setFlagReturnFirst(String flagReturnFirst) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flagReturnFirst = flagReturnFirst;
    }

    public String getFldnAudit() {
        return this.fldnAudit;
    }

    public void setFldnAudit(String fldnAudit) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.fldnAudit = fldnAudit;
    }

    public String getAuditContent() {
        return this.auditContent;
    }

    public void setAuditContent(String auditContent) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.auditContent = auditContent;
    }

    public String getSubmitDefuser() {
        return this.submitDefuser;
    }

    public void setSubmitDefuser(String submitDefuser) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.submitDefuser = submitDefuser;
    }

    public String getWhereEx() {
        return this.whereEx;
    }

    public void setWhereEx(String whereEx) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.whereEx = whereEx;
    }

    public String getBy1() {
        return this.by1;
    }

    public void setBy1(String by1) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.by1 = by1;
    }

    public String getBy2() {
        return this.by2;
    }

    public void setBy2(String by2) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.by2 = by2;
    }

    public String getBy3() {
        return this.by3;
    }

    public void setBy3(String by3) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.by3 = by3;
    }

    public String getBy4() {
        return this.by4;
    }

    public void setBy4(String by4) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.by4 = by4;
    }

    public String getBy5() {
        return this.by5;
    }

    public void setBy5(String by5) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.by5 = by5;
    }

}
