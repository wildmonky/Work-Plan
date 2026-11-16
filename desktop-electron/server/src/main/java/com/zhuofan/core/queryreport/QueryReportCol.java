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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.eval.Eval;

/** 
 * ClassName:QueryReportCol
 * 查询报表显示字段列类
 * 
 * @author   fuxiaodong 
 * @Date     2015-05-25 
 */ 
public class QueryReportCol
{
	//显示的文本
	private String text;   
	//相对应的字段名
	private String fldn;   
	//对齐方式
	private String align;  
	//扩展函数
	private String func;   
	//样式的类名
	private String styleclass;  
	//是否需要转换字符串
	private String replace;    
	//用于只有查询权限的显示扩展函数
	private String func_query;
	//是否能排序
	private String hassort="";	
	//是否显示
	private String visible="";	
	//宽度
	private String width="";	
	
	/**	 
	* Title: QueryReportCol
	* Date:2015年5月27日
	* Description:构造函数
	 */
	public QueryReportCol()
	{
		
	}
	
	/**	 
	* Title: QueryReportCol.java
	* Date:2015年5月27日
	* Description:  
	* @author 广州卓凡扶小冬
	* @param text
	* @param fldn
	 */
	public QueryReportCol(String text,String fldn)
	{
		this.text=text;
		this.fldn=fldn;
	}
	
	/**
	* Title: QueryReportCol.java
	* Date:2015年5月27日
	* Description:  
	* @author 广州卓凡扶小冬
	* @param text
	* @param fldn
	* @param align
	* @param func
	* @param styleclass
	* @param replace
	* @param func_query
	* @param hassort
	* @param visible
	* @param width
	 */
	public QueryReportCol(String text,String fldn,String align,String func,String styleclass,String replace,
			String func_query,String hassort,String visible,String width)
	{
		this.text=text;
		this.fldn=fldn;
		this.align=align;
		this.func=func;
		this.styleclass=styleclass;
		this.replace=replace;
		this.func_query=func_query;
		this.hassort=hassort;
		this.visible=visible;
		this.width=width;
	}
	
	//获取text属性
	public String getText()
	{
		return this.text;
	}
	
	//设置text属性
	public void setText(String text)
	{
		this.text=text;
	}
	
	//获取fldn属性
	public String getFldn()
	{
		return this.fldn;
	}
	
	//设置fldn属性
	public void setFldn(String fldn)
	{
		this.fldn=fldn;
	}
	
	//获取align属性
	public String getAlign()
	{
		return this.align;
	}
	
	//设置align属性
	public void setAlign(String align)
	{
		this.align=align;
	}
	
	//获取func属性
	public String getFunc()
	{
		return this.func;
	}
	
	//set func属性
	public void setFunc(String func)
	{
		this.func=func;
	}
	
	//获取classname属性
	public String getStyleClass()
	{
		return this.styleclass;
	}
	
	//设置classname属性
	public void setclassname(String styleclass)
	{
		this.styleclass=styleclass;
	}
	
	//获取replace属性
	public String getReplace()
	{
		return this.replace;
	}
	
	//设置replace属性
	public void setReplace(String  replace)
	{
		this.replace=replace;
	}
	
	//获取func_query属性
	public String getFunc_query()
	{
		return this.func_query;
	}
	
	//设置func_query属性
	public void setFunc_query(String func_query)
	{
		this.func_query=func_query;
	}
	
	//获取hassort属性
	public String getHassort()
	{
		return this.hassort;
	}
	
	//设置set_hassort属性
	public void setHassort(String hassort)
	{
		this.hassort=hassort;
	}
	
	//获取visible属性
	public String getVisible()
	{
		return this.visible;
	}
	
	//设置visible属性
	public void setVisible(String visible)
	{
		this.visible=visible;
	}
	
	
	//获取text属性
	public String getWidth()
	{
		return this.width;
	}
	
	//设置text属性
	public void setWidth(String width)
	{
		this.width=width;
	}
	
	
	/**
	* @Title: getTitle 
	* @Description: 列表头数据
	* @param links
	* @param orderType
	* @return
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public String getTitle(String links,String orderType)
	{
		StringBuffer sResult=new StringBuffer();
		
		sResult.append("<Th height=\"25\" align=\"center\" width='"+width+"' noWrap>");
		
		
		if (this.hassort.equals("1"))
		{
			String imgstr="";
			String url=links+"orderfldn="+this.fldn+"&ordertype="+orderType;
			String orderfldn=CommonFunc.getHttpRequest().getAttribute("orderfldn").toString();
			
			if (orderfldn.equals(this.fldn))
			{
				String imgsort=(orderType.equals("ASC"))
					?"/images/default/sort_asc.gif"
					:"/images/default/sort_desc.gif";
				imgstr="<img src='"+imgsort+"' />";
			}
			
			sResult.append("<a href='"+url+"' class='"+this.styleclass+"'><strong>"+
					this.text+"<strong></a>"+imgstr+"</Th>\n");
		}else
		{
			sResult.append("<strong><SPAN class='"+this.styleclass+"'>"+this.text+"</SPAN></strong></TH>");
		}
		
		return sResult.toString();
	}
	
	/**
	* @Title: getValue 
	* @Description: 获取列数据的HTML
	* @param row
	* @return
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public String getValue(HashMap<String,String> row) throws Exception
	{
		String arrayFldn[]=this.fldn.split("\\.");		
		String sFldn=arrayFldn[arrayFldn.length-1];
		String result="";
		if (row.containsKey(sFldn)) {
			if (StringUtils.isEmpty(this.func)) {
				result=row.get(sFldn);
			}else{
				result=new Eval().eval(this.func, true, row);
			}
		}

		if (StringUtils.isEmpty(this.styleclass)){
			return result;
		}

		return "<span class='"+this.styleclass+"'>"+result+"</span>";
	}
	
}
