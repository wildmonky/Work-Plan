package com.zhuofan.core.flow;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.eval.SpELTernaryParser;
import com.zhuofan.core.orm.dao.SysFlowDAO;
import com.zhuofan.core.orm.dao.SysFlowNodeDAO;
import com.zhuofan.core.orm.dao.SysTableColumnDAO;
import com.zhuofan.core.orm.dao.SysTableDAO;
import com.zhuofan.core.orm.model.SysFlow;
import com.zhuofan.core.orm.model.SysFlowNode;
import com.zhuofan.core.orm.model.SysTable;
import com.zhuofan.core.orm.model.SysTableColumn;
import com.zhuofan.core.queryreport.QueryReport;
import com.zhuofan.core.queryreport.QueryReportFilter;
import com.zhuofan.core.queryreport.QueryReportOutPut;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlowDataList {
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

    public FlowDataList(HttpServletRequest request,SysConfig sysConfig,DatasourceConfig datasourceConfig){
        this.request=request;
        this.sysConfig=sysConfig;
        this.datasourceConfig=datasourceConfig;
    }

    public QueryReportOutPut execute (String dataname,boolean flagModify) throws  Exception {
        return execute(dataname,flagModify,null);
    }
    /**
     * @Title: sysFlowNodeList
     * @Description:系统流程节点管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut execute (String dataname,boolean flagModify,String flowNodeId) throws  Exception {
        SysFlowDAO sysFlowDAO=new SysFlowDAO();
        sysFlowDAO.setWhere("FLOW_CODE='"+dataname+"'");
        SysFlow sysFlow=(SysFlow) sysFlowDAO.queryFirstRow(new SysFlow());

        if (sysFlow==null){
            return null;
        }

        SysTable sysTable=(SysTable) new SysTableDAO().queryById(new SysTable(),sysFlow.getTableId());
        if (sysTable==null){
            return null;
        }

        try(JdbcDao jdbcDao=new JdbcDao()) {
            QueryReport queryReport = new QueryReport(sysFlow.getFlowName(), request, sysConfig, datasourceConfig);

            //获取第1个节点：
            Map<String, String> mapFirstNode = jdbcDao.getFirstRow(
                    "select flow_node_id,grants from sys_flow_node where flow_id='" + sysFlow.getFlowId() + "' " +
                            "and nvl(is_childnode,'0')='0' order by sortindex");
            String firstNodeId = mapFirstNode.get("FLOW_NODE_ID");
            String firstNodeGrant = mapFirstNode.get("GRANTS");

            //-1是草稿
            String flagReturn = CommonFunc.getNotNullString(CommonFunc.getHttpVar("flag_return"));
            String flagAfter = CommonFunc.getNotNullString(CommonFunc.getHttpVar("flag_after"));
            if (StringUtils.isEmpty(flagReturn)) {
                flagReturn = "0,1";
            } else {
                if (!"1".equals(flagReturn)) {
                    flagReturn = "0";
                }
            }

            queryReport.setTable(String.format(
                    "%s a left join sys_flow_instance b on a.id=b.data_id and b.flow_id='%s'"
                            + " left join sys_flow_node c on b.flow_node_id=c.flow_node_id "
                            + " left join sys_users u on u.USERNAME=replace(b.SENDTO_USER,',','')",
                    sysTable.getTableCode(), sysFlow.getFlowId()));
            queryReport.setSelectfldn("a.*,b.DEPARTMENTID,b.flow_instance_id,b.sendto_user as sendto_user_instance," +
                    "b.data_id,c.flow_node_id, '" + flowNodeId + "' as FLOW_NODE_ID_EXEC," +
                    "(select max(sendto_user) from sys_flow_exec e " +
                    "where e.FLOW_INSTANCE_ID=b.flow_instance_id and E.EXEC_ACTION='10' " +
                    "and e.FLOW_NODE_ID='" + flowNodeId + "' ) as sendto_user," +
                    "case when b.status='-1' then '草稿' when b.flow_node_id='9999' then '已归档' " +
                    "else c.node_name end node_name");

            queryReport.setWhere_ex("1=1");
            String nodeWhereEx = "";

            SysFlowNode sysFlowNode = new SysFlowNodeDAO().queryById(new SysFlowNode(), flowNodeId);

            /////////////////////////////////////////
            //sys_flow_node的where_ex处理
            if (sysFlowNode != null && StringUtils.isNotEmpty(sysFlowNode.getWhereEx())) {
                try {
                    Map<String, Object> variables = new HashMap<>();
                    nodeWhereEx = (String) SpELTernaryParser.evaluateWithSpEL(sysFlowNode.getWhereEx(), variables);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //sys_flow_node的where_ex处理结束
            /////////////////////////////////////////

        /*
        if ("9999".equals(flowNodeId)){
            queryReport.setWhere_ex("1=1");
        }else {
            queryReport.setWhere_ex("1=1 and (nvl(b.sendto_user,'')='' or b.sendto_user like '%" + Eval.getLoginName() + ",%')");
        }
        */

            String afterNodeids = "";

            if (StringUtils.isNotEmpty(flowNodeId)) {
                sysFlowNode = (SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), flowNodeId);
            }

            if (StringUtils.isNotEmpty(flagAfter)) {
                //获取当前节点以后的所有节点，但不包括9999
                String afterFlowNodeId = flowNodeId;
                if (sysFlowNode.getIsChildnode().equals("1")) {
                    afterFlowNodeId = sysFlowNode.getParentNodeId();
                }

                ArrayList<HashMap<String, String>> listAfterNodes = jdbcDao.getResult(
                        String.format("select flow_node_id from sys_flow_node where flow_id='%s' " +
                                "and nvl(is_childnode,'0')='0' order by sortindex", sysFlow.getFlowId()));
                boolean flag = false;
                for (int i = 0; i < listAfterNodes.size(); i++) {
                    String tmpNodeid = listAfterNodes.get(i).get("FLOW_NODE_ID");
                    if (tmpNodeid.equals(afterFlowNodeId)) {
                        flag = true;
                        continue;
                    }
                    if (flag) {
                        afterNodeids = (StringUtils.isEmpty(afterNodeids)) ? "'" + tmpNodeid + "'" : afterNodeids + ",'" + tmpNodeid + "'";
                    }
                }
                if (StringUtils.isEmpty(afterNodeids)) {
                    afterNodeids = "''";
                }
            } else {
                queryReport.setWhere_ex(queryReport.getWhere_ex()
                        + " and nvl(b.flag_return,'0') in (" + flagReturn + ")");
                afterNodeids = "'" + flowNodeId + "'";
            }

            HashMap<String, Object> mapColumn = FlowService.getTableColumn(sysTable.getTableId());
            if (mapColumn.containsKey("pkField")) {
                SysTableColumn sysTableColumn = (SysTableColumn) mapColumn.get("pkField");
                queryReport.setFldnPK(sysTableColumn.getColumnCode());
                if (StringUtils.isNotEmpty(flowNodeId)) {
                    //2025-11-28:注释掉|| (!sysFlowNode.getIsChildnode().equals("1"))
                    //fxd
                    if (((flagAfter.equals("1") || sysFlowNode == null || !sysFlowNode.getIsChildnode().equals("1")))
                            && (StringUtils.isEmpty(nodeWhereEx))) {
                        queryReport.setWhere_ex(queryReport.getWhere_ex() +
                                String.format(" and b.flow_node_id in (%s)",
                                        afterNodeids));
                    } else {
                        queryReport.setWhere_ex(queryReport.getWhere_ex() + String.format(
                                " and id in (select data_id from sys_flow_instance " +
                                        "where flow_instance_id in ( select flow_instance_id from sys_flow_exec ne " +
                                        "where ne.flow_id='%s' and ne.flow_node_id in (%s) and ne.exec_action='10' %s) " +
                                        "and nvl(flag_return,'0')='%s')",
                                sysFlow.getFlowId(), afterNodeids, nodeWhereEx, flagReturn));
                    }
                }
            }

            String extraField = CommonFunc.getHttpVar("extra_field");
            if (StringUtils.isNotEmpty(extraField)) {
                String extraValue = CommonFunc.getHttpVar("extra_value");
                if (StringUtils.isNotEmpty(extraValue)) {
                    queryReport.setWhere_ex(String.format(" %s='%s'", extraField, extraValue));
                }
            }

            ///////////////////////////////////////
            //调度编号图：变电巡维登录，且变电确认节点，特殊处理
            if ("10000806".equals(flowNodeId)) {
                String loginName = Eval.getLoginName();
                if (loginName.indexOf("巡维") > 0) {
                    String whereXw = "a.DEPARTMENTS='" + loginName + "'";
                    queryReport.setWhere_ex(queryReport.getWhere_ex() + " and " + whereXw);
                }
            }

            ///////////////////////////////////////////
            //核心场所根据起单的

            //调度编号图：变电巡维登录，且变电确认节点，特殊处理
            ///////////////////////////////////////

            //自动化缺陷管理：变电处理节点10005103，特殊处理，只看当前部门的
            if ("10005103".equals(flowNodeId) && ("34645749".equals(Eval.getLoginDepartmentId())
                    || "34645751".equals(Eval.getLoginDepartmentId()) || "34645753".equals(Eval.getLoginDepartmentId()))) {
                String loginDeptId = Eval.getLoginDepartmentId();
                String whereDept = "(u.DEPARTMENTID='" + loginDeptId + "')";
                queryReport.setWhere_ex(queryReport.getWhere_ex() + " and " + whereDept);
            }
            //自动化缺陷管理：变电处理节点10005103，特殊处理，只看当前部门的
            ///////////////////////////////////////

            //用印申请：归档节点40289327，特殊处理，只允许袁思婷查看所有人的已完成流程
            if ("40289327".equals(sysFlow.getFlowId()) && "9999".equals(flowNodeId)){
                String UserName = Eval.getLoginName();
                if (!"袁思婷".equals(UserName)){
                    String where = "(u.JBR='" + UserName + "')";
                    queryReport.setWhere_ex(queryReport.getWhere_ex() + " and " + where);
                }
                System.out.print(UserName);
            }
        //用印申请：归档节点40289327，特殊处理，只允许袁思婷查看所有人的已完成流程

            if (!StringUtils.isEmpty(sysFlow.getListOrderby())) {
                queryReport.setOrderfldn(sysFlow.getListOrderby());
            }
            SysTableColumnDAO sysTableColumnDAO = new SysTableColumnDAO();

            sysTableColumnDAO.setWhere("table_id='" + sysTable.getTableId() + "' and FLAG_SHOW='1'");
            sysTableColumnDAO.setOrder("SHOW_SORTINDEX");
            ArrayList<SysTableColumn> listColumnShow = sysTableColumnDAO.query(new SysTableColumn());

            if (listColumnShow != null) {
                for (int i = 0; i < listColumnShow.size(); i++) {
                    if ("5".equals(listColumnShow.get(i).getInputType())) {
                        queryReport.addColItem(listColumnShow.get(i).getColumnName(), listColumnShow.get(i).getColumnCode(),
                                Eval.getHashMapValueFromStr(listColumnShow.get(i).getMaplist(), listColumnShow.get(i).getColumnCode()));
                    } else {
                        queryReport.addColItem(listColumnShow.get(i).getColumnName(), listColumnShow.get(i).getColumnCode());
                    }
                }
            }

            queryReport.addColItem("当前节点", "NODE_NAME");
            queryReport.addColItem("当前处理人", "SENDTO_USER_INSTANCE");

            if ("10001406".equals(flowNodeId)) {
                queryReport.addColItem("自动化执行情况", "SENDTO_USER",
                        Eval.EVAL + " return com.zhuofan.oms.service.FlowServiceExtra.listExtra" + flowNodeId + "(mapRow);");
            } else if ("10001405".equals(flowNodeId)) {
                queryReport.addColItem("厂站执行情况", "SENDTO_USER",
                        Eval.EVAL + " return com.zhuofan.oms.service.FlowServiceExtra.listExtra" + flowNodeId + "(mapRow);");
            }

            sysTableColumnDAO.setWhere("table_id='" + sysTable.getTableId() + "' and FLAG_FILTER='1'");
            sysTableColumnDAO.setOrder("FILTER_SORTINDEX");
            ArrayList<SysTableColumn> listColumnFilter = sysTableColumnDAO.query(new SysTableColumn());

            if (listColumnFilter != null) {
                //获取流程所有节点的信息
                String node_sql = "select * from sys_flow_node where flow_id='" + sysFlow.getFlowId() +
                        "' and (IS_CHILDNODE is null or IS_CHILDNODE<>'1')";
                ArrayList<HashMap<String, String>> node = jdbcDao.getResult(node_sql);
                String node_id_name = "";
                String node_id = "";
                for (int i = 0; i < node.size(); i++) {
                    node_id += node.get(i).get("FLOW_NODE_ID") + ",";
                    node_id_name += node.get(i).get("FLOW_NODE_ID") + ":" + node.get(i).get("NODE_NAME") + ";";
                }
                node_id = node_id.substring(0, node_id.length() - 1) + ",9999:全部;";
                node_id_name = node_id + node_id_name;

                queryReport.addQuerySet("节点", "file_flow_node_id", "nvl(b.flow_node_id,'')", QueryReportFilter.QRFilterType.selarray,
                        "", QueryReportFilter.QRFilterOper.in, "", "",
                        node_id_name, "");

//            queryReport.addQuerySet("节点","flow_node_id","", QueryReportFilter.QRFilterType.selarray,
//                    "", QueryReportFilter.QRFilterOper.in,"","", node_id_name,"");


                for (int i = 0; i < listColumnFilter.size(); i++) {
                    String inputType = listColumnFilter.get(i).getInputType();
                    if (inputType.equals("3") || inputType.equals("4")) {
                        //时间
                        queryReport.addQuerySet(listColumnFilter.get(i).getColumnName() + "(起)", listColumnFilter.get(i).getColumnCode() + "_F",
                                listColumnFilter.get(i).getColumnCode(), QueryReportFilter.QRFilterType.text, "",
                                QueryReportFilter.QRFilterOper.greaterEqual);
                        queryReport.addQuerySet(listColumnFilter.get(i).getColumnName() + "(止)", listColumnFilter.get(i).getColumnCode() + "_T",
                                listColumnFilter.get(i).getColumnCode(), QueryReportFilter.QRFilterType.text, "",
                                QueryReportFilter.QRFilterOper.lessEqual);
                    } else if (inputType.equals("5")) {
                        //key-value
                        String maplist = ":全部;" + listColumnFilter.get(i).getMaplist().replace("=>", ":");

                        queryReport.addQuerySet(listColumnFilter.get(i).getColumnName(), listColumnFilter.get(i).getColumnCode(),
                                listColumnFilter.get(i).getColumnCode(), QueryReportFilter.QRFilterType.selarray, "",
                                QueryReportFilter.QRFilterOper.equal, "", "", maplist, "");
                    } else if (inputType.equals("6")) {
                        //sqldata
                        queryReport.addQuerySet(listColumnFilter.get(i).getColumnName(), listColumnFilter.get(i).getColumnCode(),
                                listColumnFilter.get(i).getColumnCode(), QueryReportFilter.QRFilterType.selarray, "",
                                QueryReportFilter.QRFilterOper.equal);
                    } else {
                        queryReport.addQuerySet(listColumnFilter.get(i).getColumnName(), listColumnFilter.get(i).getColumnCode(),
                                listColumnFilter.get(i).getColumnCode());
                    }
                }
            }

            if (flagModify) {
                //2025-05-27:给邓景柱写死，开特权中可以修改
                if ("邓景柱".equals(Eval.getLoginName())) {
                    if ("ZDHQXCLJL".equals(dataname)) {
                        queryReport.addActionItem("",
                                Eval.getEditUrl("W_ZDHQXCLJL", "group_code=all", "ID"),
                                "修改当前数据", "text", "修改", "");
                    }
                }


                if (FlowService.checkFlowNodeGrant(Eval.getLoginUserId(), Eval.getLoginDepartmentId(), firstNodeGrant)) {
                    //第一个节点才有删除功能和启动流程功能
                    if (flowNodeId.equals(firstNodeId)) {
                        queryReport.setOther("<button type=button class='btn btn-primary' onclick=\"javascript:createNewFlow('" + dataname + "');\">启动新流程</button>");
                    }
                    if (dataname.equals("ZDHCZDZZDGLLC")) {
                        if (!flagAfter.equals("1")) {
                            if (flowNodeId.equals("10001401")) {
                                queryReport.addActionItem("",
                                        Eval.getDeleteUrl("SYS_FLOW_INSTANCE", "FLOW_INSTANCE_ID",
                                                sysTable.getTableCode(),"ID"),
                                        "删除当前数据", "text", "删除", "");
                            }
                        }
                    } else {
                        queryReport.addActionItem("",
                                Eval.getDeleteUrl("SYS_FLOW_INSTANCE",
                                        "FLOW_INSTANCE_ID",sysTable.getTableCode(),"ID"),
                                "删除当前数据", "text", "删除", "");
                    }


                    queryReport.addActionItem("", Eval.getOpenUrlAction("../omsserver/Flow/Start?",
                            "flow_instance_id", "FLOW_INSTANCE_ID", "", ""),
                            "复制前数据到草稿箱", "text", "复制到草稿", "");
                    //用印申请特殊修改
                    if(dataname.equals("YYSQ")){
                        queryReport.addActionItem("", Eval.getOpenUrlAction("../app/flowform.html?flow_code=YYSQ&",
                                        "data_id", "ID", "", ""),
                                "编辑", "text", "编辑", "");
                    }
                    //运行领域需求升级
                    if (dataname.equals("YXLYXQ")) {
                        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zdh/print_scbd.html?", "id", "DATA_ID", "", "")
                                , "生成WORD", "text", "生成WORD", "");
                    }
                    //系统运行领域功能配置变化
                    if (dataname.equals("YXLYBH")) {
                        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zdh/print_pzbhd.html?", "id", "DATA_ID", "", "")
                                , "生成WORD", "text", "生成WORD", "");
                    }
                }

            }

            queryReport.execQuery();

            QueryReportOutPut queryReportOutPut = queryReport.getOutPut();
            queryReportOutPut.setFlowTableCode(sysTable.getTableCode());

            jdbcDao.close();
            return queryReportOutPut;
        }
    }
}
