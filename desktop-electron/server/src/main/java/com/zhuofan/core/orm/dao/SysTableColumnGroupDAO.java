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

package com.zhuofan.core.orm.dao;

import java.util.ArrayList;

import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.model.SysTableColumnGroup;
import com.zhuofan.core.orm.model.SysUserRole;

/**
 * SysTableColumnGroupDAO 自定义表字段分组DAO
 * 
 * @author fuxiaodong
 * @Date 2015-12-21
 *
 */
public class SysTableColumnGroupDAO extends BaseOrmSupport<SysTableColumnGroup> {

	/**
	 * 
	 * @方法 
	 * @描述
	 * @param 
	 * @return  
	 * @param tableID 表ID
	 * @param groupCode 分组编码
	 * @return
	 */
	public SysTableColumnGroup queryByTableIDCode(String tableID, String groupCode) {
		this.setWhere("TABLE_ID='"+tableID+"' AND GROUP_CODE='"+groupCode+"'");
		try
		{
			return (SysTableColumnGroup)this.queryFirstRow(new SysTableColumnGroup());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * 
	 * @方法 queryByTableID
	 * @描述 用于返回某数据表的所有分组
	 * @param tableID 数据表ID
	 * @return
	 */
	public ArrayList<SysTableColumnGroup> queryByTableID(String tableID) {
		this.setWhere("TABLE_ID='"+tableID+"'");
		this.setOrder("SORTINDEX ASC");
		try
		{
			return (ArrayList<SysTableColumnGroup>)this.query(new SysTableColumnGroup());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}	
	}
}
