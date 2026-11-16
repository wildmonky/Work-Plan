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

/** 
 * ClassName:QueryReportPaging
 * 查询报表主类
 * 
 * @author   fuxiaodong 
 * @Date     2015-06-02
 */ 
public class QueryReportPaging 
{
	//URL前绷
	private String link;
	//页面总数
	private int pageCount;
	//当前页面
	private int pageNo;
	//每页显示数目
	private int pageNumber;
	//是否显示页面选择框
	private boolean showSelect;

	public void setLink(String link)
	{
		this.link=link;
	}
	
	public String getLink()
	{
		return this.link;
	}
	
	public void setPageCount(int pageCount)
	{
		this.pageCount=pageCount;
	}
	
	public int getPageCount()
	{
		return this.pageCount;
	}
	
	public void setPageNo(int pageNo)
	{
		this.pageNo=pageNo;
	}
	
	public int getPageNo()
	{
		return this.pageNo;
	}
	
	public void setPageNumber(int pageNumber)
	{
		this.pageNumber=pageNumber;
	}
	
	public int getPageNumber()
	{
		return this.pageNumber;
	}
	
	public void setShowSelect(boolean showSelect)
	{
		this.showSelect=showSelect;
	}
	
	public boolean getShowSelect()
	{
		return this.showSelect;
	}
	
	/**
	* @Title: getOutPut 
	* @Description:输出html值 
	* @return
	* @author 广州卓凡扶小冬
	* @throws
	 */
	public String getOutPut()
	{
		StringBuffer sResult=new StringBuffer();
		
		if (this.pageNo<1) return "";
		
		sResult.append("<a href=\""+this.link+"pageno=1\"><<</a>\n");
		int number=Math.min(10,pageCount);
		int middle=number/2;
		int from=Math.max(1,this.pageNo-middle);
		int to=Math.min(from+number,Math.min(pageCount,pageNo+number));
		
		for (int i=from;i<=to;i++)
		{
			if (i==pageNo)
			{
				sResult.append("<a href=\""+link+"pageno="+String.valueOf(i)+"\"><b><font color=red>"+
						String.valueOf(i)+"</font></b></a>\n");
			}else
			{
				sResult.append("<a href=\""+link+"pageno="+String.valueOf(i)+"\">"+String.valueOf(i)+"</a>\n");
			}
	    }
		sResult.append("<a href=\""+link+"pageno="+pageCount+"\">>></a>\n");
		
		//自由选择页面显示数量
		if (this.showSelect)
		{
			sResult.append("<select class=\"pagenum\" onchange=\"window.location.href='"+link+"pageno=1&pagenumber='+this.value;\">\n");
			String[] sArray=new String[]{"20","30","50","100","200","5000"};
			for (int i=0;i<sArray.length;i++)
			{
				String sItem=sArray[i];
				String selected=(sItem.equals(String.valueOf(pageNumber)))?" selected ":"";
				sResult.append("<option value="+sItem+selected+">"+sItem+"</option>\n");							
			}
		}
		
		return  sResult.toString();
	}
	
}