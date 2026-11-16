/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_ROLE_PRIVILEGES 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */  
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysRolePrivileges
 * 用户角色表
 * @author   fuxiaodong 
 * @Date     2015-05-19 
 *
 */
@Table(tableName="SYS_ROLE_PRIVILEGES",comment="角色权限信息")
public class SysRolePrivileges extends BaseModel
{
	@TableField(name="PRIVILEGEID",isPrimaryKey=true)
	private String privilegeId;
	@TableField(name="ROLEID")
	private String roleId;
	@TableField(name="MODULEID")
	private String moduleId;	
	
	public String getRoleId()
	{
		return this.roleId;		
	}
	
	public void setRoleId(String roleId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.roleId=roleId;
	}	

	public String getPrivilegeId()
	{
		return this.privilegeId;		
	}
	
	public void setPrivilegeId(String privilegeId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.privilegeId=privilegeId;
	}
	
	public String getModuleId()
	{
		return this.moduleId;		
	}
	
	public void setModuleId(String moduleId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.moduleId=moduleId;
	}
}
