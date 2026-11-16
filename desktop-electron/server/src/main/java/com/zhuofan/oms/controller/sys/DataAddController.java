package com.zhuofan.oms.controller.sys;

import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import com.zhuofan.core.common.MD5;
import com.zhuofan.core.common.ServerKeyIv;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.core.orm.dao.SysFlowDAO;
import com.zhuofan.core.orm.dao.SysTableColumnDAO;
import com.zhuofan.core.orm.dao.SysTableColumnGroupDAO;
import com.zhuofan.core.orm.dao.SysTableDAO;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.oms.orm.model.rest.RestDataAddModel;
import com.zhuofan.oms.service.DataEditService;
import com.zhuofan.oms.service.ErmsDataAdd;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 */
@RestController
@CrossOrigin
public class DataAddController extends BaseController {

    //系统录入对应关系
    public final static Map<String, String> MAPSYS_TABLENAME_CLASS = new HashMap<String, String>() {{
        put("sys_user", "SYS_USERS");
        put("sys_dept", "SYS_DEPARTMENTS");
        put("sys_role", "SYS_ROLES");
        put("sys_table_class", "SYS_TABLE_CLASS");
        put("sys_table", "SYS_TABLE");
        put("sys_table_group", "SYS_TABLE_COLUMN_GROUP");
        put("sys_table_column", "SYS_TABLE_COLUMN");
        put("sys_module", "SYS_MODULES");
        put("sys_queryreport", "SYS_QUERYREPORT");
        put("sys_flow","SYS_FLOW");
        put("sys_flow_node","SYS_FLOW_NODE");
        put("SYS_FLOW_INSTANCE","SYS_FLOW_INSTANCE");
        put("OMS_JZB_EDIT","OMS_JZB_EDIT");
}};

    //业务录入对应关系
    public final static Map<String, String> MAPBUS_TABLENAME_CLASS = new HashMap<String, String>() {{
        put("zdh_log", "ZDH_LOG");
        put("OMS_JZB","OMS_JZB");
        put("GJXHXXHL_SETTING","GJXHXXHL_SETTING");

		put("OMS_XMXXGL","OMS_XMXXGL");
        put("OMS_FHDL","OMS_FHDL");
        put("OMS_RISK","OMS_RISK");
        put("OMS_FESTA","OMS_FESTA");
        put("ZDH_INSPECT","ZDH_INSPECT");
        put("OMS_ZBBD_CEWEN","OMS_ZBBD_CEWEN");
        put("OMS_SUBSTATION","OMS_SUBSTATION");

        put("W_ASXMXX","W_ASXMXX");
        put("W_ASFPGL","W_ASFPGL");
        put("W_JHZFZJ","W_JHZFZJ");

        put("ALARM_INFO","ALARM_INFO");
        put("ALARM_PHONE_LIST","ALARM_PHONE_LIST");
        put("ZWGJ_WXGD_INFO","ZWGJ_WXGD_INFO");

        put("YF_JZB","YF_JZB");
        put("YF_GFFH_PROJ","YF_GFFH_PROJ");

        // 计划任务 规划
        put("WORK_UNIT", "WORK_UNIT");
        put("TRANSFORMER_SUBSTATION","TRANSFORMER_SUBSTATION");
        put("CREW","CREW");
        put("CREW_MEMBER", "CREW_MEMBER");
        put("SECOND_PLAN_RECORD", "SECOND_PLAN_RECORD");
        put("WARN_INFO_PEER_WEEK", "WARN_INFO_PEER_WEEK");
        //调控中心作业计划
        put("OMS_ZXL", "OMS_ZXL");
        put("OMS_DUTY", "OMS_DUTY");

        //自动化厂站信息管理
        put("CZXX_DDBHGL", "CZXX_DDBHGL");
        put("CZXX_ZDHCZDZ", "CZXX_ZDHCZDZ");
        //母线电压
        put("OMS_BUSBAR", "OMS_BUSBAR");
        //500kV开关
        put("OMS_BRK500", "OMS_BRK500");
        //10kV开关柜
        put("OMS_KGB10", "OMS_KGB10");
        //自动化智能告警
        put("ZDHGJ_ALL", "ZDHGJ_ALL");
        //自动化智能告警
        put("W_ASNDTZJH", "W_ASNDTZJH");
        //配网重过载参数
        put("PWGJ_VALUE_SET", "PWGJ_VALUE_SET");
        put("PWGJ_ELINK_INFO", "PWGJ_ELINK_INFO");
        put("PWGJ_KXZGZ", "PWGJ_KXZGZ");
        put("PWGJ_PHONE_LIST", "PWGJ_PHONE_LIST");
        put("PW_CHZ_DZPZ", "PWGJ_CHZ_DZPZ");
        put("PWGJ_FEEDER","PWGJ_FEEDER");
        put("PWGJ_FEEDER_DEADSET","PWGJ_FEEDER_DEADSET");
        put("PW_DEADNUM_LINE_SET","PWGJ_DEADNUM_SET");
        put("PWGJ_REPLACE","PWGJ_REPLACE");
        put("PW_CHZ","PWGJ_CHZ");
        put("PWGJ_CHZ_SET","PWGJ_CHZ_SET");
        put("PW_DEADNUM_LINE","PWGJ_DEADNUM_LINE");

        put("W_ZDHQXCLJL","W_ZDHQXCLJL");
        put("OMS_FHYC","OMS_FHYC");
        put("HXCSQXSQ_SQNR","HXCSQXSQ_SQNR");
        put("WORK_PLAN_LOAD_RATIO", "WORK_PLAN_LOAD_RATIO");
        put("OMS_FHYC_DPS", "OMS_FHYC_DPS");
    }};

    /*
     * 数据列表修改/新增
     * 参数:无
     * 返回数据:
     *{
			result:0/1,
			data:list
		};
     */
    @RequestMapping(value = "/Data/Add",method = RequestMethod.GET)
    public Map<String, Object> DataAdd(@RequestParam Map<String,String> params) throws Exception{
        String dataName=CommonFunc.nullToSpace(params.get("dataname"));
        String groupCode=CommonFunc.nullToSpace(params.get("group_code"));//.toLowerCase();
        String actionType = CommonFunc.nullToSpace(params.get("actiontype"));
        String pkId = CommonFunc.nullToSpace(params.get("pk_id"));

        if (StringUtils.isEmpty(actionType)){
            actionType=CommonObject.AT_ADD;
        }

        if (StringUtils.isEmpty(groupCode)){
            groupCode="info";
        }

        ///////////////////////////////////////
        //前置处理器:用于判断是否有权限修改/新增此数据
        String methodNameB=CommonFunc.changeWord(dataName+"_"+actionType,true);
        DataEditService dataEditService=new DataEditService(params,sysConfig);

        Method methodB=null;

        try {
            methodB= dataEditService.getClass().getMethod(methodNameB, null);
        }catch(Exception e){
            System.out.println("methodNameB:"+methodNameB);
            e.printStackTrace();
        }

        if (methodB!=null){
            Map<String, Object> mapB=(Map<String, Object>)methodB.invoke(dataEditService,null);
            if (mapB!=null){
                return mapB;
            }
        }
        ///////////////////////////////////////

        //1.获取数据对像值
        String tableCode ="";
        if (MAPSYS_TABLENAME_CLASS.containsKey(dataName)){
            tableCode = MAPSYS_TABLENAME_CLASS.get(dataName);
        }else if(MAPBUS_TABLENAME_CLASS.containsKey(dataName)) {
            tableCode = MAPBUS_TABLENAME_CLASS.get(dataName);
        }

        if ("".equals(tableCode)){
            return CommonFunc.makeErrResult("500001");
        }

        if (!actionType.equals(CommonObject.AT_EDIT) && !actionType.equals(CommonObject.AT_VIEW)){
            actionType=CommonObject.AT_ADD;
        }

        //2.获取表名
        String where = "table_code='"+tableCode+"'";
        SysTableDAO sysTableDAO=new SysTableDAO();
        SysTable sysTable=(SysTable)sysTableDAO.queryByTableCode(tableCode);

        if (sysTable==null) {
            return CommonFunc.makeErrResult("500002");
        }

        String tableClassName=CommonFunc.changeWord(tableCode);
        String tableClassNameDao=tableClassName+"DAO";
        Object objectData;
        Object objectDao=CommonFunc.createTableInstance(tableClassNameDao);

        if (actionType.equals(CommonObject.AT_EDIT) || actionType.equals(CommonObject.AT_VIEW)) {
            objectData=objectDao.getClass().getMethod("queryById", new Class[]
                    {Object.class,String.class}).invoke(objectDao,new Object[]
                    {CommonFunc.createTableInstance(tableClassName),pkId});
        } else {
            objectData=CommonFunc.createTableInstance(tableClassName);
        }


        if ((objectDao==null) || (objectData==null)) {
            return CommonFunc.makeErrResult("500003");
        }

        String pageTitle = (actionType.equals(CommonObject.AT_EDIT)) ?"修改":(actionType.equals(CommonObject.AT_VIEW)) ?"查看":"新增";
        pageTitle+=sysTable.getTableName() +"数据";

        //获取分组名
        String tableId=String.valueOf(sysTable.getTableId());
        SysTableColumnGroupDAO sysTableColumnGroupDAO=new SysTableColumnGroupDAO();
        ArrayList<SysTableColumnGroup> listGroup=sysTableColumnGroupDAO.queryByTableID(tableId);

        //获取字段名
        SysTableColumnDAO sysTableColumnDAO=new SysTableColumnDAO();

        HashMap<String,Object> listGroupCol=new HashMap<String,Object>();
        String[] arrGroupShowtype=groupCode.split(",");

        try(JdbcDao jdbcDao=new JdbcDao()) {
            for (int i = 0; i < listGroup.size(); i++) {
                String groupId = String.valueOf(listGroup.get(i).getTableColumnGroupId());
                if ("all".equals(groupCode)) {
                    listGroup.get(i).setExtraInfo("1");
                } else {
                    if (CommonFunc.inArray(listGroup.get(i).getGroupCode(), arrGroupShowtype)) {
                        listGroup.get(i).setExtraInfo("1");
                    } else {
                        listGroup.get(i).setExtraInfo("0");
                    }
                }

                ArrayList<SysTableColumn> listCol = sysTableColumnDAO.queryByGroupID(listGroup.get(i).getTableColumnGroupId());
                for (int x = 0; x < listCol.size(); x++) {
                    //给每列的数据进行赋值
                    SysTableColumn sysTableColumn = listCol.get(x);
                    String val = "";
                    String columnCode = sysTableColumn.getColumnCode();
                    String defaultValue = sysTableColumn.getDefaultvalue();

                    String methodName = "get" + CommonFunc.changeWord(columnCode);
                    Method getMethod;

                    try {
                        getMethod = objectData.getClass().getMethod(methodName, new Class[]{});
                        Object tmpVal = getMethod.invoke(objectData, new Object[]{});
                        if (tmpVal != null) {
                            val = String.valueOf(tmpVal);
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    if (CommonFunc.inArray(listGroup.get(i).getGroupCode(), arrGroupShowtype) && !defaultValue.isEmpty() && val.isEmpty()) {
                        //设置默认值:只有当前分组且有默认值
                        Eval eval = new Eval();
                        val = CommonFunc.getNotNullString(eval.eval(defaultValue));
                    }

                    listCol.get(x).setExtraInfo(CommonFunc.getNotNullString(val));

                    //设置每列的属性值
                    long inputType = Long.valueOf(sysTableColumn.getInputType());
                    if (inputType == 5 || inputType == 25) {
                        //MAP的单选和多选，从MAP中查询
                        LinkedHashMap<String, String> mapList = new LinkedHashMap<>();
                        if (sysTableColumn.getMaplist().indexOf("return") == 0) {
                            mapList = (LinkedHashMap<String, String>) new Eval().eval(sysTableColumn.getMaplist());
                        } else {
                            //参考支持格式：
                            //10=>500kV变电站;20=>220kV变电站;30=>220kV变低;40=>110kV变电站;50=>10kV大电流柜;60=>220kV线路;70=>110kV线路;
                            String[] arrTmp = sysTableColumn.getMaplist().trim().split(";");
                            for (int xxx = 0; xxx < arrTmp.length; xxx++) {
                                if (StringUtils.isEmpty(arrTmp[xxx].trim())) {
                                    continue;
                                }

                                String[] arrTmpItem = arrTmp[xxx].trim().split("=>", 2);
                                if (arrTmpItem.length == 2) {
                                    mapList.put(arrTmpItem[0].trim(), arrTmpItem[1].trim());
                                } else {
                                    mapList.put(arrTmp[xxx], arrTmp[xxx]);
                                }
                            }
                        }
                        listCol.get(x).setExtraInfo1(mapList);
                    } else if ((inputType == 6) || (inputType == 12)) {
                        //从SQL语句中查询
                        String sql = sysTableColumn.getSqltext().trim();
                        if (sql.substring(0, Eval.EVAL.length()).equals(Eval.EVAL)) {
                            String evalSql = sql.substring(Eval.EVAL.length());
                            Eval eval = new Eval();
                            sql = CommonFunc.getNotNullString(eval.eval(evalSql));
                        }

                        listCol.get(x).setExtraInfo1(jdbcDao.getResult(sql));

                    }
                }

                HashMap<String, Object> objTmp = new HashMap<String, Object>();
                objTmp.put("groupInfo", listGroup.get(i));
                objTmp.put("listCol", listCol);
                listGroupCol.put(groupId, objTmp);
            }

            jdbcDao.close();
        }

        RestDataAddModel model=new RestDataAddModel();
        model.setPageTitle(pageTitle);
        model.setDataName(dataName);
        model.setActionType(actionType);
        model.setPkId(pkId);
        model.setGroupCode(groupCode);
        model.setSysTable(sysTable);
        model.setListGroup(listGroup);
        model.setListGroupCol(listGroupCol);
        model.setObjectData(objectData);

        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("sys_title",sysConfig.getTitle());

        mapData.put("datainfo", JSONObject.toJSON(model));

        return CommonFunc.makeSussResult(mapData,new ServerKeyIv(sysConfig.getTempfilePath()));

    }

    /*
     * 数据列表的保存操作
     * 参数:无
     * 返回数据:
     *{
            result:0/1,
            msg:提示消息
        };
     */
    @RequestMapping(value = "/Data/Save",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> DataSave(@RequestParam Map<String,Object> params,HttpServletRequest req) throws Exception{
        Map<String,Object> mapPost=CommonFunc.checkServerPost(params,sysConfig);

        if (mapPost.get("result").equals("0")){
            //失败
            return mapPost;
        }

        Map<String,String> mapVar=(Map<String,String>)mapPost.get("vars");

        String actionType=CommonFunc.getNotNullString(params.get("actiontype"));
        String tableId=CommonFunc.getNotNullString(params.get("table_id"));
        String groupCode=CommonFunc.getNotNullString(params.get("group_code"));
        String pkId=CommonFunc.getNotNullString(params.get("pk_id"));
        String dataname=CommonFunc.getNotNullString(params.get("dataname"));
        actionType = (!actionType .equals(CommonObject.AT_EDIT))?CommonObject.AT_ADD:CommonObject.AT_EDIT;

        SysTableDAO sysTableDAO=new SysTableDAO();
            SysTable sysTable=(SysTable)sysTableDAO.queryById(new SysTable(),tableId);

        if (sysTable==null) {
            return CommonFunc.makeErrResult("500002");
        }

        //根据表名获取类名
        String className=CommonFunc.changeWord(sysTable.getTableCode());
        String classNameFull=(className.substring(0,3).equals("Sys"))?"com.zhuofan.core.orm.model."+className:"com.zhuofan.oms.orm.model."+className;
        String classNameFullDao=(className.substring(0,3).equals("Sys"))?"com.zhuofan.core.orm.dao."+className+"DAO":"com.zhuofan.oms.orm.dao."+className+"DAO";

        Class clazz=Class.forName(classNameFull);

        Object entry;
        Object entryDao=Class.forName(classNameFullDao).newInstance();

        if (actionType.equals(CommonObject.AT_ADD)){
            entry=clazz.newInstance();
        }else{
            Method method=entryDao.getClass().getMethod("queryById", new Class[] {Object.class,String.class});
            entry=method.invoke(entryDao,new Object[]{clazz.newInstance(),pkId});
        }

        BaseModel baseModel= (BaseModel)entry;
        baseModel.clearChangeInfo(entry);

        Field[] fields = clazz.getDeclaredFields();

        for(Field field:fields){
            TableField tf = field.getAnnotation(TableField.class);
            if (tf == null) {
                continue;
            }
            String fieldName=tf.name();

            if (mapVar.containsKey(fieldName) && !"AUTO".equals(tf.idType())) {
                String val=mapVar.get(fieldName);
                boolean flag=true;
                if (tf.isPrimaryKey()){
                    if (actionType.equals(CommonObject.AT_ADD)) {
                        //主键,自动取值
                        if(!StringUtils.isNotBlank(pkId)){
                            pkId = new JdbcDao().getSeqNextVal();
                        }
                        val = pkId;
                    }else{
                        flag=false;
                    }
                }

                if (flag) {
                    String methodName=CommonFunc.changeWord(fieldName);
                    methodName = "set"
                            + methodName.substring(0, 1).toUpperCase()
                            + methodName.substring(1);

                    if (tf.type().equals(TableField.TF_LOCAL_DATE_TIME)) {
                        try {
                            Method setMethod = entry.getClass().getMethod(methodName, LocalDateTime.class);
                            setMethod.invoke(entry, LocalDateTime.parse(mapVar.get(fieldName), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (tf.type().equals(TableField.TF_BIGDECIMAL)) {
                        try {
                            Method setMethod = entry.getClass().getMethod(methodName, BigDecimal.class);
                            setMethod.invoke(entry, new BigDecimal(mapVar.get(fieldName)));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (tf.type().equals(TableField.TF_NUMBER)){
                        if (CommonFunc.isNumeric(val)) {
                            Method setMethod =null;
                            try {
                                setMethod = entry.getClass().getMethod(methodName, new Class[]{long.class});
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (setMethod!=null){
                                setMethod.invoke(entry, new Object[]{Long.valueOf(val)});
                            }else{
                                try {
                                    setMethod = entry.getClass().getMethod(methodName, new Class[]{double.class});
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if (setMethod!=null) {
                                    setMethod.invoke(entry, new Object[]{Double.valueOf(val)});
                                } else {
                                    try {
                                        setMethod = entry.getClass().getMethod(methodName, short.class);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    if (setMethod!=null) {
                                        setMethod.invoke(entry, Short.valueOf(val));
                                    }else{
                                        try {
                                            setMethod = entry.getClass().getMethod(methodName, Long.class);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        if (setMethod!=null) {
                                            setMethod.invoke(entry, Long.valueOf(val));
                                        }
                                    }
                                }

                            }
                        }
                    }else{
                        Method setMethod = entry.getClass().getMethod(methodName, new Class[]{String.class});

                        if (tf.inputType()==TableField.TFInpytType.password) {
                            //密码：单独处理
                            String md5Password = MD5.GetMD5Code(val);
                            if (actionType.equals(CommonObject.AT_ADD)){
                                setMethod.invoke(entry, new Object[]{md5Password});
                            }else{
                                if (StringUtils.isNotEmpty(val)){
                                    setMethod.invoke(entry, new Object[]{md5Password});
                                }
                            }


                        }else {
                            setMethod.invoke(entry, new Object[]{val});
                        }
                    }
                }
            }else{
                //文件上传处理
                if (tf.type().equals(TableField.TF_FILEUPLOAD)){
                    //文件上传,需特殊处理
                    ErmsDataAdd ermsDataAdd=new ErmsDataAdd();
                    ermsDataAdd.uploadFile(req,fieldName,sysConfig.getEfileSavepath(),entry);
                }
            }

        }

        //保存数据
        Method saveMethod;
        if (actionType.equals(CommonObject.AT_ADD)){
            saveMethod=entryDao.getClass().getMethod("saveNew",new Class[]{Object.class});
        }else{
            saveMethod=entryDao.getClass().getMethod("update",new Class[]{Object.class});
        }
        boolean saveResult=(boolean)saveMethod.invoke(entryDao,new Object[]{entry});

        if (!saveResult){
            BaseOrmSupport baseOrmSupport=(BaseOrmSupport) entryDao;
            return CommonFunc.makeErrResult("600099",baseOrmSupport.getLastError());
        }

        ///////////////////////////////////////
        //后置处理器:用于处理在保存数据成功后,执行额外的操作
        String methodNameAS=CommonFunc.changeWord(dataname+"_Save",true);
        DataEditService dataEditService=new DataEditService(params,entry,sysConfig);

        Method methodAS=null;

        try {
            methodAS= dataEditService.getClass().getMethod(methodNameAS, null);
        }catch(Exception e){
            System.out.println("methodNameAS:"+methodNameAS);
            e.printStackTrace();
        }

        if (methodAS!=null){
            methodAS.invoke(dataEditService,null);
        }
        ///////////////////////////////////////

        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("pkId",pkId);
        mapData.put("redirectFlag","1");
        mapData.put("redirectUrl","dataadd.html?actiontype=edit&group_code="+groupCode+"&dataname="+dataname+"&pk_id="+pkId);

        return CommonFunc.makeSussResult(mapData);
    }

    /*
     * 数据列表的删除
     * 参数:dataname,删除的数据名,pk_id:删除的主键
     * 返回数据:
     *{
            result:0/1,
            msg:提示消息
        };
     */
    @RequestMapping(value = "/Data/Delete",method = RequestMethod.GET)
    public Map<String, Object> DataDelete(@RequestParam Map<String,String> params) throws Exception {
        String dataName=CommonFunc.getNotNullString(params.get("dataname"));
        String pkIds=CommonFunc.getNotNullString(params.get("pk_id"));

        //1.获取数据表名
        String tableCode ="";
        if (MAPSYS_TABLENAME_CLASS.containsKey(dataName)){
            tableCode = MAPSYS_TABLENAME_CLASS.get(dataName);
        }else if(MAPBUS_TABLENAME_CLASS.containsKey(dataName)) {
            tableCode = MAPBUS_TABLENAME_CLASS.get(dataName);
        }

        if (tableCode.equals("")){
            return CommonFunc.makeErrResult("500001");
        }

        //2.数据记录数据
        String tableClassName=CommonFunc.changeWord(tableCode);
        String tableClassNameDao=tableClassName+"DAO";
        Object objectDao=CommonFunc.createTableInstance(tableClassNameDao);

        String arrIds[]=pkIds.split(",");
        for (int i=0;i<arrIds.length;i++) {
            Object objectData;
            objectData = objectDao.getClass().getMethod("queryById", new Class[]
                    {Object.class, String.class}).invoke(objectDao, new Object[]
                    {CommonFunc.createTableInstance(tableClassName), arrIds[i]});

            if ((!"SYS_FLOW_INSTANCE".equals(dataName)) && (objectData == null)) {
                return CommonFunc.makeErrResult("500003");
            }

            //3.删除数据(注意:此处在删除数据前,可以统一做一个回收站/备份机制处理,将删除的数据对象,保存到删除表中,以供以后恢复或备查)
            boolean deleteFlag =false;

            if (objectData!=null){
                deleteFlag=(boolean) objectDao.getClass().getMethod("deleteById", new Class[]
                        {Object.class, String.class}).invoke(objectDao, new Object[]
                        {objectData, arrIds[i]});
            }

            //4.如果是删除流程实例，则删除子表
            if ("SYS_FLOW_INSTANCE".equals(dataName)){
                try(JdbcDao jdbcDao=new JdbcDao()) {
                    if (objectData!=null) {
                        jdbcDao.execute("delete from sys_flow_exec where flow_instance_id='" + arrIds[i] + "'");

                        SysFlowInstance sysFlowInstance = (SysFlowInstance) objectData;
                        SysFlow sysFlow = (SysFlow) new SysFlowDAO().queryById(new SysFlow(), sysFlowInstance.getFlowId());
                        if (sysFlow != null) {
                            SysTable sysTable = (SysTable) new SysTableDAO().queryById(new SysTable(), sysFlow.getTableId());
                            if (sysTable != null) {
                                jdbcDao.execute("delete from " + sysTable.getTableCode() + " where id='" + sysFlowInstance.getDataId() + "'");
                            }
                        }
                    }else{
                        //仅仅删除业务数据
                        String busTableCode=CommonFunc.getNotNullString(params.get("bus_table_code"));
                        if (!StringUtils.isEmpty(busTableCode)) {
                            String busPkId = CommonFunc.getNotNullString(params.get("bus_pk_id"));
                            jdbcDao.execute("delete from " + busTableCode + " where id='" + busPkId + "'");
                        }
                    }

                    jdbcDao.close();
                }
            }
        }

        //if (!deleteFlag){
        //return CommonFunc.makeErrResult("500009");
        //}

        return CommonFunc.makeSussResult(null);
    }
}
