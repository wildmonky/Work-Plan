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

package com.zhuofan.core.orm.dao;

import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.model.SysUserRole;
import com.zhuofan.core.orm.model.SysUsers;

/**
 * SysUserRoleDAO
 * 用户角色表DAO
 * @author   fuxiaodong 
 * @Date     2015-05-20
 *
 */
public class SysUserRoleDAO extends BaseOrmSupport<SysUserRole>{
    /**
     * 根据用户名获取单条记录
     * 
     */ 
	public SysUserRole getByUserId(String userId) 
	{
		this.setWhere("USERID='"+userId+"'");
		try
		{
			return (SysUserRole)this.queryFirstRow(new SysUserRole());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
