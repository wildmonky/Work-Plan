/** 
 * com.izhuofan.core.queryreport
 * 
 * 
 *   ver     date           author 
 * ====================================================== 
 *           2015-05-26      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */  

package com.zhuofan.core.queryreport;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * ClassName:QueryReport
 * 查询报表主类
 * 
 * @author   fuxiaodong 
 * @Date     2015-05-25 
 */ 
public class QueryReport 
{
	private static final  String WHERE_AND=" AND ";
	private static final  String WHERE_OR=" OR ";

	private List<HashMap<String,String>> listResult;
	//页面标题
	private String dataname="";
	private String title="";
	private String title_short="";
	private String remark="";
	//查询的字段
	private String selectfldn="";   
	
	//报表代码
	private String listTableHead;
	
	
	//查询的表
	private String table="" ;       
	//排序字段
	private String orderfldn="";    
	//排序方式
	private String ordertype="";    
	//查询条件对象数组
	private ArrayList<QueryReportFilter> queryset;     
	//查询条件总数
	private int querycount;   
	//列对象数组
	private ArrayList<QueryReportCol> cols;              
	//所有的列数
	private int colcount;     
	//所有的操作对象数组
	private ArrayList<QueryReportAction> actions;      
	//操作对象总数
	private int actioncount;  
	//当前页面
	private int pageno=1;       
	//每页所显示的数量
	private int pagenumber=30;   
	//ID字段名
	private String fldnPK="";      
	//其它
	private String other="";
	//其它（后面）
	private String otherEnd="";

	//是否为打印版本
	private String querytype="1";     
	private String returnfldn;
	private String returnvalue;
	//样式数组
	private String styles="";		
	//链接地址,仅包含条件
	private String link="";			
	//扩展的查询条件
	private String where_ex="";				
	//统计的SQL语句
	private String sql_total="";		
	private String sql_total_fldn="";
	//分组
	private String groupby="";
	//删除标记
	private boolean delete_flag=false;
	private String listtype="";
	private String rowclick_script="";
	//按钮后面的扩展功能
	private String other_button="";
	//查询列表内容(输出)
	private ArrayList<ArrayList<String>> listContent;
	//查询条件（输出）
	private ArrayList<String> listFilter;
	//列标头（输出）
	private ArrayList<String> listCol;

	//列标头字符（输出）
	private ArrayList<String> listColFldn;
	//排序类型
	private int flagSort;
	//总记录数
	private int recordcount;
	private int rowfrom;
	private int rowto;
	private int pagecount;

	private HttpServletRequest request;
	private SysConfig sysConfig;
	private DatasourceConfig datasourceConfig;

	private String actionTitleText="操作";

	//是否配置导入功能按钮
	private int flagImport;

	//是否配置全选序号
	private int flagCheckbox;

	public HttpServletRequest getRequest(){
		return this.request;
	}

	public void setRequest(HttpServletRequest request){
		this.request=request;
	}

	public SysConfig getSysConfig(){
		return this.sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig){
		this.sysConfig=sysConfig;
	}

	public DatasourceConfig getDatasourceConfig() {
		return this.datasourceConfig;
	}

	public void setDatasourceConfig(DatasourceConfig datasourceConfig){
		this.datasourceConfig=datasourceConfig;
	}



	public QueryReport(HttpServletRequest request,SysConfig sysConfig,DatasourceConfig datasourceConfig)
	{
		this.request=request;
		this.sysConfig=sysConfig;
		this.datasourceConfig=datasourceConfig;
		this.flagImport=0;
		this.flagCheckbox=1;
	}

	/**
	* Title: QueryReport.java
	* Date:2015年5月27日
	* Description:构造函数  
	* @author 广州卓凡扶小冬
	* @param title
	 */
	public QueryReport(String title,HttpServletRequest request,SysConfig sysConfig,DatasourceConfig datasourceConfig)
	{
		this.title=title;
		this.title_short=title;
		this.request=request;
		this.sysConfig=sysConfig;
		this.datasourceConfig=datasourceConfig;
		this.dataname=CommonFunc.getHttpVar("dataname");

	}
	
	public void setListtype(String listtype)
	{
		this.listtype=listtype;
	}
	
	public String getListtype()
	{
		return this.listtype;
	}
	
	public void setRowclick_script(String rowclick_script)
	{
		this.rowclick_script=rowclick_script;
	}
	
	public ArrayList getQueryContent()
	{
		return this.listContent;
	}
	
	public String getRowclick_script()
	{
		return this.rowclick_script;
	}
	
	//设置delete_flag属性
	public void setDelete_flag(boolean delete_flag)
	{
		this.delete_flag=delete_flag;
	}
	
	//获取delete_flag属性
	public boolean getDelete_flag()
	{
		return this.delete_flag;
	}
	
	//设置sql_total属性'
	public void setSql_total(String sql_total)
	{
		this.sql_total=sql_total;
	}

	public void setSql_total_fldn(String sql_total_fldn)
	{
		this.sql_total_fldn=sql_total_fldn;
	}

	public String getDataname()
	{
		return this.dataname;
	}

	public void setDataname(String dataname)
	{
		this.dataname=dataname;
	}

	public String getSql_total()
	{
		return this.sql_total;
	}

	public String getSql_total_fldn()
	{
		return this.sql_total_fldn;
	}	
	
	//获取groupby属性
	public String getGroupby()
	{
		return this.groupby;
	}
	
	//设置groupby属性'
	public void setGroupby(String groupby)
	{
		this.groupby=groupby;
	}
	
	//获取styles属性
	public String getStyles()
	{
		return this.styles;
	}
	
	//设置styles属性'
	public void setStyles(String styles)
	{
		this.styles=styles;
	}
	
	//获取title属性
	public String getTitle()
	{
		return this.title;
	}
	
	//设置title属性
	public void setTitle(String title)
	{
		this.title=title;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	//获取selectfldn属性
	public String getSelectfldn()
	{
		return this.selectfldn;
	}
	
	//设置selectfldn属性
	public void setSelectfldn(String selectfldn)
	{
		this.selectfldn=selectfldn;
	}
	
	//获取table属性
	public String geTtable()
	{
		return this.table;
	}
	
	//设置table属性
	public void setTable(String table)
	{
		this.table=table;
	}
	
	//获取orderfldn属性
	public String getOrderfldn()
	{
		return this.orderfldn;
	}
	
	//设置orderfldn属性
	public void setOrderfldn(String orderfldn)
	{
		this.orderfldn=orderfldn;
	}
	
	//获取ordertype属性
	public String getOrdertype()
	{
		return this.ordertype;
	}
	
	//设置ordertype属性
	public void setOrdertype(String ordertype)
	{
		this.ordertype=ordertype;
	}
	
	//获取pageno属性
	public int getPpageno()
	{
	return this.pageno;
	}
	
	//设置pageno属性
	public void setPageno(int pageno)
	{
		this.pageno=pageno;
	}
	
	//获取pagenumber属性
	public int getPagenumber()
	{
		return this.pagenumber;
	}
	
	//设置pagenumber属性
	public void setpagenumber(int pagenumber)
	{
		this.pagenumber=pagenumber;
	}
	
	//获取fldnPK字段
	public String getFldnPK()
	{
		return this.fldnPK;
	}
	
	//设置fldnPK属性
	public void setFldnPK(String fldnPK)
	{
		this.fldnPK=fldnPK;
	}
	
	//获取other属性
	public String getOther()
	{
		return this.other;
	}		
	
	//设置other属性
	public void setOther(String other)
	{
		this.other=other;
	}


	//获取other_button属性
	public String getOther_button()
	{
		return this.other_button;
	}		
	
	//设置other_button属性
	public void setOther_button(String other_button)
	{
		this.other_button=other_button;
	}
	
	//获取查询结果
	public String getReturnfldn()
	{
		return this.returnfldn;
	}
	
	//设置returnfldn
	public void setReturnfldn(String returnfldn)
	{
		this.returnfldn=returnfldn;
	}
	
	//返回returnvalue
	public String getReturnvalue()
	{
		return this.returnvalue;
	}
	
	//获取links属性
	public String getLink()
	{
		return this.link;
	}
	
	//获取where_ex属性
	public String getWhere_ex()
	{
		return this.where_ex;
	}
	
	//设置where_ex属性
	public void setWhere_ex(String where_ex)
	{
		this.where_ex=where_ex;
	}
	
	/**
	* @Title: addQuerySetImpl 
	* @Description: 
	* @param queryReportFilter
	* @author 广州卓凡扶小冬
	* @throws
	 */
	private void addQuerySetImpl(QueryReportFilter queryReportFilter)
	{
		if (this.queryset==null)
		{
			this.queryset=new ArrayList<QueryReportFilter>();
		}
				
		this.queryset.add(queryReportFilter);
		this.querycount=this.queryset.size();
	}
	
	/**
	 * 
	* @Title: addQuerySet 
	* @Description: 添加一个查询条件的设置
	* @param text
	* @param varname
	* ………………
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public void addQuerySet(String text,String varname,String fldn)
	{
		QueryReportFilter queryReportFilter=new QueryReportFilter(text, varname, fldn);
		this.addQuerySetImpl(queryReportFilter);		
	}
	
	/**
	 * 
	* @Title: addQuerySet 
	* @Description: 添加一个查询条件的设置
	* @param text
	* @param varname
	* ………………
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public void addQuerySet(String text,String varname,String fldn,QueryReportFilter.QRFilterType type,
			String varvalue,QueryReportFilter.QRFilterOper oper)
	{
		QueryReportFilter queryReportFilter=new QueryReportFilter
				(text, varname, fldn, type, varvalue, oper, "","","","");
		this.addQuerySetImpl(queryReportFilter);		
	}
	
	/**
	 * 
	* @Title: addQuerySet 
	* @Description: 添加一个查询条件的设置
	* @param text
	* @param varname
	* ………………
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public void addQuerySet(String text,String varname,String fldn,QueryReportFilter.QRFilterType type,
			String varvalue,QueryReportFilter.QRFilterOper oper,String sql,String sqlfldn,String array,String style)
	{
		QueryReportFilter queryReportFilter=new QueryReportFilter
				(text, varname, fldn, type, varvalue, oper, sql, sqlfldn, style, array);
		this.addQuerySetImpl(queryReportFilter);		
	}
	
	/**
	* @Title: addActionItemImpl 
	* @Description: 添加一个新的操作对象
	* @param queryReportAction
	* @author 广州卓凡扶小冬
	* @throws
	 */
	private void addActionItemImpl(QueryReportAction queryReportAction)
	{
		if (this.actions==null)
		{
			this.actions=new ArrayList<QueryReportAction>();
		}
		
		this.actions.add(queryReportAction);
		this.actioncount=this.actions.size();
	}
	
	/**
	* @Title: addActionItem 
	* @Description:添加一个新的列对象 
	* @return
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public void addActionItem(String image,String url)
	{
		QueryReportAction queryReportAction=new QueryReportAction(image,url);		
		this.addActionItemImpl(queryReportAction);
	}

	public void addActionItem(String image,String url,String title,String type,String text,String target)
	{
		QueryReportAction queryReportAction=new QueryReportAction(image,url,title,type,text,target);
		this.addActionItemImpl(queryReportAction);
	}

	public void addActionItem(String image,String url,String title,String type,String text,String target,String showScript)
	{
		QueryReportAction queryReportAction=new QueryReportAction(image,url,title,type,text,target,showScript);
		this.addActionItemImpl(queryReportAction);
	}

	/**
	* @Title: addColItemImpl 
	* @Description:增加一个新的列对象 
	* @param queryReportCol
	* @author 广州卓凡扶小冬
	* @throws
	 */
	private void addColItemImpl(QueryReportCol queryReportCol)
	{
		if (this.cols==null)
		{
			this.cols=new ArrayList<QueryReportCol>();
		}
		
		this.cols.add(queryReportCol);
		this.colcount=this.cols.size();		
	}
	
	/**
	* @Title: addColItem 
	* @Description:添加一个新的列对象 
	* @param:image
	* @param url
	* @param title
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public void addColItem(String url,String title)
	{
		QueryReportCol queryReportCol=new QueryReportCol(url,title);		
		this.addColItemImpl(queryReportCol);		
	}
	
	public void addColItem(String url,String title,String func)
	{
		QueryReportCol queryReportCol=new QueryReportCol(url,title);
		queryReportCol.setFunc(func);
		this.addColItemImpl(queryReportCol);		
	}

	public void addColItem(String url,String title,String func,String styleclass)
	{
		QueryReportCol queryReportCol=new QueryReportCol(url,title);
		queryReportCol.setFunc(func);
		queryReportCol.setclassname(styleclass);
		this.addColItemImpl(queryReportCol);

	}
	
	public void addColItem(String text,String fldn,String func,String align,String styleclass,String replace,
			String func_query,String hassort,String visible,String width)
	{
		QueryReportCol queryReportCol=new QueryReportCol( text, fldn, align, func, styleclass, replace,
				 func_query, hassort, visible, width);		
		this.addColItemImpl(queryReportCol);
	}
	
	/**
	* @Title: GetValue 
	* @Description:得到相关的变量,并保存到相关的对象中去 
	* @return
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public void getValue()
	{
		String sResult="";
		
		String URI=request.getRequestURI();
		String reportCode=request.getParameter("report_code");
		String type=request.getParameter("type");
		String queryType=request.getParameter("querytype");
		String orderFldn=request.getParameter("orderfldn");
		String orderType=request.getParameter("orderType");
		String sPagenumber=request.getParameter("pagenumber");
		String sPageno=request.getParameter("pageno");
		
		if (CommonFunc.isNumeric(sPagenumber))
		{
			this.pagenumber=Integer.valueOf(sPagenumber);
		}

		if (CommonFunc.isNumeric(sPageno))
		{
			this.pageno=Integer.valueOf(pageno);
		}		
		
		this.link=URI+"?report_code="+reportCode+"&type="+type+"&orderfldn="+orderFldn+"&ordertype="+orderType+"&querytype="+queryType+"&";
		//得到查询条件
		for ( int i=0;i<this.querycount;i++)
		{
			String sVarName=this.queryset.get(i).getVarname();
			String sVarValue=request.getParameter(sVarName);
			if ("null".equals(sVarValue)){
				sVarValue="";
			}

			String noInitValue=CommonFunc.getNotNullString(request.getParameter("noinitvalue"));
			
			if ((noInitValue!=null) && (!noInitValue.equals("1")) && (StringUtils.isEmpty(sVarValue)))
			{
				sVarValue=this.queryset.get(i).getVarvalue();
			}
			
			this.queryset.get(i).setVarvalue(sVarValue);

			//2025-09-02，fuxiaodong
			//此处手动硬屏蔽NULL。原因：部分前台JS生成的，空值为为NULL字符传过来
			if (StringUtils.isNotEmpty(sVarValue) && !"null".equalsIgnoreCase(sVarValue))
			{
				this.link+=sVarName+"="+sVarValue+"&";
			}
		}
		
		//设置查询方式
		if (StringUtils.isNotEmpty(queryType)) this.querytype=queryType;
		//设置当前页码
		String sPageNo=request.getParameter("pageno");
		if (CommonFunc.isNumeric(sPageNo))
		{
			this.pageno=Integer.valueOf(sPageNo);
		}
	}
	
	/**
	* @Title: getSqlWhere 
	* @Description: 
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public String getSqlWhere() throws Exception
	{
		String sResult="";
		
		this.getValue();
		
		for (int i=0 ;i<this.querycount;i++)
		{
			String sItem="";
			
			String fldn=this.queryset.get(i).getFldn();			
			if (StringUtils.isEmpty(fldn)) continue;
			
			String sValue=this.queryset.get(i).getVarvalue();
			QueryReportFilter.QRFilterType type=this.queryset.get(i).getType();
			QueryReportFilter.QRFilterOper oper=this.queryset.get(i).getOper();
			
			if ((!type.equals(QueryReportFilter.QRFilterType.func)) && (StringUtils.isEmpty(sValue))) continue;
	

			//func类型：单独处理
			if (type.equals(QueryReportFilter.QRFilterType.func))
			{
				sValue=new Eval().eval(fldn, true);						
			}else
			{
				switch(oper)
				{
					case like:
					case notlike:
						sValue="'%"+sValue+"%'";
						break;
					
					case in:
					case notin:

						if (type.equals(QueryReportFilter.QRFilterType.selarraymore)){
							//特殊经处理：拼装成IN格式的字符串
							String[] arrTmp=sValue.split(",");
							String sValueTmp="";
							for (int xxx=0;xxx<arrTmp.length;xxx++){
								sValueTmp=(xxx==0)?"'"+arrTmp[xxx]+"'":sValueTmp+",'"+arrTmp[xxx]+"'";
							}
							sValue = "(" + sValueTmp + ")";
						}else {
							sValue = "(" + sValue + ")";
						}
						break;
					
					case base64data:
						sValue=sValue;
						break;
										
					default:
						if (type.equals(QueryReportFilter.QRFilterType.oradate))
						{
							sValue="TO_DATE('"+sValue+"','yyyy-mm-dd')";
						}else if (type.equals(QueryReportFilter.QRFilterType.unixtime))
						{							
							long time=CommonFunc.date2Unix(sValue,"yyyy-MM-dd");
							sValue=String.valueOf(time);
						}else
						{
							sValue="'"+sValue+"'";
						}
						break;
				}//switch
			}//if func
	
			if (StringUtils.isEmpty(sValue)) continue; 
	
			if (oper.equals(QueryReportFilter.QRFilterOper.base64data))
			{
				sValue=CommonFunc.base64Decode(sValue.replace(" ", "+"));
				sItem=sItem;
			}else if (type.equals(QueryReportFilter.QRFilterType.func))
			{
				sItem=sValue;
			}else
			{
				sItem=fldn+" "+oper.toString()+sValue;
			}
			
			sResult=(StringUtils.isEmpty(sResult))?sItem:sResult+(this.querytype.equals("1")?WHERE_AND:WHERE_OR)+sItem;
		}//for 
	
		if (StringUtils.isNotEmpty(this.where_ex))
		{
			sResult=" WHERE ("+this.where_ex+")"+(StringUtils.isEmpty(sResult)?"":" AND ("+sResult+")");
		}else
		{
			sResult=(StringUtils.isEmpty(sResult))?"":" WHERE "+sResult;
		}
		
		if (StringUtils.isNotEmpty(this.sql_total))
		{
			this.sql_total=this.sql_total.replace("{where}",sResult);
		}
		
		return sResult;
	}
	
	//得到查询语句
	/**
	* @Title: getSql 
	* @Description:获取查询SQL 
	* @return
	* @author 广州卓凡扶小冬
	* @throws
	*/
	public HashMap<String,String> getSql() throws Exception
	{
		HashMap<String,String> mapResult=new HashMap<String,String>();
		
		String orderStr="";	
		if (StringUtils.isNotEmpty(this.orderfldn))
		{
			orderStr=" ORDER BY "+this.orderfldn+" "+ this.ordertype ;
		}
		
		String groupStr="";	
		if (StringUtils.isNotEmpty(this.groupby))
		{
			groupStr=" group BY "+this.groupby;
		}
		
		String where="";
		try
		{
			where=this.getSqlWhere();
		}catch (Exception e)
		{
			e.printStackTrace();
			return mapResult;
		}
		
		String sql="SELECT "+this.selectfldn+" FROM "+this.table+" "+where+" "+ groupStr;

		mapResult.put("where",where);
		mapResult.put("orderStr",orderStr);

		sql=sql+orderStr;

		sql=sql.replace("&quot;","\"");
		
		String sqlCount="SELECT COUNT(*) MYCOUNT FROM "+this.table+" "+where;
		
		mapResult.put("sql", sql);
		mapResult.put("sqlCount", sqlCount);
		return mapResult;
	}
	
	/**
	* @Title: execQuery 
	* @Description:执行数据查询 
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public void execQuery() throws Exception
	{
		String orderFldn=CommonFunc.getHttpVar("orderFldn");
		String orderType=CommonFunc.getHttpVar("orderType");
		if (StringUtils.isNotEmpty(orderFldn) && StringUtils.isNotEmpty(orderType)){
			this.setOrderfldn(orderFldn+" "+orderType);
		}


		HashMap<String,String> mapSql=this.getSql();

		String sqlCount="select count(*) MYCOUNT from ("+mapSql.get("sql")+" )";
		String sql=mapSql.get("sql");
		String where =mapSql.get("where");
		String orderStr=mapSql.get("orderStr");


		try(JdbcDao jdbcDao=new JdbcDao()) {

			recordcount = CommonFunc.strToInt(jdbcDao.getFieldValue(sqlCount, "MYCOUNT"));

			//此处插入导出支持功能
			String exportPageno = CommonFunc.getNotNullString(CommonFunc.getHttpVar("export_pageno"));

			if ("-1".equals(exportPageno)) {
				this.pagenumber = recordcount;
				this.pageno = 1;
			}

			this.pagecount = (int) (Math.ceil((double) recordcount / (double) this.pagenumber));
			this.pageno = Math.min(this.pagecount, Math.max(1, this.pageno));
			this.rowfrom = Math.max(0, Math.min(this.recordcount, (this.pageno - 1) * this.pagenumber));
			this.rowto = Math.min(recordcount, Math.max(0, this.rowfrom + this.pagenumber));

			sql = JdbcDao.getLimitSql(sql, this.rowfrom, this.rowto);

			if (StringUtils.isNotEmpty(this.sql_total)) {
				sql = sql + " union all " + this.sql_total;
				//sql=sql+" "+where+" ";
				if (StringUtils.isNotEmpty(this.sql_total_fldn)) {
					sql = sql + " " + this.sql_total_fldn;
				}

				sql = "select * from (" + sql + ") " + orderStr;
			}

			listResult = jdbcDao.getResult(sql);

			///////////////////////////////
			//listCol
			this.listCol = new ArrayList();

			for (int i = 0; i < this.colcount; i++) {
				String colTitle = this.cols.get(i).getTitle(this.link, request.getParameter("ordertype"));
				listCol.add(this.cols.get(i).getText());
			}
			///////////////2025-05-19新增
			//listColFldn
			this.listColFldn = new ArrayList();
			for (int i = 0; i < this.colcount; i++) {
				listColFldn.add(this.cols.get(i).getFldn());
			}

			if (this.actioncount > 0) {
				listCol.add(actionTitleText);
			}

			///////////////////////////////
			//listContent
			this.listContent = new ArrayList();
			if (null != listResult) {
				for (int i = 0; i < listResult.size(); i++) {
					ArrayList<String> listItem = new ArrayList<String>();
					HashMap<String, String> row = listResult.get(i);
					// 添加主键
					listItem.add((row.containsKey(this.fldnPK)) ? row.get(this.fldnPK) : "");

					for (int j = 0; j < this.colcount; j++) {
						listItem.add(this.cols.get(j).getValue(row));
					}
					// 获取操作对象
					if (this.actioncount > 0) {
						String actionItem = "";
						for (int j = 0; j < this.actioncount; j++) {
							actionItem += this.actions.get(j).getOutPut(row) + "&nbsp;&nbsp;";
						}
						listItem.add(actionItem);
					}

					this.listContent.add(listItem);
				}
			}

			//////////////////////////////
			//添加filter
			this.listFilter = new ArrayList();
			for (int i = 0; i < this.querycount; i++) {
				this.listFilter.add(this.queryset.get(i).getOutPut());
			}

			jdbcDao.close();
		}
	}
	
	/**	
	* @Title: getOutPut 
	* @Description:将查询结果进行封装并返回 
	* @return
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public QueryReportOutPut getOutPut()
	{
		QueryReportOutPut qro=new QueryReportOutPut();

		qro.setDataname(dataname);
		//uri
		qro.setUri(request.getRequestURI());
		//标题
		qro.setTitle(this.title);
		qro.setTitle_short(this.title_short);
		qro.setRemark(this.remark);
		//总记录数
		qro.setRecordcount(String.valueOf(this.recordcount));
		//当前页码
		qro.setPageno(String.valueOf(this.pageno));
		//总页数
		qro.setPagecount(String.valueOf(this.pagecount));
		//每页显示数
		qro.setPagenumber(String.valueOf(this.pagenumber));
		//开始记录数
		qro.setRowfrom(String.valueOf(Math.min(this.recordcount,this.rowfrom+1)));
		//结束记录数
		qro.setRowto(String.valueOf(this.rowto));
		
		qro.setListTableHead(this.listTableHead);
		//其它信息
		qro.setOther(this.other);
		qro.setOtherEnd(this.otherEnd);
		//报表代码
		qro.setReportcode(request.getParameter("report_code"));
		
		//查询条件输出
		qro.setListFilter(this.listFilter);
		
		//列标头输出
		qro.setListCol(this.listCol);
		//列标头输出
		qro.setListColFldn(this.listColFldn);///////////////2025-05-19新增
		qro.setFlagSort(this.flagSort);
		//查询结果集
		qro.setListContent(this.listContent);

		String[] arrTmp=orderfldn.trim().split(" ",2);
		qro.setOrderfldn(arrTmp[0].trim());
		if (arrTmp.length>1){
			qro.setOrdertype(arrTmp[1].trim());
		}

		int number=Math.min(10,this.pagecount);
		int middle=number/2;
		int pagefrom=Math.max(1,this.pageno-middle);
		qro.setPagefrom(pagefrom);
		qro.setPageto(Math.min(pagefrom+number,Math.min(this.pagecount,this.pageno+number)));

		//加入总计行
//		calcTotal(qro.getReportcode());
		
		QueryReportPaging queryReportPaging=new QueryReportPaging();
		queryReportPaging.setLink(this.link);
		queryReportPaging.setPageCount(this.pagecount);
		queryReportPaging.setPageNo(this.pageno);
		queryReportPaging.setPageNumber(this.pagenumber);
		queryReportPaging.setShowSelect(true);
		
		String paging=queryReportPaging.getOutPut();
		qro.setPaging(paging);

		qro.setActionCount(this.actioncount);
		qro.setFlagImport(flagImport);

		qro.setFlagCheckbox(flagCheckbox);

		return qro;
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

	public String getTitle_short() {
		return title_short;
	}

	public void setTitle_short(String title_short) {
		this.title_short = title_short;
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

	public int getFlagSort() {
		return flagSort;
	}

	public void setFlagSort(int flagSort) {
		this.flagSort = flagSort;
	}

	public String getActionTitleText() {
		return actionTitleText;
	}

	public void setActionTitleText(String actionTitleText) {
		this.actionTitleText = actionTitleText;
	}

	public void addHtmlPageQuery(String label, String varName, String varValue, String fldn) {
		QueryReportFilter queryReportFilter = new QueryReportFilter(label, varName, fldn);
		queryReportFilter.setVarvalue(varValue);
		this.listFilter.add(queryReportFilter.getOutPut());
	}

	public String getOtherEnd() {
		return otherEnd;
	}

	public void setOtherEnd(String otherEnd) {
		this.otherEnd = otherEnd;
	}
}
