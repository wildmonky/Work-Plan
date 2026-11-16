/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author  
 *           2015-05-25      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */ 

package com.zhuofan.core.orm.model;

/**
 * ClassName:SysUserDept
 * 
 * @author   fuxiaodong 
 * @Date     2015-05-25 
 */
public class SysUserDept 
{
	private SysUsers sysUser;
	private SysDepartments sysDept;
	
	public SysUsers getSysUser()
	{
		return sysUser;
	}
	
	public void setSysUser(SysUsers sysUser)
	{
		this.sysUser=sysUser;
	}	
	
	public SysDepartments getSysDept()
	{
		return sysDept;
	}
	
	public void setSysDept(SysDepartments sysDept)
	{
		this.sysDept=sysDept;
	}
	
}
