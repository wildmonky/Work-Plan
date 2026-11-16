/** 
 * com.izhuofan.core.queryreport
 * 
 * 
 *   ver     date           author 
 * ====================================================== 
 *           2015-06-02      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */ 

package com.zhuofan.core.queryreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/** 
 * ClassName:QueryReportOutPut
 * 查询报表向外输出对象
 * 
 * @author   fuxiaodong 
 * @Date     2015-05-25 
 */ 
public class QueryReportOutPut 
{
	//类型
	private String dataname;
	//标题
	private String title;
	private String title_short;
	//总记录数
	private String recordcount;
	//当前页码
	private String pageno;
	//总页数
	private String pagecount;
	//每页显示数
	private String pagenumber;
	//开始记录数
	private String rowfrom;
	//结束记录数
	private String rowto;	
	//其它信息
	private String other;
	private String otherEnd;
	//报表代码
	private String reportcode;	
	
	//报表代码
	private String listTableHead;
	
	//查询条件输出
	private ArrayList<String> listFilter;
	
	//列标头输出
	private ArrayList<String> listCol;

	//列标头字符名输出 2025-05-19
	private ArrayList<String> listColFldn;
	//表头排序类型
	private int flagSort;
	//列标头输出
	private LinkedHashMap<String,String> mapField;

	//查询结果集
	private ArrayList<ArrayList<String>> listContent;
	
	//分页
	private String paging;
	
	//URI(action)
	private String uri;
	//备注
	private String remark;

	//页面数字开始
	private int pagefrom;

	//页面数字结束
	private int pageto;

	//操作项数
	private int actionCount;

	//流程表名
	private String flowTableCode;

	//是否有导出功能
	private int flagImport;

	private int flagCheckbox;

	private List<HashMap<String,String>> listResult;

	private String orderfldn;

	private String ordertype;

	public void setPagefrom(int pagefrom){
		this.pagefrom=pagefrom;
	}

	public int getPagefrom(){
		return this.pagefrom;
	}

	public void setPageto(int pageto){
		this.pageto=pageto;
	}

	public int getPageto(){
		return this.pageto;
	}

	//分页开始
	
	public void setTitle(String title)
	{
		this.title=title;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	//////////////////////////
	public void setReportcode(String reportcode)
	{
		this.reportcode=reportcode;
	}
	
	public String getReportcode()
	{
		return this.reportcode;
	}
	
	//////////////////////////
	public void setRecordcount(String recordcount)
	{
		this.recordcount=recordcount;
	}
	
	public String getRecordcount()
	{
		return this.recordcount;
	}
	
	//////////////////////////
	public void setPageno(String pageno)
	{
		this.pageno=pageno;
	}
	
	public String getPageno()
	{
		return this.pageno;
	}	
	
	//////////////////////////
	public void setPagecount(String pagecount)
	{
		this.pagecount=pagecount;
	}
	
	public String getPagecount()
	{
		return this.pagecount;
	}	
	
	//////////////////////////
	public void setPagenumber(String pagenumber)
	{
		this.pagenumber=pagenumber;
	}
	
	public String getPagenumber()
	{
		return this.pagenumber;
	}	
	
	//////////////////////////
	public void setRowfrom(String rowfrom)
	{
		this.rowfrom=rowfrom;
	}
	
	public String getRowfrom()
	{
		return this.rowfrom;
	}
	
	//////////////////////////
	public void setRowto(String rowto)
	{
		this.rowto=rowto;
	}
	
	public String getRowto()
	{
		return this.rowto;
	}
	
	//////////////////////////
	public void setOther(String other)
	{
		this.other=other;
	}
	
	public String getOther()
	{
		return this.other;
	}	
	
	//////////////////////////
	public void setListFilter(ArrayList listFilter)
	{
		this.listFilter=listFilter;
	}
	
	public ArrayList getListFilter()
	{
		return this.listFilter;
	}		
	
	//////////////////////////
	public void setListCol(ArrayList listCol)
	{
		this.listCol=listCol;
	}
	
	public ArrayList getListCol()
	{
		return this.listCol;
	}
	
	//////////////////////////
	public void setListContent(ArrayList listContent)
	{
		this.listContent=listContent;
	}
	
	public ArrayList getListContent()
	{
		return this.listContent;
	}	
	
	//////////////////////////
	public void setPaging(String paging)
	{
		this.paging=paging;
	}
	
	public String getPaging()
	{
		return this.paging;
	}
	
	//////////////////////////
	public void setUri(String uri)
	{
		this.uri=uri;
	}
	
	public String getUri()
	{
		return this.uri;
	}

	/**
	 * @return listTableHead
	 */
	public String getListTableHead() {
		return listTableHead;
	}

	/**
	 * @param listTableHead 要设置的 listTableHead
	 */
	public void setListTableHead(String listTableHead) {
		this.listTableHead = listTableHead;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDataname() {
		return dataname;
	}

	public void setDataname(String dataname) {
		this.dataname = dataname;
	}

	public List<HashMap<String, String>> getListResult() {
		return listResult;
	}

	public void setListResult(List<HashMap<String, String>> listResult) {
		this.listResult = listResult;
	}

	public LinkedHashMap<String, String> getMapField() {
		return mapField;
	}

	public void setMapField(LinkedHashMap<String, String> mapField) {
		this.mapField = mapField;
	}

	public int getActionCount() {
		return actionCount;
	}

	public void setActionCount(int actionCount) {
		this.actionCount = actionCount;
	}

	public String getTitle_short() {
		return title_short;
	}

	public void setTitle_short(String title_short) {
		this.title_short = title_short;
	}

	public String getFlowTableCode() {
		return flowTableCode;
	}

	public void setFlowTableCode(String flowTableCode) {
		this.flowTableCode = flowTableCode;
	}

	public int getFlagImport() {
		return flagImport;
	}

	public void setFlagImport(int flagImport) {
		this.flagImport = flagImport;
	}

	public int getFlagCheckbox() {
		return flagCheckbox;
	}

	public void setFlagCheckbox(int flagCheckbox) {
		this.flagCheckbox = flagCheckbox;
	}

	public ArrayList<String> getListColFldn() {
		return listColFldn;
	}

	public void setListColFldn(ArrayList<String> listColFldn) {
		this.listColFldn = listColFldn;
	}

	public int getFlagSort() {
		return flagSort;
	}

	public void setFlagSort(int flagSort) {
		this.flagSort = flagSort;
	}

	public String getOrderfldn() {
		return orderfldn;
	}

	public void setOrderfldn(String orderfldn) {
		this.orderfldn = orderfldn;
	}

	public String getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public String getOtherEnd() {
		return otherEnd;
	}

	public void setOtherEnd(String otherEnd) {
		this.otherEnd = otherEnd;
	}
}
