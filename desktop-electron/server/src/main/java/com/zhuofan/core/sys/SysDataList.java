package com.zhuofan.core.sys;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.queryreport.QueryReport;
import com.zhuofan.core.queryreport.QueryReportFilter;
import com.zhuofan.core.queryreport.QueryReportOutPut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;

public class SysDataList {
    private HttpServletRequest request;
    private SysConfig sysConfig;
    private DatasourceConfig datasourceConfig;

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

    public SysDataList(HttpServletRequest request,SysConfig sysConfig,DatasourceConfig datasourceConfig){
        this.request=request;
        this.sysConfig=sysConfig;
        this.datasourceConfig=datasourceConfig;
    }

    /**
     * @Title: sysFlowNodeExecList
     * @Description:系统流程节点管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysFlowNodeExecList () throws  Exception {
        QueryReport	queryReport=new QueryReport("流程审核意见查看",request,sysConfig,datasourceConfig);
        String flowInstanceId=CommonFunc.getHttpVar("flow_instance_id");

        queryReport.setTable(" sys_flow_exec a left join sys_flow_node b on a.flow_node_id=b.flow_node_id " +
                " left join sys_flow c on a.flow_id=c.flow_id ");
        queryReport.setSelectfldn("c.flow_name,b.node_name,a.*," +
                "case when a.exec_action='10' then '待处理'  when a.exec_action='30' then '已确认' when a.exec_action='40' then '已回退' end EXEC_ACTION_S ");
        queryReport.setWhere_ex("a.flow_instance_id='"+flowInstanceId+"'");
        queryReport.setOrderfldn("a.create_time asc");

        StringBuffer sbFunc=new StringBuffer();
        queryReport.addColItem("流程名称","FLOW_NAME");
        queryReport.addColItem("节点名称","NODE_NAME");
        queryReport.addColItem("创建时间","CREATE_TIME");
        queryReport.addColItem("接收人","SENDTO_USER");
        queryReport.addColItem("审核时间","EXEC_TIME");
        queryReport.addColItem("审核人","USERNAME");
        queryReport.addColItem("审核状态","EXEC_ACTION_S");
        queryReport.addColItem("审核意见","AUDIT_CONTENT");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }


    /**
     * @Title: sysFlowNodeList
     * @Description:系统流程节点管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysFlowNodeList () throws  Exception {
        QueryReport	queryReport=new QueryReport("系统自定义业务流程节点管理",request,sysConfig,datasourceConfig);

        String findFlowId=CommonFunc.getHttpVar("find_flow_id");

        queryReport.setTable("SYS_FLOW_NODE A LEFT JOIN SYS_FLOW B ON A.FLOW_ID=B.FLOW_ID " +
                "LEFT JOIN SYS_TABLE_COLUMN_GROUP C ON A.TABLE_COLUMN_GROUP_ID=C.TABLE_COLUMN_GROUP_ID");
        queryReport.setSelectfldn("A.*,B.FLOW_NAME,C.GROUP_NAME");
        queryReport.setFldnPK("FLOW_NODE_ID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_flow_node&flow_id="+findFlowId+"');\">添加新的流程节点</button>");

        queryReport.addQuerySet("节点名称","find_nodename","A.NODE_NAME");
        queryReport.addQuerySet("节点代码","find_nodecode","A.NODE_CODE");

        StringBuffer sbFunc=new StringBuffer();
        queryReport.addColItem("流程名称","FLOW_NAME");
        queryReport.addColItem("节点名称","NODE_NAME");
        queryReport.addColItem("节点代码","NODE_CODE");
        queryReport.addColItem("类型","NODE_TYPE",Eval.getHashMapValue("CommonObject.mapFlowNodeType", "NODE_TYPE"));
        queryReport.addColItem("分组名称","GROUP_NAME");
        queryReport.addColItem("顺序","SORTINDEX");
        queryReport.addColItem("分支条件","SWITCH_CODE");

        queryReport.addActionItem("", Eval.getEditUrl("sys_flow_node","flow_node_id","FLOW_NODE_ID")
                ,"修改当前节点的资料","text","修改","");

        queryReport.addActionItem("", Eval.getDeleteUrl("sys_flow_node","FLOW_NODE_ID"),"删除当前节点资料","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysFlowList
     * @Description:系统流程管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysFlowList () throws  Exception {
        QueryReport	queryReport=new QueryReport("系统自定义业务流程管理",request,sysConfig,datasourceConfig);

        String findTableId=CommonFunc.getHttpVar("find_table_id");

        queryReport.setTable("SYS_FLOW A LEFT JOIN SYS_TABLE B ON A.TABLE_ID=B.TABLE_ID");
        queryReport.setSelectfldn("A.*,B.TABLE_NAME,B.TABLE_CODE");
        queryReport.setFldnPK("FLOW_ID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_flow');\">添加新的自定义流程</button>");

        queryReport.addQuerySet("流程名称","find_tablename","A.FLOW_NAME");
        queryReport.addQuerySet("流程代码","find_groupname","A.FLOW_CODE");

        StringBuffer sbFunc=new StringBuffer();
        queryReport.addColItem("流程名称","FLOW_NAME");
        queryReport.addColItem("流程名称","FLOW_CODE");
        queryReport.addColItem("表名","TABLE_CODE");
        queryReport.addColItem("创建人","CREATE_USER");
        queryReport.addColItem("创建日期","CREATE_TIME");
        queryReport.addColItem("备注","REMARK");

        queryReport.addActionItem("", Eval.getEditUrl("sys_flow","flow_id","FLOW_ID")
                ,"修改当前流程的资料","text","修改","");

        queryReport.addActionItem("", Eval.getDeleteUrl("sys_flow","FLOW_ID"),"删除当前流程资料","text","删除","");

        StringBuffer bufferUrl=new StringBuffer();
        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:window.open('datalist.html?dataname=sys_flow_node&find_flow_id=\"+mapRow.get(\"FLOW_ID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"节点列表","text","节点","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysTableGroupList
     * @Description:数据表分组
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysTableGroupList () throws  Exception{
        QueryReport	queryReport=new QueryReport("系统自定义表分组查询管理",request,sysConfig,datasourceConfig);

        String findTableId=CommonFunc.getHttpVar("find_table_id");

        queryReport.setTable("SYS_TABLE_COLUMN_GROUP A LEFT JOIN SYS_TABLE B ON A.TABLE_ID=B.TABLE_ID");
        queryReport.setSelectfldn("A.*,B.TABLE_NAME");
        queryReport.setFldnPK("TABLE_COLUMN_GROUP_ID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_table_group&table_id="+findTableId+"');\">添加新的自定义表分组</button>");

        queryReport.addQuerySet("表中文名","find_tablename","B.TABLE_NAME");
        queryReport.addQuerySet("分组名","find_groupname","A.GROUP_NAME");
        queryReport.addQuerySet("","find_table_id","A.TABLE_ID",
                QueryReportFilter.QRFilterType.hide,"",QueryReportFilter.QRFilterOper.equal);

        StringBuffer sbFunc=new StringBuffer();
        queryReport.addColItem("表中文名","TABLE_NAME");
        queryReport.addColItem("分组名称","GROUP_NAME");
        queryReport.addColItem("分组编码","GROUP_CODE");
        queryReport.addColItem("排序","SORTINDEX");
        queryReport.addColItem("备注","REMARK");

        queryReport.addActionItem("", Eval.getEditUrl("sys_table_group","table_column_group_id","TABLE_COLUMN_GROUP_ID")
                ,"修改当前分组的资料","text","修改","");

        queryReport.addActionItem("", Eval.getDeleteUrl("sys_table_group","TABLE_COLUMN_GROUP_ID"),"删除当前表分组资料","text","删除","");

        StringBuffer bufferUrl=new StringBuffer();
        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:window.open('datalist.html?dataname=sys_table_column&find_table_id=\"+mapRow.get(\"TABLE_ID\")+\"&find_table_column_group_id=\"+mapRow.get(\"TABLE_COLUMN_GROUP_ID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"分组字段列表","text","字段","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysTableColumnList
     * @Description:数据表字段
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysTableColumnList () throws  Exception{
        QueryReport	queryReport=new QueryReport("系统自定义表字段查询管理",request,sysConfig,datasourceConfig);

        String findTableId=CommonFunc.getHttpVar("find_table_id");
        String findTableColumnGroupId=CommonFunc.getHttpVar("find_table_column_group_id");

        queryReport.setTable("SYS_TABLE_COLUMN A LEFT JOIN SYS_TABLE B ON A.TABLE_ID=B.TABLE_ID LEFT JOIN SYS_TABLE_COLUMN_GROUP C ON A.TABLE_COLUMN_GROUP_ID=C.TABLE_COLUMN_GROUP_ID");
        queryReport.setSelectfldn("A.*,B.TABLE_NAME,C.GROUP_NAME");
        queryReport.setFldnPK("TABLE_COLUMN_ID");
        queryReport.setOrderfldn(" C.SORTINDEX ASC,A.SORTINDEX ASC ");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_table_column" +
                "&table_id="+findTableId+"&table_column_group_id="+findTableColumnGroupId+"');\">添加新的字段</button>");

        queryReport.addQuerySet("表中文名","find_classname","A.TABLE_NAME");
        queryReport.addQuerySet("字段名","find_columnname","A.COLUMN_NAME");
        queryReport.addQuerySet("字段编码","find_columnname","A.COLUMN_CODE");
        queryReport.addQuerySet("","find_table_id","A.TABLE_ID",
                QueryReportFilter.QRFilterType.hide,"",QueryReportFilter.QRFilterOper.equal);
        queryReport.addQuerySet("","find_table_column_group_id","A.TABLE_COLUMN_GROUP_ID",
                QueryReportFilter.QRFilterType.hide,"",QueryReportFilter.QRFilterOper.equal);

        StringBuffer bufferUrl=new StringBuffer();
        queryReport.addColItem("表中文名","TABLE_NAME");
        queryReport.addColItem("字段名称","COLUMN_NAME");
        queryReport.addColItem("字段编码","COLUMN_CODE");
        queryReport.addColItem("分组","GROUP_NAME");
        queryReport.addColItem("类型","COLUMN_TYPE");
        queryReport.addColItem("长度","COLUMN_LENGTH");
        queryReport.addColItem("主键","ISPK",Eval.getHashMapValue("CommonObject.mapYesNo", "ISPK"));
        queryReport.addColItem("允许为空","NOTNULL",Eval.getHashMapValue("CommonObject.mapYesNo", "NOTNULL"));
        queryReport.addColItem("默认值","DEFAULTVALUE");
        queryReport.addColItem("排序号","SORTINDEX");
        queryReport.addColItem("备注","REMARK");

        queryReport.addColItem("录入方式","INPUT_TYPE",Eval.getHashMapValue("CommonObject.sysTableColumn_inputType", "INPUT_TYPE"));
        queryReport.addColItem("隐藏","ISHIDE",Eval.getHashMapValue("CommonObject.mapYesNo", "ISHIDE"));

        queryReport.addActionItem("", Eval.getEditUrl("sys_table_column","table_column_id","TABLE_COLUMN_ID"),"修改当前字段的资料","text","修改","");

        queryReport.addActionItem("", Eval.getDeleteUrl("sys_table_column","TABLE_COLUMN_ID"),"删除当前字段","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: passErrlogList
     * @Description:密码错误日志
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut passErrlogList () throws  Exception{
        return this.syslogListImpl("loginfail","系统登录失败日志管理");
    }

    /**
     * @Title: syslogList
     * @Description:系统日志
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut syslogList() throws Exception{
        return this.syslogListImpl("sys","系统运行日志管理");
    }

    private QueryReportOutPut syslogListImpl(String logType,String pageTitle) throws  Exception{
        QueryReport queryReport=new QueryReport(pageTitle,request,sysConfig,datasourceConfig);

        queryReport.setTable("SYS_LOGS a ");
        queryReport.setSelectfldn("A.*");
        queryReport.setOrdertype("desc");
        queryReport.setOrderfldn("log_time");
        queryReport.setFldnPK("LOG_ID");
        queryReport.setWhere_ex("log_type='"+logType+"'");

        queryReport.addQuerySet("日志内容","find_content","content");
        queryReport.addQuerySet("操作人","find_username","username");
        queryReport.addQuerySet("模块名称","find_module_name","module_name");
        queryReport.addQuerySet("IP","find_id","ip");

        queryReport.addColItem("时间","LOG_TIME",Eval.getEval_Row_Unix2Data("LOG_TIME"));
        queryReport.addColItem("操作人","USERNAME");
        queryReport.addColItem("模块名称","MODULE_NAME");
        queryReport.addColItem("IP","IP");
        queryReport.addColItem("日志内容","CONTENT");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysFankuiList
     * @Description:系统反馈列表s
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysFankuiList()throws Exception{
        return this.sysOaListImpl("系统问题反馈管理","fankui");
    }

    private QueryReportOutPut sysOaListImpl(String pageTitle,String oaType) throws Exception{
        QueryReport queryReport=new QueryReport(pageTitle,request,sysConfig,datasourceConfig);

        queryReport.setTable("SYS_OA");
        queryReport.setSelectfldn("*");
        queryReport.setOrdertype("desc");
        queryReport.setOrderfldn("add_time");
        queryReport.setFldnPK("OA_ID");
        queryReport.setWhere_ex("oa_type='"+oaType+"'");

        queryReport.addQuerySet("标题", "find_oa_title","OA_TITLE");
        queryReport.addQuerySet("内容", "find_oa_content","OA_CONTENT");

        queryReport.addColItem("创建时间", "ADD_TIME", Eval.getEval_Row_Unix2Data("ADD_TIME"));
        queryReport.addColItem("标题", "OA_TITLE");
        queryReport.addColItem("发送人", "ADD_BY_USER");
        queryReport.addColItem("结束时间", "END_TIME", Eval.getEval_Row_Unix2Data("END_TIME"));
        queryReport.addColItem("接收人", "NOTICE_USER");
        queryReport.addColItem("备注", "REMARK");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysModuleList
     * @Description:系统模块列表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysModuleList() throws  Exception{
        QueryReport	queryReport=new QueryReport("系统模块管理",request,sysConfig,datasourceConfig);

        queryReport.setTable("SYS_MODULES a left join SYS_MODULES b on a.parentid=b.moduleid");
        queryReport.setSelectfldn("A.*,b.modulename MODULENAME_PARENT");
        queryReport.setOrdertype("asc");
        queryReport.setOrderfldn("a.sortindex");
        queryReport.setFldnPK("MODULEID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_module');\">添加新的系统模块</button>");

        queryReport.addQuerySet("模块名称","find_modulename","A.MODULENAME");
        queryReport.addQuerySet("模块URL","find_url","A.URL");
        queryReport.addQuerySet("备注","find_remark","A.REMARK");
        queryReport.addQuerySet("上级ID","find_parentid","A.PARENTID",QueryReportFilter.QRFilterType.hide,"-1",
                QueryReportFilter.QRFilterOper.equal,"","","","");

        queryReport.addColItem("模块名称","MODULENAME");
        queryReport.addColItem("模块URL","URL");
        queryReport.addColItem("上级模块名称","MODULENAME_PARENT");
        queryReport.addColItem("排序号","SORTINDEX");
        queryReport.addColItem("备注","REMARK");

        queryReport.addActionItem("", Eval.getEditUrl("sys_module","moduleid","MODULEID"),"修改当前模块的资料","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("sys_module","MODULEID"),"删除当前角色","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysNotifyList
     * @Description:系统通知列表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysNotifyList() throws Exception {
        QueryReport queryReport = new QueryReport("系统通知管理", request, sysConfig, datasourceConfig);

        queryReport.setTable("SYS_NOTICE");
        queryReport.setSelectfldn("*");
        queryReport.setFldnPK("P_NOTICE_ID");

        queryReport.addQuerySet("通知标题", "find_biaoti", "BIAOTI");
        queryReport.addQuerySet("通知内容", "find_neirong", "NEIRONG");

        queryReport.addColItem("通知时间", "CREATETIME", Eval.getEval_Row_Unix2Data("CREATETIME"));
        queryReport.addColItem("通知人","CREATEUSER");
        queryReport.addColItem("通知标题", "BIAOTI");
        queryReport.addColItem("通知内容", "NEIRONG");
        queryReport.addColItem("通知对像", "TONGZHIDUIXIANG");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysReportList
     * @Description:系统查询报表列表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysReportList() throws Exception{
        QueryReport	queryReport=new QueryReport("查询报表设计管理",request,sysConfig,datasourceConfig);

        queryReport.setTable("SYS_QUERYREPORT");
        queryReport.setSelectfldn("*");
        queryReport.setFldnPK("QUERY_REPORT_ID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_report');\">设计新的查询报表</button>");

        queryReport.addQuerySet("报表名称","find_name","REPORT_NAME");
        queryReport.addQuerySet("报表代码","find_code","REPORT_CODE");
        queryReport.addQuerySet("报表标题","find_title","TITLE");
        queryReport.addQuerySet("数据表名","find_tablename","TABLENAME");
        queryReport.addQuerySet("创建人","find_createuser","CREATEUSER");

        StringBuffer sbFunc=new StringBuffer();
        sbFunc.append("return \"<a target='_blank' href='SysQueryReport_execQuery.action?report_code=\"+mapRow.get(\"REPORT_CODE\")+\"'>\"+mapRow.get(\"REPORT_NAME\")+\"</a>\";");
        queryReport.addColItem("报表名称","REPORT_NAME",Eval.EVAL+sbFunc.toString());
        queryReport.addColItem("报表代码","REPORT_CODE");
        queryReport.addColItem("创建人","CREATEUSER");

        queryReport.addColItem("创建时间","CREATETIME",Eval.getEval_Row_Unix2Data("CREATETIME"));
        queryReport.addColItem("报表标题","TITLE");
        queryReport.addColItem("数据表名","TABLENAME");
        queryReport.addColItem("字段列表","SELECTFLDN");
        queryReport.addColItem("排序字段","ORDERFLDN");
        queryReport.addColItem("分组字段","GROUPBY");
        queryReport.addColItem("扩展条件","WHERE_EX");

        queryReport.addActionItem("", Eval.getEditUrl("sys_report","query_report_id","QUERY_REPORT_ID")
                ,"修改本查询报表","text","修改","");

        StringBuffer bufferUrl=new StringBuffer();
        bufferUrl.append("String url=\"javascript:window.open('SysQueryReport_Add.action?actiontype=add&source_id=\"+mapRow.get(\"QUERY_REPORT_ID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"复制本查询报表","text","复制","");

        queryReport.addActionItem("", Eval.getDeleteUrl("sys_report","QUERY_REPORT_ID"),"删除本查询报表","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysRequireList
     * @Description:系统需求列表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysRequireList() throws  Exception{
        return this.sysOaListImpl("系统需求管理","require");
    }

    /**
     * @Title: sysTableClassList
     * @Description:查询方法实现(查询数据表分类)
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysTableClassList() throws Exception{
        QueryReport	queryReport=new QueryReport("系统自定义分类查询管理",request,sysConfig,datasourceConfig);

        queryReport.setTable("SYS_TABLE_CLASS A");
        queryReport.setSelectfldn("A.*");
        queryReport.setFldnPK("TABLE_CLASS_ID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_table_class');\">添加新的自定义分类</button>");

        queryReport.addQuerySet("分类名称","find_classname","A.CLASS_NAME");
        queryReport.addQuerySet("备注","find_remark","A.REMARK");

        StringBuffer sbFunc=new StringBuffer();
        queryReport.addColItem("分类名称","CLASS_NAME");
        queryReport.addColItem("备注","REMARK");
        queryReport.addActionItem("", Eval.getEditUrl("sys_table_class","table_class_id","TABLE_CLASS_ID"),"修改当前分类的资料","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("sys_table_class",  "TABLE_CLASS_ID"),"删除当前分类","text","删除","");

        queryReport.execQuery();

        return  queryReport.getOutPut();
    }

    /**
     * @Title: sysTableList
     * @Description:查询方法实现(查询数据表)
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysTableList() throws Exception {
        QueryReport	queryReport=new QueryReport("系统自定义表查询管理",request,sysConfig,datasourceConfig);

        String findParentId=request.getParameter("find_parent_id");
        findParentId=findParentId==null?"":findParentId;

        queryReport.setTable("SYS_TABLE A LEFT JOIN SYS_TABLE_CLASS B ON A.TABLE_CLASS_ID=B.TABLE_CLASS_ID");
        queryReport.setSelectfldn("A.*,B.CLASS_NAME");
        queryReport.setFldnPK("TABLE_ID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_table&parent_id="+findParentId+"');\">添加新自定义表</button>");

        queryReport.addQuerySet("表中文名","find_classname","A.TABLE_NAME");
        queryReport.addQuerySet("数据库表名","find_remark","A.TABLE_CODE");
        queryReport.addQuerySet("","find_parent_id","A.PARENT_ID",
                QueryReportFilter.QRFilterType.hide,"0",QueryReportFilter.QRFilterOper.equal);

        StringBuffer sbFunc=new StringBuffer();
        queryReport.addColItem("分类名称","CLASS_NAME");
        queryReport.addColItem("表中文名","TABLE_NAME");
        queryReport.addColItem("数据库表名","TABLE_CODE");
        queryReport.addColItem("表类型","IS_SUBTABLE",Eval.getHashMapValue("CommonObject.sysTable_IsSubTable", "IS_SUBTABLE"));
        queryReport.addColItem("录入方式","INPUT_TYPE",Eval.getHashMapValue("CommonObject.sysTable_InputType", "INPUT_TYPE"));

        queryReport.addColItem("创建时间","CREATE_TIME",Eval.getEval_Row_Unix2Data("CREATE_TIME"));
        queryReport.addColItem("创建人","CREATE_USER");
        queryReport.addColItem("备注","REMARK");


        StringBuffer bufferUrl=new StringBuffer();

        queryReport.addActionItem("", Eval.getEditUrl("sys_table","table_id","TABLE_ID"),"修改当前表的资料","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("sys_table",  "TABLE_ID"),"删除当前表","text","删除","");
//		queryReport.addActionItem("", Eval.getEditUrl("SysTable_Delete","table_id","TABLE_ID"),"删除当前表","text","删除","");

        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:window.open('datalist.html?dataname=sys_table&find_parent_id=\"+mapRow.get(\"TABLE_ID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"子表管理","text","子表","");

        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:window.open('datalist.html?dataname=sys_table_group&find_table_id=\"+mapRow.get(\"TABLE_ID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"字段分组管理","text","分组","");

        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:window.open('datalist.html?dataname=sys_table_column&find_table_id=\"+mapRow.get(\"TABLE_ID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"字段管理","text","字段","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysDeptList
     * @Description:查询方法实现(查询组织部门列表)
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysDeptList() throws Exception {
        QueryReport	queryReport=new QueryReport("系统部门组织管理",request,sysConfig,datasourceConfig);

        queryReport.setTable("SYS_DEPARTMENTS a left join SYS_DEPARTMENTS b on a.parentid=b.DEPARTMENTID");
        queryReport.setSelectfldn("A.*,b.DEPARTMENTNAME DEPARTMENTNAME_PARENT");
        queryReport.setFldnPK("DEPARTMENTID");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_dept')\">添加新部门</button>");

        queryReport.addQuerySet("部门名称","find_name","DEPARTMENTNAME");
        queryReport.addQuerySet("备注","find_remark","REMARK");
        queryReport.addQuerySet("上级ID","find_parentid","A.PARENTID",QueryReportFilter.QRFilterType.hide,"-1",
                QueryReportFilter.QRFilterOper.equal,"","","","");

        StringBuffer sbFunc=new StringBuffer();
        sbFunc.append("String sResult=\"<a href='\"+CommonFunc.getRequestURI()+\"?find_parentid=\"+mapRow.get(\"DEPARTMENTID\")+\"'>\"+mapRow.get(\"DEPARTMENTNAME\")+\"</a>\";\n");
        sbFunc.append("return sResult;");
        queryReport.addColItem("部门名称","DEPARTMENTNAME",Eval.EVAL+sbFunc.toString());
        queryReport.addColItem("上级部门名称","DEPARTMENTNAME_PARENT");
        queryReport.addColItem("备注","REMARK");

        StringBuffer bufferUrl=new StringBuffer();
        bufferUrl.append("String url=\"javascript:window.open('dataadd.html?dataname=sys_dept&actiontype=edit&pk_id=\"+mapRow.get(\"DEPARTMENTID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"修改当前部门的资料","text","修改 ","");

        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:window.open('datalist.html?dataname=sys_user&find_deptname=\"+mapRow.get(\"DEPARTMENTNAME\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"查看部门下面的所有用户","text","用户","");

        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:jsCommon.deleteTableData('sys_dept','\"+mapRow.get(\"DEPARTMENTID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"删除当前部门","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysRoleList
     * @Description:查询方法实现(查询系统角色列表)
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysRoleList() throws Exception{
        QueryReport	queryReport=new QueryReport("系统角色管理",request,sysConfig,datasourceConfig);

        queryReport.setTable("SYS_ROLES");
        queryReport.setSelectfldn("*");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_role')\">添加新角色</button>");

        queryReport.addQuerySet("部门类型","find_depttype","DEPT_TYPE");
        queryReport.addQuerySet("角色名称","find_name","ROLENAME");
        //queryReport.addQuerySet("模块类型","find_mtype","M_TYPE",QueryReportFilter.QRFilterType.selarray,"",QueryReportFilter.QRFilterOper.equal,"","","OAS系统","");
        queryReport.addQuerySet("备注","find_remark","REMARK");

        queryReport.addColItem("角色名称","ROLENAME");
        queryReport.addColItem("部门类型","DEPT_TYPE");
        queryReport.addColItem("模块分类","M_TYPE");
        queryReport.addColItem("备注","REMARK");

        StringBuffer bufferUrl=new StringBuffer();
        bufferUrl.append("String url=\"javascript:window.open('module/sysrole/grantset.html?roleid=\"+mapRow.get(\"ROLEID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"修改当前角色的权限","text","权限","");

        bufferUrl=new StringBuffer();
        bufferUrl.append("String url=\"javascript:window.open('dataadd.html?dataname=sys_role&actiontype=edit&pk_id=\"+mapRow.get(\"ROLEID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"修改该角色的资料","text","修改","");

        bufferUrl=new StringBuffer();
        bufferUrl.append("String url=\"javascript:jsCommon.deleteTableData('sys_role','\"+mapRow.get(\"ROLEID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"删除当前角色","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: sysUserList
     * @Description:查询方法实现(查询系统用户列表)
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut sysUserList() throws Exception
    {
        QueryReport queryReport=new QueryReport("系统用户帐号管理",request,sysConfig,datasourceConfig);

        queryReport.setTable("sys_users a left join  sys_departments b on a.departmentid=b.departmentid");
        queryReport.setSelectfldn("A.*,b.DEPARTMENTNAME");
        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:window.open('dataadd.html?dataname=sys_user')\">添加新用户</button>");


        queryReport.addQuerySet("用户名称","find_username","USERNAME");
        queryReport.addQuerySet("部门名称","find_deptname","DEPARTMENTNAME");
        queryReport.addQuerySet("是否可用","find_actived","NVL(ACTIVED,'0')",
                QueryReportFilter.QRFilterType.selarray,"",QueryReportFilter.QRFilterOper.equal,"","","1:可用,0:禁用","");
        queryReport.addQuerySet("备注","find_remark","REMARK");

        queryReport.addColItem("用户名称","USERNAME");
        queryReport.addColItem("性别","SEX");
        queryReport.addColItem("部门名称","DEPARTMENTNAME");

        StringBuffer sbFunc=new StringBuffer();
        sbFunc.append("String sResult=(mapRow.get(\"ACTIVED\").equals(\"1\"))?");
        sbFunc.append("\"<font color=blue>可用</font>\":\"<font color=red>禁用</font>\";");
        sbFunc.append("return sResult;");
        queryReport.addColItem("是否可用","ACTIVED", Eval.EVAL+sbFunc.toString());
        queryReport.addColItem("备注","REMARK");

        StringBuffer bufferUrl=new StringBuffer();
        bufferUrl.append("String url=\"javascript:jsCommon.do_userroleset('\"+mapRow.get(\"USERID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"分配角色权限给当前用户","text","分配角色","");

        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:window.open('dataadd.html?dataname=sys_user&actiontype=edit&pk_id=\"+mapRow.get(\"USERID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"修改该用户的资料","text","修改","");

        bufferUrl.setLength(0);
        bufferUrl.append("String url=\"javascript:deleteTableData('sys_user','\"+mapRow.get(\"USERID\")+\"');\";\n");
        bufferUrl.append("return url;\n");
        queryReport.addActionItem("", Eval.EVAL+bufferUrl.toString(),"删除当前用户","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }
}
