package com.zhuofan.oms.controller.sys;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.eval.SpELTernaryParser;
import com.zhuofan.core.flow.FlowDataList;
import com.zhuofan.core.flow.FlowService;
import com.zhuofan.core.orm.dao.SysFlowDAO;
import com.zhuofan.core.orm.dao.SysFlowNodeDAO;
import com.zhuofan.core.orm.model.SysFlow;
import com.zhuofan.core.orm.model.SysFlowNode;
import com.zhuofan.core.queryreport.QueryReportOutPut;
import com.zhuofan.core.sys.SysDataList;
import com.zhuofan.core.util.ExcelUtil;
import com.zhuofan.oms.service.BusDataList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 */
@Slf4j
@RestController
@CrossOrigin
public class DataListController extends BaseController{

    //流程列表的配置
    public final static String FLOW_DATALIST_EXTRA="{\"DDBHGL\":{\"fieldName\":\"state_enable\",\"filter\":[{\"value\":\"待执行\",\"text\":\"待执行\"},{\"value\":\"执行中\",\"text\":\"执行中\"},{\"value\":\"历史\",\"text\":\"历史\"}]},\"ZDHCZDZZDGLLC\":{\"fieldName\":\"f_ZT\",\"filter\":[{\"value\":\"执行中\",\"text\":\"执行中\"},{\"value\":\"历史\",\"text\":\"历史\"}]}}";


    //配置型查询列表对应关系
    public final static Map<String, String> MAP_CONF_DATALIST = new HashMap<String, String>() {{
        put("PW_CHZ_DZPZ", "pwChzDzpz");
        put("PW_CHZ_TTJL", "pwChzTtjl");
        put("PW_CHZ", "pwChz");
        put("PWGJ_REPLACE", "pwgjReplace");
        put("PWGJ_CHZ_EDIT", "pwChzEdit");
        put("PWGJ_GUAPAI_DDZY","pwgjGuapaiDdzy");

        put("PW_DEADNUM_LINE", "pwDeadnumLine");
        put("PW_NOTIFY_DEADNUM_LINE", "pwNotifyDeadnumLine");
        put("PW_DEADNUM_LINE_MARK", "pwDeadnumLineMark");
        put("PW_DEADNUM_LINE_SET", "pwDeadnumLineSet");
        put("V_ALARM_YCEVENTLOG", "vAlarmYceventlog");
        put("PWGJ_API_LOG", "pwgjApiLog");
        put("PW_DEADNUM_LINE_HIS","pwDeadnumLineHis");
        put("V_ALARM_YCEVENTLOG_CHZ","vAlarmYceventlogChz");
        put("PWGJ_CHZ_SET", "pwgjChzSet");

        put("PWGJ_FEEDER", "pwgjFeeder");
        put("PWGJ_FEEDER_DEADSET", "pwgjFeederDeadset");
        put("PWGJ_MESSAGE", "pwgjMessage");
        put("PW_KXZGZ_NEW","pwKxzgzNew");

        put("ZWDKXWSB_YXFS","wZdhjxjh");
        put("ZWDKXWSB_BHTT","tblBhParts");
        put("ZWDKXWSB_BHDZ","wZdjslc");
        put("ZWDKXWSB_AZDZ", "wAzzzdzzddb");
        put("ZWDKXWSB_AZTT","omsAsdwork");
        put("ZWDKXWSB_XSBQD","wBwtjshlcXsbtcsqd");
        put("ZWDKXWSB_FXKZ","wZdhfxgl");
        put("ZWDKXWSB_CZXL","operationticket");
        put("ZWDKXWSB_CZXK","permissiongd");
        put("ZWDKXWSB_JXWKG","tblShenQing");
        put("ZWDKXWSB_DDRZ","tRunlogLogBdsbtzDd");
    }};

    //系统查询列表对应关系
    public final static Map<String, String> MAP_SYSDATALIST = new HashMap<String, String>() {{
        put("errlog_pass", "passErrlogList");
        put("errlog_sys", "syslogList");
        put("sys_fankui", "sysFankuiList");
        put("sys_module", "sysModuleList");
        put("sys_notify", "sysNotifyList");
        put("sys_report", "sysReportList");
        put("sys_require", "sysRequireList");
        put("sys_role", "sysRoleList");
        put("sys_table", "sysTableList");
        put("sys_table_class", "sysTableClassList");
        put("sys_user", "sysUserList");
        put("sys_dept", "sysDeptList");
        put("sys_table_group", "sysTableGroupList");
        put("sys_table_column", "sysTableColumnList");
        put("sys_flow","sysFlowList");
        put("sys_flow_node","sysFlowNodeList");

        put("FLOW_NODE_EXEC","FLOW_NODE_EXEC");
    }};

    //业务查询列表对应关系
    public final static Map<String, String> MAP_FLOWDATALIST = new HashMap<String, String>() {{
        put("DDBHGL", "");
		put("ZDHQXCLJL","");
		put("ZDHYSHZ","");
        put("SDSBXL110KV","");
        put("KVSDSBXL","");
        put("AVCCLGLEPC","");
        put("ZDDMKZ","");
        put("SDXLXL500","");
        put("ZB10QFJXJDLHHXZFHKZZ","");
        put("ZBDMKZ","");
        put("ASFPGL","");
        put("AZCLKZGL","");
        put("MXZCYXFS","");
        put("XLZHZJXD","");
        put("ZDHCZDZZDGLLC","");
        put("ZDHXTGPCZLC","");
        put("DDJGZZDGZPGL","");
        put("ZDH_DEFECT","");
        put("CTDZDLB","");
        put("YXLYXQ","");
        put("YXLYBH","");
        put("JXDMKZ","");
        put("ZDHGZLLD","");
        put("KXKZGL","");
        put("GJXHHT","");
        put("HXCSSQRYXX","");
        put("HXCSQXSQ_SQNR","");
        put("HXCSQXSQ","");
        put("YYSQ","");
    }};

    //业务查询流程表对应关系(只读流程)
    public final static Map<String, String> MAP_FLOWDATALIST_READ = new HashMap<String, String>() {{
        put("AZZZDZQKBS","");
        put("AZZZTTGL_JBNR","");
        put("AZZZYCJQXBSGL","");
        put("BXZZJRLC","");
        put("BXZZJXLC","");
        put("JBDZSQD","");
        put("JBLSDZD","");
        put("LBQCJZZJXBBLC","");
        put("TSDLXXSBGL","");
        put("ZDHXTGZYCGLEPC","");
        put("CTDZDLB","");
        put("ZDHXQGL","");
        put("AZZZTYSP","");
    }};

    //非流程类业务查询表
    public final static Map<String, String> MAP_BUSDATALIST = new HashMap<String, String>() {{
        put("TRACE_LIST", "traceList");
        put("TPZZ_SITE_LIST","tpzzSiteList");
        put("TOP_LINE_LIST","topLineList");
        put("TRACE_ITEM_LIST","traceItemList");
        put("TRACE_ITEM_LIST_LINE","traceItemListLine");
        put("OMS_JZB","OMS_JZB");
        put("GJXHXXHL_SETTING","GJXHXXHL_SETTING");
        put("GJXHHT","GJXHHT");
        put("HXCSSQRYXX","HXCSSQRYXX");
        put("HXCSQXSQ_SQNR","HXCSQXSQ_SQNR");
		put("OMS_XMXXGL","OMS_XMXXGL");
        put("ZDH_INSPECT","ZDH_INSPECT");

        put("zdh_logview","zdhLgview");

        put("V_SCADA_DATA_DM","vScadaDataDm");
        put("V_SCADA_DATA_ZBBD","vScadaDataZbbd");
        put("V_SCADA_DATA_LINE110","vScadaDataLine110");
        put("V_SCADA_DATA_LINE220","vScadaDataLine220");
        put("V_SCADA_DATA_LINE500","vScadaDataLine500");
        put("OMS_FHDL","omsFhdl");
        put("OMS_RISK","omsRisk");
        put("OMS_ZBBD_CEWEN","omsZbbdCewen");

        put("OMS_SUBSTATION","OMS_SUBSTATION");
        put("OMS_FESTA","OMS_FESTA");

        put("ASXMXX","ASXMXX");
        put("FPFHGL","FPFHGL");
        put("JHZFZJ","JHZFZJ");

        put("ALARM_INFO","ALARM_INFO");
        put("ALARM_PHONE_LIST","ALARM_PHONE_LIST");
        put("ZWGJ_WXGD_INFO","ZWGJ_WXGD_INFO");

        //运方部门业务
        put("YF_GFFH_PROJ","YF_GFFH_PROJ");
        put("YF_JZB","YF_JZB");
        put("YF_GFFH_BDZ500","YF_GFFH_BDZ500");
        put("YF_GFFH_BDZ220","YF_GFFH_BDZ220");
        put("YF_GFFH_BD220","YF_GFFH_BD220");
        put("YF_GFFH_BDZ110","YF_GFFH_BDZ110");
        put("YF_GFFH_DLG10","YF_GFFH_DLG10");
        put("YF_GFFH_XL220","YF_GFFH_XL220");
        put("YF_GFFH_XL110","YF_GFFH_XL110");
        put("YF_GFFH_DCCL","YF_GFFH_DCCL");
        put("YF_GFFH_ZJRZB","YF_GFFH_ZJRZB");

        // 计划任务 规划
        put("WORK_UNIT", "WORK_UNIT");
        put("TRANSFORMER_SUBSTATION","TRANSFORMER_SUBSTATION");
        put("CREW","CREW");
        put("CREW_MEMBER", "CREW_MEMBER");
        put("SECOND_PLAN_RECORD", "SECOND_PLAN_RECORD");
        put("WARN_INFO_PEER_WEEK", "WARN_INFO_PEER_WEEK");

        //调控中心 职创
        put("OMS_DUTY", "OMS_DUTY");

        //自动化厂站信息管理
        put("CZXX_DDBHGL", "CZXX_DDBHGL");
        put("CZXX_ZDHCZDZ", "CZXX_ZDHCZDZ");

        //基准表：500开关
        put("OMS_JZB_BRK500", "omsJzbBrk500");

        //母线电压
        put("OMS_BUSBAR", "OMS_BUSBAR");
        //500kV开关
        put("OMS_BRK500", "OMS_BRK500");
        //10kV开关柜
        put("OMS_KGB10", "OMS_KGB10");
        //自动化智能告警
        put("ZDHGJ_ALL", "ZDHGJ_ALL");
        //配网智能告警
        put("PWGJ_ALL", "PWGJ_ALL");
        //配网重过载
        put("PW_KXZGZ", "PW_KXZGZ");
        //配网重合闸
        put("PW_CHZ", "PW_CHZ");
        //配网通知联络人信息
        put("PWGJ_PHONE_LIST", "PWGJ_PHONE_LIST");
        //安升年度投资计划
        put("W_ASNDTZJH", "W_ASNDTZJH");
        //主网调控行为识别-检修计划
        put("ZWDKXWSB_JXJH", "ZWDKXWSB_JXJH");
        //主网调控行为识别-运行方式
        put("ZWDKXWSB_YXFS", "ZWDKXWSB_YXFS");
//        put("ZWDKXWSB_BHTT", "ZWDKXWSB_BHTT");

        put("ALARM_YXEVENTLOG","yxEventLog");
        put("ALARM_YKEVENTLOG","ykEventLog");
        put("ALARM_SWITCH_LOG","alarmSwitchLog");
        put("ALARM_SWITCH_CHECK","alarmSwitchCheck");
        put("ALARM_DISCONNECTOR_STATISTIC","alarmDisconnectorStatistic");
        put("SCADA_INVARIANT_VALUE_WATCH", "scadaInvariantValueWatch");
        put("ALARM_FREQUENT_RECORD", "alarmFrequencyRecord");
        put("ALARM_FREQUENT_DETAIL_RECORD", "alarmFrequentDetailRecord");
        put("ALARM_AVC_RECORD", "alarmAvcRecord");
        put("ALARM_WITHOUT_RESET_RECORD", "alarmWithoutResetRecord");
        put("WORK_PLAN_SEND_MESSAGE_RECORD", "workPlanSendMessageRecord");
        put("OMS_JZB_FHYC", "OMS_JZB_FHYC");
        put("HXCSQXSQ_SQNR", "HXCSQXSQ_SQNR");
        put("OMS_FHYC_DPS", "OMS_FHYC_DPS");
        put("CREW_WORK_LOAD_RATE_DAY_RECORD", "CREW_WORK_LOAD_RATE_DAY_RECORD");
        put("WORK_PLAN_LOAD_RATIO", "WORK_PLAN_LOAD_RATIO");
    }};


    /*
     * 数据列表查询请求
     * 参数:无
     * 返回数据:
     *{
			result:0/1,
			data:list
		};
     */
    @RequestMapping(value = "/Data/List",method = RequestMethod.GET)
    public Map<String, Object> UserModule(@RequestParam Map<String,String> params) throws Exception {
        String sysTitle=sysConfig.getTitle();
        String flowNodeId=CommonFunc.getNotNullString(CommonFunc.getHttpVar("flow_node_id"));

        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("showFlowNode","0");
        mapData.put("processStartTime",CommonFunc.unix2Date(CommonFunc.unixTimeStr()));

        //1.检查是否登录
//        if (!CommonFunc.checkIsLogined(request)){
//            log.error("未登录: {}", request.getRequestURI());
//            return CommonFunc.makeErrResult("300099");
//        }

        //2.必须有dataname
        String dataname=params.get("dataname");
        if (dataname.isEmpty()){
            return CommonFunc.makeErrResult("400001");
        }

        //3.检查dataname是否存在
        QueryReportOutPut queryReportOutPut=null;
        //2025-06-19:此处增加配置查询列表兼容
        //配置路径：query/xxx.json,xxx与MAP_CONF_DATALIST中的value值对应
        if (MAP_CONF_DATALIST.containsKey(dataname)){
            BusDataList busDataList=new BusDataList(request,sysConfig,datasourceConfig);

            //此处调用通用的简易查询
            String methodName=MAP_CONF_DATALIST.get(dataname).trim();
            queryReportOutPut=busDataList.CommonSimpleList(dataname,methodName);

            if (queryReportOutPut==null){
                return CommonFunc.makeErrResult("400001");
            }

        }else if (MAP_BUSDATALIST.containsKey(dataname)){
            BusDataList busDataList=new BusDataList(request,sysConfig,datasourceConfig);
            if (dataname.equals("TRACE_LIST")) {
                queryReportOutPut = busDataList.traceList();
            }else if (dataname.equals("TPZZ_SITE_LIST")){
                queryReportOutPut=busDataList.tpzzSiteList();
            }else if (dataname.equals("TOP_LINE_LIST")){
                queryReportOutPut=busDataList.topLineList();
            } else if (dataname.equals("zdh_logview")){
                queryReportOutPut=busDataList.zdhLogview();
            }else if (dataname.equals("OMS_JZB")){
            queryReportOutPut=busDataList.omsJzbList();
             }else if (dataname.equals("OMS_XMXXGL")){
                queryReportOutPut=busDataList.omsXmxxglList();
            }else if (dataname.equals("ZDH_INSPECT")){
                queryReportOutPut=busDataList.ZdhInspectList();
            }else if (dataname.equals("V_SCADA_DATA_DM")){
                queryReportOutPut=busDataList.scadaDataDm();
            }else if (dataname.equals("V_SCADA_DATA_ZBBD")){
                queryReportOutPut=busDataList.scadaDataZbbd();
            }else if (dataname.equals("V_SCADA_DATA_LINE110")){
                queryReportOutPut=busDataList.scadaData110();
            }else if (dataname.equals("V_SCADA_DATA_LINE220")){
                queryReportOutPut=busDataList.scadaData220();
            }else if (dataname.equals("V_SCADA_DATA_LINE500")){
                queryReportOutPut=busDataList.scadaData500();
            }else if (dataname.equals("OMS_FHDL")){
                queryReportOutPut=busDataList.omsFhdl();
            }else if (dataname.equals("OMS_RISK")){
                queryReportOutPut=busDataList.omsRisk();
            }else if (dataname.equals("OMS_SUBSTATION")){
                queryReportOutPut=busDataList.omsSubstation();
            }else if (dataname.equals("ASXMXX")){
                queryReportOutPut=busDataList.asxmxx();
            }else if (dataname.equals("FPFHGL")){
                queryReportOutPut=busDataList.fpfhgl();
            }else if (dataname.equals("JHZFZJ")){
                queryReportOutPut=busDataList.jhzfzj();
            }else if (dataname.equals("ALARM_INFO")){
                queryReportOutPut=busDataList.zwgjList();
            }else if (dataname.equals("ALARM_PHONE_LIST")){
                queryReportOutPut=busDataList.alarmCallPhone();
            }else if (dataname.equals("ZWGJ_WXGD_INFO")){
                queryReportOutPut=busDataList.zwgjYwgdList();
            }else if (dataname.equals("YF_JZB")){
                //运方基准表
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_BD220")){
                //运方220KV变低
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_DCCL")){
                //运方电厂出力
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_BDZ110")){
                //运方110KV变电站
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_DLG10")){
                //运方10KV大电流柜
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_XL220")){
                //运方220KV线路
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_XL110")){
                //运方110KV线路
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_ZJRZB")){
                //运方镇街容载比
                queryReportOutPut=busDataList.yfJzbList();
            }else if (dataname.equals("YF_GFFH_PROJ")){
                //运方镇街容载比
                queryReportOutPut=busDataList.yfGffhProj();
            }else if (dataname.equals("OMS_FESTA")){
                //运方镇街容载比
                queryReportOutPut=busDataList.omsFesta();
            }else if (dataname.equals("OMS_ZBBD_CEWEN")){
                //运方镇街容载比
                queryReportOutPut=busDataList.omsZbbdCewen();
            } else if ("WORK_UNIT".equals(dataname)) {
                queryReportOutPut=busDataList.workUnit();
            } else if ("TRANSFORMER_SUBSTATION".equals(dataname)) {
                queryReportOutPut=busDataList.transformerSubstation();
            } else if ("CREW".equals(dataname)) {
                queryReportOutPut=busDataList.crew();
            } else if ("CREW_MEMBER".equals(dataname)) {
                queryReportOutPut=busDataList.crewMember();
            } else if ("SECOND_PLAN_RECORD".equals(dataname)) {
                queryReportOutPut=busDataList.secondPlanRecord();
            }else if ("OMS_DUTY".equals(dataname)) {
                queryReportOutPut=busDataList.zbryb();
            }else if (dataname.equals("CZXX_DDBHGL")){
                //厂站信息管理_调度编号图管理
                queryReportOutPut=busDataList.czxx_ddbhgl();
            }else if (dataname.equals("CZXX_ZDHCZDZ")){
                //厂站信息管理_自动化厂站定值
                queryReportOutPut=busDataList.czxx_zdhczdz();
            }else if (dataname.equals("OMS_BUSBAR")){
                //母线电压
                queryReportOutPut=busDataList.oms_jzb_busbar();
            }else if (dataname.equals("OMS_BRK500")){
                //500kV开关
                queryReportOutPut=busDataList.oms_brk500();
            }else if (dataname.equals("OMS_KGB10")){
                //10kV开关柜
                queryReportOutPut=busDataList.oms_kgb10();
            }else if (dataname.equals("ZDHGJ_ALL")){
                //自动化智能告警
                queryReportOutPut=busDataList.zdhgj_all();
            }else if (dataname.equals("PWGJ_ALL")){
                //配网智能告警
                queryReportOutPut=busDataList.pwgj_all();
            }else if (dataname.equals("PW_KXZGZ")){
                //配网馈线重过载
                queryReportOutPut=busDataList.pw_kxzgz();
            }else if (dataname.equals("PW_CHZ")){
                //配网重合闸
                queryReportOutPut=busDataList.pwChz();
            }else if (dataname.equals("PWGJ_PHONE_LIST")){
                    //配网重合闸
                    queryReportOutPut=busDataList.pw_phone_list();
            }else if (dataname.equals("W_ASNDTZJH")){
                //安生年度投资计划
                queryReportOutPut=busDataList.w_asndtzjh();
            }else if (dataname.equals("ZWDKXWSB_JXJH")) {
                //主网调控行为识别-检修计划
                queryReportOutPut=busDataList.zwdkxwsb_jxjh();
            }else if (dataname.equals("ZWDKXWSB_YXFS")) {
                //主网调控行为识别-运行方式
                queryReportOutPut=busDataList.zwdkxwsb_yxfs();
            } else if ("ALARM_YXEVENTLOG".equals(dataname)) {
                queryReportOutPut = busDataList.yxEventLog();
            } else if ("ALARM_YKEVENTLOG".equals(dataname)) {
                queryReportOutPut = busDataList.ykEventLog();
            } else if ("ALARM_SWITCH_LOG".equals(dataname)) {
                queryReportOutPut = busDataList.alarmSwitchLog();
            } else if ("ALARM_DISCONNECTOR_STATISTIC".equals(dataname)) {
                queryReportOutPut = busDataList.alarmDisconnectorStatistic();
            } else if ("ALARM_FREQUENT_RECORD".equals(dataname)) {
                queryReportOutPut = busDataList.alarmFrequentRecord();
            } else if ("ALARM_FREQUENT_DETAIL_RECORD".equals(dataname)) {
                queryReportOutPut = busDataList.alarmFrequentDetailRecord();
            } else if ("ALARM_AVC_RECORD".equals(dataname)) {
                queryReportOutPut = busDataList.alarmAvcRecord();
            } else if ("ALARM_WITHOUT_RESET_RECORD".equals(dataname)) {
                queryReportOutPut = busDataList.alarmWithoutResetRecord();
            } else if ("WORK_PLAN_SEND_MESSAGE_RECORD".equals(dataname)) {
                queryReportOutPut = busDataList.workPlanSendMessage();
            } else if ("OMS_JZB_FHYC".equals(dataname)) {
                queryReportOutPut = busDataList.zwddjzb_fhyc();
            } else if ("CREW_WORK_LOAD_RATE_DAY_RECORD".equals(dataname)) {
                queryReportOutPut=busDataList.crewWorkLoadRateDayRecord();
            } else if ("WORK_PLAN_LOAD_RATIO".equals(dataname)) {
                queryReportOutPut=busDataList.workPlanLoadRatio();
            } else if ("OMS_FHYC_DPS".equals(dataname)) {
                queryReportOutPut=busDataList.fhycDps();
            }else if ("GJXHXXHL_SETTING".equals(dataname)) {
                queryReportOutPut=busDataList.gjxhxxhl_setting();
            }else if ("GJXHHT".equals(dataname)) {
                queryReportOutPut=busDataList.gjxhht();
            }else if ("HXCSSQRYXX".equals(dataname)) {
                queryReportOutPut=busDataList.hxcssqryxx();
            }else if ("HXCSQXSQ_SQNR".equals(dataname)) {
                queryReportOutPut=busDataList.hxcssqryxx();
            }

        else{
                //此处调用通用的简易查询
                String methodName=MAP_BUSDATALIST.get(dataname).trim();
                queryReportOutPut=busDataList.CommonSimpleList(dataname,methodName);

                if (queryReportOutPut==null){
                    return CommonFunc.makeErrResult("400001");
                }
            }
        }else if (MAP_FLOWDATALIST.containsKey(dataname)) {
            String deptId=Eval.getLoginDepartmentId();
//            String flowNodeId=CommonFunc.getNotNullString(CommonFunc.getHttpVar("flow_node_id"));
            FlowDataList flowDataList=new FlowDataList(request,sysConfig,datasourceConfig);
            String status=CommonFunc.getNotNullString(CommonFunc.getHttpVar("status"));
            queryReportOutPut=flowDataList.execute(dataname,true,flowNodeId);

            //获取表名
            String tableCode=queryReportOutPut.getFlowTableCode();

            //获取流程的所有节点
            SysFlowDAO sysFlowDAO=new SysFlowDAO();
            sysFlowDAO.setWhere("flow_code='"+dataname+"'");
            //sysFlowDAO.setOrder("exec_time asc");
            SysFlow sysFlow=(SysFlow)  sysFlowDAO.queryFirstRow(new SysFlow());

            if (sysFlow!=null){
                mapData.replace("showFlowNode","1");

                ArrayList<SysFlowNode> listNode=FlowService.getAllFlowNodeAndChild(sysFlow.getFlowId());
                try(JdbcDao jdbcDao=new JdbcDao()) {
                    JSONArray jsonArray = new JSONArray();

                    //计算当前节点下的业务数量
                    SysFlowNode sysFlowNode = new SysFlowNode();
                    sysFlowNode.setFlowNodeId(FlowService.NODE_END);
                    sysFlowNode.setNodeName("已归档");
                    listNode.add(sysFlowNode);

                    for (int i = 0; i < listNode.size(); i++) {
                        JSONObject jsonItem = new JSONObject();
                        jsonItem.put("flowNodeId", listNode.get(i).getFlowNodeId());
                        jsonItem.put("nodeName", listNode.get(i).getNodeName());
                        jsonItem.put("nodeType", listNode.get(i).getNodeType());
                        jsonItem.put("isChildNode", listNode.get(i).getIsChildnode());
                        jsonItem.put("parentNodeId", listNode.get(i).getParentNodeId());
                        jsonItem.put("count", "0");
                        jsonItem.put("countReturn", "0");
                        jsonItem.put("countSend", "0");
                        jsonItem.put("countAll", "0");

                        String whereEx = "";
                        if (StringUtils.isNotEmpty(listNode.get(i).getWhereEx())) {
                            try {
                                Map<String, Object> variables = new HashMap<>();
                                whereEx = (String) SpELTernaryParser.evaluateWithSpEL(listNode.get(i).getWhereEx(), variables);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        String itemWhereEx = "";
                        String nodeType = CommonFunc.getNotNullString(listNode.get(i).getNodeType());
                        String nodeIdItem = listNode.get(i).getFlowNodeId();
                        if (nodeType.equals(FlowService.NODETYPE_MORE) || FlowService.NODE_END.equals(nodeIdItem)) {
                            itemWhereEx = String.format("b.flow_id='%s' and b.flow_node_id='%s'",
                                    sysFlow.getFlowId(), nodeIdItem);
                        } else {
                            if (StringUtils.isEmpty(whereEx)) {
                                itemWhereEx = String.format("b.flow_id='%s' and b.flow_node_id='%s'",
                                        sysFlow.getFlowId(), nodeIdItem);
                            } else {
                                itemWhereEx = String.format("b.flow_id='%s' and b.FLOW_INSTANCE_ID in " +
                                                "(select FLOW_INSTANCE_ID from sys_flow_exec ne " +
                                                "where ne.flow_node_id='%s' and ne.EXEC_ACTION='10' %s)",
                                        sysFlow.getFlowId(), nodeIdItem, whereEx);
                            }
                        }
                        String sqlTotal = String.format(
                                "SELECT sum(case when nvl(b.flag_return,'0')='0' then 1 else 0 end) mycount," +
                                        "sum(case when nvl(b.flag_return,'0')='1' then 1 else 0 end) mycount1 " +
                                        "FROM %s a left join sys_flow_instance b on a.id=b.data_id " +
                                        "left join sys_flow_node c on b.flow_node_id=c.flow_node_id " +
                                        "where %s",
                                queryReportOutPut.getFlowTableCode(), itemWhereEx);

                    /*
                    String sqlTotal = String.format("SELECT sum(case when nvl(b.flag_return,'0')='0' then 1 else 0 end) mycount," +
                                        "sum(case when nvl(b.flag_return,'0')='1' then 1 else 0 end) mycount1 " +
                                        "FROM %s a left join sys_flow_instance b on a.id=b.data_id " +
                                        "left join sys_flow_node c on b.flow_node_id=c.flow_node_id " +
                                        "where b.flow_id='%s'  and b.flow_node_id='%s' " +
                                    "and (nvl(b.sendto_user,'')='' or b.sendto_user like '%%%s,%%')",
                                queryReportOutPut.getFlowTableCode(), sysFlow.getFlowId(), listNode.get(i).getFlowNodeId(),Eval.getLoginName());

                     */
                        Map<String, String> mapTotal = jdbcDao.getFirstRow(sqlTotal);
                        if (listNode.get(i).getFlowNodeId().equals(FlowService.NODE_END)
                                || FlowService.checkFlowNodeGrant(Eval.getLoginUserId(), deptId, listNode.get(i).getGrants())
                                || listNode.get(i).getNodeType().equals(FlowService.NODETYPE_MORE)
                                || FlowService.NODETYPE_MORE.equals(("30"))) {

                            if (mapTotal.containsKey("MYCOUNT")) {
                                jsonItem.put("count", CommonFunc.strToDouble(mapTotal.get("MYCOUNT")));
                            }
                            if (mapTotal.containsKey("MYCOUNT1")) {
                                jsonItem.put("countReturn", CommonFunc.strToDouble(mapTotal.get("MYCOUNT1")));
                            }
                            jsonItem.put("countAll", CommonFunc.strToDouble(mapTotal.get("MYCOUNT"))
                                    + CommonFunc.strToDouble(mapTotal.get("MYCOUNT1")));
                        }
                        jsonItem.put("countOther", CommonFunc.strToDouble(mapTotal.get("MYCOUNT"))
                                + CommonFunc.strToDouble(mapTotal.get("MYCOUNT1")));

                        jsonArray.add(jsonItem);
                    }

                    double countAll = 0;
                    //计算非会签子节点的统计
                    for (int i = jsonArray.size() - 2; i >= 0; i--) {
                        JSONObject jsonItem = (JSONObject) jsonArray.get(i);
                        if (jsonItem.get("isChildNode").toString().equals("1")) {
                            continue;
                        }
                        jsonItem.put("countSend", countAll);
                        countAll += Double.valueOf(jsonItem.get("countOther").toString());

                    }
                    //计算会签子节点的统计
                    for (int i = jsonArray.size() - 2; i >= 0; i--) {
                        JSONObject jsonItem = (JSONObject) jsonArray.get(i);
                        if (!jsonItem.get("isChildNode").toString().equals("1")) {
                            continue;
                        }
                        for (int j = 0; j < jsonArray.size(); j++) {
                            JSONObject jsonTmp = (JSONObject) jsonArray.get(j);
                            System.out.println(jsonTmp.get("flowNodeId").toString() + ";" + jsonItem.get("parentNodeId").toString());
                            if (jsonTmp.get("flowNodeId").toString().equals(jsonItem.get("parentNodeId").toString())) {
                                jsonItem.put("countSend", jsonTmp.get("countSend"));
                            }
                        }

                        //需单独查询总数
                        String whereEx = "";
                        SysFlowNode sysFlowNodeTmp = new SysFlowNodeDAO().queryById(
                                new SysFlowNode(), (String) jsonItem.get("flowNodeId"));

                        if (sysFlowNodeTmp != null && StringUtils.isNotEmpty(sysFlowNodeTmp.getWhereEx())) {
                            try {
                                Map<String, Object> variables = new HashMap<>();
                                whereEx = (String) SpELTernaryParser.evaluateWithSpEL(sysFlowNodeTmp.getWhereEx(), variables);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        String sqlTotal = String.format("SELECT count(*) mycount FROM %s a where id in (" +
                                        "     select data_id from sys_flow_instance b where flow_instance_id in (" +
                                        "     select flow_instance_id from  sys_flow_exec ne " +
                                        "    where ne.flow_id='%s' and ne.flow_node_id='%s' " +
                                        "  and ne.exec_action='10' and nvl(flag_return,'0')='0') %s) ",
                                queryReportOutPut.getFlowTableCode(), sysFlow.getFlowId(), jsonItem.get("flowNodeId"),
                                whereEx);

                    /*
                    String sqlTotal = String.format("SELECT count(*) mycount FROM %s a where id in (" +
                                    "     select data_id from sys_flow_instance b where flow_instance_id in (" +
                                    "     select flow_instance_id from  sys_flow_exec d " +
                                    "    where d.flow_id='%s' and d.flow_node_id='%s' and d.exec_action='10' and nvl(flag_return,'0')='0') " +
                                    " and (nvl(b.sendto_user,'')='' or b.sendto_user like '%%%s,%%'))",
                            queryReportOutPut.getFlowTableCode(), sysFlow.getFlowId(), jsonItem.get("flowNodeId"),Eval.getLoginName());

                     */
                        jsonItem.put("count", jdbcDao.getFieldValue(sqlTotal, "MYCOUNT"));
                        jsonItem.put("countAll", Double.valueOf(jdbcDao.getFieldValue(sqlTotal, "MYCOUNT")) +
                                Double.valueOf(jsonItem.get("countReturn").toString()));
                    }

                    mapData.put("flowNodeId", flowNodeId);
                    mapData.put("flowNodeTotal", jsonArray);
                    mapData.put("loginUserID", Eval.getLoginUserId());
                    mapData.put("loginDepartmentId", Eval.getLoginDepartmentId());
                    //测试id
//                    mapData.put("loginUserID", "yuansiting@dg.gd.csg.cn");
//                    mapData.put("loginDepartmentId", "27361");
                    //获取扩展查询条件
                    JSONArray jsonArrFilterExtra = new JSONArray();
                    JSONObject jsonExtra = JSONObject.parseObject(FLOW_DATALIST_EXTRA);
                    if (jsonExtra.containsKey(dataname)) {
                        String fieldName = jsonExtra.getJSONObject(dataname).getString("fieldName");
                        JSONArray jsonArrFilter = jsonExtra.getJSONObject(dataname).getJSONArray("filter");
                        for (int xx = 0; xx < jsonArrFilter.size(); xx++) {
                            JSONObject jsonItem = (JSONObject) jsonArrFilter.get(xx);
                            JSONObject jsonFilter = new JSONObject();

                            String fieldValue = jsonItem.getString("value");
                            String showText = jsonItem.getString("text");

                            //查询数据
                            String sqlTotal = String.format("SELECT count(*) mycount FROM %s a left join sys_flow_instance b on " +
                                            "   a.id=b.data_id and b.flow_id='%s' where %s='%s' ",
                                    queryReportOutPut.getFlowTableCode(), sysFlow.getFlowId(), fieldName, fieldValue);

                            jsonFilter.put("fieldName", fieldName);
                            jsonFilter.put("fieldValue", fieldValue);
                            jsonFilter.put("showText", showText);
                            jsonFilter.put("extraValue", CommonFunc.getHttpVar("extra_value"));
                            jsonFilter.put("countAll", jdbcDao.getFieldValue(sqlTotal, "MYCOUNT"));

                            jsonArrFilterExtra.add(jsonFilter);

                        }
                    }

                    jdbcDao.close();
                    mapData.put("jsonArrFilterExtra", jsonArrFilterExtra);
                }
            }


        }else if (MAP_FLOWDATALIST_READ.containsKey(dataname)) {
            FlowDataList flowDataList=new FlowDataList(request,sysConfig,datasourceConfig);
            mapData.replace("showFlowNode","1");
            queryReportOutPut=flowDataList.execute(dataname,false);
        }else if (MAP_SYSDATALIST.containsKey(dataname)){
            SysDataList sysDataList=new SysDataList(request,sysConfig,datasourceConfig);
            try {
                if (dataname.equals("sys_user")) {
                    queryReportOutPut = sysDataList.sysUserList();
                }else if (dataname.equals("sys_role")){
                    queryReportOutPut = sysDataList.sysRoleList();
                }else if (dataname.equals("sys_dept")){
                    queryReportOutPut = sysDataList.sysDeptList();
                }else if (dataname.equals("sys_table")){
                    queryReportOutPut = sysDataList.sysTableList();
                }else if (dataname.equals("sys_table_class")){
                    queryReportOutPut = sysDataList.sysTableClassList();
                }else if (dataname.equals("sys_module")){
                    queryReportOutPut = sysDataList.sysModuleList();
                }else if (dataname.equals("sys_report")){
                    queryReportOutPut = sysDataList.sysReportList();
                }else if (dataname.equals("errlog_pass")){
                    queryReportOutPut = sysDataList.passErrlogList();
                }else if (dataname.equals("errlog_sys")){
                    queryReportOutPut = sysDataList.syslogList();
                }else if (dataname.equals("sys_notify")){
                    queryReportOutPut = sysDataList.sysNotifyList();
                }else if (dataname.equals("sys_fankui")){
                    queryReportOutPut = sysDataList.sysFankuiList();
                }else if (dataname.equals("sys_require")){
                    queryReportOutPut = sysDataList.sysRequireList();
                }else if (dataname.equals("sys_table_group")){
                    queryReportOutPut = sysDataList.sysTableGroupList();
                }else if (dataname.equals("sys_table_column")){
                    queryReportOutPut = sysDataList.sysTableColumnList();
                }else if (dataname.equals("sys_flow")){
                    queryReportOutPut = sysDataList.sysFlowList();
                }else if (dataname.equals("sys_flow_node")){
                    queryReportOutPut = sysDataList.sysFlowNodeList();
                }else if (dataname.equals("FLOW_NODE_EXEC")){
                    queryReportOutPut = sysDataList.sysFlowNodeExecList();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            return CommonFunc.makeErrResult("400002");
        }

        ////此处插入导出支持功能
        String exportPageno=CommonFunc.getNotNullString(CommonFunc.getHttpVar("export_pageno"));
        if (StringUtils.isEmpty(exportPageno)) {
            mapData.put("sys_title", sysTitle);
            mapData.put("datainfo", JSONObject.toJSON(queryReportOutPut));
            mapData.put("flowNodeId", flowNodeId);
            mapData.put("getLoginDepartmentName", Eval.getLoginDepartmentName());
            return CommonFunc.makeSussResult(mapData);
        }

        //正式的导出功能
        int actionFlag=queryReportOutPut.getActionCount()>0?1:0;
        ArrayList listContent=queryReportOutPut.getListContent();
        ArrayList listCol=queryReportOutPut.getListCol();

        XSSFWorkbook wb= ExcelUtil.getXSSFWorkbook("Sheet1",listCol,listContent,null,actionFlag,queryReportOutPut.getTitle());
        try{
            String fileName=queryReportOutPut.getTitle_short()+"-"+CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyyMMddhhmm")+".xlsx";
            setResponseHeader(response,fileName);
            OutputStream os=response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/Data/Export",method = RequestMethod.POST)
    public Map<String, Object> UserModuleExport(@RequestParam String dataName, @RequestParam LocalDate date, HttpServletResponse response) throws Exception{
        String sysTitle=sysConfig.getTitle();

        Map<String, Object> mapData = new HashMap<String, Object>();

        //1.检查是否登录
//        if (!CommonFunc.checkIsLogined(request)){
//            return CommonFunc.makeErrResult("300099");
//        }

        //3.检查dataname是否存在
        if (MAP_BUSDATALIST.containsKey(dataName)){
            BusDataList busDataList=new BusDataList(request,sysConfig,datasourceConfig);
//            if ("OMS_BUSBAR".equals(dataName)){
//                //母线电压
//                setResponseHeader(response, "母线电压导出.xlsx");
//                busDataList.oms_jzb_busbar_export(response);
//            }
        }

        return null;
    }
}
