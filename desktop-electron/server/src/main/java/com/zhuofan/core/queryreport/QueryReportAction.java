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

import org.apache.commons.lang3.StringUtils;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.eval.Eval;

/** 
 * ClassName:QueryReportAction
 * 查询报表操作设置类
 * 
 * @author   fuxiaodong 
 * @Date     2015-05-25 
 */ 
public class QueryReportAction
{
	//显示类型,如text,image
	private String type="";
	//显示的文本
	private String text=""; 
	//显示的图像URL
	private String image="";
	//链接到的URL
	private String url="";
	//显示标题
	private String title="";	
	//链接目标
	private String target=""; 
	//span样式
	private String spanStyle="cursor:pointer;";
	//是否展示的脚本控制
	private String showScript="";

	//构造函数
	public QueryReportAction()
	{
		this.type="text";
		this.title="";
		this.text="";
		this.target="";
	}
	
	public QueryReportAction(String image,String url)
	{
	    this.image=image;
	    this.url=url;
	}
	
	public QueryReportAction(String image,String url,String title,String type,String text,String target)
	{
	    this.image=image;
	    this.url=url;
	    this.title=title;
	    this.type=(type.equals("image"))?type:"text";
	    this.text=text;
	    this.target=target;
	}

	public QueryReportAction(String image,String url,String title,String type,String text,String target,String showScript)
	{
		this.image=image;
		this.url=url;
		this.title=title;
		this.type=(type.equals("image"))?type:"text";
		this.text=text;
		this.target=target;
		this.showScript=showScript;
	}

	public String getSpanStyle()
	{
		return this.spanStyle;
	}
	
	public void setSpaStyle(String spanStyle)
	{
		this.spanStyle=spanStyle;
	}
	
	//获取type属性
	public String getType()
	{
		return this.type;
	}

	//设置type属性
	public void setType(String type)
	{
		this.type=(type.equals("image"))?type:"text";
	}
	
	//获取text属性
	public String  getText()
	{
		return this.text;
	}

	//设置text属性
	public void setText(String text)
	{
		this.text=text;
	}

	//获取image属性
	public String  getTmage()
	{
		return this.image;
	}

	//设置image属性
	public void setImage(String image)
	{
		this.image=image;
	}

	//获取url属性
	public String getUrl()
	{
		return this.url;
	}

	//设置url属性
	public void setUrl(String url)
	{
		this.url=url;
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

	//写出资料,row为一行数据记录
	public String getOutPut(HashMap <String,String> row) throws Exception
	{
		String sResult="";
		String sUrl=new Eval().eval(url,true,row);

		if (StringUtils.isNotEmpty(this.showScript)){
			//权限脚本不为空，则需检查权限
			if (this.showScript.equals("0")){
				//无权限
				return "";
			}

			//判断是否有权限
			String[] arrTmp=this.showScript.split("=",2);
			if (arrTmp.length==2 && row.containsKey(arrTmp[0])){
				if (!row.get(arrTmp[0]).trim().equals(arrTmp[1].trim())){
					//无权限
					return "";
				}
			}
		}

		if (!StringUtils.isEmpty(url))
		{
			String sTitle=this.title;
			String sHref=CommonFunc.equalsFirst(url, CommonFunc.JS)
				?"href='#' onclick=\""+url+"\""
				:"href='"+url+"'";
			if (type.equals("text"))
			{
				String sText=new Eval().eval(text,true,row); 
				if (CommonFunc.equalsFirst(sUrl, CommonFunc.JS))
				{
					sResult="<span title=\""+sTitle+"\" style='"+this.spanStyle+"' onclick=\""+sUrl+"\">"+sText+"</span>\n";
				}else
				{
					sResult="<a "+sHref+" title=\""+sTitle+"\" target=\""+this.target+"\">"+sText+"</a>\n";
				}
			}else
			{
				sResult="<a "+sHref+"><img border=\"0\" width=\"16\" height=\"16\" src=\""+image+"\" alt=\""+sTitle+"\"></a>\n";
			}
		}
		return sResult;
	}


	public String getShowScript() {
		return showScript;
	}

	public void setShowScript(String showScript) {
		this.showScript = showScript;
	}
}