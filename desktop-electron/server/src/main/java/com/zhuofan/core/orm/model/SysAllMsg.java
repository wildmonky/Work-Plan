/**
 * 
 */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * @author Administrator
 *
 */
@Table(
	    tableName = "SYS_ALLMSG")
public class SysAllMsg extends BaseModel {

    @TableField(
            name = "ID",
            type = TableField.TF_VARCHAR,
            isPrimaryKey = true)
	private String id;
    
    @TableField(
            name = "MSG",
            type = TableField.TF_VARCHAR)
	private String msg;
    
    @TableField(
            name = "systime",
            type = TableField.TF_UNIXTIME)
	private long systime;
    
    @TableField(
            name = "TZRID",
            type = TableField.TF_VARCHAR)
	private String tzrid;
	
	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id 要设置的 id
	 */
	public void setId(String id) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.id = id;
	}
	/**
	 * @return msg
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg 要设置的 msg
	 */
	public void setMsg(String msg) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.msg = msg;
	}
	/**
	 * @return tzrid
	 */
	public String getTzrid() {
		return tzrid;
	}
	/**
	 * @param tzrid 要设置的 tzrid
	 */
	public void setTzrid(String tzrid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.tzrid = tzrid;
	}
	/**
	 * @return systime
	 */
	public long getSystime() {
		return systime;
	}
	/**
	 * @param systime 要设置的 systime
	 */
	public void setSystime(long systime) {
		this.systime = systime;
	}
	
}
