package com.zhuofan.core.orm.dao;

import java.util.ArrayList;

import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.model.SysTableColumn;
import com.zhuofan.core.orm.model.SysTableColumnGroup;

/**
 * SysTableColumnDAO 自定义表字段设置DAO
 * 
 * @author fuxiaodong
 * @Date 2015-12-21
 *
 */
public class SysTableColumnDAO extends BaseOrmSupport<SysTableColumn> {

	/**
	 * 
	 * @方法 
	 * @描述  
	 * @param tableColumnGroupID 分组ID
	 * @return
	 */
	public ArrayList<SysTableColumn> queryByGroupID(String tableColumnGroupID)
	{
		this.setWhere("TABLE_COLUMN_GROUP_ID='"+String.valueOf(tableColumnGroupID)+"'");
		this.setOrder(" SORTINDEX ASC,TABLE_COLUMN_ID ASC ");
		
		try
		{
			return (ArrayList<SysTableColumn>)this.query(new SysTableColumn());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * 
	 * @方法 queryByTableID
	 * @描述 根据表ID获取字段列表获取
	 * @param tableID
	 * @return
	 */
	public ArrayList<SysTableColumn> queryByTableID(String tableID){
		this.setWhere("TABLE_ID='"+tableID+"'");
		this.setOrder(" SORTINDEX ASC ");
		
		try
		{
			return (ArrayList<SysTableColumn>)this.query(new SysTableColumn());
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}