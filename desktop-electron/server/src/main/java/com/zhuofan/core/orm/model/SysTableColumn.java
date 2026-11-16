/** 
 * com.izhuofan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 自定义表字段设置:SYS_TABLE_COLUMN
 *           2015-12-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysTableColumn 自定义表字段设置
 * 
 * @author fuxiaodong
 * @Date 2015-12-21
 *
 */
@Table(tableName = "SYS_TABLE_COLUMN", comment = "自定义表字段设置")
public class SysTableColumn extends BaseModel {
	@TableField(name = "TABLE_COLUMN_ID", type = "VARCHAR", comment = "主键ID", isPrimaryKey = true)
	private String tableColumnId;

	@TableField(name = "TABLE_ID", type = "VARCHAR", comment = "表ID")
	private String tableId;

	@TableField(name = "COLUMN_NAME", type = "VARCHAR", comment = "字段中文名")
	private String columnName;

	@TableField(name = "COLUMN_CODE", type = "VARCHAR", comment = "字段数据库名称")
	private String columnCode;

	@TableField(name = "COLUMN_TYPE", type = "VARCHAR", comment = "字段类型")
	private String columnType;//可选(字符串、数字、文件、日期
	
	@TableField(name = "COLUMN_LENGTH", type = "VARCHAR", comment = "字段长度")
	private String columnLength;

	@TableField(name = "NULLABLE", type = "VARCHAR", comment = "是否允许为空")
	private String nullable;

	@TableField(name = "DEFAULTVALUE", type = "VARCHAR", comment = "默认值")
	private String defaultvalue;

	@TableField(name = "ISPK", type = "VARCHAR", comment = "是否主键")
	private String ispk;

	@TableField(name = "ISHIDE", type = "VARCHAR", comment = "是否隐藏")
	private String ishide;

	@TableField(name = "INPUT_TYPE", type = "VARCHAR", comment = "输入方式")
	private String inputType;//单行、多行、日期录入、通过数组（关键字）、从数据库录取、文件、多选

	@TableField(name = "ARRAYLIST", type = "VARCHAR", comment = "数组值")
	private String arraylist;

	@TableField(name = "MAPLIST", type = "VARCHAR", comment = "HashMap值")
	private String maplist;

	@TableField(name = "SQLTEXT", type = "VARCHAR", comment = "sql语句")
	private String sqltext;

	@TableField(name = "TABLE_COLUMN_GROUP_ID", type = "VARCHAR", comment = "分组ID")
	private String tableColumnGroupId;

	@TableField(name = "SORTINDEX", type = "VARCHAR", comment = "排序号")
	private String sortindex;

	@TableField(name = "REMARK", type = "VARCHAR", comment = "备注")
	private String remark;
	
	@TableField(name = "CREATE_TIME", type = "VARCHAR", comment = "创建时间")
	private String createTime;

	@TableField(name = "CREATE_USER", type = "VARCHAR", comment = "创建人")
	private String createUser;
	
	@TableField(name = "COLSPAN", type = "VARCHAR", comment = "单元格合并数")
	private String colspan ="1";
	
	@TableField(name = "COLUMN_DOTLENGTH", type = "VARCHAR", comment = "小数位")
	private String columnDotlength="0";

	@TableField(name = "FLAG_SHOW", type = "VARCHAR", comment = "展示标记")
	private String flagShow;

	@TableField(name = "FLAG_FILTER", type = "VARCHAR", comment = "查询标记")
	private String flagFilter;


	@TableField(name = "FILTER_SORTINDEX", type = "VARCHAR", comment = "查询标记")
	private String filterSortindex;

	@TableField(name = "SHOW_SORTINDEX", type = "VARCHAR", comment = "查询标记")
	private String showSortindex;

	@TableField(name = "FLAG_NODE_EXEC", type = "VARCHAR", comment = "查询标记")
	private String flagNodeExec;

	public String getColumnDotlength() {
		return columnDotlength;
	}

	public void setColumnDotlength(String columnDotlength) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.columnDotlength = columnDotlength;
	}

	public String getColspan() {
		return colspan;
	}

	public void setColspan(String colspan) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.colspan = colspan;
	}

	public String getTableColumnId() {
		return this.tableColumnId;
	}

	public void setTableColumnId(String tableColumnId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableColumnId = tableColumnId;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setTableId(String tableId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableId = tableId;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public void setColumnName(String columnName) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.columnName = columnName;
	}

	public String getColumnCode() {
		return this.columnCode;
	}

	public void setColumnCode(String columnCode) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.columnCode = columnCode;
	}

	public String getColumnType() {
		return this.columnType;
	}

	public void setColumnType(String columnType) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.columnType = columnType;
	}

	public String getColumnLength() {
		return this.columnLength;
	}

	public void setColumnLength(String columnLength) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.columnLength = columnLength;
	}

	public String getNullable() {
		return this.nullable;
	}

	public void setNullable(String nullable) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.nullable = nullable;
	}

	public String getDefaultvalue() {
		return this.defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.defaultvalue = defaultvalue;
	}

	public String getIspk() {
		return this.ispk;
	}

	public void setIspk(String ispk) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ispk = ispk;
	}

	public String getIshide() {
		return this.ishide;
	}

	public void setIshide(String ishide) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ishide = ishide;
	}

	public String getInputType() {
		return this.inputType;
	}

	public void setInputType(String inputType) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.inputType = inputType;
	}

	public String getArraylist() {
		return this.arraylist;
	}

	public void setArraylist(String arraylist) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.arraylist = arraylist;
	}

	public String getMaplist() {
		return this.maplist;
	}

	public void setMaplist(String maplist) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.maplist = maplist;
	}

	public String getSqltext() {
		return this.sqltext;
	}

	public void setSqltext(String sqltext) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sqltext = sqltext;
	}

	public String getTableColumnGroupId() {
		return this.tableColumnGroupId;
	}

	public void setTableColumnGroupId(String tableColumnGroupId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableColumnGroupId = tableColumnGroupId;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.createUser = createUser;
	}

	public String getFlagShow() {
		return flagShow;
	}

	public void setFlagShow(String flagShow) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.flagShow = flagShow;
	}

	public String getFlagFilter() {
		return flagFilter;
	}

	public void setFlagFilter(String flagFilter) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.flagFilter = flagFilter;
	}

	public String getFilterSortindex() {
		return filterSortindex;
	}

	public void setFilterSortindex(String filterSortindex) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.filterSortindex = filterSortindex;
	}

	public String getShowSortindex() {
		return showSortindex;
	}

	public void setShowSortindex(String showSortindex) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.showSortindex = showSortindex;
	}

	public String getFlagNodeExec() {
		return flagNodeExec;
	}

	public void setFlagNodeExec(String flagNodeExec) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.flagNodeExec = flagNodeExec;
	}
}
