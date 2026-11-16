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
@Table(tableName = "SYS_FUTURE")
public class SysFuture extends BaseModel {
	@TableField(name = "FID", isPrimaryKey = true)
	private String fid;
	// 事件主题
	@TableField(name = "FTITLE", type = TableField.TF_VARCHAR)
	public String ftitle;

	// 具体描述
	@TableField(name = "FCONTENT", type = TableField.TF_VARCHAR)
	public String fcontent;

	// 开始时间
	@TableField(name = "FTSTART", type = TableField.TF_VARCHAR)
	public String ftstart;

	// 结束时间
	@TableField(name = "FTEND", type = TableField.TF_VARCHAR)
	public String ftend;

	// 事件类型（文档、会议、出差）
	@TableField(name = "FTYPE", type = TableField.TF_VARCHAR)
	public String ftype;

	// 重要级别（0重要）
	@TableField(name = "FIMPORTTENT", type = TableField.TF_VARCHAR)
	public String fimporttent;

	// 创建人
	@TableField(name = "FCREATOR", type = TableField.TF_VARCHAR)
	public String fcreator;

	// 系统时间
	@TableField(name = "FSYSTIME", type = TableField.TF_VARCHAR)
	public String fsystime;

	// 是否完成（0否），100是，22取消
	@TableField(name = "FISOK", type = TableField.TF_VARCHAR)
	public String fisok;

	// 附件
	@TableField(name = "FFJ", type = TableField.TF_VARCHAR)
	public String ffj;

	// 任务来源1个人（需要审核），2部门
	@TableField(name = "FFROM", type = TableField.TF_VARCHAR)
	public String ffrom;
	
	// 负责人名称
	@TableField(name = "FFZRNAME", type = TableField.TF_VARCHAR)
	public String ffzrname;
		
	// 负责人ID
	@TableField(name = "FFZRID", type = TableField.TF_VARCHAR)
	public String ffzrid;
		
	// 实际完成时间
	@TableField(name = "FWCSJ", type = TableField.TF_VARCHAR)
	public String fwcsj;

	// 完成情况
	@TableField(name = "FWCQK", type = TableField.TF_VARCHAR)
	public String fwcqk;

	// 审核人员
	@TableField(name = "FSHR", type = TableField.TF_VARCHAR)
	public String fshr;

	// 审核时间
	@TableField(name = "FSHSJ", type = TableField.TF_VARCHAR)
	public String fshsj;

	// 审核人员发表意见
	@TableField(name = "FSHBZ", type = TableField.TF_VARCHAR)
	public String fshbz;

	// 是否归档
	@TableField(name = "FGD", type = TableField.TF_VARCHAR)
	public String fgd;

	// 归档时间
	@TableField(name = "FGDTIME", type = TableField.TF_VARCHAR)
	public String fgdtime;

	// 是否审核通过（0不通过，1通过）
	@TableField(name = "FSHTG", type = TableField.TF_NUMBER)
	public Number fshtg;

	// 是否指派（0是，1否）
	@TableField(name = "FISZP", type = TableField.TF_NUMBER)
	public Number fiszp;
	
	public String getFid() {
		return this.fid;
	}

	public void setFid(String fid) {
		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fid = fid;
	}

	public String getFtitle() {
		return this.ftitle;

	}

	public void setFtitle(String ftitle) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ftitle = ftitle;

	}

	public String getFcontent() {
		return this.fcontent;

	}

	public void setFcontent(String fcontent) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fcontent = fcontent;

	}

	public String getFtstart() {
		return this.ftstart;

	}

	public void setFtstart(String ftstart) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ftstart = ftstart;

	}

	public String getFtend() {
		return this.ftend;

	}

	public void setFtend(String ftend) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ftend = ftend;

	}

	public String getFtype() {
		return this.ftype;

	}

	public void setFtype(String ftype) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ftype = ftype;

	}

	public String getFfj() {
		return this.ffj;

	}

	public void setFfj(String ffj) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ffj = ffj;

	}

	public String getFfrom() {
		return this.ffrom;

	}

	public void setFfrom(String ffrom) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ffrom = ffrom;

	}

	public String getFimporttent() {
		return this.fimporttent;

	}

	public void setFimporttent(String fimporttent) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fimporttent = fimporttent;

	}

	public String getFcreator() {
		return this.fcreator;

	}

	public void setFcreator(String fcreator) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fcreator = fcreator;

	}

	public String getFsystime() {
		return this.fsystime;

	}

	public void setFsystime(String fsystime) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fsystime = fsystime;

	}

	public String getFisok() {
		return this.fisok;

	}

	public void setFisok(String fisok) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fisok = fisok;

	}

	public String getFwcsj() {
		return this.fwcsj;

	}

	public void setFwcsj(String fwcsj) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fwcsj = fwcsj;

	}

	public String getFwcqk() {
		return this.fwcqk;

	}

	public void setFwcqk(String fwcqk) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fwcqk = fwcqk;

	}

	public String getFfzrname() {
		return this.ffzrname;

	}

	public void setFfzrname(String ffzrname) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ffzrname = ffzrname;

	}

	public String getFfzrid() {
		return this.ffzrid;

	}

	public void setFfzrid(String ffzrid) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.ffzrid = ffzrid;

	}

	public String getFshr() {
		return this.fshr;

	}

	public void setFshr(String fshr) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fshr = fshr;

	}

	public String getFshsj() {
		return this.fshsj;

	}

	public void setFshsj(String fshsj) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fshsj = fshsj;

	}

	public String getFshbz() {
		return this.fshbz;

	}

	public void setFshbz(String fshbz) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fshbz = fshbz;

	}

	public String getFgd() {
		return this.fgd;

	}

	public void setFgd(String fgd) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fgd = fgd;

	}

	public String getFgdtime() {
		return this.fgdtime;

	}

	public void setFgdtime(String fgdtime) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fgdtime = fgdtime;

	}

	public Number getFshtg() {
		return this.fshtg;

	}

	public void setFshtg(Number fshtg) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fshtg = fshtg;

	}

	public Number getFiszp() {
		return this.fiszp;

	}

	public void setFiszp(Number fiszp) {

		addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
		this.fiszp = fiszp;

	}
}
