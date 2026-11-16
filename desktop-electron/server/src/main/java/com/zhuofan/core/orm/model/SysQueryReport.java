/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 查询报表主表:SYS_QUERYREPORT
 *           2015-05-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysQueryReport 查询报表主表
 * 
 * @author fuxiaodong
 * @Date 2015-05-21
 * 
 */
@Table(tableName = "SYS_QUERYREPORT", comment = "查询报表主表")
public class SysQueryReport extends BaseModel {
	@TableField(name = "QUERY_REPORT_ID", type = "NUMBER", comment = "报表ID", isPrimaryKey = true)
	private long queryReportId;

	@TableField(name = "REPORT_NAME", type = "VARCHAR", comment = "报表名称")
	private String reportName;

	@TableField(name = "REPORT_CODE", type = "VARCHAR", comment = "报表代码")
	private String reportCode;

	@TableField(name = "CREATETIME", type = "NUMBER", comment = "创建时间")
	private long createTime;

	@TableField(name = "CREATEUSER", type = "VARCHAR", comment = "创建人")
	private String createUser;

	@TableField(name = "TITLE", type = "VARCHAR", comment = "标题")
	private String title;

	@TableField(name = "TABLENAME", type = "VARCHAR", comment = "表名")
	private String tableName;

	@TableField(name = "DELETE_TABLENAME", type = "VARCHAR", comment = "删除表名")
	private String deleteTableName;

	@TableField(name = "DELETE_SQL", type = "VARCHAR", comment = "删除SQL")
	private String deleteSql;

	@TableField(name = "SELECTFLDN", type = "VARCHAR", comment = "选择字段")
	private String selectFldn;

	@TableField(name = "FLDN_ID", type = "VARCHAR", comment = "主键字段")
	private String fldnId;

	@TableField(name = "ORDERFLDN", type = "VARCHAR", comment = "排序字段")
	private String orderFldn;

	@TableField(name = "ORDERTYPE", type = "VARCHAR", comment = "排序方式")
	private String orderType;

	@TableField(name = "PAGENUMBER", type = "NUMBER", comment = "每页显示数量")
	private long pageNumber;

	@TableField(name = "OTHER", type = "VARCHAR", comment = "自定义内容")
	private String other;

	@TableField(name = "STYLES", type = "VARCHAR", comment = "样式")
	private String styles;

	@TableField(name = "WHERE_EX", type = "VARCHAR", comment = "扩展查询条件")
	private String whereEx;

	@TableField(name = "SQL_TOTAL", type = "VARCHAR", comment = "统计SQL语句")
	private String sqlTotal;

	@TableField(name = "SQL_TOTAL_FLDN", type = "VARCHAR", comment = "统计字段")
	private String sqlTotalFldn;

	@TableField(name = "GROUPBY", type = "VARCHAR", comment = "分组条件")
	private String groupBy;

	@TableField(name = "HAS_DELETE_FLAG", type = "VARCHAR", comment = "是否显示删除标记")
	private String hasDeleteFlag;

	@TableField(name = "LISTTYPE", type = "VARCHAR", comment = "报表类别")
	private String listType;

	@TableField(name = "ROWCLICK_SCRIPT", type = "VARCHAR", comment = "行点击JS脚本")
	private String rowClickScript;

	@TableField(name = "OTHER_BUTTON", type = "VARCHAR", comment = "自定义按钮")
	private String otherButton;

	public long getQueryReportId() {
		return this.queryReportId;
	}

	public void setQueryReportId(long queryReportId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.queryReportId = queryReportId;
	}

	public String getReportName() {
		return this.reportName;
	}

	public void setReportName(String reportName) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.reportName = reportName;
	}

	public String getReportCode() {
		return this.reportCode;
	}

	public void setReportCode(String reportCode) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.reportCode = reportCode;
	}

	public long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(long createTime) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.createUser = createUser;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.title = title;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableName = tableName;
	}

	public String getDeleteTableName() {
		return this.deleteTableName;
	}

	public void setDeleteTableName(String deleteTableName) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.deleteTableName = deleteTableName;
	}

	public String getDeleteSql() {
		return this.deleteSql;
	}

	public void setDeleteSql(String deleteSql) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.deleteSql = deleteSql;
	}

	public String getSelectFldn() {
		return this.selectFldn;
	}

	public void setSelectFldn(String selectFldn) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.selectFldn = selectFldn;
	}

	public String getFldnId() {
		return this.fldnId;
	}

	public void setFldnId(String fldnId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fldnId = fldnId;
	}

	public String getOrderFldn() {
		return this.orderFldn;
	}

	public void setOrderFldn(String orderFldn) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.orderFldn = orderFldn;
	}

	public String getOrderType() {
		return this.orderType;
	}

	public void setOrderType(String orderType) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.orderType = orderType;
	}

	public long getPageNumber() {
		return this.pageNumber;
	}

	public void setPageNumber(long pageNumber) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.pageNumber = pageNumber;
	}

	public String getOther() {
		return this.other;
	}

	public void setOther(String other) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.other = other;
	}

	public String getStyles() {
		return this.styles;
	}

	public void setStyles(String styles) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.styles = styles;
	}

	public String getWhereEx() {
		return this.whereEx;
	}

	public void setWhereEx(String whereEx) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.whereEx = whereEx;
	}

	public String getSqlTotal() {
		return this.sqlTotal;
	}

	public void setSqlTotal(String sqlTotal) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sqlTotal = sqlTotal;
	}

	public String getSqlTotalFldn() {
		return this.sqlTotalFldn;
	}

	public void setSqlTotalFldn(String sqlTotalFldn) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sqlTotalFldn = sqlTotalFldn;
	}

	public String getGroupBy() {
		return this.groupBy;
	}

	public void setGroupBy(String groupBy) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.groupBy = groupBy;
	}

	public String getHasDeleteFlag() {
		return this.hasDeleteFlag;
	}

	public void setHasDeleteFlag(String hasDeleteFlag) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.hasDeleteFlag = hasDeleteFlag;
	}

	public String getListType() {
		return this.listType;
	}

	public void setListType(String listType) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.listType = listType;
	}

	public String getRowClickScript() {
		return this.rowClickScript;
	}

	public void setRowClickScript(String rowClickScript) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.rowClickScript = rowClickScript;
	}

	public String getOtherButton() {
		return this.otherButton;
	}

	public void setOtherButton(String otherButton) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.otherButton = otherButton;
	}

}