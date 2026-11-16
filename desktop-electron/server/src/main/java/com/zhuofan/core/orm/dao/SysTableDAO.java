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

package com.zhuofan.core.orm.dao;

import java.util.ArrayList;

import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.model.SysTable;
import com.zhuofan.core.orm.model.SysTableColumn;

/**
 * SysTableDAO 自定义表DAO
 * 
 * @author fuxiaodong
 * @Date 2015-12-21
 *
 */
public class SysTableDAO extends BaseOrmSupport<SysTable> {

	/**
	 * 
	 * @方法 queryByTableCode
	 * @描述 根据表CODE，返回表信息
	 * @param tableCode 表CODE
	 * @return
	 */
	public SysTable queryByTableCode(String tableCode){
		this.setWhere("TABLE_CODE='"+tableCode+"'");
		try
		{
			return (SysTable)this.queryFirstRow(new SysTable());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * 
	 * @方法 querySubTable
	 * @描述 根据主表ID，返回该主表的子表列表
	 * @param parentID 主表ID
	 * @return
	 */
	public ArrayList<SysTable> querySubTable(String parentID){
		this.setWhere("IS_SUBTABLE=1 AND PARENT_ID='"+parentID+"'");
		try
		{
			return (ArrayList<SysTable>)this.query(new SysTable());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * @方法 querySubTableByGroup
	 * @描述 根据主表ID，分组ID，返回子表列表
	 * @param parentID
	 * @param groupID
	 * @return
	 */
	public ArrayList<SysTable> querySubTableByGroup(String parentID,String groupID){
		this.setWhere("IS_SUBTABLE=1 AND PARENT_ID='"+parentID+"' AND TABLE_COLUMN_GROUP_ID='"+groupID+"'");
		
		try
		{
			return (ArrayList<SysTable>)this.query(new SysTable());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}		
	}
}