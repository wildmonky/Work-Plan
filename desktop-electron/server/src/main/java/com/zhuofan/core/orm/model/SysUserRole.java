/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_USER_ROLE 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */  
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * 用户角色表
 * @author   fuxiaodong 
 * @Date     2015-05-19 
 *
 */
@Table(tableName="SYS_USER_ROLE",comment="用户角色表")
public class SysUserRole extends BaseModel
{
	@TableField(name="USER_ROLE_ID",isPrimaryKey=true)
	private String userRoleId;
	
	@TableField(name="USERID")
	private String userId;
	
	@TableField(name="ROLEID")
	private String roleId;
	
	public String getUserRoleId()
	{
		return this.userRoleId;		
	}
	
	public void setUserRoleId(String userRoleId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.userRoleId=userRoleId;
	}
	
	public String getUserId()
	{
		return this.userId;		
	}
	
	public void setUserId(String userId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.userId=userId;
	}
	
	public String getRoleId()
	{
		return this.roleId;		
	}
	
	public void setRoleId(String roleId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.roleId=roleId;
	}	
	
}
