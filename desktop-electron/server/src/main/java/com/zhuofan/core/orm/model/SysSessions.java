/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_SESSIONS 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */  
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysSessions
 * 登录在线表
 * @author   fuxiaodong 
 * @Date     2015-05-19 
 *
 */
@Table(tableName="SYS_SESSIONS",comment="登录在线表")
public class SysSessions extends BaseModel
{
	@TableField(name="SESSIONID",isPrimaryKey=true)
	private String sessionId;
	@TableField(name="USERID")
	private String userId;	
	@TableField(name="LOGINTIME",type="NUMBER")
	private long loginTime;
	@TableField(name="LOGINIP")
	private String loginIp;		
	@TableField(name="LASTTIME",type="NUMBER")
	private long lastTime;	
	@TableField(name="LASTURL")
	private String lastUrl;	
	@TableField(name="USERNAME")
	private String userName;	
	
	public String getSessionId()
	{
		return this.sessionId;		
	}
	
	public void setSessionId(String sessionId)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.sessionId=sessionId;
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

	public String getLoginIp()
	{
		return this.loginIp;		
	}
	
	public void setLoginIp(String loginIp)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.loginIp=loginIp;
	}	

	public long getLoginTime()
	{
		return this.loginTime;		
	}
	
	public void setLoginTime(long loginTime)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.loginTime=loginTime;
	}
	
	public long getLastTime()
	{
		return this.lastTime;		
	}
	
	public void setLastTime(long lastTime)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.lastTime=lastTime;
	}
	
	public String getLastUrl()
	{
		return this.lastUrl;		
	}
	
	public void setLastUrl(String lastUrl)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.lastUrl=lastUrl;
	}		
	
	public String getUserName()
	{
		return this.userName;		
	}
	
	public void setUserName(String userName)
	{
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());		
		this.userName=userName;
	}		
	
}
