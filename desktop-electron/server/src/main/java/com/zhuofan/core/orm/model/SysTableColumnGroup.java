/** 
 * com.izhuofan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 自定义表字段分组:SYS_TABLE_COLUMN_GROUP
 *           2015-12-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysTableColumnGroup 自定义表字段分组
 * 
 * @author fuxiaodong
 * @Date 2015-12-21
 *
 */
@Table(tableName = "SYS_TABLE_COLUMN_GROUP", comment = "自定义表字段分组")
public class SysTableColumnGroup extends BaseModel {
	@TableField(name = "TABLE_COLUMN_GROUP_ID", type = "VARCHAR", comment = "主键ID", isPrimaryKey = true)
	private String tableColumnGroupId;

	@TableField(name = "TABLE_ID", type = "VARCHAR", comment = "表ID")
	private String tableId;

	@TableField(name = "GROUP_NAME", type = "VARCHAR", comment = "分组名称")
	private String groupName;

	@TableField(name = "SORTINDEX", type = "VARCHAR", comment = "排序号")
	private String sortindex;

	@TableField(name = "REMARK", type = "VARCHAR", comment = "备注")
	private String remark;
	
	@TableField(name = "GROUP_CODE", type = "VARCHAR", comment = "分组的代码")
	private String groupCode;
	
	
	public String getTableColumnGroupId() {
		return this.tableColumnGroupId;
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

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.groupName = groupName;
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

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.groupCode = groupCode;
	}
	
}
