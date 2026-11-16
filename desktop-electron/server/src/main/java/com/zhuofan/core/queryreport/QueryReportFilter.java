/** 
 * com.izhuofan.core.orm
 * 
 * 
 *   ver     date           author 
 * ====================================================== 
 *           2015-05-26      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */  

package com.zhuofan.core.queryreport;

import org.apache.commons.lang3.StringUtils;

import com.zhuofan.core.common.CommonFunc;

import java.util.Optional;

/** 
 * ClassName:QueryReportFilter
 * 查询报表查询条件设置类
 * 
 * @author   fuxiaodong 
 * @Date     2015-05-25 
 */ 
public class QueryReportFilter
{
	//过滤条件类型,可选择:text,date,selarray,selsql,none,site,line,siteline
	public enum QRFilterType{
		text,unixtime,oradate,selarray,selsql,none,hide,br,func,datetime,selarraymore;

		public static QRFilterType fromString(String type){
			for (QRFilterType o:QRFilterType.values()){
				if (o.toString().equalsIgnoreCase(type)){
					return o;
				}
			}

			return null;
		}
	}
	
	//数据库操作符
	public enum QRFilterOper{
		//模糊比较
		like("LIKE"),		
		notlike("NOT LIKE"),
		//等于
		equal("="),
		//不等于
		notEqual("<>"),
		//大于
		greater(">"),
		//大于等于
		greaterEqual(">="),
		//小于
		less("<"),
		//小于等于
		lessEqual("<="),
		//in
		in("IN"),
		notin("NOT IN"),
		//base64
		base64data("");
		
		private String sqloper;
		
		// 构造方法
        private QRFilterOper(String sqloper) 
        {
            this.sqloper = sqloper;
        }
        
        // 覆盖方法
        @Override
        public String toString() 
        {
        	return this.sqloper;
        }

        public static QRFilterOper fromString(String sqloper){
        	for (QRFilterOper o:QRFilterOper.values()){
        		if (o.sqloper.equalsIgnoreCase(sqloper)){
        			return o;
				}
			}

        	return null;
		}
	}
	
	//显示文本
	private String text;
	//过滤条件类型
	private QRFilterType type=QRFilterType.text;  
	//名称
	private String varname;  
	//值
	private String varvalue="";
	//扩展的SQL语句
	private String sql="";    
	//扩展的字段(SQL相关)
	private String sqlfldn="";
	//SQL字段
	private String fldn;   
	//数组
	private String array="";  
	//查询方式
	private String querytype;
	//比较符
	private QRFilterOper oper=QRFilterOper.like;     
	//扩展样式
	private String style="";
	
	//构造函数
	public QueryReportFilter()
	{
	}
	
	//构造函数
	public QueryReportFilter(String text,String varname,String fldn)
	{
		this.text=text;
		this.varname=varname;
		this.fldn=fldn;
	}
	
	public QueryReportFilter(String text,String varname,String fldn,QRFilterType type,String varvalue,QRFilterOper oper,
			String sql,String sqlfldn,String style,String array)
	{
		this.text=text;
		this.varname=varname;
		this.fldn=fldn;
		this.varvalue=varvalue;
		this.type=type;
		this.oper=oper;
		this.sql=sql;
		this.sqlfldn=sqlfldn;
		this.style=style;
		this.array=array;
	}
	
	//获取text属性
	public  String getText()
	{
		return this.text;
	}
	
	//设置text属性
	public  void setText(String text)
	{
		this.text=text;
	}
	
	//获取type属性
	public  QRFilterType getType()
	{
		return this.type;
	}
	
	//设置type属性
	public  void setType(QRFilterType type)
	{
		this.type=type;
	}
	
	//获取varname属性
	public  String getVarname()
	{
		return this.varname;
	}
	
	//设置varname属性
	public  void setvarname(String varname)
	{
		this.varname=varname;
	}
	
	//获取varvalue属性
	public  String getVarvalue()
	{
	return this.varvalue;
	}
	
	//设置varvalue属性
	public  void setVarvalue(String varvalue)
	{
		this.varvalue=varvalue;
	}
	
	//获取sql属性
	public  String getSql()
	{
		return this.sql;
	}
	
	//设置sql属性
	public  void setSql(String sql)
	{
		this.sql=sql;
	}
	
	//获取fldn属性
	public  String getFldn()
	{
		return this.fldn;
	}
	
	//设置fldn属性
	public  void setFldn(String fldn)
	{
		this.fldn=fldn;
	}
	
	//获取array属性
	public  String getArray()
	{
		return this.array;
	}
	
	//设置array属性
	public  void setArray(String array)
	{
		this.array=array;
	}
	
	//获取oper属性
	public  QRFilterOper getOper()
	{
		return this.oper;
	}
	
	//设置oper属性
	public  void setOper(QRFilterOper oper)
	{
		this.oper=oper;
	}
	
	//获取sqlfldn属性
	public  String getSqlfldn()
	{
		return this.sqlfldn;
	}
	
	//设置sqlfldn属必
	public  void setSqlfldn(String sqlfldn)
	{
		this.sqlfldn=sqlfldn;
	}
	
	//输出数据
	public String getOutPut()
	{
		String name=this.varname;
		String value=this.varvalue;

		if (this.type==QRFilterType.br)
		{
			return "</tr></table><table class=listtb><tr>";
		}
		
		StringBuffer sResult=new StringBuffer();
		
		if (!CommonFunc.inArray(this.type,new QRFilterType[]{QRFilterType.hide,QRFilterType.func}))
		{
			sResult.append("<span  class='input-group-addon addon'>"+this.text+"</span>");
		}
		
		switch(this.type)
		{	
			case hide:
			case func:
				sResult.append("<textarea name='"+name+"' readonly style='display:none'>"+Optional.ofNullable(value).orElse("")+"</textarea>");
				break;
			case text:
				//输出为input
				String style=this.style.isEmpty()?"form-control input-width-seach":this.style;
				sResult.append("<input class='form-control input-width-seach' name='"+name+"' type='text' id='"+name+"' value='"+ Optional.ofNullable(value).orElse("")+"' size='12' class='"+style+"'>");
				break;				
			case unixtime:
				//value=CommonFunc.unix2Date(value,"yyyy-MM-dd");				
//				sResult.append("<input onFocus=\"WdatePicker({autoPickDate:true,dateFmt:'yyyy-MM-dd'})\" name='"+name+"' type='text' id='"+name+"' value='"+Optional.ofNullable(value).orElse("")+"' size='12' class='"+this.style+"'>");
				sResult.append("<input class='form-control input-width-seach' onFocus=\"showLaydate(this)\" name='"+name+"' type='text' id='"+name+"' value='"+Optional.ofNullable(value).orElse("")+"' size='12' class='"+this.style+"'>");

				break;
			case oradate:
//				sResult.append("<input onFocus=\"WdatePicker({autoPickDate:true,dateFmt:'yyyy-MM-dd'})\" name='"+name+"' type='text' id='"+name+"' value='"+Optional.ofNullable(value).orElse("")+"' size='12' class='"+this.style+"'>");

				sResult.append("<input onFocus=\"showLaydate(this)\" name='"+name+"' type='text' id='"+name+"' value='"+Optional.ofNullable(value).orElse("")+"' size='12' class='"+this.style+"'>");

				break;

			case datetime:
				//value=CommonFunc.unix2Date(value,"yyyy-MM-dd");
//				sResult.append("<input onFocus=\"WdatePicker({autoPickDate:true,dateFmt:'yyyy-MM-dd'})\" name='"+name+"' type='text' id='"+name+"' value='"+Optional.ofNullable(value).orElse("")+"' size='12' class='"+this.style+"'>");
				sResult.append("<input _other='time' class='form-control input-width-seach datetimepicker' onFocus=\"showLaydateTime(this)\" name='"+name+"' type='text' id='"+name+"' value='"+Optional.ofNullable(value).orElse("")+"' size='12' class='"+this.style+"'>");

				break;

			case selarray:
			case selsql:
				sResult.append("<select name='"+name+"' id='"+name+"' class='form-control "+this.style+"'>\n");
				if (this.type.equals(QRFilterType.selarray))
				{
					String[] strArray=array.split(";");
					sResult.append(CommonFunc.addArrayToOption(strArray,value,"请选择"));
				}else
				{
					sResult.append(CommonFunc.addSqlResultToOption(sql,fldn,fldn,value,""));
				}//if
				sResult.append("</select>\n");
				break;
			case selarraymore:
				String[] strArray=array.split(";");

				sResult.append(String.format(
						"<select multiple sel_values='%s' name='%s' id='%s' class='form-control selectpicker %s'>",
						value,name,name,this.style));
				sResult.append(CommonFunc.addArrayToOption(strArray,value,"请选择"));
				sResult.append("</select>\n");
				break;
		}//switch

		return sResult.toString();
	}//function
}
