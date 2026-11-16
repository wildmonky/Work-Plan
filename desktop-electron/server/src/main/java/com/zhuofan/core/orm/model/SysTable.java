/** 
 * com.izhuofan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 自定义表:SYS_TABLE
 *           2015-12-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

import com.zhuofan.core.orm.BaseModel;

/**
 * SysTable 自定义表
 * 
 * @author fuxiaodong
 * @Date 2015-12-21
 *
 */
@Table(tableName = "SYS_TABLE", comment = "自定义表")
public class SysTable extends BaseModel {
	@TableField(name = "TABLE_ID", type = "VARCHAR", comment = "主键ID", isPrimaryKey = true)
	private String tableId;

	@TableField(name = "TABLE_NAME", type = "VARCHAR", comment = "表中文名")
	private String tableName;

	@TableField(name = "TABLE_CODE", type = "VARCHAR", comment = "数据库表名")
	private String tableCode;

	@TableField(name = "INPUT_TYPE", type = "VARCHAR", comment = "输入方式")
	private String inputType;

	@TableField(name = "TABLE_CLASS_ID", type = "VARCHAR", comment = "分类")
	private String tableClassId;

	@TableField(name = "REMARK", type = "VARCHAR", comment = "备注")
	private String remark;

	@TableField(name = "CREATE_TIME", type = "VARCHAR", comment = "创建时间")
	private String createTime;

	@TableField(name = "CREATE_USER", type = "VARCHAR", comment = "创建人")
	private String createUser;

	@TableField(name = "IS_SUBTABLE", type = "VARCHAR", comment = "是否为子表")
	private String isSubtable ="0";

	@TableField(name = "PARENT_ID", type = "VARCHAR", comment = "父表ID")
	private String parentId;

	@TableField(name = "TABLE_COLUMN_GROUP_ID", type = "VARCHAR", comment = "字段分组ID")
	private String tableColumnGroupId;
	
	public String getTableColumnGroupId() {
		return tableColumnGroupId;
	}

	public void setTableColumnGroupId(String tableColumnGroupId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableColumnGroupId = tableColumnGroupId;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setTableId(String tableId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableId = tableId;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableName = tableName;
	}

	public String getTableCode() {
		return this.tableCode;
	}

	public void setTableCode(String tableCode) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableCode = tableCode;
	}

	public String getInputType() {
		return this.inputType;
	}

	public void setInputType(String inputType) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.inputType = inputType;
	}

	public String getTableClassId() {
		return this.tableClassId;
	}

	public void setTableClassId(String tableClassId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableClassId = tableClassId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.remark = remark;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
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

	public String getIsSubtable() {
		return this.isSubtable;
	}

	public void setIsSubtable(String isSubtable) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.isSubtable = isSubtable;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.parentId = parentId;
	}

}
