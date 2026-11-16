/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 查询报表操作表:SYS_QUERYREPORT_ACTION
 *           2015-05-21      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */
package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.BaseModel;

/**
 * SysQueryReportAction 查询报表操作表
 * 
 * @author fuxiaodong
 * @Date 2015-05-21
 * 
 */
@Table(tableName = "SYS_QUERYREPORT_ACTION", comment = "查询报表操作表")
public class SysQueryReportAction extends BaseModel {
	@TableField(name = "QUERY_REPORT_ACTION_ID", type = "NUMBER", comment = "主键ID", isPrimaryKey = true)
	private long queryReportActionId;

	@TableField(name = "QUERY_REPORT_ID", type = "NUMBER", comment = "查询报表ID")
	private long queryReportId;

	@TableField(name = "IMAGE", type = "VARCHAR", comment = "图片地址")
	private String image;

	@TableField(name = "URL", type = "VARCHAR", comment = "操作链接地址")
	private String url;

	@TableField(name = "TITLE", type = "VARCHAR", comment = "提示文本")
	private String title;

	@TableField(name = "TYPE", type = "VARCHAR", comment = "显示类别")
	private String type;

	@TableField(name = "TEXT", type = "VARCHAR", comment = "显示文本")
	private String text;

	@TableField(name = "SORTINDEX", type = "VARCHAR", comment = "排列顺序")
	private String sortIndex;

	@TableField(name = "SHOWFILTER", type = "VARCHAR", comment = "显示条件")
	private String showFilter;

	public long getQueryReportActionId() {
		return this.queryReportActionId;
	}

	public void setQueryReportActionId(long queryReportActionId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.queryReportActionId = queryReportActionId;
	}

	public long getQueryReportId() {
		return this.queryReportId;
	}

	public void setQueryReportId(long queryReportId) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.queryReportId = queryReportId;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.image = image;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.url = url;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.title = title;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.type = type;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.text = text;
	}

	public String getSortIndex() {
		return this.sortIndex;
	}

	public void setSortIndex(String sortIndex) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.sortIndex = sortIndex;
	}

	public String getShowFilter() {
		return this.showFilter;
	}

	public void setShowFilter(String showFilter) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.showFilter = showFilter;
	}

}