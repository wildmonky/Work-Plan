/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 查询报表显示列表:SYS_QUERYREPORT_COL
 *           2015-05-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysQueryReportCol 查询报表显示列表
 * 
 * @author fuxiaodong
 * @Date 2015-05-21
 * 
 */
@Table(tableName = "SYS_QUERYREPORT_COL", comment = "查询报表显示列表")
public class SysQueryReportCol extends BaseModel {
	@TableField(name = "QUERY_REPORT_COL_ID", type = "NUMBER", comment = "主键ID", isPrimaryKey = true)
	private long queryReportColId;

	@TableField(name = "QUERY_REPORT_ID", type = "NUMBER", comment = "查询报表ID")
	private long queryReportId;

	@TableField(name = "TEXT", type = "VARCHAR", comment = "文本")
	private String text;

	@TableField(name = "FLDN", type = "VARCHAR", comment = "字段名")
	private String fldn;

	@TableField(name = "ALIGN", type = "VARCHAR", comment = "提示文本")
	private String align;

	@TableField(name = "TYPE", type = "VARCHAR", comment = "对齐方式")
	private String type;

	@TableField(name = "FUNC", type = "VARCHAR", comment = "eval代码")
	private String func;

	@TableField(name = "CLASSNAME", type = "VARCHAR", comment = "样式名")
	private String className;

	@TableField(name = "REPLACE", type = "VARCHAR", comment = "是否替换")
	private String replace;

	@TableField(name = "FUNC_QUERY", type = "VARCHAR", comment = "evl替换代码")
	private String funcQuery;

	@TableField(name = "HASSORT", type = "VARCHAR", comment = "是否排序")
	private String hasSort;

	@TableField(name = "VISIBLE", type = "VARCHAR", comment = "是否可见")
	private String visible;

	@TableField(name = "SORTINDEX", type = "VARCHAR", comment = "排列顺序")
	private String sortIndex;

	public long getQueryReportColId() {
		return this.queryReportColId;
	}

	public void setQueryReportColId(long queryReportColId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.queryReportColId = queryReportColId;
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

	public String getFldn() {
		return this.fldn;
	}

	public void setFldn(String fldn) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fldn = fldn;
	}

	public String getAlign() {
		return this.align;
	}

	public void setAlign(String align) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.align = align;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.type = type;
	}

	public String getFunc() {
		return this.func;
	}

	public void setFunc(String func) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.func = func;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.className = className;
	}

	public String getReplace() {
		return this.replace;
	}

	public void setReplace(String replace) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.replace = replace;
	}

	public String getFuncQuery() {
		return this.funcQuery;
	}

	public void setFuncQuery(String funcQuery) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.funcQuery = funcQuery;
	}

	public String getHasSort() {
		return this.hasSort;
	}

	public void setHasSort(String hasSort) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.hasSort = hasSort;
	}

	public String getVisible() {
		return this.visible;
	}

	public void setVisible(String visible) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.visible = visible;
	}

	public String getSortIndex() {
		return this.sortIndex;
	}

	public void setSortIndex(String sortIndex) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sortIndex = sortIndex;
	}

}