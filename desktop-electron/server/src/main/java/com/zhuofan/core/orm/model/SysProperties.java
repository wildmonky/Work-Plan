/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_PROPERTIES 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */  
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * SysProperties
 * 自定义属性信息
 * @author   fuxiaodong 
 * @Date     2015-05-19 
 *
 */
@Table(tableName="SYS_PROPERTIES",comment="自定义属性信息")
public class SysProperties extends BaseModel
{
	@TableField(name="PROPERTYID",isPrimaryKey=true)
	private String propertyId;
	
	@TableField(name="PROPERTY_NAME", type = TableField.TF_VARCHAR)
	private String propertyName;
	
	@TableField(name="REMARK", type = TableField.TF_VARCHAR)
	private String remark;	
	
	@TableField(name="PROPERTY_TYPE", type = TableField.TF_VARCHAR)
	private String propertyType;
	
	@TableField(name="PROPERTY_VALUE", type = TableField.TF_VARCHAR)
	private String propertyValue;	
	
	public String getPropertyId()
	{
		return this.propertyId;		
	}
	
	public void setPropertyId(String propertyId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.propertyId=propertyId;
	}
	
	public String getPropertyName()
	{
		return this.propertyName;		
	}
	
	public void setPropertyName(String propertyName)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.propertyName=propertyName;
	}	

	public String getRemark()
	{
		return this.remark;		
	}
	
	public void getRemark(String remark)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.remark=remark;
	}	

	public String getPropertyType()
	{
		return this.propertyType;		
	}
	
	public void setPropertyType(String propertyType)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.propertyType=propertyType;
	}
	
	public String getPropertyValue()
	{
		return this.propertyValue;		
	}
	
	public void setPropertyValue(String propertyValue)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.propertyValue=propertyValue;
	}	
	
}
