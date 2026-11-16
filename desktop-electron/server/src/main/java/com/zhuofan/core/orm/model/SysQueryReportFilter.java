/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 查询报表条件设置表:SYS_QUERYREPORT_FILTER
 *           2015-05-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysQueryReportFilter 查询报表条件设置表
 * 
 * @author fuxiaodong
 * @Date 2015-05-21
 * 
 */
@Table(tableName = "SYS_QUERYREPORT_FILTER", comment = "查询报表条件设置表")
public class SysQueryReportFilter extends BaseModel {
	@TableField(name = "QUERY_REPORT_FILTER_ID", type = "NUMBER", comment = "主键ID", isPrimaryKey = true)
	private long queryReportFilterId;

	@TableField(name = "QUERY_REPORT_ID", type = "NUMBER", comment = "查询报表ID")
	private long queryReportId;

	@TableField(name = "TEXT", type = "VARCHAR", comment = "文本")
	private String text;

	@TableField(name = "VARNAME", type = "VARCHAR", comment = "变量名称")
	private String varName;

	@TableField(name = "FLDN", type = "VARCHAR", comment = "字段名称")
	private String fldn;

	@TableField(name = "FILTERTYPE", type = "VARCHAR", comment = "过滤类别")
	private String filterType;

	@TableField(name = "VARVALUE", type = "VARCHAR", comment = "变量类别")
	private String varValue;

	@TableField(name = "OPER", type = "VARCHAR", comment = "操作方式")
	private String oper;

	@TableField(name = "SQL", type = "VARCHAR", comment = "SQL语句")
	private String sql;

	@TableField(name = "SQLFLDN", type = "VARCHAR", comment = "SQL字段")
	private String sqlFldn;

	@TableField(name = "ARRAY", type = "VARCHAR", comment = "数组值")
	private String array;

	@TableField(name = "STYLE", type = "VARCHAR", comment = "样式")
	private String style;

	@TableField(name = "排列顺序", type = "VARCHAR", comment = "NUMBER")
	private String sortIndex;

	public long getQueryReportFilterId() {
		return this.queryReportFilterId;
	}

	public void setQueryReportFilterId(long queryReportFilterId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.queryReportFilterId = queryReportFilterId;
	}

	public long getQueryReportId() {
		return this.queryReportId;
	}

	public void setQueryReportId(long queryReportId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.queryReportId = queryReportId;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.text = text;
	}

	public String getVarName() {
		return this.varName;
	}

	public void setVarName(String varName) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.varName = varName;
	}

	public String getFldn() {
		return this.fldn;
	}

	public void setFldn(String fldn) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fldn = fldn;
	}

	public String getFilterType() {
		return this.filterType;
	}

	public void setFilterType(String filterType) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.filterType = filterType;
	}

	public String getVarValue() {
		return this.varValue;
	}

	public void setVarValue(String varValue) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.varValue = varValue;
	}

	public String getOper() {
		return this.oper;
	}

	public void setOper(String oper) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.oper = oper;
	}

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sql = sql;
	}

	public String getSqlFldn() {
		return this.sqlFldn;
	}

	public void setSqlFldn(String sqlFldn) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sqlFldn = sqlFldn;
	}

	public String getArray() {
		return this.array;
	}

	public void setArray(String array) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.array = array;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.style = style;
	}

	public String getSortIndex() {
		return this.sortIndex;
	}

	public void setSortIndex(String sortIndex) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sortIndex = sortIndex;
	}

}