package com.zhuofan.oms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.config.ConfigFileInfo;
import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.SysUsers;
import com.zhuofan.core.queryreport.QueryReport;
import com.zhuofan.core.queryreport.QueryReportFilter;
import com.zhuofan.core.queryreport.QueryReportOutPut;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BusDataList {
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

    public BusDataList(HttpServletRequest request, SysConfig sysConfig, DatasourceConfig datasourceConfig){
        this.request=request;
        this.sysConfig=sysConfig;
        this.datasourceConfig=datasourceConfig;
    }


    /**
     * @Title: CommonSimpleList
     * @Description:通用的简易查询列表功能
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut CommonSimpleList(String dataname,String confname) {
        //1.获取配置文件，转成json格式
        CommonLogger commonLogger=CommonLogger.getInstance();

        String confFilename= ConfigFileInfo.getConfigPath("query/"+confname+".json");
        File file=new File(confFilename);

        commonLogger.info("CommonSimpleList:confFilename:"+confFilename);

        if (!file.exists()){
            confFilename="/home/apps/oms/omsserver/"+"query/"+confname+".json";
        }

        commonLogger.info("CommonSimpleList:confFilename:"+confFilename);

        String content=CommonFunc.readTxtFile(confFilename);
        JSONObject jsonConf=JSONObject.parseObject(content);

        commonLogger.info("CommonSimpleList:content:"+content);

        if (jsonConf==null){
            commonLogger.info("CommonSimpleList:jsonConf is null");
            return null;
        }

        QueryReport queryReport = new QueryReport(jsonConf.getString("title"), request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(jsonConf.getInteger("pagenumber"));

        String findYearno=CommonFunc.getHttpVar("find_yearno");

        queryReport.setTable(jsonConf.getString("table"));

        queryReport.setSelectfldn(jsonConf.getString("selectfldn"));
        queryReport.setOrderfldn(CommonFunc.getNotNullString(jsonConf.getString("orderfldn")));
        queryReport.setFldnPK(jsonConf.getString("fldnpk"));
        queryReport.setWhere_ex(jsonConf.getString("where_ex"));


        if (StringUtils.isNotEmpty(jsonConf.getString("flagImport"))) {
            queryReport.setFlagImport(Integer.valueOf(jsonConf.getString("flagImport")));
        }else {
            queryReport.setFlagImport(1);
        }

        //////////////////////////////////
        //查询条件
        JSONArray jsonListQueryset=jsonConf.getJSONArray("list_queryset");
        if (jsonListQueryset!=null) {
            for (int i = 0; i < jsonListQueryset.size(); i++) {
                JSONObject jsonQueryset = jsonListQueryset.getJSONObject(i);
                if (jsonQueryset == null) {
                    continue;
                }
                QueryReportFilter.QRFilterType qrFilterType=QueryReportFilter.QRFilterType.text;
                QueryReportFilter.QRFilterOper qrFilterOper= QueryReportFilter.QRFilterOper.like;
                String mapValue="";
                if (jsonQueryset.containsKey("oper")){
                    qrFilterOper= QueryReportFilter.QRFilterOper.fromString(jsonQueryset.getString("oper"));
                }
                if (jsonQueryset.containsKey("type")){
                    qrFilterType= QueryReportFilter.QRFilterType.fromString(jsonQueryset.getString("type"));
                }
                if (jsonQueryset.containsKey("mapValue")){
                    mapValue= jsonQueryset.getString("mapValue");
                }

                queryReport.addQuerySet(
                        jsonQueryset.getString("text"),
                        jsonQueryset.getString("varname"),
                        jsonQueryset.getString("fldn"),
                        qrFilterType,"",qrFilterOper,"","",mapValue,""
                );
            }
        }
        //////////////////////////////////


        //////////////////////////////////
        //查询展示列
        JSONArray jsonListcol=jsonConf.getJSONArray("list_col");
        for (int i=0;i<jsonListcol.size();i++){
            JSONObject jsonCol=jsonListcol.getJSONObject(i);
            if (jsonCol==null){
                continue;
            }
            String colTitle=jsonCol.getString("text");
            String fieldName=jsonCol.getString("field");
            String evaljs=jsonCol.getString("evaljs");

            String mapName="";
            if (jsonCol.containsKey("mapName")){
                mapName=jsonCol.getString("mapName");
            }

            if (StringUtils.isEmpty(mapName)) {
                if (StringUtils.isEmpty(evaljs)) {
                    queryReport.addColItem(colTitle, fieldName);
                }else{
                    String[] arrEvaljs=evaljs.split(",");
                    queryReport.addColItem(colTitle, fieldName,Eval.jsCol(arrEvaljs[0],fieldName,arrEvaljs[1]));
                }
            }else{
                queryReport.addColItem(colTitle,fieldName,
                        Eval.getHashMapValue(mapName,jsonCol.getString("field"))
                );
            }
        }

        //////////////////////////////////

        //////////////////////////////////
        //增加操作列
        if (jsonConf.containsKey("list_action")) {
            JSONArray jsonListAction = jsonConf.getJSONArray("list_action");
            for (int i = 0; i < jsonListAction.size(); i++) {
                JSONObject jsonAction = jsonListAction.getJSONObject(i);
                if (jsonAction == null) {
                    continue;
                }
                String actionText= jsonAction.getString("text");
                String actionTitle= jsonAction.getString("title");
                String func = jsonAction.getString("func");
                String roleId=jsonAction.getString("roleId");
                String username=jsonAction.getString("username");

                boolean flag=true;
                if (StringUtils.isNotEmpty(roleId)){
                    //判断是否有权限
                    flag= GrantService.checkUserGrantByRoleId(Eval.getLoginUserId(),
                            CommonFunc.getConfigByKey(roleId));
                }else if (StringUtils.isNotEmpty(username)){
                    flag=CommonFunc.inArray(Eval.getLoginName(),username.split(","));
                }

                if (flag) {
                    StringBuffer bufferUrl = new StringBuffer();
                    bufferUrl.append("javascript:" + func + "(this);");
                    String showScript=jsonAction.containsKey("showScript")?
                            jsonAction.getString("showScript"):"";
                    queryReport.addActionItem("", bufferUrl.toString(),
                            actionTitle, "text", actionText, "",showScript);
                }
            }
        }

        //////////////////////////////////
        //其它操作和按钮
        if ("1".equals(jsonConf.getString("button_new"))){
            queryReport.setOther(CommonFunc.getNotNullString(jsonConf.getString("other"))
                    +"<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname="+dataname+"&actiontype=add\")' type='button'>新增</button>");
        }else{
            queryReport.setOther(CommonFunc.getNotNullString(jsonConf.getString("other")));
        }

        if ("1".equals(jsonConf.getString("action_delete"))) {
            queryReport.setOtherEnd(CommonFunc.getNotNullString(jsonConf.getString("other_end"))
                    +"<button class='btn btn-primary mr-2' onclick='jsCommon.deleteTableDataBatch(this)' type='button'>删除所选</button>");
        }else{
            if (StringUtils.isNotEmpty(jsonConf.getString("other_end"))) {
                queryReport.setOtherEnd(jsonConf.getString("other_end"));
            }
        }

        if ("1".equals(jsonConf.getString("action_edit"))) {
            queryReport.addActionItem("", Eval.getEditUrl(dataname, "id",
                    jsonConf.getString("fldnpk")), "修改", "text", "修改", "");
        }

        if ("1".equals(jsonConf.getString("action_delete"))) {
            queryReport.addActionItem("", Eval.getDeleteUrl(dataname,jsonConf.getString("fldnpk")),
                    "删除本条数据","text","删除","");
        }

        try {
            queryReport.execQuery();
        }catch (Exception e){
            e.printStackTrace();
        }

        return queryReport.getOutPut();
    }


    /**
     * @Title: omsZbbdCewen
     * @Description:主变变低测温
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut omsZbbdCewen() throws Exception {
        QueryReport queryReport = new QueryReport("主变变低过载测温表", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("OMS_ZBBD_CEWEN A ");
        queryReport.setSelectfldn("A.*,NVL(FLAG_CEWEN,'0') FLAG_CEWEN1 ");
        queryReport.setOrderfldn("CREATE_TIME DESC ");

        queryReport.setFldnPK("ZBBD_CEWEN_ID");

        String loginDeptName=Eval.getLoginDepartmentName();
        if (loginDeptName.indexOf("巡维")>0){
            //巡维登录只可以看自己部门的
            queryReport.setWhere_ex("a.xunwei='"+loginDeptName+"'");
        }

        queryReport.addQuerySet("日期","find_ymd","YMD");
        queryReport.addQuerySet("设备名称","find_equipment","EQUIPMENT");

        //queryReport.addColItem("序号","XH");

        queryReport.addColItem("巡维中心","XUNWEI");
        queryReport.addColItem("设备名称","EQUIPMENT");
        queryReport.addColItem("过载时间","CREATE_TIME");
        queryReport.addColItem("负载率","FZL");
        queryReport.addColItem("是否完成测温","FLAG_CEWEN1",
                Eval.getHashMapValue("CommonObject.mapYesNo","FLAG_CEWEN1"));
        queryReport.addColItem("测温时间","CEWEN_TIME");
        queryReport.addColItem("测温人","USERNAME");
        queryReport.addColItem("环境温度","HJWD");
        queryReport.addColItem("测温方式","CEFS");
        queryReport.addColItem("测温电流","CEDL");
        queryReport.addColItem("A相温度","AXWD");
        queryReport.addColItem("B相温度","BXWD");
        queryReport.addColItem("C相温度","CXWD");
        queryReport.addColItem("备注","BZ");

        queryReport.addActionItem("", Eval.getEditUrl("OMS_ZBBD_CEWEN","id","ZBBD_CEWEN_ID")
                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_ZBBD_CEWEN","ZBBD_CEWEN_ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: omsFesta
     * @Description:节假日表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut omsFesta() throws Exception {
        QueryReport queryReport = new QueryReport("节假日管理", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("OMS_FESTA A ");
        queryReport.setSelectfldn("A.* ");
        queryReport.setOrderfldn("YEARNO DESC,DAYNO ASC ");

        queryReport.setFldnPK("FESTA_ID");

        queryReport.addQuerySet("年份","find_yearno","YEARNO");

        //queryReport.addColItem("序号","XH");
        queryReport.addColItem("年份","YEARNO");
        queryReport.addColItem("日期","DAYNO");
        queryReport.addColItem("类型","FESTA_TYPE");
        queryReport.addColItem("名称","FESTA_NAME");

        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=OMS_FESTA&actiontype=add\")' type='button'>新增</button>");

        queryReport.addActionItem("", Eval.getEditUrl("OMS_FESTA","id","FESTA_ID")
                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_FESTA","FESTA_ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }


    /**
     * @Title: yfGffhProj
     * @Description:运方高峰负荷项目表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut yfGffhProj() throws Exception {
//        String ymd= CommonFunc.getHttpVar("ymd");

        QueryReport queryReport = new QueryReport("运方高峰负荷项目表", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("YF_GFFH_PROJ A ");
        queryReport.setSelectfldn("A.* ");
        // queryReport.setWhere_ex("ymd='"+ymd+"'");
        queryReport.setOrderfldn("CREATE_TIME DESC ");

        queryReport.setFldnPK("ID");

        queryReport.addQuerySet("项目名称","find_proj_name","PROJ_NAME");
        queryReport.addQuerySet("数据查询时间点","find_query_time","QUERY_TIME");
        queryReport.addQuerySet("创建人","find_create_user","CREATE_USER");

        //queryReport.addColItem("序号","XH");
        queryReport.addColItem("项目名称","PROJ_NAME");
        queryReport.addColItem("数据查询时间点","QUERY_TIME");
        queryReport.addColItem("创建人","CREATE_USER");
        queryReport.addColItem("创建时间","CREATE_TIME");
        queryReport.addColItem("项目备注","PROJ_CONTENT");

        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=YF_GFFH_PROJ&actiontype=add\")' type='button'>新增</button>&nbsp;" +
                "<button class='btn btn-primary mr-2' onclick='window.open(\"datalist.html?dataname=YF_JZB\")' type='button'>基准表管理</button>&nbsp;");


        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/yunfang/main.html?","proj_id","PROJ_ID","","")
                ,"查看本项目的数据统计分析","text","数据统计","");

        queryReport.addActionItem("", Eval.getEditUrl("YF_GFFH_PROJ","id","PROJ_ID")
                ,"修改基准表","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("YF_GFFH_PROJ","PROJ_ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: yfJzbList
     * @Description:运方基准表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut yfJzbList() throws Exception {
//        String ymd= CommonFunc.getHttpVar("ymd");

        QueryReport queryReport = new QueryReport("运方高峰负荷基准表", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("YF_JZB A ");
        queryReport.setSelectfldn("A.* ");
        // queryReport.setWhere_ex("ymd='"+ymd+"'");
        queryReport.setOrderfldn("XH ASC ");

        queryReport.setFldnPK("ID");

        queryReport.addQuerySet("变电站","find_bdz","nvl(BDZ,'')");
        queryReport.addQuerySet("断面名称","find_dmmc","nvl(DMMC,'')");
        queryReport.addQuerySet("类型","find_type","nvl(TYPE,'')", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in,"","",
                "10,20,30,40,50,60,70,99:全部;10:500kV变电站;20:220kV变电站;30:220kV变低;30:110kV变电站;50:10kV大电流柜;60:220kV线路;70:110kV线路;99:辅助计算点","");

        //queryReport.addColItem("序号","XH");
        queryReport.addColItem("变电站","BDZ");
        queryReport.addColItem("断面名称","DMMC");
        queryReport.addColItem("分类","TYPE",Eval.getHashMapValue("CommonObject.mapYfJCBType","TYPE"));
        queryReport.addColItem("额定容量（MVA）","EDRL");
        queryReport.addColItem("控制断面（MW）","KZDM");
        queryReport.addColItem("数据点ID","ID_P");
        queryReport.addColItem("区局","QJ");
        queryReport.addColItem("镇街","ZJ");
        queryReport.addColItem("备注","BZ");

        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=YF_JZB&actiontype=add\")' type='button'>新增</button>&nbsp;");


        queryReport.addActionItem("", Eval.getEditUrl("YF_JZB","id","JZB_ID")
                ,"修改基准表","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("YF_JZB","JZB_ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: jhzfzj
     * @Description:安生项目支付计划管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut jhzfzj() throws Exception {
        QueryReport queryReport = new QueryReport("项目支付计划管理", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(50);
        queryReport.setTable("W_JHZFZJ");
        queryReport.setSelectfldn("*");
        //queryReport.setFlagImport(1);

        //queryReport.setOrderfldn("add_time desc");

        queryReport.addQuerySet("项目编码","find_f_xmbh","F_XMBH");
        queryReport.addQuerySet("项目名称","find_f_xmmc","F_XMMC");
        queryReport.addQuerySet("年份","find_nf","NF");

        queryReport.addColItem("项目编码","F_XMBH");
        queryReport.addColItem("项目名称","F_XMMC");
        queryReport.addColItem("年份","NF");
        queryReport.addColItem("1月","Y1");
        queryReport.addColItem("2月","Y2");
        queryReport.addColItem("3月","Y3");
        queryReport.addColItem("4月","Y4");
        queryReport.addColItem("5月","Y5");
        queryReport.addColItem("6月","Y6");
        queryReport.addColItem("7月","Y7");
        queryReport.addColItem("8月","Y8");
        queryReport.addColItem("9月","Y9");
        queryReport.addColItem("10月","Y10");
        queryReport.addColItem("11月","Y11");
        queryReport.addColItem("12月","Y12");
        queryReport.addColItem("合计","HJ");

        queryReport.addActionItem("", Eval.getDeleteUrl("W_JHZFZJ","ID"),"删除本条信息","text","删除","");

        queryReport.addActionItem("", Eval.getEditUrl("W_JHZFZJ","id","ID")
                ,"修改本条信息","text","修改","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: fpfhgl
     * @Description:安生项目信息管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut fpfhgl() throws Exception {
        QueryReport queryReport = new QueryReport("发票、付款登记", request, sysConfig, datasourceConfig);
        queryReport.setTitle_short("发票付款管理");
        queryReport.setpagenumber(50);
        queryReport.setTable("V_ASFPGL");

        //需要使用统计功能，因此必须指定列，不可使用*
        queryReport.setSelectfldn("ID,F_XMBH,F_XMMC,F_XMXZ,F_SSFB,F_XMFZR,F_FPNR," +
                "to_char(F_KPJH) F_KPJH,to_char(F_KPJHBHS) F_KPJHBHS," +
                "F_LXND,F_YHBH_V,F_FPZS_V,F_SBRQ,F_KPDW,F_SFZXT_V,F_BZ,F_SE");
        queryReport.setFlagImport(1);
        queryReport.setOrderfldn("F_SBRQ desc");

        //统计列要比setSelectfldn多一列，因为程序会自动中上rownum列在最后
        queryReport.setSql_total("select '','合计','','','','',''," +
                "round(sum(f_kpjh)/10000,2) || '万' f_kpjh,round(sum(f_kpjhbhs)/10000,2) || '万' f_kpjhbhs, " +
                "'','','','','','','','',''"+
                " from V_ASFPGL {where}");
        //queryReport.setOrderfldn("add_time desc");

        queryReport.addQuerySet("项目编码","find_f_xmbh","F_XMBH");
        queryReport.addQuerySet("项目名称","find_f_xmmc","F_XMMC");

        queryReport.addQuerySet("项目性质","find_f_xmxz","F_XMXZ",QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.equal,"","",
                ";电网建设;主网建设;配网建设;小型基建;技改;修理;科技;信息化;职创;其他","");

        queryReport.addQuerySet("发票内容","find_f_fpnrr","F_FPNR",QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.equal,"","",
                ";材料费;专利费","");

        queryReport.addQuerySet("开票金额","find_f_kpjh","F_KPJH");
        queryReport.addQuerySet("业务编号","find_f_ywbh","F_YWBH");
        queryReport.addQuerySet("开票单位","find_f_kpdw","F_KPDW");

        queryReport.addQuerySet("是否资产域协同","find_f_sfzxt","F_SFZXT",QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.equal,"","", ";是;否","");

        queryReport.addQuerySet("上报年度","find_nd","ND",QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.equal,"","",
                ";2017;2018;2019;2020;2021;2022;2023;2024;2025;2026;2027;2028;2029;2030","");

        queryReport.addQuerySet("是否已结算","find_f_sfjs","F_SFJS",QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.equal,"","",";是;否","");


        queryReport.addColItem("项目编码","F_XMBH");
        queryReport.addColItem("项目名称","F_XMMC");
        queryReport.addColItem("项目性质","F_XMXZ");
		queryReport.addColItem("所属分部","F_SSFB");
		queryReport.addColItem("项目负责人","F_XMFZR");
        queryReport.addColItem("发票内容","F_FPNR");
        queryReport.addColItem("开票金额","F_KPJH");
        queryReport.addColItem("开票金额（不含税）","F_KPJHBHS");
        queryReport.addColItem("税额","F_SE");
        queryReport.addColItem("业务编号","F_YHBH_V");
        queryReport.addColItem("发票张数","F_FPZS_V");
        queryReport.addColItem("所属分部","F_SSFB");
        queryReport.addColItem("上报日期","F_SBRQ");
        queryReport.addColItem("开票单位","F_KPDW");
        queryReport.addColItem("是否资产域协同","F_SFZXT_V");
        queryReport.addColItem("备注","F_BZ");
		
		queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=W_ASFPGL'>新增发票</a>");
        queryReport.addActionItem("", Eval.getDeleteUrl("W_ASFPGL","ID"),"删除本条发票信息","text","删除","");

        queryReport.addActionItem("", Eval.getEditUrl("W_ASFPGL","id","ID")
                ,"修改本条发票信息","text","修改","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: ASXMXX
     * @Description:安生项目信息管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut asxmxx() throws Exception {
        String findFXmxz=CommonFunc.getHttpVar("find_f_xmxz");
        QueryReport queryReport = new QueryReport(findFXmxz+"项目信息管理", request, sysConfig, datasourceConfig);

        if (StringUtils.isNotEmpty(findFXmxz)){
            if (findFXmxz.equals("其它")) {
                queryReport.setWhere_ex("(F_XMXZ not like  '技改%' and F_XMXZ not like  '修理%' and F_XMXZ not like  '科技%'" +
                        " and F_XMXZ not like  '信息化%' and F_XMXZ not like  '主网建设%' and F_XMXZ not like  '小型基建%' " +
                        " and F_XMXZ not like  '电网建设%' and F_XMXZ not like  '配网建设%')");
            }else{
                queryReport.setWhere_ex("F_XMXZ like '"+findFXmxz+"%'");
            }
        }

        //project_id
        queryReport.setpagenumber(50);
        queryReport.setTable("W_ASXMXX");
        queryReport.setSelectfldn("*");
        //queryReport.setOrderfldn("add_time desc");
        queryReport.setFlagImport(1);


        queryReport.addQuerySet("项目编码","find_f_xmbh","F_XMBH");
        queryReport.addQuerySet("项目名称","find_f_xmmc","F_XMMC");
        queryReport.addQuerySet("所属分部","find_f_ssfb","F_SSFB");
        queryReport.addQuerySet("项目负责人","find_f_xmfzr","F_XMFZR");
        queryReport.addQuerySet("是否结算","find_f_sfjs","F_SFJS", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.equal,"","",";是;否","");
        //queryReport.addQuerySet("项目性质","find_f_xmxz","F_XMXZ",QueryReportFilter.QRFilterType.selarray,
        //        "", QueryReportFilter.QRFilterOper.equal,"","",
        //        ";电网建设;主网建设项目;配网建设项目;小型基建项目;技改项目;修理项目;科技项目;信息化项目;其他项目(成本费用项目)","");
        queryReport.addQuerySet("资金支付","find_f_zjzf","F_ZJZF");
        queryReport.addQuerySet("立项年度","find_f_lxnd","F_LXND",QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.equal,"","",
                ";2017;2018;2019;2020;2021;2022;2023;2024;2025;2026;2027;2028;2029;2030","");

        queryReport.addColItem("项目编码","F_XMBH");
        queryReport.addColItem("项目名称","F_XMMC");
		queryReport.addColItem("项目性质","F_XMXZ");
		queryReport.addColItem("所属分部","F_SSFB");
		queryReport.addColItem("立项年度","F_LXND");
		queryReport.addColItem("要求结算年度","F_YQJSND");
		queryReport.addColItem("项目负责人","F_XMFZR");
        queryReport.addColItem("资金跟踪人员","F_ZJGZRY");
		queryReport.addColItem("资金支付","F_ZJZF");
		queryReport.addColItem("项目总开支（含税）","F_XMZKZHS");
		queryReport.addColItem("以前年度总开支（含税）","F_YQNDZKZHS");
		queryReport.addColItem("本年投资（含税）","F_BNTZHS");
		queryReport.addColItem("总投资","F_ZTZ");
		queryReport.addColItem("合同名称","F_HTMC");
        queryReport.addColItem("合同约定资金支付期数","F_HTYDZJZFQS");
		queryReport.addColItem("合同约定资金支付期数","F_YWCZJZFQS");  
		queryReport.addColItem("本年投资（含税）","F_BNTZHS");
		queryReport.addColItem("本年已支付资金（含税）","F_BNYZFZJHS");
		queryReport.addColItem("资金完成率（%）（含税）","F_ZJWCLHS");
        queryReport.addColItem("年度预算 (不含税)","F_NDYS");
        queryReport.addColItem("本年已支付资金(不含税)","F_BNYZFZJ");
        queryReport.addColItem("资金完成率（%）(不含税)","F_ZJWCL");

        queryReport.addActionItem("", Eval.getDeleteUrl("W_ASXMXX","ID"),"删除本条项目信息","text","删除","");

        queryReport.addActionItem("", Eval.getEditUrl("W_ASXMXX","id","ID")
                ,"修改本条项目信息","text","修改","");

        queryReport.addActionItem("", Eval.getOpenUrlAction("datalist.html?dataname=FPFHGL&","find_f_xmbh","F_XMBH","","")
                ,"查看本条项目的发票付款信息","text","发票付款","");

        queryReport.addActionItem("", Eval.getOpenUrlAction("../omsserver/Export/Asxmzfjd?","project_id","ID","","")
                ,"导出本条项目的支付进度信息","text","导出支付进度表","");

        queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=W_ASXMXX'>新增项目</a>"+
                "&nbsp;&nbsp;<a role=button target=_blank href='../omsserver/Export/Asxmzfjd?xmxz="+findFXmxz+"'>导出支付进度表</a>");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: ZdhInspectList
     * @Description:自动化交班记录查看
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut ZdhInspectList() throws Exception {
        //String ymd= CommonFunc.getHttpVar("ymd");

        QueryReport queryReport = new QueryReport("自动化交接班记录", request, sysConfig, datasourceConfig);

        queryReport.setpagenumber(100);
        queryReport.setTable("ZDH_INSPECT A left join sys_departments b on a.deptid=b.departmentid");
        queryReport.setSelectfldn("A.*,b.DEPARTMENTNAME");
        //queryReport.setWhere_ex("DEPTID='"+Eval.getLoginDepartmentId()+"'");
        queryReport.setOrderfldn("YMD desc");

        queryReport.addQuerySet("值号","find_type_name","TYPE_NAME");
        String depts=String.format(";%s:主网;%s:配网;%s:辅助",CommonFunc.getConfigByKey("sys.deptid_zdhyb"),
                CommonFunc.getConfigByKey("sys.deptid_zdheb"),
                CommonFunc.getConfigByKey("sys.deptid_zdhfz"));
        queryReport.addQuerySet("部门","find_deptid","DEPTID",
                QueryReportFilter.QRFilterType.selarray,"",
                QueryReportFilter.QRFilterOper.equal,"","",depts,"");

        queryReport.addColItem("值号","BANNO");
        queryReport.addColItem("部门","DEPARTMENTNAME");
        queryReport.addColItem("值班员","WORKERS");
        queryReport.addColItem("交班员","WORKERS_THIS");
        queryReport.addColItem("交班时间","YMD");
        queryReport.addColItem("接班员","WORKERS_NEXT");
        queryReport.addColItem("接班时间","CHANGE_TIME");
        //queryReport.addColItem("回退人","HUITUI");
        //queryReport.addColItem("回退时间","HTTIME");
        queryReport.addColItem("值班状态","START",Eval.getHashMapValue("CommonObject.mapJjbStartType", "START"));

        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zdh/logview.html?",
                "banno","BANNO","",""),"值班日志","text","值班日志","");
        queryReport.addActionItem("", Eval.getViewDataUrl("ZDH_INSPECT","INSPECT_ID","INSPECT_ID")
                ,"交班信息","text","交班信息","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }



    /**
     * @Title: ZdhInspectList
     * @Description:自动化交班记录查看
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut omsXmxxglList() throws Exception {
        //String ymd= CommonFunc.getHttpVar("ymd");

        QueryReport queryReport = new QueryReport("项目信息管理", request, sysConfig, datasourceConfig);

        queryReport.setpagenumber(100);
        queryReport.setTable("W_ASXMXX");
        queryReport.setSelectfldn("*");
        //queryReport.setWhere_ex("DEPTID='"+Eval.getLoginDepartmentId()+"'");
        queryReport.setOrderfldn("F_JHJSSJ desc");

        queryReport.addQuerySet("项目名称","find_f_xmmc","F_XMMC");
		queryReport.addQuerySet("项目编号","find_f_xmbh","F_XMBH");

        queryReport.addColItem("项目名称","F_XMMC");
        queryReport.addColItem("项目编号","F_XMBH");
        queryReport.addColItem("所属分部","F_SSFB");
        queryReport.addColItem("是否结算","F_SFJS");
        queryReport.addColItem("项目负责人","F_XMFZR");
        queryReport.addColItem("计划计算时间","F_JHJSSJ");
        queryReport.addColItem("要求结算年度","F_YQJSND");
        queryReport.addColItem("总投资","F_ZTZ");
        queryReport.addColItem("立项年度","F_LXND");
		queryReport.addColItem("转资完成率","F_ZZWCL");
		queryReport.addColItem("资金跟踪人员","F_ZJGZRY");
		queryReport.addColItem("项目总开支（含税）","F_XMZKZHS");
		queryReport.addColItem("以前年度总开支（含税）","F_YQNDZKZHS");
		queryReport.addColItem("本年投资（含税）","F_BNTZHS");
		queryReport.addColItem("本年已支付资金（含税）","F_BNYZFZJHS");
		queryReport.addColItem("资金完成率（%）（含税）","F_ZJWCLHS");
		queryReport.addColItem("下达转资目标金额","F_XDZZMBJE");
		queryReport.addColItem("转资完成率","F_ZZWCL");


        queryReport.execQuery();
        return queryReport.getOutPut();

    }



    /**
     * @Title: zdhLogview
     * @Description:自动化值班日志查看
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut zdhLogview() throws Exception {
        String ymd= CommonFunc.getHttpVar("ymd");

        QueryReport queryReport = new QueryReport("自动化值班日志记录", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("ZDH_LOG a left join ZDH_LOGTYPE B on a.logtype_id=b.logtype_id ");
        queryReport.setSelectfldn("a.*,b.type_name");
        queryReport.setWhere_ex("ymd='"+ymd+"'");
        queryReport.setOrderfldn("add_time desc");

        queryReport.addQuerySet("日志类型","find_type_name","TYPE_NAME");
        queryReport.addQuerySet("日志内容","find_content","CONTENT");
        queryReport.addQuerySet("日期","ymd","YMD");

        queryReport.addColItem("日志类型","TYPE_NAME");
        queryReport.addColItem("记录时间","ADD_TIME");
        queryReport.addColItem("日志内容","CONTENT");
        queryReport.addColItem("记录人","ADD_USER");

        String showScript="0";
        //第一步：获取当前登录人员部门ID是否在主配网内
        String deptidZdhyb=CommonFunc.getConfigByKey("sys.deptid_zdhyb");
        String deptidZdheb=CommonFunc.getConfigByKey("sys.deptid_zdheb");

        String deptId=Eval.getLoginDepartmentId();
        if (deptId.equals(deptidZdhyb) || deptId.equals(deptidZdheb)){
            //自动化一二班有权限
            //找状态为2的值号
            try(JdbcDao jdbcDao=new JdbcDao()) {
                String sqlBanno = "select banno from ZDH_INSPECT where START='2' and DEPTID='" + deptId + "'";
                String currBanno = jdbcDao.getFieldValue(sqlBanno, "BANNO");
                jdbcDao.close();
                if (StringUtils.isNotEmpty(currBanno)) {
                    //有值号，需判断 值号与日志中的值号相同，即有权限修改删除
                    showScript = String.format("%s=%s", "BANNO", currBanno);
                }
            }
        }
        queryReport.addActionItem("", Eval.getEditUrl("zdh_log","log_id","LOG_ID")
                ,"修改本条日志","text","修改","",showScript);
        queryReport.addActionItem("", Eval.getDeleteUrl("zdh_log","LOG_ID"),"删除本条日志","text","删除","",showScript);

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

        /**
         * @Title: topLineList
         * @Description:拓扑追踪线路列表
         * @return
         * @throws Exception
         * @author 广州卓凡扶小冬
         * @throws
         */
    public QueryReportOutPut topLineList() throws Exception {
        QueryReport queryReport=new QueryReport("站变线拓扑列表",request,sysConfig,datasourceConfig);

        queryReport.setTable("V_CIM_TOP_LINES");
        queryReport.setSelectfldn("*");

        queryReport.addQuerySet("区局","find_quju","QUJU");
        queryReport.addQuerySet("供电服务中心","find_gdfwzx","GDFWZX");
        queryReport.addQuerySet("变电站","find_bdzmc","BDZMC");

        queryReport.addColItem("变电站","BDZMC");
        queryReport.addColItem("主变","ZBMC");
        queryReport.addColItem("10千伏母线","MXMC");
        queryReport.addColItem("涉及的10千伏线路","LIST_XLMC");
        queryReport.addColItem("区局","QUJU");
        queryReport.addColItem("供电服务中心","GDFWZX");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: tpzzSiteList
     * @Description:拓扑追踪主变列表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut tpzzSiteList() throws Exception {
        String traceResultId= CommonFunc.getHttpVar("find_trace_result_id");
        QueryReport queryReport=new QueryReport("拓扑追踪主变相关",request,sysConfig,datasourceConfig);

        queryReport.setTable("OMS_TRACE_RESULT_ITEM");
        queryReport.setSelectfldn("*");
        queryReport.setWhere_ex("ITEM_TYPE ='供电变压器'"+(StringUtils.isEmpty(traceResultId)?"":" AND TRACE_RESULT_ID="+traceResultId));

        queryReport.addColItem("站名","SITE_NAME");
        queryReport.addColItem("电压等级","VOL_LEVEL");
        queryReport.addColItem("设备名称","EQU_NAME");
        queryReport.addColItem("值","ITEM_VALUE");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: traceList
     * @Description:拓扑追踪
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut traceList() throws Exception
    {
        QueryReport queryReport=new QueryReport("",request,sysConfig,datasourceConfig);

        queryReport.setTable("OMS_TRACE_RESULT o left join vm_tp v on o.trace_result_id = v.byqid");
        queryReport.setSelectfldn("o.trace_result_id, o.worker,o.worktime,o.site_name||','||vol_level||','||o.equ_name zzsb," +
                "v.cbyq,v.cmx,v.cfh,case when (nvl(site_name,'/')='') then '/' else nvl(site_name,'/') end site_name," +
                "case when instr(equ_name,'线')>0 then substring(equ_name,0,instr(equ_name,'线'))  else equ_name end equ_name, " +
                "vol_level,v.sc,rownum,nvl(v.fh,0) FH ");

        queryReport.setOrderfldn("WORKTIME");
        queryReport.setOrdertype("DESC");
        queryReport.addQuerySet("操作人","find_worker","WORKER");
        queryReport.addQuerySet("操作时间","find_worktime","WORKTIME");
        queryReport.addQuerySet("所属厂站","find_site_name","SITE_NAME");
        queryReport.addQuerySet("电压等级","find_vol_level","VOL_LEVEL");
        queryReport.addQuerySet("设备","find_equ_name","EQU_NAME");

        queryReport.addColItem("操作人","WORKER");
        queryReport.addColItem("操作时间","WORKTIME",Eval.getEval_Row_Unix2Data("WORKTIME"));
        queryReport.addColItem("追踪设备所属厂站","SITE_NAME");
        queryReport.addColItem("追踪设备电压等级","VOL_LEVEL");
        queryReport.addColItem("追踪设备","EQU_NAME",
                Eval.getOpenUrl("datalist.html?dataname=TRACE_ITEM_LIST&",
                        "trace_result_id","TRACE_RESULT_ID","EQU_NAME",""));
        queryReport.addColItem("供电变压器数","CBYQ",Eval.getListUrl("TPZZ_SITE_LIST",
                "find_trace_result_id","TRACE_RESULT_ID","CBYQ"));
        queryReport.addColItem("10kV供电母线数","CMX");
        queryReport.addColItem("10KV馈线数","CFH");
        queryReport.addColItem("总负荷","FH");

        queryReport.addActionItem("", Eval.getOpenUrlAction("datalist.html?dataname=TRACE_ITEM_LIST&",
                "trace_result_id","TRACE_RESULT_ID","EQU_NAME",""),"查看供电母线","text","查看供电母线","");

        queryReport.addActionItem("", Eval.getOpenUrlAction("datalist.html?dataname=TRACE_ITEM_LIST_LINE&",
                "trace_result_id","TRACE_RESULT_ID","EQU_NAME",""),"查看供电馈线","text","查看供电馈线","");

        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zwdd/trace_linetotal.html?",
                "trace_result_id","TRACE_RESULT_ID","EQU_NAME",""),"负荷跌落分析","text","负荷跌落分析","");

        queryReport.addActionItem("", Eval.getOpenUrlAction("../omsserver/Zwdd/TraceDownload?",
                "trace_result_id","TRACE_RESULT_ID","EQU_NAME",""),"原文件下载","text","原文件下载","");

        queryReport.setOther("<a role=button target=_blank href='datalist.html?dataname=TOP_LINE_LIST'>查看最新站变线拓扑结构</a>");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: omsJzbList
     * @Description:oms基准表
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut omsJzbList() throws Exception {
//        String ymd= CommonFunc.getHttpVar("ymd");
        String dmfh=CommonFunc.getHttpVar("dmfh");
        QueryReport queryReport = new QueryReport("基准表设置", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(200);
        queryReport.setTable("OMS_JZB");
        queryReport.setSelectfldn("*,NVL(FLAG_HIDE,'0') FLAG_HIDE1,NVL(SFJXDM,'0') SFJXDM1,NVL(SFYXGJL,'0') SFYXGJL1,NVL(SFTJ,'0') SFTJ1");
       // queryReport.setWhere_ex("ymd='"+ymd+"'");
        queryReport.setOrderfldn("CASE WHEN ZCXLZ IS NULL OR ZCXLZ = '' OR ZCXLZ<=1 THEN 1 ELSE 0 END,DYDJ DESC");
        if (!StringUtils.isEmpty(dmfh)) {
            queryReport.setWhere_ex("jtype='10'");
        }
        queryReport.setFldnPK("ID");

        queryReport.addQuerySet("电压等级","find_dydj","nvl(DYDJ,'')");
        queryReport.addQuerySet("厂站","find_name","nvl(NAME,'')");
        queryReport.addQuerySet("设备","find_equipment","nvl(EQUIPMENT,'') || nvl(EQUIPMENT1,'')");
        queryReport.addQuerySet("部门","find_depart","nvl(DEPART,'')");
        if (StringUtils.isEmpty(dmfh)) {
            queryReport.addQuerySet("类型", "find_jtype", "nvl(JTYPE,'')", QueryReportFilter.QRFilterType.selarray,
                    "", QueryReportFilter.QRFilterOper.in, "", "",
                    "20,30,40,50,70,80,90,100:全部;20:10kV主变变低;40:110kV线路;30:220kV线路;50:500kV线路;" +
                            "70:10kV以上主变变低;80:500kV开关;90:10kV开关柜;100:母线电压;", "");
        }
        queryReport.addQuerySet("是否生效","find_sftj","nvl(sftj,'0')", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in,"","",
                "1,0:全部;1:是;0:否","");
        queryReport.addQuerySet("是否隐藏","find_flag_hide","nvl(flag_hide,'0')", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in,"","",
                "1,0:全部;1:是;0:否","");
        queryReport.addQuerySet("是否有修改","find_sfyxgjl","nvl(sfyxgjl,'0')", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in,"","",
                "1,0:全部;1:是;0:否","");
        //queryReport.addQuerySet("是否检修断面","find_sfjxdm","nvl(sfjxdm,'0')", QueryReportFilter.QRFilterType.selarray,
        //        "", QueryReportFilter.QRFilterOper.in,"","",
        //        "1,0:全部;1:是;0:否","");

        queryReport.addColItem("电压","DYDJ");
        if (StringUtils.isEmpty(dmfh)) {
            queryReport.addColItem("厂站", "NAME");
            queryReport.addColItem("设备", "EQUIPMENT1");
        }else{
            queryReport.addColItem("数据库名称", "NAME");
            queryReport.addColItem("断面名称", "EQUIPMENT1");
        }
        queryReport.addColItem("正常限流值","ZCXLZ");
        queryReport.addColItem("控制值","KZZ");
        queryReport.addColItem("额定值","EDDL");
        queryReport.addColItem("部门","DEPART");
        queryReport.addColItem("显示名称","EQUIPMENT");
        queryReport.addColItem("类型","JTYPE", Eval.getHashMapValue("CommonObject.mapJCBType","JTYPE"));
        queryReport.addColItem("是否生效","SFTJ1", Eval.getHashMapValue("CommonObject.mapYesNo","SFTJ1"));
        queryReport.addColItem("是否隐藏","FLAG_HIDE1", Eval.getHashMapValue("CommonObject.mapYesNo","FLAG_HIDE1"));
        queryReport.addColItem("是否检修断面","SFJXDM1", Eval.getHashMapValue("CommonObject.mapYesNo","SFJXDM1"));
        queryReport.addColItem("有效期","YXQ");
        queryReport.addColItem("是否有修改","SFYXGJL1", Eval.getHashMapValue("CommonObject.mapYesNo","SFYXGJL1"));

        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=OMS_JZB_EDIT&actiontype=add\")' type='button'>新增</button>&nbsp;" +
                "<input type=button onclick='jzb_hidesel()' form=formList class='btn btn-primary mr-2' value='隐藏所选'>&nbsp;");

//        queryReport.addActionItem("", Eval.getOpenUrlAction("dataadd.html?dataname=OMS_JZB_EDIT&pid&actiontype=add?","pk_id","ID","ID","")
//                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.getEditAddUrl("OMS_JZB_ID","OMS_JZB_EDIT", "", "ID")
                , "修改基准表", "text", "修改", "");
//        queryReport.addActionItem("", Eval.getEditUrl("OMS_JZB_EDIT", "", "ID")
//                , "修改基准表", "text", "修改", "");
//        queryReport.addActionItem("", Eval.getOpenUrlAction("dataadd.html?dataname=OMS_JZB_EDIT&&actiontype=add&","pk_id","ID","","")
//                ,"修改基准表","text","修改","");

        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_JZB_EDIT","OMS_JZB_ID"),"删除修改记录","text","删除修改记录","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: scadaDataDm
     * @Description:自动化值班日志查看
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut scadaDataDm() throws Exception {
        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());
        }

        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
//        queryReport.setTable("V_SCADA_DATA_LINE110");

        String tableName=String.format("(select a.*," +
                "case when a.fzl is null or a.fzl=0 then '' else round(to_number(a.fzl),2) ||'%%' end fzl_r ," +
                "b.maxval maxval_yday,b.maxval_time maxval_time_yday,b.fzl_max fzl_max_yday " +
                "from (select * from v_scada_data_dm  where ymd='%s' ) as a " +
                "left join (select * from v_scada_data_dm where ymd='%s' )as b on a.scada_id=b.scada_id where " +
                "a.scada_id not in ('8444255022809092','8444261073289217','8444257205420033','8444257888108545')) xx ",ymd,y_ymd);
        queryReport.setTable(tableName);
        queryReport.setSelectfldn("xx.*,case when substring(now(),0,10)='"+ymd+"' then to_char(CURRVAL) else '/' end CURRVAL1," +
                "case when substring(now(),0,10)='"+ymd+"' then FZL_R else '/' end FZL_R1");
        queryReport.setWhere_ex("ymd='"+ymd+"'");
        queryReport.setOrderfldn("fzl_max desc nulls last");

        //queryReport.setOther("<a role=button target=_blank href='../omsserver/Zwdd/ExportGZSB?ymd="+ymd+"'>导出主网设备重载预警清单</a>");

        queryReport.addQuerySet("断面名称","find_equipment","EQUIPMENT");
//        queryReport.addQuerySet("","find_content","CONTENT");
//        queryReport.addQuerySet("日期","ymd","YMD");

        queryReport.addQuerySet("日期","ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,
                QueryReportFilter.QRFilterOper.equal);

        queryReport.addColItem("断面名","EQUIPMENT");
        queryReport.addColItem("实时值/MW","CURRVAL1");
        queryReport.addColItem("控制值/MW","ZCXLZ");
        queryReport.addColItem("实时负载率","FZL_R1");
        queryReport.addColItem("最大值发生时刻","MAXVAL_TIME");
        queryReport.addColItem("最大值/MW","MAXVAL");
        queryReport.addColItem("当日最大负载率","FZL_MAX");
        queryReport.addColItem("昨日最大值时刻","MAXVAL_TIME_YDAY");
        queryReport.addColItem("昨日最大值/MW","MAXVAL_YDAY");
        queryReport.addColItem("昨日最大负载率","FZL_MAX_YDAY");
//        queryReport.addActionItem("", Eval.getEditUrl("zdh_log","log_id","LOG_ID")
//                ,"修改本条日志","text","修改","");
//        queryReport.addActionItem("", Eval.getDeleteUrl("zdh_log","LOG_ID"),"删除本条日志","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: scadaDataZbbd
     * @Description:主变变低
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut scadaDataZbbd() throws Exception {
        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());

        }

        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setFldnPK("SCADA_ID");
//        queryReport.setTable("V_SCADA_DATA_LINE110");

        String tableName=String.format("(select a.*,case when a.fzl is null or a.fzl=0 then '' else round(to_number(a.fzl),2) ||'%%' end fzl_r ," +
                "b.maxval maxval_yday,b.maxval_time maxval_time_yday,b.fzl_max fzl_max_yday," +
                "case when a.zcxlz=0 then '' else b.maxval/a.zcxlz end fzl_yday " +
                "from (select * from v_scada_data_zbbd  where ymd='%s' ) as a " +
                "left join (select * from v_scada_data_zbbd where ymd='%s' ) as b on a.scada_id=b.scada_id) xx",ymd,y_ymd);
        queryReport.setTable(tableName);
        queryReport.setSelectfldn("xx.*,case when substring(now(),0,10)='"+ymd+"' then to_char(CURRVAL) else '/' end CURRVAL1," +
                "case when substring(now(),0,10)='"+ymd+"' then FZL_R else '/' end FZL_R1");
        queryReport.setOrderfldn("fzl_max desc nulls last");

        queryReport.addQuerySet("厂站","find_name","NAME");
        queryReport.addQuerySet("间隔","find_equipment","EQUIPMENT");
//        queryReport.addQuerySet("日期","ymd","YMD");

        queryReport.addQuerySet("日期","ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,
                QueryReportFilter.QRFilterOper.equal);

        queryReport.addColItem("厂站","NAME");
        queryReport.addColItem("间隔","EQUIPMENT");
        queryReport.addColItem("实时电流值/A","CURRVAL1");
        queryReport.addColItem("实时负载率","FZL_R1");
        queryReport.addColItem("额定电流/A","EDDL");
        queryReport.addColItem("红线值/A","HXZ");
        queryReport.addColItem("控制值/A","ZCXLZ");
        queryReport.addColItem("备注","");
        queryReport.addColItem("当日最大值时刻","MAXVAL_TIME");
        queryReport.addColItem("当日最大值/A","MAXVAL");
        queryReport.addColItem("当日最大负载率","FZL_MAX");
        queryReport.addColItem("昨日最大值时刻","MAXVAL_TIME_YDAY");
        queryReport.addColItem("昨日最大值/A","MAXVAL_YDAY");
        queryReport.addColItem("昨日最大负载率","FZL_MAX_YDAY");

//        queryReport.addActionItem("", Eval.getEditUrl("zdh_log","log_id","LOG_ID")
//                ,"修改本条日志","text","修改","");
//        queryReport.addActionItem("", Eval.getDeleteUrl("zdh_log","LOG_ID"),"删除本条日志","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: scadaData110
     * @Description:110kV线路
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut scadaData110() throws Exception {
        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());

        }

        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
//        queryReport.setTable("V_SCADA_DATA_LINE110");

        String tableName=String.format("(select a.*,case when a.fzl is null or a.fzl=0 then '' else  round(to_number(a.fzl),2) ||'%%' end fzl_r," +
                "b.maxval maxval_yday,b.maxval_time maxval_time_yday,b.fzl_max fzl_max_yday," +
                "case when a.zcxlz=0 then '' else b.maxval/a.zcxlz end  fzl_yday " +
                "from (select * from v_scada_data_line110  where ymd='%s' ) as a " +
                "left join (select * from v_scada_data_line110 where ymd='%s' )as b on a.scada_id=b.scada_id and a.name=b.name) xx",ymd,y_ymd);
        queryReport.setTable(tableName);

        queryReport.setSelectfldn("xx.*,case when substring(now(),0,10)='"+ymd+"' then to_char(CURRVAL) else '/' end CURRVAL1," +
                        "case when substring(now(),0,10)='"+ymd+"' then FZL_R else '/' end FZL_R1");

        queryReport.setOrderfldn("fzl_max desc nulls last");

        queryReport.addQuerySet("厂站","find_name","NAME");
        queryReport.addQuerySet("线路","find_equipment","EQUIPMENT");
//        queryReport.addQuerySet("日期","ymd","YMD");

        queryReport.addQuerySet("日期","ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,QueryReportFilter.QRFilterOper.equal);
        System.out.print( LocalDate.now().minusDays(1));

        queryReport.addColItem("厂站","NAME");
        queryReport.addColItem("线路","EQUIPMENT");
        queryReport.addColItem("实时电流值/A","CURRVAL1");
        queryReport.addColItem("实时负载率","FZL_R1");
        queryReport.addColItem("正常限流值/A","ZCXLZ");
        queryReport.addColItem("事故限流值/A","SGXLZ");
        queryReport.addColItem("当日最大值时刻","MAXVAL_TIME");
        queryReport.addColItem("当日最大值/A","MAXVAL");
        queryReport.addColItem("当日最大负载率","FZL_MAX");
        queryReport.addColItem("昨日最大值时刻","MAXVAL_TIME_YDAY");
        queryReport.addColItem("昨日最大值/A","MAXVAL_YDAY");
        queryReport.addColItem("昨日最大负载率","FZL_MAX_YDAY");

//        queryReport.addActionItem("", Eval.getEditUrl("zdh_log","log_id","LOG_ID")
//                ,"修改本条日志","text","修改","");
//        queryReport.addActionItem("", Eval.getDeleteUrl("zdh_log","LOG_ID"),"删除本条日志","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: scadaData220
     * @Description:220kV线路
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut scadaData220() throws Exception {
        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());

        }

        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
//        queryReport.setTable("V_SCADA_DATA_LINE110");

        String tableName=String.format("(select a.*,case when a.fzl is null or a.fzl=0 then '' else  round(to_number(a.fzl),2)||'%%' end fzl_r," +
                "b.maxval maxval_yday,b.maxval_time maxval_time_yday,b.fzl_max fzl_max_yday," +
                "case when a.zcxlz=0 then '' else b.maxval/a.zcxlz end fzl_yday " +
                "from (select * from v_scada_data_line220  where ymd='%s' ) as a " +
                "left join (select * from v_scada_data_line220 where ymd='%s' )as b on a.scada_id=b.scada_id) xx",ymd,y_ymd);
        queryReport.setTable(tableName);
        queryReport.setSelectfldn("xx.*,case when substring(now(),0,10)='"+ymd+"' then to_char(CURRVAL) else '/' end CURRVAL1," +
                "case when substring(now(),0,10)='"+ymd+"' then FZL_R else '/' end FZL_R1");

        queryReport.setWhere_ex("ymd='"+ymd+"'");

        queryReport.setOrderfldn("fzl_max desc nulls last");

        queryReport.addQuerySet("厂站","find_name","NAME");
        queryReport.addQuerySet("线路","find_equipment","EQUIPMENT");
//        queryReport.addQuerySet("日期","ymd","YMD");

        queryReport.addQuerySet("日期","ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,QueryReportFilter.QRFilterOper.equal,
                ymd,"","","");


        queryReport.addColItem("厂站","NAME");
        queryReport.addColItem("线路","EQUIPMENT");
        queryReport.addColItem("实时电流值/A","CURRVAL1");
        queryReport.addColItem("实时负载率","FZL_R1");
        queryReport.addColItem("正常限流值/A","ZCXLZ");
        queryReport.addColItem("事故限流值/A","SGXLZ");
        queryReport.addColItem("当日最大值时刻","MAXVAL_TIME");
        queryReport.addColItem("当日最大值/A","MAXVAL");
        queryReport.addColItem("当日最大负载率","FZL_MAX");
        queryReport.addColItem("昨日最大值时刻","MAXVAL_TIME_YDAY");
        queryReport.addColItem("昨日最大值/A","MAXVAL_YDAY");
        queryReport.addColItem("昨日最大负载率","FZL_MAX_YDAY");

//        queryReport.addActionItem("", Eval.getEditUrl("zdh_log","log_id","LOG_ID")
//                ,"修改本条日志","text","修改","");
//        queryReport.addActionItem("", Eval.getDeleteUrl("zdh_log","LOG_ID"),"删除本条日志","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: scadaData500
     * @Description:500kV线路
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut scadaData500() throws Exception {
        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());

        }

        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
//        queryReport.setTable("V_SCADA_DATA_LINE110");

        String tableName=String.format("(select a.*,case when a.fzl is null or a.fzl=0 then '' else  round(to_number(a.fzl),2)||'%%' end fzl_r," +
                "b.maxval maxval_yday,b.maxval_time maxval_time_yday,b.fzl_max fzl_max_yday," +
                "case when a.zcxlz=0 then '' else b.maxval/a.zcxlz end fzl_yday " +
                "from (select * from v_scada_data_line500  where ymd='%s' ) as a " +
                "left join (select * from v_scada_data_line500 where ymd='%s' )as b on a.scada_id=b.scada_id) xx",ymd,y_ymd);
        queryReport.setTable(tableName);

        queryReport.setSelectfldn("xx.*,case when substring(now(),0,10)='"+ymd+"' then to_char(CURRVAL) else '/' end CURRVAL1," +
                "case when substring(now(),0,10)='"+ymd+"' then FZL_R else '/' end FZL_R1");

        queryReport.setWhere_ex("ymd='"+ymd+"'");
        queryReport.setOrderfldn("fzl_max desc nulls last");

        queryReport.addQuerySet("厂站","find_name","NAME");
        queryReport.addQuerySet("线路","find_equipment","EQUIPMENT");
//        queryReport.addQuerySet("日期","ymd","YMD");

        queryReport.addQuerySet("日期","ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,QueryReportFilter.QRFilterOper.equal,
                ymd,"","","");


        queryReport.addColItem("厂站","NAME");
        queryReport.addColItem("线路","EQUIPMENT");
        queryReport.addColItem("实时电流值/A","CURRVAL1");
        queryReport.addColItem("实时负载率","FZL_R1");
        queryReport.addColItem("正常限流值/A","ZCXLZ");
        queryReport.addColItem("事故限流值/A","SGXLZ");
        queryReport.addColItem("当日最大值时刻","MAXVAL_TIME");
        queryReport.addColItem("当日最大值/A","MAXVAL");
        queryReport.addColItem("当日最大负载率","FZL_MAX");
        queryReport.addColItem("昨日最大值时刻","MAXVAL_TIME_YDAY");
        queryReport.addColItem("昨日最大值/A","MAXVAL_YDAY");
        queryReport.addColItem("昨日最大负载率","FZL_MAX_YDAY");

//        queryReport.addActionItem("", Eval.getEditUrl("zdh_log","log_id","LOG_ID")
//                ,"修改本条日志","text","修改","");
//        queryReport.addActionItem("", Eval.getDeleteUrl("zdh_log","LOG_ID"),"删除本条日志","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();

    }

    /**
     * @Title: omsFhdl
     * @Description:电网运行情况分析查询
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut omsFhdl() throws Exception {
        //每日新增一条数据
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String sqlToday = String.format("select count(*) MYCOUNT from oms_fhdl where ymd='%s'", LocalDate.now().toString());
            if ("0".equals(jdbcDao.getFieldValue(sqlToday, "MYCOUNT"))) {
                //自动新增一条数据
                String sqlNew = String.format("insert into oms_fhdl (FHDL_ID,YMD) values ('%s','%s')", jdbcDao.getSeqNextVal(), LocalDate.now().toString());
                jdbcDao.execute(sqlNew);
            }
            jdbcDao.close();

            QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
            queryReport.setpagenumber(1000);
            queryReport.setTable("OMS_FHDL A");

            String ocsidDgwg = CommonFunc.getConfigByKey("sys.ocsid_dgwg");
            queryReport.setSelectfldn("A.*,to_number(SUBSTRING(ymd,6,2)) || '月' MONTHS,SUBSTRING(ymd,6,5) DAYS," +
                    "(select round(avg(value)*24,2) from oms_scada_data_his b where b.ymd=a.ymd and b.scada_id='" + ocsidDgwg + "') CALC_JFDL");
            queryReport.setOrderfldn("ymd ASC nulls last");

            queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=OMS_FHDL&actiontype=add'>录入电量信息</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
                    "<a role=button target=_blank href='datalist.html?dataname=OMS_RISK'>运行风险管理</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
                    "<a role=button target=_blank href='datalist.html?dataname=OMS_XTSBXL'>因发热下调设备限流</a>");

            String year = CommonFunc.getHttpVar("find_year");
            if (StringUtils.isEmpty(year)) {
                year = String.valueOf(LocalDate.now().getYear());
            }

            queryReport.addQuerySet("年", "find_year", "to_number(SUBSTRING(ymd,1,4))", QueryReportFilter.QRFilterType.selarray, year,
                    QueryReportFilter.QRFilterOper.equal, "",
                    "", "2021;2022;2023;2024;2025;2026;2027;2028;2029;2030;2031;2032;2033", "");

            queryReport.addQuerySet("月", "find_month", "to_number(SUBSTRING(ymd,6,2))", QueryReportFilter.QRFilterType.selarray,
                    String.valueOf(LocalDate.now().getMonth().getValue()),
                    QueryReportFilter.QRFilterOper.in, "",
                    "", "1,2,3,4,5,6,7,8,9,10,11,12:全年;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12", "");

            queryReport.addColItem("月份", "MONTHS");
            queryReport.addColItem("日期", "DAYS");
            queryReport.addColItem("日最高负荷/MW", "DAY_MAX_FH");
            queryReport.addColItem("系统最高负荷/MW", "DAY_MAX_XTFH");
            queryReport.addColItem("光伏日电量/MWh", "DAY_GF");
            queryReport.addColItem("日电量/MWh", "DAY_DL", Eval.getEval_Fhdl_Modify("DAY_DL", "MODIFY_FLAG"));
            queryReport.addColItem("积分电量/MWh", "CALC_JFDL");
            queryReport.addColItem("OCS月累计电量/MWh", "MONTH_OCS_DL", Eval.getEval_Fhdl_Modify("MONTH_OCS_DL", "MODIFY_FLAG_MONTH"));
            queryReport.addColItem("OCS年累计电量/MWh", "YEAR_OCS_DL", Eval.getEval_Fhdl_Modify("YEAR_OCS_DL", "MODIFY_FLAG_YEAR"));
            //queryReport.addColItem("（市场修正）月累计电量/MWh","MONTH_XZ_DL");
            //queryReport.addColItem("（市场修正）年累计电量/MWh","YEAR_XZ_DL");
            queryReport.addColItem("网供指标早峰(MW)", "WGZB1");
            queryReport.addColItem("网供指标午峰(MW)", "WGZB2");
            queryReport.addColItem("网供指标晚峰(MW)", "WGZB3");

            queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zwdd/fhdl_modify_log.html?fhdl_id", "", "FHDL_ID", "FHDL_ID", ""),
                    "查看该天的负荷电量的修改记录", "text", "修改记录", "");

            queryReport.addActionItem("", Eval.getEditUrl("OMS_FHDL", "", "FHDL_ID")
                    , "修改该天的负荷电量资料", "text", "修改", "");
            //queryReport.addActionItem("", Eval.getDeleteUrl("OMS_FHDL","FHDL_ID"),"删除本条数据","text","删除","");

            queryReport.execQuery();

            return queryReport.getOutPut();
        }
    }

    /**
     * @Title: omsRisk
     * @Description:电网运行风险管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut omsRisk() throws Exception {
        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("OMS_RISK A");
        queryReport.setSelectfldn("A.*");
        queryReport.setOrderfldn("TIME_FROM DESC nulls last");
        queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=OMS_RISK&actiontype=add'>新增风险任务</a>&nbsp;");

        queryReport.addQuerySet("风险编号","find_risk_no","risk_no");
        queryReport.addQuerySet("标题","find_title","title");

        queryReport.addColItem("开始日期","TIME_FROM");
        queryReport.addColItem("结束日期","TIME_TO");
        queryReport.addColItem("风险等级","RISK_LEVEL");
        queryReport.addColItem("风险任务书","RISK_NO");
        queryReport.addColItem("标题","TITLE");
        queryReport.addColItem("管控要求","CONTENT");

        queryReport.addActionItem("", Eval.getEditUrl("OMS_RISK","","RISK_ID")
                ,"修改风险任务资料","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_RISK","RISK_ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: omsSubstation
     * @Description:变电站资料管理
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public QueryReportOutPut omsSubstation() throws Exception {
        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("OMS_SUBSTATION A");
        queryReport.setSelectfldn("A.*");
        queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=OMS_SUBSTATION&actiontype=add'>新增变电站资料</a>&nbsp;");

        queryReport.addQuerySet("变电站名称","find_substation_name","SUBSTATION_NAME");
        queryReport.addQuerySet("电压等级","find_vol_level","VOL_LEVEL");
        queryReport.addQuerySet("巡维中心","find_xwzx","XWZX");
        queryReport.addQuerySet("变电所","find_bds","BDS");

        queryReport.addColItem("变电站名称","SUBSTATION_NAME");
        queryReport.addColItem("电压等级","VOL_LEVEL");
        queryReport.addColItem("巡维中心","XWZX");
        queryReport.addColItem("变电所","BDS");

        queryReport.addActionItem("", Eval.getEditUrl("OMS_SUBSTATION","","SUBSTATION_ID")
                ,"修改变电站资料","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_SUBSTATION","SUBSTATION_ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }

    /**
     * @Title: zwgjList
     * @Description:主网告警查询
     * @return
     * @throws Exception
     * @author ldw
     * @throws
     */
    public QueryReportOutPut zwgjList() throws Exception {
        String find_source=CommonFunc.getHttpVar("find_source");
        String status=CommonFunc.getHttpVar("find_status");

        String callStatus = CommonFunc.getHttpVar("find_call_status");
        String flag = CommonFunc.getHttpVar("flag");
        String tmp_title = "";
        if ("DICP_ALARMINFO".equals(find_source)) {
            tmp_title = "告警信号异常通知";
        } else if ("V_SCADA_DATA_ZBBD".equals(find_source)) {
            tmp_title = "主变变低负载率大于90%重测温度";
        }

        QueryReport queryReport = new QueryReport(tmp_title+"实时告警", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("ALARM_INFO A");
        queryReport.setOrderfldn(" a.create_time desc ");
        queryReport.setSelectfldn("A.*");

        StringBuilder whereBuilder = new StringBuilder();
        whereBuilder.append(" A.alarm_source = '").append(find_source).append("'");
        if (StringUtils.isNotBlank(status)) {
            whereBuilder.append(" and a.status in (").append(status).append(")");
        }
        if (StringUtils.isNotBlank(callStatus)) {
            String[] split = callStatus.split(",");
            callStatus = Arrays.stream(split).map(e -> "'" + e + "'").collect(Collectors.joining(","));
            whereBuilder.append(" and a.call_status in (").append(callStatus).append(")");
        }

        queryReport.setWhere_ex(whereBuilder.toString());

        queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=ALARM_INFO&actiontype=add'>新增告警</a>&nbsp;");

        queryReport.addQuerySet("变电站名称","find_stationName","STATION_NAME");
        if ("DICP_ALARMINFO".equals(find_source) && !"1".equals(flag)) {
            queryReport.addQuerySet("通知状态", "find_call_status", null, QueryReportFilter.QRFilterType.selarray,
                    StringUtils.isBlank(callStatus) ? "'待通知','通知中','接口异常','通知成功','通知失败'" : "'" + callStatus + "'",
                    QueryReportFilter.QRFilterOper.in, "", "",
                    ":全部;待通知:待通知;通知中:通知中;接口异常:接口异常;通知成功:通知成功;通知失败:通知失败;", "");
        }
        queryReport.addQuerySet("关键字","find_key_word", "STATION_NAME||CONTENT");
        queryReport.addColItem("id","ID", null, null, null, null, null, "", "false", "0px");
        queryReport.addColItem("告警时间","ALARM_TIME");
        queryReport.addColItem("厂站","STATION_NAME");
        queryReport.addColItem("告警内容","CONTENT");
        if ("DICP_ALARMINFO".equals(find_source)) {
            queryReport.addColItem("告警状态","STATUS");
            queryReport.addColItem("告警等级","LEVEL");
        }
        queryReport.addColItem("电话通知状态","CALL_STATUS");
        queryReport.addColItem("语音模板内容","AUDIO_CONTENT");


        queryReport.addActionItem("", Eval.js("showPreviewModal", "ID"),
                "生成语音", "text", "生成语音", "");
        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zwgj/zwgj_gjtz_addtz.html?type=view&gj_id","","ID","ID",""),
                "通知记录","text","通话状态","");
        queryReport.addActionItem("", Eval.js("callOnce","ID"),
                "一键拨打","text","一键拨打","");
        // 语音模板
        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zwgj/zwgj_gjtz_template_edit.html?type=view&gj_id","","ID","ID",""),
                "语音模板","text","语音模板","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * @Title: zwgjUserList
     * @Description:主网告警通知人员信息查询
     * @return
     * @throws Exception
     * @author ldw
     * @throws
     */
    public QueryReportOutPut alarmCallPhone() throws Exception {
        String findSubstationName=CommonFunc.getHttpVar("find_station_name");
        QueryReport queryReport = new QueryReport("告警电话列表配置", request, sysConfig, datasourceConfig);

        String table = "\"OMS_ZF\".\"ALARM_PHONE_LIST\" phones " +
                (StringUtils.isNotBlank(findSubstationName) ? "where phones.unit_name like '%" + findSubstationName + "%'" : "") +
                "\tORDER BY phones.priority";

        queryReport.setpagenumber(100);
        queryReport.setTable(table);
        queryReport.setSelectfldn("*");
        queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=ALARM_PHONE_LIST&actiontype=add'>新增告警电话信息</a>&nbsp;");

        queryReport.addColItem("部门","UNIT_NAME");
        queryReport.addColItem("联系电话","PHONE");
        queryReport.addColItem("优先级","PRIORITY");


//        queryReport.addActionItem("", Eval.getEditUrl("ALARM_PHONE_LIST","","ID")
//                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.js("operationAlarm","ID", "edit")
                ,"修改","text","修改","");
//        queryReport.addActionItem("", Eval.getDeleteUrl("ALARM_PHONE_LIST","ID"),"删除本条数据","text","删除","");
        queryReport.addActionItem("", Eval.js("operationAlarm","ID", "delete"),"删除本条数据","text","删除","");


        queryReport.execQuery();

        queryReport.addHtmlPageQuery("站点","find_station_name", findSubstationName,"UNIT_NAME");
        return queryReport.getOutPut();
    }

    /**
     * @Title: zwgjYwgdList
     * @Description:主网告警运维工单信息查询
     * @return
     * @throws Exception
     * @author ldw
     * @throws
     */
    public QueryReportOutPut zwgjYwgdList() throws Exception {
//        String find_sfqr=CommonFunc.getHttpVar("find_sfqr");
        QueryReport queryReport = new QueryReport("运维工单信息", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("ZWGJ_WXGD_INFO a left join ALARM_INFO B on a.GJID=b.PID ");

        queryReport.setSelectfldn("a.*,b.GJDJ,b.GJNR");
//        queryReport.setWhere_ex(" SFQR = '"+find_sfqr+"'");
//        queryReport.setOther("<a role=button target=_blank href='dataadd.html?dataname=ZWGJ_WXGD_INFO&actiontype=add'>新增人员信息</a>&nbsp;");


        queryReport.addQuerySet("运维工单号","find_ywgdh","YWGDH");


        queryReport.addColItem("运维工单编号","YWGDH");
        queryReport.addColItem("工单状态","GDZT");
        queryReport.addColItem("告警等级","b.GJDJ");
        queryReport.addColItem("告警内容","GJNR");
        queryReport.addColItem("接收人","JSRID");
        queryReport.addColItem("接收状态","JSZT");

        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/zwgj/zwgj_gjtz_addtz.html?type=check&gj_id","","GJID","GJID",""),
                "告警详情","text","告警详情","");

//        queryReport.addActionItem("", Eval.getEditUrl("ZWGJ_WXGD_INFO","","a.PID")
//                ,"修改","text","修改","");
//        queryReport.addActionItem("", Eval.getDeleteUrl("ZWGJ_WXGD_INFO","a.PID"),"删除本条数据","text","删除","");

        queryReport.execQuery();

        return queryReport.getOutPut();
    }


    public QueryReportOutPut workUnit() throws Exception {
        QueryReport queryReport = new QueryReport("工作单位管理", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("WORK_UNIT");
        queryReport.setSelectfldn(" * ");
        queryReport.setOrderfldn("UNIT ASC");

        queryReport.addQuerySet("单位","find_unit","UNIT");

        queryReport.addColItem("单位","UNIT");
        queryReport.addColItem("简称","SHORT_NAME");

        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=WORK_UNIT&actiontype=add\")' type='button'>新增</button>");

        queryReport.addActionItem("", Eval.getEditUrl("WORK_UNIT","","ID")
                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("WORK_UNIT","ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut transformerSubstation() throws Exception {
        QueryReport queryReport = new QueryReport("变电站管理", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("TRANSFORMER_SUBSTATION A left join WORK_UNIT B ON B.ID = A.UNIT_ID");
        queryReport.setSelectfldn(" A.ID,A.NAME, A.UNIT_ID, A.VOLTAGE, A.MATCH_PATTERN, A.TOWN, A.CREW_JB_ID, A.CREW_TX_ID, (case  when A.IS_THIRTEEN_ADD_SIXTEEN = 1 then '是' else '否' end case) AS IS_THIRTEEN_ADD_SIXTEEN , B.UNIT as UNIT_NAME ");
        queryReport.setOrderfldn("UNIT_NAME ASC, NAME ASC");

        queryReport.addQuerySet("单位","find_unit","B.UNIT");
        queryReport.addQuerySet("变电站","find_station","NAME");

        queryReport.addColItem("单位","UNIT_NAME");
        queryReport.addColItem("变电站","NAME");
        queryReport.addColItem("正则","MATCH_PATTERN");
        queryReport.addColItem("镇区","TOWN");

        try(JdbcDao jdbcDao=new JdbcDao()) {
            ArrayList<HashMap<String, String>> result = jdbcDao.getResult(
                    "select COLUMN_NAME from SYS_TABLE_COLUMN WHERE TABLE_ID = (select TABLE_ID from SYS_TABLE WHERE TABLE_CODE = 'TRANSFORMER_SUBSTATION') and column_code = 'IS_THIRTEEN_ADD_SIXTEEN'");
            jdbcDao.close();

            if (result != null && !result.isEmpty()) {
                queryReport.addColItem(result.get(0).get("COLUMN_NAME"), "IS_THIRTEEN_ADD_SIXTEEN");
            } else {
                queryReport.addColItem("是否14+16", "IS_THIRTEEN_ADD_SIXTEEN");
            }


            queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=TRANSFORMER_SUBSTATION&actiontype=add\")' type='button'>新增</button>");

            queryReport.addActionItem("", Eval.getEditUrl("TRANSFORMER_SUBSTATION", "", "ID")
                    , "修改", "text", "修改", "");
            queryReport.addActionItem("", Eval.getDeleteUrl("TRANSFORMER_SUBSTATION", "ID"), "删除本条数据", "text", "删除", "");

            queryReport.execQuery();
            return queryReport.getOutPut();
        }
    }

    public QueryReportOutPut crew() throws Exception {
        QueryReport queryReport = new QueryReport("班组管理", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("CREW C LEFT JOIN WORK_UNIT B ON C.UNIT_ID = B.ID");
        queryReport.setSelectfldn(" C.*, B.UNIT AS UNIT_NAME ");
        queryReport.setOrderfldn("UNIT_NAME ASC, C.NAME ASC");

        queryReport.addQuerySet("单位","find_unit","B.UNIT");
        queryReport.addQuerySet("班组","find_crew","C.NAME");

        queryReport.addColItem("单位","UNIT_NAME");
        queryReport.addColItem("班组","NAME");
        queryReport.addColItem("类型","TYPE");
        queryReport.addColItem("简称","SHORT_NAME");


        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=CREW&actiontype=add\")' type='button'>新增</button>");

        queryReport.addActionItem("", Eval.getEditUrl("CREW","","ID")
                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("CREW","ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut crewMember() throws Exception {
        QueryReport queryReport = new QueryReport("班组成员管理", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("CREW_MEMBER m left join CREW c on m.crew_id = c.id left join work_unit w on c.unit_id = w.id");
        queryReport.setSelectfldn(" m.ID, m.NAME, m.CREW_ID, (case when m.IS_MANAGER = 1 then '是' else '否' end case) IS_MANAGER, c.name AS CREW_NAME, w.UNIT AS UNIT_NAME");
        queryReport.setOrderfldn("UNIT_NAME ASC, CREW_NAME ASC, NAME ASC");


        queryReport.addQuerySet("单位","find_unit","w.UNIT");
        queryReport.addQuerySet("班组","find_crew_name","c.name");
        queryReport.addQuerySet("班组成员姓名","find_name","m.NAME");


        queryReport.addColItem("所属单位","UNIT_NAME");
        queryReport.addColItem("所在班组","CREW_NAME");
        queryReport.addColItem("成员名称","NAME");
        queryReport.addColItem("是否负责人","IS_MANAGER");
        queryReport.addColItem("标识","FLAG");


        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=CREW_MEMBER&actiontype=add\")' type='button'>新增</button>");

        queryReport.addActionItem("", Eval.getEditUrl("CREW_MEMBER","","ID")
                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("CREW_MEMBER","ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut secondPlanRecord() throws Exception {
        QueryReport queryReport = new QueryReport("每周警告数量管理", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("SECOND_PLAN_RECORD");
        queryReport.setSelectfldn(" NUM, MAIN_NUM, SUB_NUM, FORMATDATETIME(TIME, 'yyyy-MM-dd') AS TIME");

        StringBuilder timeRange = new StringBuilder();
        if (StringUtils.isNotBlank(request.getParameter("find_start_time"))) {
            timeRange.append(" TIME >= '").append(request.getParameter("find_start_time")).append("'");
            if (StringUtils.isNotBlank(request.getParameter("find_end_time"))) {
                timeRange.append(" and TIME <= '").append(request.getParameter("find_end_time")).append("'");
            }
        }

        queryReport.setWhere_ex(timeRange.toString());
        queryReport.setOrderfldn(" TIME DESC ");

        queryReport.addQuerySet("开始时间","find_start_time","");
        queryReport.addQuerySet("结束时间","find_end_time","");


        queryReport.addColItem("时间","TIME");
        queryReport.addColItem("二次作业数量","NUM");
        queryReport.addColItem("主调作业数量","MAIN_NUM");
        queryReport.addColItem("配调作业数量","SUB_NUM");


        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=SECOND_PLAN_RECORD&actiontype=add\")' type='button'>新增</button>");

        queryReport.addActionItem("", Eval.getDeleteUrl("SECOND_PLAN_RECORD","TIME"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut zbryb() throws Exception {
        QueryReport queryReport = new QueryReport("主网调控员值班表", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("OMS_DUTY");
        queryReport.setSelectfldn(" ID, NAME, DATE, TYPE, REMARK");
        queryReport.setOrderfldn(" DATE ASC ");

        queryReport.addQuerySet("值班人","find_name","NAME");
        queryReport.addQuerySet("值班日期","find_time_start","nvl(DATE,'')",
                QueryReportFilter.QRFilterType.oradate,"", QueryReportFilter.QRFilterOper.greaterEqual);
        queryReport.addQuerySet("-","find_time_end","nvl(DATE,'')",
                QueryReportFilter.QRFilterType.oradate,"", QueryReportFilter.QRFilterOper.lessEqual);

        queryReport.addColItem("值班人","NAME");
        queryReport.addColItem("值班日期","DATE");
        queryReport.addColItem("班次类型","TYPE");
        queryReport.addColItem("备注","REMARK");


        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=OMS_DUTY&actiontype=add\")' type='button'>新增</button>");

        queryReport.addActionItem("", Eval.getEditUrl("OMS_DUTY","","ID")
                ,"修改","text","修改","");
        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_DUTY","ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }



    /**
     * @Title: czxx_ddbhgl
     * @Description:自动化厂站信息_调度编号图
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut czxx_ddbhgl() throws Exception {
        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        //queryReport.setTitle_short("调度编号图管理");
        queryReport.setpagenumber(50);
        queryReport.setTable("V_DDBHGL");

        //需要使用统计功能，因此必须指定列，不可使用*
        queryReport.setSelectfldn("ID,F_TDMC,F_CZM,F_XGJXT,F_BZQM,F_BZSJ,F_BZFJ,F_SSLX,F_BZBZ,F_SHRQM," +
                "F_SHSJ,F_SHYJ,F_ZRSHQM,F_ZRSHSJ,F_ZRSHYJ,F_DDYFRQM,F_YFSJ,F_YFBZ,F_BDXCQRQM,F_QRSJ," +
                "F_GATEWAY_1,F_QRBZ,F_DDZXRQM,F_DDZXSJ,F_ZFBHT,F_SFGXSTBHT,F_BZSX,F_ZDHZXQM,F_ZXSJ," +
                "F_SFYJXG,F_SFGXSBTZ,F_ZDHBZSX,F_BDXCZXQM,F_HDSJ,F_SFYXCYZ,F_SFSTBHT,F_BDBZSX,F_DDQRQM," +
                "F_DDQRSJ,F_DDQRBZSX,F_GATEWAY_2,F_FLOW_STATUS,F_FLOW_STATUS_CHILD,F_DYDJ,F_BH,F_TJTBH," +
                "F_DDBH,F_TDZT,F_TXBH,F_DDQRR,F_DDZCQRSJ,F_QRYJ,F_JHZXRQ,F_YTZCLR,F_YTZCLSJ,F_YTZCLYJ," +
                "F_SFXBDQR,F_QRR,F_QSJ,F_DDQRYJ,F_BDHDYY,F_APPLY_STATE,F_ZDH,STATE_ENABLE,DEPARTMENTS," +
                "F_ZBD,ZT,ZPD,F_LX,TDMC");
        queryReport.setFlagImport(1);
        queryReport.setOrderfldn("ID desc");

        queryReport.addQuerySet("图档名称","find_mc","nvl(TDMC,'')");
        queryReport.addQuerySet("厂站名称","find_czmc","nvl(F_TDMC||F_CZM,'')");
        queryReport.addQuerySet("类型","find_sslx","LX", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.notin,"","",
                "0,2,3:T接图;1:非T接图","");

        queryReport.addQuerySet("状态","find_state_enable","nvl(ZT,0)", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in,"","",
                "1:执行中;2:历史","");

        queryReport.addColItem("图档名称","F_TDMC");
        if(request.getParameter("find_sslx").equals("1")){
            queryReport.addColItem("厂站名称","F_CZM");
        }
        queryReport.addColItem("电压等级","F_DYDJ");
        queryReport.addColItem("类型","F_LX");
        queryReport.addColItem("执行时间","F_ZXSJ");
//        queryReport.addColItem("编制签名","F_BZQM");
//        queryReport.addColItem("图档更新说明","F_BZBZ");
//        queryReport.addColItem("编号","F_DDBH");
//        queryReport.addColItem("计划执行日期","F_JHZXRQ");
//        queryReport.addColItem("主/配调","F_ZBD");
        queryReport.addColItem("状态","STATE_ENABLE");

        queryReport.addActionItem("", Eval.getOpenUrlAction("flowform.html?flow_code=DDBHGL&data_id","","ID","ID",""),
                "详细信息","text","详细信息","");
        if(!request.getParameter("find_state_enable").equals("1,2")){
            queryReport.addActionItem("", Eval.getOpenUrlAction("datalist.html?dataname=CZXX_DDBHGL&find_sslx="+request.getParameter("find_sslx")+"&find_czmc=&find_state_enable=1,2&find_mc","","TDMC","TDMC",""),
                "历史版本","text","历史版本","");
        }
        queryReport.execQuery();
        return queryReport.getOutPut();

    }


  /**
  
     * @Title: czxx_zdhczdz
     * @Description:自动化厂站信息_自动化厂站定值
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut czxx_zdhczdz() throws Exception {
        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        //queryReport.setTitle_short("自动化厂站定值");
        queryReport.setpagenumber(50);
        queryReport.setTable("V_ZDHCZDZZDGLLC");

        //需要使用统计功能，因此必须指定列，不可使用*
        queryReport.setSelectfldn("ID,F_BZR,F_BZSJ,F_SHR,F_SHSJ,F_SHYJ,F_PZR,F_PZSJ,F_PZYJ,F_CKZZBH,F_CZMC," +
                "F_SSJG,F_SBMC,F_ZZXH,F_BBH,F_ZZDZ,F_TABH,F_TVBH,F_YMDZ,F_FJ,F_YDZZBH,F_YDCZMC,F_YDZZXH," +
                "F_YDZZDZ,F_YDYMDZ,F_SJBYFS,F_YDFJ,F_YDXXZFBH,F_XXCZMC,F_DKDW,F_XXZZXH,F_SCCJ,F_ZFBXH," +
                "F_XXFJ,F_ZXR,F_ZXSJ,F_ZXJG,F_FLOW_STATUS,F_FLOW_STATUS_CHILD,F_JBM,F_JHLX,F_CKZXR,F_CKZXSJ," +
                "F_CKZXJG,F_YDZXR,F_YDZXSJ,F_YDZXJG,F_XXZXR,F_XXZXSJ,F_XXZXJG,F_SSSJ,F_YDXH,F_XXXH,F_XXGCZSX," +
                "F_XXGCZXX,F_XXSSJSX,F_XXSSJXX,F_ZFLX,F_XXSSSJ,F_SFSBZD,F_GATEWAY1,F_XFR,F_XFSJ,F_XFYJ,F_BH," +
                "GDOMSID,F_DDZX,F_CZMCS,F_CKCZMC,F_CKSSJG,F_CKSBMC,F_CKZZXH,F_CKBBH,F_CKZZDZ,F_CKTABH,F_CKTVBH," +
                "F_CSYMDZ,F_CKFJ,F_CKDZDLX,F_YDDZDLX,F_DGDW,CKYWID,YDYWID,XXYWID,F_XXDZDLX,F_CKCZMCID," +
                "F_YDCZMCID,F_XXCZMCID,F_XXJHSSKSSJ,F_XXJHSSJSSJ,F_CKJHSSKSSJ,F_CKJHSSJSSJ,F_YDJHSSKSSJ," +
                "F_YDJHSSJSSJ,F_YDSQDW,F_YDSQDWID,F_CKSQDW,F_CKSQDWID,F_XXCZID,F_YDCZID,F_CKCZID,F_DGDWID," +
                "F_SBID,F_DZDLX,F_CKZZID,F_PMSZID,PMSID,F_CKBDZZX,F_CKBDZSJ,F_CKBDZZXYJ,F_YDBDZZXR,F_YDBDZZXSJ," +
                "F_YDBDZZXYJ,F_YDXXBDZZXR,F_YDXXBDZZXSJ,F_YDXXBDZZXYJ,F_BGYY,F_ZT,F_CZZDHZZSHR,F_CZZDHZZSHSJ," +
                "F_CZZDHZZSHYJ,F_ZZZDHZZSHR,F_ZZZDHZZSHSJ,F_ZZZDHZZSHYJ,F_ZDSHR,F_ZDSHSJ,F_ZDSHYJ,F_ZZZDHZXR," +
                "F_ZZZDHZXSJ,F_ZZZDHZXYJ,F_CZZDHZXR,F_CZZDHZXSJ,F_CZZDHZXYJ,F_ZDHFJ,F_ZDFJ,F_JHYSKSRQ," +
                "F_JHYSJSRQ,F_LX,F_SJKX,ZT,LX");
        queryReport.setFlagImport(1);
        queryReport.setOrderfldn("ID desc");

        queryReport.addQuerySet("厂站名称","find_czmc","F_CZMC");
//        queryReport.addQuerySet("类型","find_lx","nvl(LX,0)", QueryReportFilter.QRFilterType.selarray,
//                "", QueryReportFilter.QRFilterOper.in,"","",
//                "0,1,2,3:全部;1:基建站;2:间隔改造;3:信号规范化","");
        queryReport.addQuerySet("状态","find_state_enable","nvl(ZT,0)", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in,"","",
                "1:执行中;2:历史","");

        queryReport.addColItem("厂站名称","F_CZMC");
        queryReport.addColItem("类型","F_LX");
        queryReport.addColItem("信息编号","F_ZFBXH");
        queryReport.addColItem("执行时间","F_ZZZDHZXSJ");
        queryReport.addColItem("状态","F_ZT");
//        queryReport.addColItem("计划验收开始时间","F_JHYSKSRQ");
//        queryReport.addColItem("计划验收结束时间","F_JHYSJSRQ");
//        queryReport.addColItem("变更原因","F_BGYY");

        queryReport.addActionItem("", Eval.getOpenUrlAction("flowform.html?flow_code=ZDHCZDZZDGLLC&data_id","","ID","ID",""),
                "详细信息","text","详细信息","");
        if(!request.getParameter("find_state_enable").equals("1,2")){
            queryReport.addActionItem("", Eval.getOpenUrlAction("datalist.html?dataname=CZXX_ZDHCZDZ&find_state_enable=1,2&find_czmc","","F_CZMC","F_CZMC",""),
                    "历史版本","text","历史版本","");
        }
        queryReport.execQuery();
        return queryReport.getOutPut();

    }


    /**

     * @Title: v_oms_jzb_busbar
     * @Description:母线电压
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut oms_jzb_busbar() throws Exception {
        QueryReport queryReport = new QueryReport("母线电压", request, sysConfig, datasourceConfig);

        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());
        }

        queryReport.setpagenumber(100);
        queryReport.setTable("oms_zf.v_oms_jzb_busbar a left join  oms_zf.oms_scada_data b on a.id=jzb_id");
        queryReport.setSelectfldn("a.NAME,EQUIPMENT,DYDJ,KZZ,ZCXLZ,YMD,MAXVAL_TIME,MAXVAL,MINVAL_TIME,MINVAL");

        queryReport.setFlagImport(1);

        queryReport.setOrderfldn(" YMD DESC ");

        queryReport.addQuerySet("日期","ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,
                QueryReportFilter.QRFilterOper.equal);
        //queryReport.addQuerySet("日期","find_ymd","YMD");
        queryReport.addQuerySet("设备名称","find_equipment","EQUIPMENT");
        queryReport.addQuerySet("厂站名称","find_name","NAME");

        queryReport.addColItem("厂站名称","NAME");
        queryReport.addColItem("设备名称","EQUIPMENT");
        queryReport.addColItem("电压等级","DYDJ");
        queryReport.addColItem("上限","ZCXLZ");
        queryReport.addColItem("下限","KZZ");
        queryReport.addColItem("统计时间","YMD");
        queryReport.addColItem("最大值时间","MAXVAL_TIME");
        queryReport.addColItem("最大值","MAXVAL");
        queryReport.addColItem("最小值时间","MINVAL_TIME");
        queryReport.addColItem("最小值","MINVAL");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }
    /**

     * @Title: oms_brk500
     * @Description:500开关
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut oms_brk500() throws Exception {
        QueryReport queryReport = new QueryReport("500kV开关", request, sysConfig, datasourceConfig);

        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());
        }

        queryReport.setpagenumber(100);
        queryReport.setTable("oms_zf.V_OMS_JZB_BRK500 a left join  oms_zf.oms_scada_data b on a.id=jzb_id");
        queryReport.setSelectfldn("a.NAME,EQUIPMENT,DYDJ,KZZ,ZCXLZ,YMD,MAXVAL_TIME,MAXVAL,MINVAL_TIME,MINVAL");

        queryReport.setFlagImport(1);

        queryReport.setOrderfldn(" YMD DESC ");

        queryReport.addQuerySet("日期","find_ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,
                QueryReportFilter.QRFilterOper.equal);
        queryReport.addQuerySet("设备名称","find_equipment","EQUIPMENT");
        queryReport.addQuerySet("厂站名称","find_name","NAME");

        queryReport.addColItem("厂站名称","NAME");
        queryReport.addColItem("设备名称","EQUIPMENT");
        queryReport.addColItem("正常限流值/A","ZCXLZ");
        queryReport.addColItem("当日最大值时刻","MAXVAL_TIME");
        queryReport.addColItem("当日最大值/A","MAXVAL");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**

     * @Title: v_oms_jzb_busbar
     * @Description:母线电压
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut oms_kgb10() throws Exception {
        QueryReport queryReport = new QueryReport("10kV开关柜", request, sysConfig, datasourceConfig);

        String ymd= CommonFunc.getHttpVar("ymd");
        String y_ymd = "";//获取昨天日期
        if (ymd.isEmpty()){
            ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");
            y_ymd = String.valueOf(LocalDate.now().minusDays(1));
        }else{
            Date d = CommonFunc.string2Date(ymd,"yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            y_ymd = sdf.format(calendar.getTime());
        }

        queryReport.setpagenumber(100);
        queryReport.setTable("oms_zf.V_OMS_JZB_KGB10 a left join  oms_zf.oms_scada_data b on a.id=jzb_id");
        queryReport.setSelectfldn("a.NAME,EQUIPMENT,DYDJ,KZZ,ZCXLZ,YMD,MAXVAL_TIME,MAXVAL,MINVAL_TIME,MINVAL");

        queryReport.setFlagImport(1);

        queryReport.setOrderfldn(" YMD DESC ");

        queryReport.addQuerySet("日期","find_ymd","YMD",QueryReportFilter.QRFilterType.oradate,ymd,
                QueryReportFilter.QRFilterOper.equal);

        queryReport.addQuerySet("设备名称","find_equipment","EQUIPMENT");
        queryReport.addQuerySet("厂站名称","find_name","NAME");

        queryReport.addColItem("厂站名称","NAME");
        queryReport.addColItem("设备名称","EQUIPMENT");
        queryReport.addColItem("正常限流值/A","ZCXLZ");
        queryReport.addColItem("当日最大值时刻","MAXVAL_TIME");
        queryReport.addColItem("当日最大值/A","MAXVAL");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }


    /**

     * @Title: zdhgj_all
     * @Description:自动化智能告警
     * @return
     * @throws Exception
     * @author 广州卓凡冯承基
     * @throws
     */
    public QueryReportOutPut zdhgj_all() throws Exception {
        QueryReport queryReport = new QueryReport("自动化智能告警", request, sysConfig, datasourceConfig);
        String start_time=CommonFunc.getHttpVar("start_time");
        String end_time=CommonFunc.getHttpVar("end_time");
        String nav_item = CommonFunc.getHttpVar("nav_item");
        int num = Integer.parseInt(nav_item);
        String[] group = {"","事故","异常","越限","变位","告知","智能",""};
        String table_name = "";

        List<String> list = getMonth(start_time,end_time);//获取查询跨过的月份，拼凑查询语句
        for (int i=0;i < list.size(); i++) {
            table_name += "UNION select * from ZWOCS.ALARM_ALLEVENTLOG"+list.get(i);
        }
        queryReport.setpagenumber(100);

        //统计分析信息
        if(num!=7){
            table_name = "(select OBJECT_OID,count(ALARM_CONTENT) as AORNUM,ALARM_CONTENT,ALARM_GROUP,PIC_NAME from  " +
                    "("+table_name.substring(6)+")" + "where ACTING_TIME>='"+start_time+"' and ACTING_TIME<='"+
                    end_time+"' group by OBJECT_OID,ALARM_CONTENT,ALARM_GROUP,PIC_NAME)";

            queryReport.setTable(table_name);
            queryReport.setSelectfldn("OBJECT_OID,AORNUM,ALARM_CONTENT,ALARM_GROUP,PIC_NAME");

            //StringBuilder timeRange = new StringBuilder();
            if(num!=0 && num!=6){
                queryReport.setWhere_ex("ALARM_GROUP like '%"+group[num]+"%'");
            }
            queryReport.setOrderfldn(" AORNUM DESC ");
            queryReport.addQuerySet("关键字","find_gjz","ALARM_CONTENT||ALARM_GROUP||PIC_NAME");
            queryReport.addQuerySet("厂站名称","find_czmc","PIC_NAME");


            queryReport.addColItem("告警内容","ALARM_CONTENT");
            queryReport.addColItem("告警次数","AORNUM");
            queryReport.addColItem("告警类型","ALARM_GROUP");
            queryReport.addColItem("厂站名称","PIC_NAME");
        }else{
            //详细信息
            queryReport.setTable("("+table_name.substring(6)+")");
            queryReport.setSelectfldn("REF_OID,ALARM_GROUP,CONFIRM_FLAG,CONFIRM_TIME,ALARM_CONTENT,ACTING_DESC,PIC_NAME");

            queryReport.setOrderfldn(" CONFIRM_TIME DESC ");
            queryReport.addQuerySet("关键字","find_gjz","ALARM_CONTENT||ACTING_DESC||PIC_NAME");
            queryReport.addQuerySet("厂站名称","find_czmc","PIC_NAME");

            queryReport.addColItem("序号","REF_OID");
            queryReport.addColItem("事件级别","ALARM_GROUP");
            queryReport.addColItem("确认标志","CONFIRM_FLAG");
            queryReport.addColItem("时间","CONFIRM_TIME");
            queryReport.addColItem("告警内容","ALARM_CONTENT");
            queryReport.addColItem("动作描述","ACTING_DESC");
            queryReport.addColItem("厂站名称","PIC_NAME");
        }



        //queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=SECOND_PLAN_RECORD&actiontype=add\")' type='button'>新增</button>");

        //queryReport.addActionItem("", Eval.getDeleteUrl("SECOND_PLAN_RECORD","TIME"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**

     * @Title: pwgj_all
     * @Description:配网智能告警
     * @return
     * @throws Exception
     * @author 广州卓凡梁狄文
     * @throws
     */
    public QueryReportOutPut pwgj_all() throws Exception {
        QueryReport queryReport = new QueryReport("配网智能告警", request, sysConfig, datasourceConfig);
        String start_time=CommonFunc.getHttpVar("start_time");
        String end_time=CommonFunc.getHttpVar("end_time");
        String nav_item = CommonFunc.getHttpVar("nav_item");
        int num = Integer.parseInt(nav_item);
        String[] group = {"","事故","异常","越限","变位","告知","智能",""};
        String table_name = "";

        queryReport.setpagenumber(100);

        //统计分析信息
//        if(num==6){
//            table_name = "(select OTHER_DATA,count(OTHER_DATA) as AORNUM,station_name,voltage_level from (\n" +
//                    "SELECT \n" +
//                    "CONTENT,\n" +
//                    "    SUBSTR(CONTENT, 1, 19) AS date_time, -- 提取日期和时间部分\n" +
//                    "    REGEXP_SUBSTR(CONTENT, '[0-9]+kV') AS voltage_level, -- 提取电压等级（如110kV）\n" +
//                    "    CASE \n" +
//                    "        WHEN INSTR(CONTENT, 'kV') >= 32 or INSTR(CONTENT, 'kV') = 0  THEN\n" +
//                    "              SUBSTR(\n" +
//                    "        REGEXP_SUBSTR(CONTENT, '[^ ]+站'),\n" +
//                    "        INSTR(REGEXP_SUBSTR(CONTENT, '[^ ]+站'), 'kV'), -- 从kV后开始\n" +
//                    "        INSTR(SUBSTR(REGEXP_SUBSTR(CONTENT, '[^ ]+站'), INSTR(REGEXP_SUBSTR(CONTENT, '[^ ]+站'), 'kV') + 2), '站')+1 -- 找到“站”字\n" +
//                    "    )\n" +
//                    "        ELSE\n" +
//                    "              SUBSTR(\n" +
//                    "        REGEXP_SUBSTR(CONTENT, '[^ ]+站'),\n" +
//                    "        INSTR(REGEXP_SUBSTR(CONTENT, '[^ ]+站'), 'kV')+2, -- 从kV后开始\n" +
//                    "        INSTR(SUBSTR(REGEXP_SUBSTR(CONTENT, '[^ ]+站'), INSTR(REGEXP_SUBSTR(CONTENT, '[^ ]+站'), 'kV') + 2), '站') -- 找到“站”字\n" +
//                    "    )\n" +
//                    "     END  station_name, -- 提取站点名称（如大龙站）\n" +
//                    "  SUBSTR(CONTENT,26) AS other_data\n" +
//                    "FROM \n" +
//                    "    PWOCS.DMS_SMART_EVENT_WARN_INFO \n" +
//                    "where OCCUR_TIME>='"+start_time+"' and OCCUR_TIME<='"+end_time+"'"+
//                    "   ) \n" +
//                    "   group by OTHER_DATA,station_name,voltage_level)";
//
//
//            queryReport.setTable(table_name);
//            queryReport.setSelectfldn("OTHER_DATA,AORNUM,STATION_NAME,VOLTAGE_LEVEL");
//
//            queryReport.setOrderfldn(" AORNUM DESC ");
//            queryReport.addQuerySet("关键字","find_gjz","OTHER_DATA||STATION_NAME||VOLTAGE_LEVEL");
//            queryReport.addQuerySet("厂站名称","find_czmc","STATION_NAME");
//
//
//            queryReport.addColItem("告警内容","OTHER_DATA");
//            queryReport.addColItem("告警次数","AORNUM");
//            // queryReport.addColItem("告警类型","ALARM_GROUP");
//            queryReport.addColItem("厂站名称","STATION_NAME");
//        }
//        //变位事件
//        if(num==4){
//            table_name = "(select count(res_info) as AORNUM,res_info,station_name from\n" +
//                    "(\n" +
//                    "SELECT\n" +
//                    "   CONTENT,OCCUR_TIME,\n" +
//                    "    REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 3) AS dev_name,\n" +
//                    "    REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 4) AS line_name,\n" +
//                    "    REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 5) AS station_name,\n" +
//                    "    REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 6) AS status_info,\n" +
//                    "    REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 3)||REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 8)|| REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 9) as res_info,\n" +
//                    "     REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 8)|| REGEXP_SUBSTR(CONTENT, '[^ ]+', 1, 9) AS info\n" +
//                    "\n" +
//                    "FROM\n" +
//                    "    PWOCS.DMS_YX_BW " +
//                    "where OCCUR_TIME>='"+start_time+"' and OCCUR_TIME<='"+end_time+"')"+
//                    "    group by res_info,station_name)";
//
//
//
//
//
//            queryReport.setTable(table_name);
//            queryReport.setSelectfldn("RES_INFO,AORNUM,STATION_NAME");
//
//            queryReport.setOrderfldn(" AORNUM DESC ");
//            queryReport.addQuerySet("关键字","find_gjz","RES_INFO||STATION_NAME");
//            queryReport.addQuerySet("厂站名称","find_czmc","STATION_NAME");
//
//
//            queryReport.addColItem("告警内容","RES_INFO");
//            queryReport.addColItem("告警次数","AORNUM");
//            // queryReport.addColItem("告警类型","ALARM_GROUP");
//            queryReport.addColItem("厂站名称","STATION_NAME");
//        }

        //1、遥测越限
        if(num==1){
            table_name = "(select OCCUR_TIME,CONTENT,status,value from pwocs.dms_yc_over " +
                    "where OCCUR_TIME>='"+start_time+"' and OCCUR_TIME<='"+end_time+"')";


            queryReport.setTable(table_name);
            queryReport.setSelectfldn("OCCUR_TIME,CONTENT,VALUE");

            queryReport.setOrderfldn(" OCCUR_TIME DESC ");
            queryReport.addQuerySet("关键字","find_gjz","CONTENT");
            queryReport.addQuerySet("厂站名称","find_czmc","CONTENT");

            queryReport.addColItem("告警时间","OCCUR_TIME");
            queryReport.addColItem("告警内容","CONTENT");
            queryReport.addColItem("数值","VALUE");
            // queryReport.addColItem("告警类型","ALARM_GROUP");
            //queryReport.addColItem("厂站名称","STATION_NAME");
        }

        //2、智能告警
        if(num==2){
            table_name = "(select OCCUR_TIME,CONTENT,status from PWOCS.DMS_SMART_EVENT_WARN_INFO " +
                    "where OCCUR_TIME>='"+start_time+"' and OCCUR_TIME<='"+end_time+"')";


            queryReport.setTable(table_name);
            queryReport.setSelectfldn("OCCUR_TIME,CONTENT");

            queryReport.setOrderfldn(" OCCUR_TIME DESC ");
            queryReport.addQuerySet("关键字","find_gjz","CONTENT");
            queryReport.addQuerySet("厂站名称","find_czmc","CONTENT");

            queryReport.addColItem("告警时间","OCCUR_TIME");
            queryReport.addColItem("告警内容","CONTENT");
//            queryReport.addColItem("数值","VALUE");
            // queryReport.addColItem("告警类型","ALARM_GROUP");
            //queryReport.addColItem("厂站名称","STATION_NAME");
        }
        //3、遥信变位
        if(num==3){
            table_name = "(select OCCUR_TIME,CONTENT,status from pwocs.dms_yx_bw " +
                    "where OCCUR_TIME>='"+start_time+"' and OCCUR_TIME<='"+end_time+"')";


            queryReport.setTable(table_name);
            queryReport.setSelectfldn("OCCUR_TIME,CONTENT");

            queryReport.setOrderfldn(" OCCUR_TIME DESC ");
            queryReport.addQuerySet("关键字","find_gjz","CONTENT");
            queryReport.addQuerySet("厂站名称","find_czmc","CONTENT");

            queryReport.addColItem("告警时间","OCCUR_TIME");
            queryReport.addColItem("告警内容","CONTENT");
//            queryReport.addColItem("数值","VALUE");
            // queryReport.addColItem("告警类型","ALARM_GROUP");
            //queryReport.addColItem("厂站名称","STATION_NAME");
        }
        //4、事故告警
        if(num==4){
            table_name = "(select OCCUR_TIME,CONTENT,status from pwocs.dms_accident_info " +
                    "where OCCUR_TIME>='"+start_time+"' and OCCUR_TIME<='"+end_time+"')";


            queryReport.setTable(table_name);
            queryReport.setSelectfldn("OCCUR_TIME,CONTENT");

            queryReport.setOrderfldn(" OCCUR_TIME DESC ");
            queryReport.addQuerySet("关键字","find_gjz","CONTENT");
            queryReport.addQuerySet("厂站名称","find_czmc","CONTENT");

            queryReport.addColItem("告警时间","OCCUR_TIME");
            queryReport.addColItem("告警内容","CONTENT");
//            queryReport.addColItem("数值","VALUE");
            // queryReport.addColItem("告警类型","ALARM_GROUP");
            //queryReport.addColItem("厂站名称","STATION_NAME");
        }
        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut pw_kxzgz() throws Exception {
        QueryReport queryReport = new QueryReport("", request, sysConfig, datasourceConfig);
        String start_time=CommonFunc.getHttpVar("start_time");
        String end_time=CommonFunc.getHttpVar("end_time");
        String nav_item = CommonFunc.getHttpVar("nav_item");
        String orderFldn = CommonFunc.getHttpVar("orderFldn");
        String flagSort = CommonFunc.getHttpVar("flagSort");
        if (StringUtils.isEmpty(flagSort)){
            flagSort = "1";
        }
        int num = Integer.parseInt(nav_item);

        queryReport.setpagenumber(100);

            if (num == 1) {
                String ymd=CommonFunc.unix2Date(CommonFunc.unixTimeStr(),"yyyy-MM-dd");

                queryReport.setTable("V_PWGJ_KXZGZ_24H a ");
                queryReport.setFldnPK("KXZGZ_ID");
                queryReport.setSelectfldn("a.*");
                if (StringUtils.isNotEmpty(orderFldn)){
                    if (flagSort.equals("1")){
                        queryReport.setOrderfldn(orderFldn+ "  DESC ");
                        queryReport.setFlagSort(2);
                    }else{
                        queryReport.setOrderfldn(orderFldn+ "  ASC ");
                        queryReport.setFlagSort(1);
                    }
                }else{
                    queryReport.setOrderfldn("FZL DESC ");
                }

                queryReport.addQuerySet("厂站名称", "find_czmc", "SITE_NAME");
                queryReport.addQuerySet("馈线", "find_kx", "FEEDER_NO");
                queryReport.addQuerySet("片区局", "find_gdfj", "GDFJ");
                queryReport.addQuerySet("镇街", "find_zj", "GDFWZX");


                queryReport.addColItem("变电站", "SITE_NAME");
                queryReport.addColItem("馈线编号", "FEEDER_NO");
                queryReport.addColItem("片区局", "GDFJ");  
                queryReport.addColItem("镇街", "GDFWZX");
                queryReport.addColItem("安全电流值(A)", "INOM_SAFE");
                queryReport.addColItem("告警时间", "OTIME");
                queryReport.addColItem("刷新时间", "REFRESH_TIME");

                queryReport.addColItem("负荷值(A)", "INOM_REFRESH1");
                queryReport.addColItem("负载率(%)", "FZL_REFRESH");

                StringBuffer sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"FLAG_CONFIRM\").toString();\n");
                sbFunc.append("return \"1\".equals(sResult)?\"是\":\"否\";");
                queryReport.addColItem("通知状态", "NOTIFY_INFO");
                queryReport.addColItem("本次持续(分)", "TOTAL_INFO_CURR");
                queryReport.addColItem("今日累计(分)", "TOTAL_INFO");

                sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"NOTIFY_CONTENT\").toString();\n");
                sbFunc.append("sResult=String.format(\"<span onclick=\\\"viewPwElinkInfo(this)\\\">查看</span>\",sResult);\n");
                sbFunc.append("return sResult;");
                //queryReport.addColItem("通知情况", "TZNR",Eval.EVAL+sbFunc.toString());

                //queryReport.addColItem("是否电话通知", "ISCALL");
                queryReport.addColItem("备注", "REMARK");
                queryReport.addActionItem("", Eval.getEditUrl("PWGJ_KXZGZ","kxzgz_id","KXZGZ_ID")
                        ,"添加备注","text","添加备注","");
                //queryReport.addActionItem("", Eval.js("pwCallOnce","ID"),"一键拨打","text","一键拨打","");
                StringBuffer bufferUrl = new StringBuffer();
                bufferUrl.append("javascript:pw_kxzgz_querydts(this);");

                queryReport.addActionItem("", bufferUrl.toString()
                        ,"查看当前设备的电流曲线","text","电流曲线","");

                queryReport.addActionItem("", Eval.js("pwSendElinkOnce","KXZGZ_ID"),
                        "发送ELINK通知","text","发送ELINK","","FLAG_CONFIRM='1'");

                queryReport.addActionItem("", Eval.js("pwCallOnce","KXZGZ_ID"),
                        "一键拨打","text","一键拨打","","FLAG_CONFIRM='1'");

            }else if (num == 2){
                queryReport.setTable("PWGJ_KXZGZ a ");
                queryReport.setFldnPK("KXZGZ_ID");
                queryReport.setSelectfldn("a.*,TO_CHAR(start_time, 'YYYY-MM-DD HH24:MI:SS') as OTIME," +
                        "TO_CHAR(end_time, 'YYYY-MM-DD HH24:MI:SS') as STIME," +
                        "DATEDIFF(minute,start_time,nvl(end_time,sysdate)) DIFF_MINUTE");
                queryReport.setWhere_ex("(flag_notify_elink1='1' or flag_notify_tel1='1' or " +
                        "flag_notify_elink2='1' or flag_notify_tel3='1' or " +
                        "flag_notify_elink2='1' or flag_notify_tel3='1' )");
                queryReport.setOrderfldn("start_time DESC ");
                queryReport.setActionTitleText("人工推送");

                queryReport.addQuerySet("厂站名称", "find_czmc", "SITE_NAME");
                queryReport.addQuerySet("馈线", "find_kx", "FEEDER_NO");
                queryReport.addQuerySet("片区局", "find_gdfj", "GDFJ");
                queryReport.addQuerySet("镇街", "find_zj", "ZJ");

                queryReport.addQuerySet("负载率(%)", "find_zzl", "FZL",
                        QueryReportFilter.QRFilterType.text, "", QueryReportFilter.QRFilterOper.greater);
                queryReport.addQuerySet("-", "find_zzl1", "FZL",
                        QueryReportFilter.QRFilterType.text, "", QueryReportFilter.QRFilterOper.lessEqual);
                queryReport.addQuerySet("时间", "find_time_query1", "nvl(START_TIME,'')",
                        QueryReportFilter.QRFilterType.datetime, "", QueryReportFilter.QRFilterOper.greater);
                queryReport.addQuerySet("-", "find_time_query2", "nvl(START_TIME,'')",
                        QueryReportFilter.QRFilterType.datetime, "", QueryReportFilter.QRFilterOper.lessEqual);

                StringBuffer sbFunc=new StringBuffer();
                queryReport.addColItem("变电站", "SITE_NAME");
                queryReport.addColItem("馈线编号", "FEEDER_NO");
                queryReport.addColItem("片区局", "GDFJ");
                queryReport.addColItem("镇街", "GDFWZX");
                //queryReport.addColItem("安全电流值(A)", "AQDL");
                queryReport.addColItem("告警时间", "OTIME");
                //queryReport.addColItem("恢复时间", "RSTIME");
                //queryReport.addColItem("累计时长", "DIFF_MINUTE");
                queryReport.addColItem("通知时间", "STIME");
                queryReport.addColItem("通知类别", "ZGZ_TYPE");

                sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"FLAG_NOTIFY_ELINK\").toString();\n");
                sbFunc.append("sResult=\"1\".equals(sResult)?\"已发文字通知\":(\"0\".equals(sResult)?\"未发文字通知\":sResult);\n");
                sbFunc.append("return sResult;");
                queryReport.addColItem("文字通知情况", "FLAG_NOTIFY_ELINK",Eval.EVAL+sbFunc.toString());

                sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"FLAG_NOTIFY_TEL\").toString();\n");
                sbFunc.append("sResult=\"1\".equals(sResult)?\"已电话通知\":(\"0\".equals(sResult)?\"未电话通知\":sResult);\n");
                sbFunc.append("return sResult;");
                queryReport.addColItem("电话通知情况", "FLAG_NOTIFY_TEL",Eval.EVAL+sbFunc.toString());

                sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"NOTIFY_CONTENT\").toString();\n");
                sbFunc.append("sResult=String.format(\"<span onclick=\\\"viewPwElinkInfoHis(this)\\\">查看</span>\",sResult);\n");
                sbFunc.append("return sResult;");
                queryReport.addColItem("通知详情", "NOTIFY_CONTENT",Eval.EVAL+sbFunc.toString());
                //queryReport.addColItem("通知方式", "TZFS");
                //queryReport.addColItem("对方回复情况", "DFFHQK");

                queryReport.addActionItem("", Eval.js("pwSendElinkOnce","KXZGZ_ID"),
                        "发送ELINK通知","text","发送ELINK","");

                queryReport.addActionItem("", Eval.js("pwCallOnce","KXZGZ_ID"),
                        "一键拨打","text","一键拨打","");
            }else if (num == 3){
                queryReport.setTable("PWGJ_VALUE_SET");
                queryReport.setSelectfldn("PID,MKZ,ZFZL,ZFZSJ,GFZL,GFZSJ,CGFZL,CGFZSJ,MSGINFO,CGMSGINFO,MSGINFO_1_2");

                queryReport.addColItem("门槛档位(%)", "MKZ");
                queryReport.addColItem("第一次通知负载率(%)", "ZFZL");
                queryReport.addColItem("第一次通知持续时间(分钟)", "ZFZSJ");
                queryReport.addColItem("第二次通知负载率(%)", "GFZL");
                queryReport.addColItem("第二次通知持续时间(分钟)", "GFZSJ");
                queryReport.addColItem("第三次通知负载率(%)", "CGFZL");
                queryReport.addColItem("第三次通知持续时间(分钟)", "CGFZSJ");

                StringBuffer sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"MSGINFO\").toString();\n");
                sbFunc.append("sResult=String.format(\"<span onclick=\\\"layer.alert('\"+sResult+\"')\\\">查看</span>\",sResult);\n");
                sbFunc.append("return sResult;");
                queryReport.addColItem("一次通知内容", "MSGINFO",Eval.EVAL+sbFunc.toString());

                sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"MSGINFO_1_2\").toString();\n");
                sbFunc.append("sResult=String.format(\"<span onclick=\\\"layer.alert('\"+sResult+\"')\\\">查看</span>\",sResult);\n");
                sbFunc.append("return sResult;");
                queryReport.addColItem("二次通知内容", "MSGINFO_1_2",Eval.EVAL+sbFunc.toString());

                sbFunc=new StringBuffer();
                sbFunc.append("String sResult=mapRow.get(\"CGMSGINFO\").toString();\n");
                sbFunc.append("sResult=String.format(\"<span onclick=\\\"layer.alert('\"+sResult+\"')\\\">查看</span>\",sResult);\n");
                sbFunc.append("return sResult;");
                queryReport.addColItem("三次通知内容", "CGMSGINFO",Eval.EVAL+sbFunc.toString());
    //            queryReport.addActionItem("", Eval.getEditUrl("PWGJ_VALUE_SET","pid","PID")
    //                    ,"修改参数设置","text","修改","");
                queryReport.addActionItem("","javascript:window.open('dataadd.html?dataname=PWGJ_VALUE_SET&pid&actiontype=edit&pk_id=1');"
                        ,"修改参数设置","text","修改","");

        }
        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    /**
     * 配网告警电话联系清单
     * @return
     * @throws Exception
     */
    public QueryReportOutPut pw_phone_list() throws Exception {
        QueryReport queryReport = new QueryReport("配调通讯录", request, sysConfig, datasourceConfig);
        String nav_item = CommonFunc.getHttpVar("nav_item");
        String orderFldn = CommonFunc.getHttpVar("orderFldn");
        String flagSort = CommonFunc.getHttpVar("flagSort");
        if (flagSort == ""){
            flagSort = "1";
        }

        queryReport.setpagenumber(100);

        String table_name = "(select * from PWGJ_PHONE_LIST)";


        queryReport.setTable(table_name);
        queryReport.setSelectfldn("*");
        queryReport.setFlagImport(1);
            if (orderFldn != ""){
                if (flagSort.equals("1")){
                    queryReport.setOrderfldn(orderFldn+ "  DESC ");
                    queryReport.setFlagSort(2);
                }else{
                    queryReport.setOrderfldn(orderFldn+ "  ASC ");
                    queryReport.setFlagSort(1);
                }

            }else{
                queryReport.setOrderfldn(" ZCDW DESC ");
            }
        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=PWGJ_PHONE_LIST&actiontype=add\")' type='button'>新增</button>");

            queryReport.addQuerySet("联系电话", "find_lxdh", "LXDH1||LXDH2||LXDH3");
            queryReport.addQuerySet("联系人", "find_lxr", "LXR1||LXR3||LXR2");
            queryReport.addQuerySet("业务类型", "find_ywlx", "YWLX");
            queryReport.addQuerySet("支持单位", "find_zj", "ZCDW");


            queryReport.addColItem("支持的业务类型", "YWLX");
            queryReport.addColItem("支持单位", "ZCDW","","col-tooltips");
            queryReport.addColItem("电话联系人1", "LXR1");
            queryReport.addColItem("电话1", "LXDH1");
            queryReport.addColItem("电话联系人2", "LXR2");
            queryReport.addColItem("电话2", "LXDH2");
            queryReport.addColItem("电话联系人3", "LXR3");
            queryReport.addColItem("电话3", "LXDH3");
            queryReport.addColItem("文字联系人", "WZLXR1","","col-tooltips");
            queryReport.addColItem("elink账号", "ELINK1","","col-tooltips");
            //queryReport.addColItem("文字联系人2", "WZLXR2");
            //queryReport.addColItem("elink账号2", "ELINK2");
            //queryReport.addColItem("文字联系人3", "WZLXR3");
            //queryReport.addColItem("elink账号3", "ELINK4");
            //queryReport.addColItem("文字联系人4", "WZLXR4");
            //queryReport.addColItem("elink账号4", "ELINK4");
            //queryReport.addColItem("elink群号", "ELINKGROUP");
            queryReport.addActionItem("", Eval.getEditUrl("PWGJ_PHONE_LIST","pid","PID")
                    ,"编辑","text","编辑信息","");


        queryReport.execQuery();
        return queryReport.getOutPut();
    }
    /**
     * 馈线重合闸相关模块
     * @return
     * @throws Exception
     */
    public QueryReportOutPut pwChz() throws Exception {
        QueryReport queryReport = new QueryReport("站内馈线开关远控重合闸", request, sysConfig, datasourceConfig);
        String start_time=CommonFunc.getHttpVar("start_time");
        String end_time=CommonFunc.getHttpVar("end_time");
        String nav_item = CommonFunc.getHttpVar("nav_item");
        int num = Integer.parseInt(nav_item);

        queryReport.setpagenumber(100);
        if (num == 1) {
            String table_name = "(select * from PWOCS.DMS_SMART_EVENT_WARN_INFO where content like '%重合闸%' " +
                    " and  OCCUR_TIME >= '" + start_time + "' and OCCUR_TIME<='" + end_time + "' " +
                    ")";


            queryReport.setTable(table_name);
            queryReport.setSelectfldn("ID,OCCUR_TIME,STATUS,CONTENT,ST_ID");

//            queryReport.setOrderfldn(" OCCUR_TIME DESC ");
//            queryReport.addQuerySet("厂站名称", "find_czmc", "SUBSTATION");
//            queryReport.addQuerySet("馈线", "find_kx", "INFO");
//            queryReport.addQuerySet("片区局", "find_gdfj", "GDFJ");
//            queryReport.addQuerySet("镇街", "find_zj", "GDFWZX");
            queryReport.addQuerySet("时间", "find_time_query", "nvl(OCCUR_TIME,'')",
                    QueryReportFilter.QRFilterType.datetime, "", QueryReportFilter.QRFilterOper.greater);

            queryReport.addColItem("变电站", "");
            queryReport.addColItem("开关号", "");
            queryReport.addColItem("馈线状态", "");
            queryReport.addColItem("定值位置", "");
            queryReport.addColItem("定值依据", "");
            queryReport.addColItem("当前位置", "");
            queryReport.addColItem("带电作业", "");
//            queryReport.addColItem("重载率", "ZZL");
//            queryReport.addColItem("通知情况", "ISELINK");
            queryReport.addColItem("备注", "");
//            queryReport.addActionItem("", Eval.getEditUrl("PWGJ_ELINK_INFO","id","ID")
//                    ,"添加备注","text","添加备注","");
        }
        if (num == 2){
            String table_name = "(select TO_CHAR(OCCUR_TIME, 'YYYY-MM-DD HH24:MI:SS') as OTIME," +
                    "TO_CHAR(SENDTIME, 'YYYY-MM-DD HH24:MI:SS') as STIME,* from OMS_ZF.PWGJ_ELINK_INFO " +
                    "where  OCCUR_TIME > '" + start_time + "' and OCCUR_TIME<='" + end_time + "' " +
                    ")";


            queryReport.setTable(table_name);
            queryReport.setSelectfldn("OTIME,SENDTIME,OCCUR_TIME,INFO,SUBSTATION,AQDL,KXNAME,DQDL,ZZL,GDFJ,KXNO,ZJ");

            queryReport.setOrderfldn(" OCCUR_TIME DESC ");
            queryReport.addQuerySet("厂站名称", "find_czmc", "SUBSTATION");
            queryReport.addQuerySet("馈线", "find_kx", "INFO");
            queryReport.addQuerySet("片区局", "find_gdfj", "GDFJ");
            queryReport.addQuerySet("镇街", "find_zj", "GDFWZX");
            queryReport.addQuerySet("时间", "find_time_query", "nvl(OCCUR_TIME,'')",
                    QueryReportFilter.QRFilterType.datetime, "", QueryReportFilter.QRFilterOper.greater);

            queryReport.addColItem("变电站", "SUBSTATION");
            queryReport.addColItem("馈线编号", "KXNAME");
            queryReport.addColItem("片区局", "GDFJ");
            queryReport.addColItem("镇街", "GDFWZX");
            queryReport.addColItem("安全电流值", "AQDL");
            queryReport.addColItem("刷新时间", "OTIME");
            queryReport.addColItem("负荷值", "DQDL");
            queryReport.addColItem("重载率", "ZZL");
//                queryReport.addColItem("剔除分析", "");
            queryReport.addColItem("通知情况", "ISELINK");
        }
        if (num == 3){
            String table_name = "(select * from OMS_ZF.PWGJ_VALUE_SET )";


            queryReport.setTable(table_name);
            queryReport.setSelectfldn("PID,MKZ,ZFZL,ZFZSJ,GFZL,GFZSJ,CGFZL,CGFZSJ,MSGINFO,CGMSGINFO");

            queryReport.addColItem("展示门槛值(%)", "MKZ");
            queryReport.addColItem("首次通知负载率1(%)", "ZFZL");
            queryReport.addColItem("首次通知持续时间1(分钟)", "ZFZSJ");
            queryReport.addColItem("首次通知负载率2(%)", "GFZL");
            queryReport.addColItem("首次通知持续时间2(分钟)", "GFZSJ");
            queryReport.addColItem("二次通知负载率(%)", "CGFZL");
            queryReport.addColItem("二次通知持续时间(分钟)", "CGFZSJ");
            queryReport.addColItem("高过载通知内容", "MSGINFO");
            queryReport.addColItem("超高过载通知内容", "CGMSGINFO");
//            queryReport.addActionItem("", Eval.getEditUrl("PWGJ_VALUE_SET","pid","PID")
//                    ,"修改参数设置","text","修改","");
            queryReport.addActionItem("","javascript:window.open('dataadd.html?dataname=PWGJ_VALUE_SET&pid&actiontype=edit&pk_id=1');"
                    ,"修改参数设置","text","修改","");

        }
        queryReport.execQuery();
        return queryReport.getOutPut();
    }
    public  List<String> getMonth(String start,String end) throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        // 获取开始年份和开始月份
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        // 获取结束年份和结束月份
        calendar.setTime(endDate);
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH);
        //
        List<String> list = new ArrayList<String>();
        for (int i = startYear; i <= endYear; i++) {
            String date = "";
            if (startYear == endYear) {
                for (int j = startMonth; j <= endMonth; j++) {
                    if (j < 9) {
                        date = i + "0" + (j + 1);
                    } else {
                        date = i + "" + (j + 1);
                    }
                    list.add(date);
                }

            } else {
                if (i == startYear) {
                    for (int j = startMonth; j < 12; j++) {
                        if (j < 9) {
                            date = i + "0" + (j + 1);
                        } else {
                            date = i + "" + (j + 1);
                        }
                        list.add(date);
                    }
                } else if (i == endYear) {
                    for (int j = 0; j <= endMonth; j++) {
                        if (j < 9) {
                            date = i + "0" + (j + 1);
                        } else {
                            date = i + "" + (j + 1);
                        }
                        list.add(date);
                    }
                } else {
                    for (int j = 0; j < 12; j++) {
                        if (j < 9) {
                            date = i + "0" + (j + 1);
                        } else {
                            date = i + "" + (j + 1);
                        }
                        list.add(date);
                    }
                }

            }

        }

        // 所有的月份已经准备好
        //System.out.println(list);
        for(int i = 0;i < list.size();i++){
            System.out.println(list.get(i));
        }
        return  list;
    }

    //安生年度投资计划
    public QueryReportOutPut w_asndtzjh() throws Exception {
        QueryReport queryReport = new QueryReport("安生年度投资计划", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(1000);
        queryReport.setTable("W_ASNDTZJH");
        queryReport.setSelectfldn("*");
        queryReport.setOrderfldn("ID DESC ");

        queryReport.setFldnPK("ID");

        queryReport.addQuerySet("年度","find_nd","ND");

        queryReport.addColItem("年度","ND");
        queryReport.addColItem("项目类型","XMLX");
        queryReport.addColItem("自动化分部","ZDHFB");
        queryReport.addColItem("通信分部","TXFB");
        queryReport.addColItem("继保分部","JBFB");
        queryReport.addColItem("主调分部","ZDFB");
        queryReport.addColItem("配调分部","PDFB");
        queryReport.addColItem("安生分部","ASFB");
        queryReport.addColItem("运方分部","YFFB");
        queryReport.addColItem("综合分部","ZHFB");
        queryReport.addColItem("合计","HJ");

        queryReport.setOther("<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=W_ASNDTZJH&actiontype=add\")' type='button'>新增</button>");

        queryReport.addActionItem("", Eval.getEditUrl("W_ASNDTZJH","","ID")
                ,"修改","text","修改","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut zwdkxwsb_jxjh() throws Exception {
        QueryReport queryReport = new QueryReport("主网调控行为识别-检修计划", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("DGZW_OMS.MWT_UD_JXSQD");
        queryReport.setSelectfldn("*");
        queryReport.setOrderfldn("ID DESC ");
        queryReport.setFldnPK("ID");

        queryReport.addColItem("工作内容","GZNR");
        queryReport.addColItem("批准工作结束时间","PZGZJSSJ");
        queryReport.addColItem("批准工作开始时间","PZGZKSSJ");
        queryReport.addColItem("申请单编号","SQDBH");
        queryReport.addColItem("检修类别","JXLB");
        queryReport.addColItem("申请单类型","SQDLX");
        queryReport.addColItem("申请单位","SQDW");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut zwdkxwsb_yxfs() throws Exception {
        QueryReport queryReport = new QueryReport("主网调控行为识别-运行方式", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("DGZW_OMS.MWT_UD_JXSQD");
        queryReport.setSelectfldn("*");
        queryReport.setOrderfldn("ID DESC ");
        queryReport.setFldnPK("ID");

        queryReport.addColItem("工作内容","GZNR");
        queryReport.addColItem("批准工作结束时间","PZGZJSSJ");
        queryReport.addColItem("批准工作开始时间","PZGZKSSJ");
        queryReport.addColItem("申请单编号","SQDBH");
        queryReport.addColItem("检修类别","JXLB");
        queryReport.addColItem("申请单类型","SQDLX");
        queryReport.addColItem("申请单位","SQDW");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

//    public QueryReportOutPut zwdkxwsb_bhtt() throws Exception {
//        QueryReport queryReport = new QueryReport("主网调控行为识别-保护投退", request, sysConfig, datasourceConfig);
//        queryReport.setpagenumber(100);
//        queryReport.setTable("DGZW_OMS.\"tbl_BhParts\"");
//        queryReport.setSelectfldn("*");
//        queryReport.setOrderfldn("BH_ID DESC ");
//        queryReport.setFldnPK("BH_ID");
//
//        queryReport.addColItem("保护名称","BH_NAME");
//        queryReport.addColItem("保护等级","BH_LEVEL");
//        queryReport.addColItem("默认保护状态","BH_STATE_NORMAL");
//        queryReport.addColItem("目前保护状态","BH_STATE_NOW");
//        queryReport.addColItem("备注","BH_NOTE");
//
//        queryReport.execQuery();
//        return queryReport.getOutPut();
//    }

    public QueryReportOutPut alarmDisconnectorStatistic() throws Exception {
        QueryReport queryReport = new QueryReport("遥控、遥信刀闸操作统计", request, sysConfig, datasourceConfig);
        // 月份
        String month = CommonFunc.getHttpVar("find_month");
        // 电压
        String voltage = CommonFunc.getHttpVar("find_voltage");

        queryReport.addQuerySet("月份","find_month", "TO_CHAR(DATE, 'YYYY-mm')",
                QueryReportFilter.QRFilterType.text, month,
                QueryReportFilter.QRFilterOper.equal
        );

        queryReport.addQuerySet("电压","find_voltage", "VOLTAGE"
                , QueryReportFilter.QRFilterType.text, voltage,
                QueryReportFilter.QRFilterOper.like
        );

        queryReport.addColItem("月份","MONTH");
        queryReport.addColItem("电压","VOLTAGE");
        queryReport.addColItem("实际遥控刀闸操作次数","REMOTE_OPERATE_STATISTIC");
        queryReport.addColItem("所有刀闸操作次数","ALL_OPERATE_STATISTIC");
        queryReport.addColItem("遥控操作实施率(%)","RATIO");

        queryReport.setTable("alarm_disconnector_statistic");
        queryReport.setSelectfldn("TO_CHAR(DATE, 'YYYY-mm') MONTH, VOLTAGE, remote_operate_statistic, all_operate_statistic, ratio");
        queryReport.setOrderfldn("DATE ASC, TO_NUMBER(VOLTAGE) DESC");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut yxEventLog() throws Exception {
        QueryReport queryReport = new QueryReport("遥信事件日志", request, sysConfig, datasourceConfig);
        String startTime=CommonFunc.getHttpVar("find_start_time");
        String endTime=CommonFunc.getHttpVar("find_end_time");
        String voltage=CommonFunc.getHttpVar("find_voltage");

        queryReport.addQuerySet("动作开始时间","find_start_time",
                "acting_time"
                , QueryReportFilter.QRFilterType.datetime, startTime, QueryReportFilter.QRFilterOper.greaterEqual);
        queryReport.addQuerySet("动作结束时间","find_end_time",
                "acting_time"
                , QueryReportFilter.QRFilterType.datetime, endTime, QueryReportFilter.QRFilterOper.lessEqual);
        queryReport.addQuerySet("告警内容","find_alarm_content","alarm_content");
        queryReport.addQuerySet("告警类型分组","find_alarm_group","alarm_group");
        queryReport.addQuerySet("告警动作类型","find_act_type","acting_desc");
        queryReport.addQuerySet("变电站","find_fac_name","fac_name");
        queryReport.addQuerySet("设备名","find_equip_name","equip_name");
        queryReport.addQuerySet("电压等级","find_voltage","vol_level",
                QueryReportFilter.QRFilterType.text, voltage,
                QueryReportFilter.QRFilterOper.equal);
        queryReport.addQuerySet("确认人","find_confirm_man","confirm_man");

        queryReport.addColItem("动作时间","ACTING_TIME");
        queryReport.addColItem("告警内容","ALARM_CONTENT");
        queryReport.addColItem("告警类型分组","ALARM_GROUP");
        queryReport.addColItem("告警动作类型","ACTING_DESC");
        queryReport.addColItem("变电站","FAC_NAME");
        queryReport.addColItem("设备名","EQUIP_NAME");
        queryReport.addColItem("电压等级","VOL_LEVEL");
        queryReport.addColItem("确认人","CONFIRM_MAN");
        queryReport.addColItem("确认时间","CONFIRM_TIME");

        queryReport.setSelectfldn("*");

        SysUsers loginInfo = Eval.getLoginInfo();
        if (loginInfo != null) {
            String canDelete = CommonFunc.getConfigByKey("privilege.alarm.disconnector.delete");
            if (StringUtils.isNotBlank(canDelete)) {
                List<String> list = Arrays.asList(canDelete.split(","));
                if (list.contains(loginInfo.getUsername())) {
                    queryReport.addActionItem("", Eval.js("deleteAlarm","ID", "yx"),
                            "遥信事件信息删除", "text", "删除", "");
                }
            }
        }

        String sql = "ALARM_YXEVENTLOG";
        queryReport.setTable(" ( select * from (" + sql + ") " +
                " WHERE DEL_FLAG =0 AND AREA_OID NOT IN ( 3096224748863489 ) AND ALARM_GROUP LIKE '刀闸动作'" +
                "   AND (ACTING_DESC LIKE '合闸' OR ACTING_DESC LIKE '分闸')" +
                "   AND ALARM_CONTENT NOT LIKE '%地刀%'" +
                "   AND ALARM_CONTENT NOT LIKE '%电容%'" +
                "   AND ALARM_CONTENT NOT LIKE '%电抗%'" +
                "   AND ALARM_CONTENT NOT LIKE '%PT%'" +
                "   AND ALARM_CONTENT NOT LIKE '%旁路%'" +
                "   AND ALARM_CONTENT NOT LIKE '%手车%'" +
                "   AND ALARM_CONTENT NOT LIKE '%站用%'" +
                "   AND ALARM_CONTENT NOT LIKE '%接地变%'" +
                "   AND (ALARM_CONTENT LIKE '%35kV%' OR ALARM_CONTENT LIKE '%110kV%' OR ALARM_CONTENT LIKE '%220kV%' OR ALARM_CONTENT LIKE '%500kV%')" +
                "   AND FAC_NAME NOT LIKE '%厂'" +
                "   AND FAC_NAME NOT LIKE '南粤换流站'" +
                ")"
        );
        queryReport.setOrderfldn("ACTING_TIME DESC");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut ykEventLog() throws Exception {
        QueryReport queryReport = new QueryReport("遥控事件日志", request, sysConfig, datasourceConfig);
        String startTime=CommonFunc.getHttpVar("find_start_time");
        String endTime=CommonFunc.getHttpVar("find_end_time");
        String voltage=CommonFunc.getHttpVar("find_voltage");

        queryReport.addQuerySet("动作开始时间","find_start_time",
                "acting_time"
                , QueryReportFilter.QRFilterType.datetime, startTime, QueryReportFilter.QRFilterOper.greaterEqual);
        queryReport.addQuerySet("动作结束时间","find_end_time",
                "acting_time"
                , QueryReportFilter.QRFilterType.datetime, endTime, QueryReportFilter.QRFilterOper.lessEqual);
        queryReport.addQuerySet("告警内容","find_alarm_content","alarm_content");
        queryReport.addQuerySet("告警类型分组","find_alarm_group","alarm_group");
        queryReport.addQuerySet("告警动作类型","find_act_type","acting_desc");
        queryReport.addQuerySet("变电站","find_fac_name","fac_name");
        queryReport.addQuerySet("设备名","find_equip_name","equip_name");
        queryReport.addQuerySet("电压等级","find_voltage","voltage",
                QueryReportFilter.QRFilterType.text, voltage,
                QueryReportFilter.QRFilterOper.equal);
        queryReport.addQuerySet("确认人","find_confirm_man","confirm_man");

        queryReport.addColItem("动作时间","ACTING_TIME");
        queryReport.addColItem("告警内容","ALARM_CONTENT");
        queryReport.addColItem("告警类型分组","ALARM_GROUP");
        queryReport.addColItem("告警动作类型","ACTING_DESC");
        queryReport.addColItem("变电站","FAC_NAME");
        queryReport.addColItem("设备名","EQUIP_NAME");
        queryReport.addColItem("电压等级","VOLTAGE");
        queryReport.addColItem("确认人","CONFIRM_MAN");
        queryReport.addColItem("确认时间","CONFIRM_TIME");

        SysUsers loginInfo = Eval.getLoginInfo();
        if (loginInfo != null) {
            String canDelete = CommonFunc.getConfigByKey("privilege.alarm.disconnector.delete");
            if (StringUtils.isNotBlank(canDelete)) {
                List<String> list = Arrays.asList(canDelete.split(","));
                if (list.contains(loginInfo.getUsername())) {
                    queryReport.addActionItem("", Eval.js("deleteAlarm","ID", "yk"),
                            "遥控事件信息删除", "text", "删除", "");
                }
            }
        }

        queryReport.setSelectfldn("*");

//        String sql = "select" +
//                " vol.DESCRIPTION voltage,log.*" +
//                " from \"OMS_ZF\".\"ALARM_YKEVENTLOG\" log" +
//                " left join ZWOCS.RTNET_BREAKER breaker ON BREAKER.SCADAOID = log.EQUIP_OID" +
//                " left join cloud.DEVICE d ON d.ID = TO_CHAR(breaker.ATTR_OID)" +
//                " left join cloud.VOLT_LEVEL vol ON vol.ID = d.R_VOLT_LEVEL_ID";
        String sql = "select" +
                " distinct log1.vol_level voltage,log.*" +
                " from \"OMS_ZF\".\"ALARM_YKEVENTLOG\" log" +
                " left join \"OMS_ZF\".\"ALARM_YXEVENTLOG\" log1 on log1.equip_oid = log.equip_oid";
        queryReport.setTable(" ( select * from (" + sql + ") " +
                " WHERE DEL_FLAG=0 AND AREA_OID NOT IN (3096224748863489) " +
                " AND ALARM_CONTENT LIKE '%刀闸%' " +
                " AND (ALARM_CONTENT LIKE '%35kV%'  " +
                " OR ALARM_CONTENT LIKE '%110kV%'  " +
                " OR ALARM_CONTENT LIKE '%220kV%'  " +
                " OR ALARM_CONTENT LIKE '%500kV%')  " +
                " AND ACTING_DESC LIKE '%执行成功%'  " +
                " AND FAC_NAME NOT LIKE '%厂%'  " +
                " AND FAC_NAME NOT LIKE '南粤换流站%'  " +
                " AND ALARM_CONTENT NOT LIKE '%pw%' " +
                " AND ALARM_CONTENT NOT LIKE '%(pas_avc%'  " +
                " AND ALARM_CONTENT NOT LIKE '%zdwjy%'  " +
                " AND ALARM_CONTENT NOT LIKE '%dmy%'  " +
                " AND ALARM_CONTENT NOT LIKE '%psy%'  " +
                " AND ALARM_CONTENT NOT LIKE '%zdlpl%'  " +
                " AND ALARM_CONTENT NOT LIKE '%djz%'  " +
                " AND ALARM_CONTENT NOT LIKE '%zdpyx%'  " +
                " AND ALARM_CONTENT NOT LIKE '%zdlcb%'  " +
                " AND ALARM_CONTENT NOT LIKE '%zhf%'  " +
                " AND ALARM_CONTENT NOT LIKE '%手车%'  " +
                " AND ALARM_CONTENT NOT LIKE '%电容%'  " +
                " AND ALARM_CONTENT NOT LIKE '%电抗%'  " +
                " AND ALARM_CONTENT NOT LIKE '%PT%'  " +
                " AND ALARM_CONTENT NOT LIKE '%旁路%'  " +
                " AND ALARM_CONTENT NOT LIKE '%手车%'  " +
                " AND ALARM_CONTENT NOT LIKE '%站用%'  " +
                " AND ALARM_CONTENT NOT LIKE '%接地变%' " +
                ")"
        );
        queryReport.setOrderfldn("ACTING_TIME DESC");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    private List<String> getYearMonthBetween(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return Collections.emptyList();
        }

        if (start == null) {
            return Collections.singletonList(end.getYear() + String.format("%02d", end.getMonthValue()));
        }

        if (end == null) {
            return Collections.singletonList(start.getYear() + String.format("%02d", start.getMonthValue()));
        }

        List<String> reList = new ArrayList<>();
        start = start.withDayOfMonth(1);
        end = end.withDayOfMonth(1);
        while (!start.isAfter(end)) {
            reList.add(start.getYear() + String.format("%02d", start.getMonthValue()));
            start = start.plusMonths(1);
        }
        return reList;
    }

    public QueryReportOutPut alarmSwitchLog() throws Exception {
        QueryReport queryReport = new QueryReport("开关分合闸信号", request, sysConfig, datasourceConfig);
        // 告警时间范围
        String startTime = CommonFunc.getHttpVar("find_start_time");
        String endTime = CommonFunc.getHttpVar("find_end_time");
        // 变电站
        String substation = CommonFunc.getHttpVar("find_substation");
        // 设备
        String equipment = CommonFunc.getHttpVar("find_equipment");
        // 内容
        String content = CommonFunc.getHttpVar("find_content");
        // 分闸、合闸、双位错、母线失压
        String actingDesc = CommonFunc.getHttpVar("find_acting_desc");

        queryReport.addQuerySet("开始时间","find_start_time", "ACTING_TIME",
                QueryReportFilter.QRFilterType.datetime, startTime,
                QueryReportFilter.QRFilterOper.greaterEqual
        );
        queryReport.addQuerySet("结束时间","find_end_time", "ACTING_TIME",
                QueryReportFilter.QRFilterType.datetime, endTime,
                QueryReportFilter.QRFilterOper.lessEqual
        );

        queryReport.addQuerySet("变电站","find_substation", "FAC_NAME"
                , QueryReportFilter.QRFilterType.text, substation,
                QueryReportFilter.QRFilterOper.like
        );

        queryReport.addQuerySet("设备名","find_equipment", "EQUIP_NAME"
                , QueryReportFilter.QRFilterType.text, equipment,
                QueryReportFilter.QRFilterOper.like
        );

        queryReport.addQuerySet("告警内容","find_content", "ALARM_CONTENT"
                , QueryReportFilter.QRFilterType.text, content,
                QueryReportFilter.QRFilterOper.like
        );

        queryReport.addQuerySet("动作类型","find_acting_desc", "ACTING_DESC"
                , QueryReportFilter.QRFilterType.text, actingDesc, QueryReportFilter.QRFilterOper.equal
        );

        queryReport.addColItem("动作时间","ACTING_TIME");
        queryReport.addColItem("变电站","FAC_NAME");
        queryReport.addColItem("设备名","EQUIP_NAME", Eval.getOpenUrl("module/bussines/zwgj/linear_point.html?", "logId", "ID", "EQUIP_NAME", ""));
        queryReport.addColItem("告警内容","ALARM_CONTENT");
        queryReport.addColItem("告警类型分组","ALARM_GROUP");
        queryReport.addColItem("告警动作类型","ACTING_DESC");
        queryReport.addColItem("确认人","CONFIRM_MAN");
        queryReport.addColItem("确认时间","CONFIRM_TIME");

        queryReport.setTable("ALARM_SWITCH_LOG");
        queryReport.setSelectfldn("*");
        queryReport.setOrderfldn("ACTING_TIME DESC");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut alarmFrequentRecord() throws Exception {
        QueryReport queryReport = new QueryReport("频发信号统计", request, sysConfig, datasourceConfig);
        // 告警时间范围
        String startTime = CommonFunc.getHttpVar("find_start_time");
        String endTime = CommonFunc.getHttpVar("find_end_time");
        // 变电站
//        String substation = CommonFunc.getHttpVar("find_substation");
        // 设备
//        String equipment = CommonFunc.getHttpVar("find_equipment");

        queryReport.addQuerySet("开始时间","find_start_time", "FREQUENT_TIME",
                QueryReportFilter.QRFilterType.datetime, startTime,
                QueryReportFilter.QRFilterOper.greaterEqual
        );
        queryReport.addQuerySet("结束时间","find_end_time", "FREQUENT_TIME",
                QueryReportFilter.QRFilterType.datetime, endTime,
                QueryReportFilter.QRFilterOper.lessEqual
        );

        queryReport.addQuerySet("变电站","find_substation", "SUBSTATION_NAME");
        queryReport.addQuerySet("设备","find_equipment", "EQUIP_NAME");
        queryReport.addQuerySet("告警信号","find_alarm_content", "ALARM_CONTENT");

        queryReport.setTable("ALARM_FREQUENT_RECORD");
        queryReport.setSelectfldn("*");
        queryReport.setpagenumber(100);
        queryReport.setOrderfldn("FREQUENT_TIME desc");

//        queryReport.addColItem("ID", "ID", null, null, null, null, null, "", "false", "0px");
        queryReport.addColItem("变电站", "SUBSTATION_NAME");
        queryReport.addColItem("设备名", "EQUIP_NAME");
        queryReport.addColItem("告警信号", "ALARM_CONTENT");
//        queryReport.addColItem("开始时间", "FIRST_TIME");
        queryReport.addColItem("开始时间", "FREQUENT_TIME");
        queryReport.addColItem("结束时间", "LAST_TIME");

        queryReport.addActionItem("", Eval.getOpenUrlAction("datalist.html?dataname=ALARM_FREQUENT_DETAIL_RECORD&","find_record_id","ID","","")
                ,"查看频发明细记录","text","查看明细","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut alarmFrequentDetailRecord() throws Exception {
        QueryReport queryReport = new QueryReport("频发信号明细", request, sysConfig, datasourceConfig);
        // 告警时间范围
        String startTime = CommonFunc.getHttpVar("find_start_time");
        String endTime = CommonFunc.getHttpVar("find_end_time");
        // 变电站
//        String substation = CommonFunc.getHttpVar("find_substation");
        // 设备
//        String equipment = CommonFunc.getHttpVar("find_equipment");
        // record_id
        String recordId = CommonFunc.getHttpVar("find_record_id");

        queryReport.addQuerySet("开始时间","find_start_time", "TIME",
                QueryReportFilter.QRFilterType.datetime, startTime,
                QueryReportFilter.QRFilterOper.greaterEqual
        );
        queryReport.addQuerySet("结束时间","find_end_time", "TIME",
                QueryReportFilter.QRFilterType.datetime, endTime,
                QueryReportFilter.QRFilterOper.lessEqual
        );
        queryReport.addQuerySet("变电站","find_substation", "SUBSTATION_NAME");
        queryReport.addQuerySet("设备","find_equipment", "EQUIP_NAME");
        queryReport.addQuerySet("告警信号","find_alarm_content", "ALARM_CONTENT");

        queryReport.setTable("ALARM_FREQUENT_DETAIL_RECORD");
        queryReport.setSelectfldn("*");
        queryReport.setpagenumber(100);
        queryReport.setOrderfldn("TIME desc");
        if (StringUtils.isNotBlank(recordId)) {
            queryReport.setWhere_ex(" record_id = '" + recordId + "'");
        }

        queryReport.addColItem("时间", "TIME");
        queryReport.addColItem("变电站", "SUBSTATION_NAME");
        queryReport.addColItem("设备名", "EQUIP_NAME");
        queryReport.addColItem("告警信号", "ALARM_CONTENT");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut alarmAvcRecord() throws Exception {
        QueryReport queryReport = new QueryReport("告警avc信号统计", request, sysConfig, datasourceConfig);
        // 告警时间范围
        String startDate = CommonFunc.getHttpVar("find_start_date");
        String endDate = CommonFunc.getHttpVar("find_end_date");
        // 设备类型
        String deviceType = CommonFunc.getHttpVar("find_device_type");
        deviceType = "全部".equals(deviceType) ? "'主变','电容器组'" : "'" + ("0".equals(deviceType) ? "主变" : "电容器组") + "'";
        // 变电站
//        String substation = CommonFunc.getHttpVar("find_substation");
        // 设备
//        String equipment = CommonFunc.getHttpVar("find_equipment");
        // record_id
        String recordId = CommonFunc.getHttpVar("find_record_id");

        queryReport.addQuerySet("开始日期","find_start_date", "DATE",
                QueryReportFilter.QRFilterType.oradate, startDate,
                QueryReportFilter.QRFilterOper.greaterEqual
        );
        queryReport.addQuerySet("结束日期","find_end_date", "DATE",
                QueryReportFilter.QRFilterType.oradate, endDate,
                QueryReportFilter.QRFilterOper.lessEqual
        );
        queryReport.addQuerySet("变电站","find_substation", "SUBSTATION_NAME");
        queryReport.addQuerySet("设备类型","find_device_type", "DEVICE_TYPE");
        queryReport.addQuerySet("操作类型","find_opt_type", "OPT_TYPE");
        queryReport.addQuerySet("自动(pas_avc)/手动(jk)","find_auto", "auto", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in, "", "",
                "0,1:全部;0:手动;1:自动", "");

        queryReport.setTable("ALARM_AVC_RECORD");
        queryReport.setSelectfldn("case when AUTO THEN '自动' ELSE '手动' end AUTO ,DATE, SUBSTATION_NAME, DEVICE_TYPE, OPT_TYPE, COUNT, LAST_LOG_TIME");
        queryReport.setpagenumber(100);
        queryReport.setOrderfldn("DATE desc, LAST_LOG_TIME desc");
        if (StringUtils.isNotBlank(recordId)) {
            queryReport.setWhere_ex(" record_id = '" + recordId + "'");
        }

        queryReport.addColItem("日期", "DATE");
        queryReport.addColItem("变电站", "SUBSTATION_NAME");
        queryReport.addColItem("设备类型", "DEVICE_TYPE");
        queryReport.addColItem("操作类型", "OPT_TYPE");
        queryReport.addColItem("自动/手动", "AUTO");
        queryReport.addColItem("总数", "COUNT");
        queryReport.addColItem("最后告警时间", "LAST_LOG_TIME");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut alarmWithoutResetRecord() throws Exception {
        QueryReport queryReport = new QueryReport("告警未复归信号统计", request, sysConfig, datasourceConfig);
        // 告警时间范围
        String startDate = CommonFunc.getHttpVar("find_start_date");
        String endDate = CommonFunc.getHttpVar("find_end_date");

        queryReport.addQuerySet("开始日期","find_start_date", "DATE",
                QueryReportFilter.QRFilterType.oradate, startDate,
                QueryReportFilter.QRFilterOper.greaterEqual
        );
        queryReport.addQuerySet("结束日期","find_end_date", "DATE",
                QueryReportFilter.QRFilterType.oradate, endDate,
                QueryReportFilter.QRFilterOper.lessEqual
        );
        queryReport.addQuerySet("变电站","find_substation", "SUBSTATION_NAME");
        queryReport.addQuerySet("告警内容","find_alarm_content", "ALARM_CONTENT");

        queryReport.setTable("ALARM_WITHOUT_RESET_RECORD");
        queryReport.setSelectfldn("case when RESET_TIME is null then null else " +
                "concat(extract(DAY from (RESET_TIME - ACTING_TIME) DAY TO SECOND),'天'" +
                ",extract(HOUR from (RESET_TIME - ACTING_TIME) DAY TO SECOND), '时'" +
                ",extract(MINUTE from (RESET_TIME - ACTING_TIME) DAY TO SECOND), '分') " +
                " end as KEEP_TIME" + ", ID, SUBSTATION_NAME, ALARM_CONTENT, ACTING_TIME, RESET_TIME");
        queryReport.setpagenumber(100);
        queryReport.setOrderfldn(" ACTING_TIME desc");

        queryReport.addColItem("变电站", "SUBSTATION_NAME");
        queryReport.addColItem("告警内容", "ALARM_CONTENT");
        queryReport.addColItem("动作时间", "ACTING_TIME");
        queryReport.addColItem("复归时间", "RESET_TIME");
        queryReport.addColItem("未复归时长", "KEEP_TIME");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut workPlanSendMessage() throws Exception {
        QueryReport queryReport = new QueryReport("日作业计划“三个拒动”清单重要厂站信息发送", request, sysConfig, datasourceConfig);
        // 时间范围
        String startDate = CommonFunc.getHttpVar("find_start_date");
        String endDate = CommonFunc.getHttpVar("find_end_date");

        queryReport.addQuerySet("开始日期","find_start_date", "CREATE_TIME",
                QueryReportFilter.QRFilterType.oradate, startDate,
                QueryReportFilter.QRFilterOper.greaterEqual
        );
        queryReport.addQuerySet("结束日期","find_end_date", "CREATE_TIME",
                QueryReportFilter.QRFilterType.oradate, endDate,
                QueryReportFilter.QRFilterOper.lessEqual
        );
        queryReport.addQuerySet("电话号码","find_phone", "TO_WHO");
        queryReport.addQuerySet("信息内容","find_message", "MESSAGE");

        queryReport.setTable("WORK_PLAN_SEND_MESSAGE_RECORD");
        queryReport.setSelectfldn("ID, TO_WHO, MESSAGE, date_format(CREATE_TIME,'%Y-%m-%d %H:%i:%s') AS CREATE_TIME, date_format(SEND_TIME,'%Y-%m-%d %H:%i:%s') AS SEND_TIME, CASE FLAG WHEN 0 THEN '待发送' WHEN 1 THEN '发送成功' ELSE '发送失败' END CASE as FLAG");
        queryReport.setpagenumber(10);
        queryReport.setOrderfldn(" CREATE_TIME desc nulls last, SEND_TIME desc nulls last");

        queryReport.addColItem("电话", "TO_WHO");
        queryReport.addColItem("信息内容", "MESSAGE");
        queryReport.addColItem("创建时间", "CREATE_TIME");
        queryReport.addColItem("发送时间", "SEND_TIME");
        queryReport.addColItem("发送结果", "FLAG");

        queryReport.addActionItem("", Eval.getOpenUrlAction("module/bussines/asfb/messageSend.html?","find_id","ID","","")
                ,"编辑发送","text","编辑发送","");
        queryReport.addActionItem("", Eval.js("sendSms", "ID"), "发送", "text", "<button class='btn-mini btn-success'>发送</button>", "");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut zwddjzb_fhyc() throws Exception {
        QueryReport queryReport = new QueryReport("主网调度-负荷预测", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("OMS_ZF.OMS_FHYC");
        queryReport.setSelectfldn("*");
        queryReport.setOrderfldn("ID DESC ");
        queryReport.setFldnPK("ID");

        queryReport.addQuerySet("断面分区","find_dmfq","DMFQ");
        queryReport.addQuerySet("断面名称","find_dmmc","DMMC");
//        queryReport.addQuerySet("设备名称","find_sbmc","SBMC");

        queryReport.addColItem("断面分区","DMFQ");
        queryReport.addColItem("断面名称","DMMC");
//        queryReport.addColItem("设备名称","SBMC");
//        queryReport.addColItem("设备有功","SBYG");
//        queryReport.addColItem("断面实时值","DMSSZ");
        queryReport.addColItem("断面限值","DMXZ");
//        queryReport.addColItem("负载率","FZL");
//        queryReport.addColItem("片区纯负荷","PQCFH");
        queryReport.addColItem("片区电厂","PQDC");
//        queryReport.addColItem("发电出力","FDCL");
        queryReport.addColItem("断面ID","DMID");
        queryReport.addColItem("出力ID","CLID");

        queryReport.setOther(
                "<a role=button href=/kemp/ex_web/oms/app/fhyc.docx>下载说明文档</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
//                "<a role=button href=/kemp/ex_web/oms/app/dataadd.html?dataname=OMS_FHYC&&actiontype=add>新增断面</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                "<button class='btn btn-primary mr-2' onclick='window.open(\"dataadd.html?dataname=OMS_FHYC&actiontype=add\")' type='button'>新增断面</button>"
        );
        queryReport.addActionItem("", Eval.getOpenUrlAction("/kemp/ex_web/oms/app/module/bussines/zwdd/fhyc_value.html?","ID","ID","","")
                ,"查看频发明细记录","text","查看曲线图","");
        queryReport.addActionItem("", Eval.getOpenUrlAction("/kemp/ex_web/oms/app/datalist.html?dataname=OMS_FHYC_DPS&TYPE=CL&","DATAID","CLID","","")
                ,"查看频发明细记录","text","查看出力值","");
        queryReport.addActionItem("", Eval.getOpenUrlAction("/kemp/ex_web/oms/app/datalist.html?dataname=OMS_FHYC_DPS&TYPE=DM&","DATAID","DMID","","")
                ,"查看频发明细记录","text","查看断面值","");
        queryReport.addActionItem("", Eval.getEditUrl("OMS_FHYC", "", "ID")
                , "修改", "text", "修改", "");

        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_FHYC","ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut crewWorkLoadRateDayRecord() throws Exception {
        QueryReport queryReport = new QueryReport("班组每日饱和度查询", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(10);
        queryReport.setTable(" CREW_WORK_LOAD_RATE_DAY_RECORD record " +
                "left join CREW crew on crew.ID = record.CREW_ID " +
                "left join WORK_UNIT unit on unit.ID = crew.UNIT_ID ");
        queryReport.setSelectfldn(" unit.UNIT, crew.NAME, record.*, " +
                " (CASE WHEN crew.SHORT_NAME IS NOT NULL THEN unit.SHORT_NAME || crew.SHORT_NAME ELSE crew.NAME END CASE) unit_crew_short_name, " +
                " length(record.member_name) legth_of_member ");
        queryReport.setOrderfldn("unit_crew_short_name,legth_of_member desc,unit.UNIT nulls last");

        String findDate = request.getParameter("find_date");

        StringBuilder selectValueStr = new StringBuilder();
        try (JdbcDao jdbcDao = new JdbcDao()) {
            ArrayList<HashMap<String, String>> resultList = jdbcDao.getResult("select " +
                    "crew.ID, (CASE WHEN crew.SHORT_NAME IS NOT NULL THEN unit.SHORT_NAME || crew.SHORT_NAME ELSE crew.NAME END CASE) crew_name " +
                    "from CREW crew " +
                    "left join WORK_UNIT unit on unit.ID = crew.UNIT_ID " +
                    "where crew.name not like '%' || '分部' || '%' " +
                    "order by crew_name"
            );
            StringBuilder allCrewIds = new StringBuilder();
            for (HashMap<String, String> map : resultList) {
                selectValueStr.append(map.get("ID")).append(":").append(map.get("CREW_NAME")).append(";");
                allCrewIds.append(map.get("ID")).append(",");
            }
            selectValueStr = new StringBuilder(allCrewIds.substring(0, allCrewIds.length() - 1) + ":所有;").append(selectValueStr);
        }

        queryReport.addQuerySet("日期","find_ymd","record.date", QueryReportFilter.QRFilterType.oradate, findDate,
                QueryReportFilter.QRFilterOper.equal);
        queryReport.addQuerySet("班组", "find_crew", "crew.ID", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in, "", "",
                selectValueStr.toString(), "");

        queryReport.addColItem("部门","UNIT");
        queryReport.addColItem("班组","NAME");
        queryReport.addColItem("日期","DATE");
        queryReport.addColItem("分类","MEMBER_NAME");
        queryReport.addColItem("当天作业饱和度系数和","SUM_RATIO");
        queryReport.addColItem("作业人数","WORKER_NUM");
        queryReport.addColItem("饱和度","RATE");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut workPlanLoadRatio() throws Exception {
        QueryReport queryReport = new QueryReport("饱和度系数配置", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(10);
        queryReport.setTable(" WORK_PLAN_LOAD_RATIO ");
        queryReport.setSelectfldn(" * ");
        queryReport.setOrderfldn(" id ");

        queryReport.addQuerySet("作业类型", "find_work_type", "WORK_TYPE");
//        queryReport.addQuerySet("作业类型","find_work_type","record.date", QueryReportFilter.QRFilterType.oradate, findDate,
//                QueryReportFilter.QRFilterOper.equal);
//        queryReport.addQuerySet("系数", "find_crew", "ratio", QueryReportFilter.QRFilterType.selarray,
//                "", QueryReportFilter.QRFilterOper., "", "",
//                selectValueStr.toString(), "");

        queryReport.addColItem("作业类型","WORK_TYPE");
        queryReport.addColItem("系数","RATIO");

        queryReport.addActionItem("", Eval.getEditUrl("WORK_PLAN_LOAD_RATIO", "id","ID"),"修改", "text", "修改", "");
        queryReport.addActionItem("", Eval.getDeleteUrl("WORK_PLAN_LOAD_RATIO","ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    public QueryReportOutPut fhycDps() throws Exception {
        String type = CommonFunc.getHttpVar("TYPE");
        String DATAID = CommonFunc.getHttpVar("DATAID");
        QueryReport queryReport = null;
        if (Objects.equals(type, "CL")){
            queryReport = new QueryReport("负荷预测-出力值", request, sysConfig, datasourceConfig);
            queryReport.addColItem("出力ID","DMID");
        }else if(Objects.equals(type, "DM")) {
            queryReport = new QueryReport("负荷预测-断面值", request, sysConfig, datasourceConfig);
            queryReport.addColItem("断面ID","DMID");
        }

        queryReport.setpagenumber(100);
        queryReport.setSelectfldn("a.*,FROM_UNIXTIME(TIME / 1000) AS FORMATTED_TIME,b.MEASURE_ID, b.ZCXLZ");
        queryReport.setTable("OMS_FHYC_DPS a LEFT JOIN OMS_ZF.OMS_JZB b ON a.DMID = b.MEASURE_ID");
        queryReport.setWhere_ex(" a.DMID = 'ME-"+DATAID+"' AND a.TYPE = '"+type+"' ");
        queryReport.setOrderfldn("a.TIME DESC ");
        queryReport.setFldnPK("a.ID");

//        queryReport.addQuerySet("断面分区","find_dmfq","DMFQ");
//        queryReport.addQuerySet("断面名称","find_dmmc","DMMC");
        queryReport.addColItem("时间","FORMATTED_TIME");
        queryReport.addColItem("数值","VALUE");
        queryReport.addColItem("额定电流","ZCXLZ");

        queryReport.setOther(
                "<a role=button href=/kemp/ex_web/oms/omsserver/Zwdd/fhyc_excel?ID="+DATAID+"&TYPE="+type+">下载当日数据表</a>&nbsp;&nbsp;&nbsp;&nbsp;"
        );


        queryReport.addActionItem("", Eval.getEditUrl("OMS_FHYC_DPS", "id", "ID")
                , "修改", "text", "修改", "");

        queryReport.addActionItem("", Eval.getDeleteUrl("OMS_FHYC_DPS","ID"),"删除本条数据","text","删除","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    //告警回填参数设置
    public QueryReportOutPut gjxhxxhl_setting() throws Exception {
        QueryReport queryReport = new QueryReport("告警信号闭环管控-参数设置", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("GJXHXXHL_SETTING");
        queryReport.setSelectfldn("*");
        queryReport.setOrderfldn("ID DESC ");
        queryReport.setFldnPK("ID");

        queryReport.addColItem("每天闭环时间","SET_DAILY_INSPECTION_TIME");
        queryReport.addColItem("闭环次数","BHCS");
        queryReport.addColItem("间隔时间(分钟)(告警多久没恢复新建数据)","PERIOD");


        queryReport.addActionItem("", Eval.getEditUrl("GJXHXXHL_SETTING", "", "ID")
                , "修改", "text", "修改", "");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    //告警回填
    public QueryReportOutPut gjxhht() throws Exception {
        String flowNodeId = CommonFunc.getHttpVar("flow_node_id");
        String sortindex = CommonFunc.getHttpVar("sortindex");
        String DepartmentName = Eval.getLoginDepartmentName();
        String deptidZdzwdd= CommonFunc.getConfigByKey("sys.deptid_zwdd");
        String DepartmentId = Eval.getLoginDepartmentId();

        //设置排序条件
        String sortIndexName = "时间排序";
        String order = "GJNR";
        if(sortindex.isEmpty() || sortindex.equals("0")) {
            sortindex="0";
            order = "GJSJ";
            sortIndexName = "告警内容排序";
        }
        //判断当前登录账号的部门是否为巡维账号
        String where = "1=1 ";
        if(!flowNodeId.isEmpty()){
            where = "flow_Node_Id="+flowNodeId;
        }

        if (DepartmentName.indexOf("巡维") != -1) {
            int index = DepartmentName.indexOf("巡维");
            String xunwei = DepartmentName.substring(0, index + 2);
            where += " and xwzx='" + xunwei+"'";
        }

        QueryReport queryReport = new QueryReport("告警信号闭环管控", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("(select case xwzx when '水乡巡维' then 1 when '莞城巡维' then 2 when '南粤巡维' then 3 " +
                "when '掌洲巡维' then 4 when '北栅巡维' then 5 when '长安巡维' then 6 when '松山湖巡维' then 7 " +
                "when '万江巡维' then 8 when '板桥巡维' then 9 when '立新巡维' then 10 when '盛丰巡维' then 11 " +
                "when '跃立巡维' then 12 when '大朗巡维' then 13 when '横沥巡维' then 14 when '东莞巡维' then 15 " +
                "when '裕元巡维' then 16 when '元江巡维' then 17 when '葵湖巡维' then 18 when '莆心巡维' then 19 " +
                "else 999 end as XWZXNUM, case GJDJ when 11 then '越限' when 19 then '越限' when 20 then '异常' " +
                "when 21 then '异常' else '事故' end as GJLX,*  from w_gjxhht) a left join SYS_FLOW_INSTANCE b " +
                "on a.id=b.data_id");
        queryReport.setSelectfldn("*");
        queryReport.setOrderfldn(order + " DESC ");
        queryReport.setFldnPK("ID");
        queryReport.setWhere_ex(where);
        String Other = "";
        if(sortindex.equals("0")){
            Other = "<a role=button class='btn btn-primary mr-2' href=datalist.html?&dataname=GJXHHT&sortindex=1&flow_node_id="+flowNodeId+">按内容排序</a>";
//            Other = "<button class='btn btn-primary mr-2' onclick='window.open(\"datalist.html?&dataname=GJXHHT&" +
//                    "sortindex=1&flow_node_id="+flowNodeId+"\")' type='button'>内容排序</button>";
        }else{
            Other = "<a role=button class='btn btn-primary mr-2' href=datalist.html?&dataname=GJXHHT&sortindex=0&flow_node_id="+flowNodeId+">按时间排序</a>";
//            Other = "<button class='btn btn-primary mr-2' onclick='window.open(\"datalist.html?&dataname=GJXHHT&" +
//                    "sortindex=0&flow_node_id="+flowNodeId+"\")' type='button'>时间排序</button>";
        }

        if (deptidZdzwdd.equals(DepartmentId) && flowNodeId.equals("10006302")) {
            Other += "&nbsp;&nbsp;&nbsp&nbsp;&nbsp;" +
                    "<button class='btn btn-primary mr-2' onclick='window.open(\"datalist.html?dataname=GJXHXXHL_SETTING\")' type='button'>参数</button>" +
                    "&nbsp;&nbsp;&nbsp&nbsp;&nbsp;" +
                    "<button type=button class='btn btn-primary' onclick=\"javascript:createNewFlow('GJXHHT');\">启动新流程</button>";
        }

        queryReport.setOther(Other);
        String startTime = CommonFunc.getHttpVar("find_start_time");
        String endTime = CommonFunc.getHttpVar("find_end_time");

        if (DepartmentName.indexOf("巡维") == -1) {
            queryReport.addQuerySet("巡维", "find_xwzxnum", "nvl(XWZXNUM,'')", QueryReportFilter.QRFilterType.selarray,
                    "", QueryReportFilter.QRFilterOper.in, "", "",
                    "'1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19':全部;1:水乡巡维;2:莞城巡维;3:南粤巡维;" +
                            "4:掌洲巡维;5:北栅巡维;6:长安巡维;7:松山湖巡维;8:万江巡维;9:板桥巡维;10:立新巡维;11:盛丰巡维;12:跃立巡维;" +
                            "13:大朗巡维;14:横沥巡维;15:东莞巡维;16:裕元巡维;17:元江巡维;18:葵湖巡维;19:莆心巡维;", "");
        }
        queryReport.addQuerySet("厂站","find_cz","CZ");
        queryReport.addQuerySet("告警类型","find_gjdj","nvl(GJDJ,'')", QueryReportFilter.QRFilterType.selarray,
                "", QueryReportFilter.QRFilterOper.in,"","",
                "11,19,20,21,22,23,24,25,26,27,28,29,30:全部;11,19:越限;20,21:异常;22,23,24,25,26,27,28,29,30:事故;","");
//        queryReport.addQuerySet("告警等级","find_gjdj","nvl(GJDJ,'')", QueryReportFilter.QRFilterType.selarray,
//                "", QueryReportFilter.QRFilterOper.in,"","",
//                "20,21,22,23,24,25,26:全部;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;","");

        queryReport.addQuerySet("开始时间","find_start_time", "GJSJ",
                QueryReportFilter.QRFilterType.datetime, startTime,
                QueryReportFilter.QRFilterOper.greaterEqual
        );
        queryReport.addQuerySet("结束时间","find_end_time", "GJSJ",
                QueryReportFilter.QRFilterType.datetime, endTime,
                QueryReportFilter.QRFilterOper.lessEqual
        );
        queryReport.addQuerySet("关键字","find_gjz","CZ||GJDJ||XWZX||GJNR||GJLX");

        queryReport.addColItem("巡维","XWZX");
        queryReport.addColItem("厂站","CZ");
        queryReport.addColItem("告警类型","GJLX");
        queryReport.addColItem("告警时间","GJSJ");
        queryReport.addColItem("告警内容","GJNR");

//
//        queryReport.addActionItem("", Eval.getOpenUrlAction("/kemp/ex_web/oms/app/flowform.html?flow_code=GJXHHT?","data_id","ID","","")
//                ,"接收通知","text","接收通知","");

        queryReport.execQuery();
        return queryReport.getOutPut();
    }

    //核心场所申请人员信息
    public QueryReportOutPut hxcssqryxx() throws Exception {
        //获取2联动元素的JSON数据
        String MAPLIST = "";
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String jsonSql = "select MAPLIST from SYS_TABLE_COLUMN WHERE TABLE_COLUMN_ID=10006706";
            MAPLIST = jdbcDao.getFieldValue(jsonSql, "MAPLIST");
        }

        QueryReport queryReport = new QueryReport("核心场所申请人员信息", request, sysConfig, datasourceConfig);
        queryReport.setpagenumber(100);
        queryReport.setTable("(select XM,SFZH,SQSJ,SSGS,SQNR.LXFS,SQNR,SQTS,FZGRQX,case when flow.FLOW_NODE_ID = '9999' then '审核通过' else '申请中' end as " +
                "SHENHE_STATUS,case when flow.FLOW_NODE_ID != '9999' or flow.FLOW_NODE_ID is null then '' when " +
                "SQNR.sqts = '长期' then '生效中' when hxcs.GLFBKTSJ is null then '' when DATEDIFF(day, hxcs.GLFBKTSJ," +
                " SYSDATE) < TO_NUMBER(REPLACE(SQNR.sqts, '天', '')) then '生效中' else '已过期' end as QX_STATUS " +
                "from W_HXCSQXSQ_SQNR SQNR inner JOIN W_HXCSQXSQ hxcs on hxcs.id = SQNR.REFID left join " +
                "sys_flow_instance flow on flow.data_id = hxcs.id)");
        queryReport.setSelectfldn(" * ");
        queryReport.setOrderfldn(" sqsj ");

        queryReport.addQuerySet("姓名","xm_cz","XM");
        queryReport.addColItem("姓名","XM");
        queryReport.addColItem("身份证","SFZH");
        queryReport.addColItem("申请时间","SQSJ");
        queryReport.addColItem("所属公司","SSGS");
        queryReport.addColItem("联系方式","LXFS");
        queryReport.addColItem("授权内容","SQNR",Eval.getDisplayValueOf41("SQNR",MAPLIST));
        queryReport.addColItem("授权天数","SQTS");
        queryReport.addColItem("非工作日权限","FZGRQX");
        queryReport.addColItem("申请状态","SHENHE_STATUS");
        queryReport.addColItem("门禁状态","QX_STATUS");
        queryReport.execQuery();
        return queryReport.getOutPut();
    }
}
