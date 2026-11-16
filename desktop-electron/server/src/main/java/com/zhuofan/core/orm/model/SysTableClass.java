
/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 自定义分类:SYS_TABLE_CLASS
 *           2015-12-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysTableClass 自定义分类
 * 
 * @author fuxiaodong
 * @Date 2015-12-21
 *
 */
@Table(tableName = "SYS_TABLE_CLASS", comment = "自定义分类")
public class SysTableClass extends BaseModel {
	@TableField(name = "TABLE_CLASS_ID", type = "VARCHAR", comment = "主键ID", isPrimaryKey = true)
	private String tableClassId;

	@TableField(name = "CLASS_NAME", type = "VARCHAR", comment = "自定义分类名称")
	private String className;

	@TableField(name = "REMARK", type = "VARCHAR", comment = "备注")
	private String remark;
	
	public String getTableClassId() {
		return this.tableClassId;
	}

	public void setTableClassId(String tableClassId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tableClassId = tableClassId;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.className = className;
		
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.remark = remark;
	}

}
