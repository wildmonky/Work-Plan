/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_USERS 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */

package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

/**
 * 文件
 * 
 * @author sunny
 * @Date 2017-07-20
 * 
 */
@Table(tableName = "SYS_FILE")
public class SysFile extends BaseModel {
	@TableField(name = "FILE_ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
	private String fileId;

	@TableField(name = "GX_ID", type = TableField.TF_VARCHAR, visible = true)
	private String gxId;
	@TableField(name = "AUTHOR", type = TableField.TF_VARCHAR, visible = true)
	private String author;

	@TableField(name = "ONTIME", type = TableField.TF_VARCHAR, visible = true)
	private String ontime;
	@TableField(name = "FPATH", type = TableField.TF_VARCHAR, visible = true)
	private String fpath;
	@TableField(name = "NAME", type = TableField.TF_VARCHAR, visible = true)
	private String name;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fileId = fileId;
	}

	/**
	 * @return the gxId
	 */
	public String getGxId() {
		return gxId;
	}

	/**
	 * @param gxId the gxId to set
	 */
	public void setGxId(String gxId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.gxId = gxId;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.author = author;
	}

	/**
	 * @return the ontime
	 */
	public String getOntime() {
		return ontime;
	}

	/**
	 * @param ontime the ontime to set
	 */
	public void setOntime(String ontime) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ontime = ontime;
	}

	/**
	 * @return the fpath
	 */
	public String getFpath() {
		return fpath;
	}

	/**
	 * @param fpath the fpath to set
	 */
	public void setFpath(String fpath) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fpath = fpath;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.name = name;
	}

}
