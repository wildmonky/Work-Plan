package com.zhuofan.core.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.kafka.KafkaClient;
import com.zhuofan.core.kafka.KafkaMessage;
import com.zhuofan.core.orm.dao.*;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.sys.UserInfoShort;
import com.zhuofan.oms.service.GrantService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
流程类
 */
public  class FlowService {
    //流程节点待处理
    public static final String FLOW_ACTION_START = "10";

    //确认流程
    public static final String FLOW_ACTION_ACCEPT = "30";

    //回退流程
    public static final String FLOW_ACTION_RETURN = "40";

    //结束
    public static final String FLOW_ACTION_END = "90";

    //草稿
    public static final String FLOW_STATUS_DRAFT = "-1";
    //执行中
    public static final String FLOW_STATUS_EXECUTING = "1";
    //结束
    public static final String FLOW_STATUS_END = "2";
    //取消
    public static final String FLOW_STATUS_CANCEL = "99";

    //流程结点类型
    //顺序节点
    public static final String NODETYPE_SEQ = "10";
    //分支结点
    public static final String NODETYPE_SWITCH = "20";
    //同步节点
    public static final String NODETYPE_MORE = "30";
    //归档结点
    public static final String NODE_END = "9999";

    /*
    更新流程对应的执行信息的业务数据：子表数据部分
    传入:流程ID,flowId
    返回:
     */
    public static boolean updateFlowSubData(String allSubtabledata, String dataId, SysFlowNode sysFlowNodeCurr) throws Exception {
        //先判断是否有权限
        if (sysFlowNodeCurr == null) {
            return false;
        }

        if (StringUtils.isEmpty(allSubtabledata.trim())) {
            return false;
        }

        JSONObject jsonData = JSONObject.parseObject(allSubtabledata);
        if (jsonData == null) {
            return false;
        }

        try(JdbcDao jdbcDao = new JdbcDao()) {
            String tableSubinfo = CommonFunc.getNotNullString(sysFlowNodeCurr.getTableSubinfo()).trim();
            String[] arrTableId = tableSubinfo.split(",");

            for (int i = 0; i < arrTableId.length; i++) {
                String tableId = arrTableId[i].trim();
                if (!jsonData.containsKey(tableId)) {
                    continue;
                }

                //获取表资料
                SysTable sysTable = (SysTable) new SysTableDAO().queryById(new SysTable(), tableId);
                if (sysTable == null) {
                    continue;
                }

                //先清除原有的数据
                String tableName = sysTable.getTableCode();
                String sql = String.format("delete from %s where REFID='%s'", tableName, dataId);
                jdbcDao.execute(sql);
                JSONArray jsonArr = (JSONArray) jsonData.get(tableId);
                if (jsonArr == null) {
                    continue;
                }

                //逐一加入新数据
                String fieldNames = "ID,REFID";
                for (int j = 0; j < jsonArr.size(); j++) {
                    String fieldValues = "";

                    JSONObject jsonItem = (JSONObject) jsonArr.get(j);
                    for (String fieldName : jsonItem.keySet()) {
                        String value = jsonItem.getString(fieldName);
                        if (j == 0) {
                            fieldNames += "," + fieldName;
                        }
                        fieldValues += ",'" + value.trim() + "'";
                    }

                    sql = String.format("insert into %s (%s) values ('%s','%s' %s)",
                            tableName, fieldNames, jdbcDao.getSeqNextVal(), dataId, fieldValues);
                    jdbcDao.execute(sql);
                }
            }

            jdbcDao.close();
        }
        return true;
    }

    /*
    根据流程ID寻找 流程的所有节点
    传入:流程ID,flowId
    返回:
     */
    public static ArrayList<SysFlowNode> getAllFlowNode(String flowId) throws Exception {
        SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
        sysFlowNodeDAO.setWhere("FLOW_ID='" + flowId + "' AND NVL(IS_CHILDNODE,'0')='0'");
        sysFlowNodeDAO.setOrder("SORTINDEX ASC");
        ArrayList<SysFlowNode> list = (ArrayList<SysFlowNode>) sysFlowNodeDAO.query(new SysFlowNode());

        return list;
    }

    /*
    根据流程ID寻找 流程的所有节点
    传入:流程ID,flowId
    返回:
     */
    public static ArrayList<SysFlowNode> getAllFlowNodeByDeptId(String flowId, String deptId) throws Exception {
        SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
        sysFlowNodeDAO.setWhere("FLOW_ID='" + flowId + "' AND NVL(IS_CHILDNODE,'0')='0'");
        sysFlowNodeDAO.setOrder("SORTINDEX ASC");
        ArrayList<SysFlowNode> list = (ArrayList<SysFlowNode>) sysFlowNodeDAO.query(new SysFlowNode());

        for (int i = list.size() - 1; i >= 0; i--) {
            boolean flag = false;
            String grants = list.get(i).getGrants();
            JSONObject json = JSONObject.parseObject(grants);
            if (json.containsKey("departmentId")) {
                String[] arrDept = json.get("departmentId").toString().split(",");
                if (CommonFunc.inArray(deptId, arrDept)) {
                    flag = true;
                }
            }

            if (!flag) {
                list.remove(i);
            }
        }

        return list;
    }

    /*
    根据流程ID寻找 流程的所有节点
    传入:流程ID,flowId
    返回:
     */
    public static ArrayList<SysFlowNode> getAllFlowNodeAndChild(String flowId) throws Exception {
        SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
        sysFlowNodeDAO.setWhere("FLOW_ID='" + flowId + "'");
        sysFlowNodeDAO.setOrder("SORTINDEX ASC");
        ArrayList<SysFlowNode> list = (ArrayList<SysFlowNode>) sysFlowNodeDAO.query(new SysFlowNode());

        return list;
    }

    /*
     * 检查节点的编辑和回退权限
     */
    public static HashMap<String, String> checkFlowExecGrant(SysFlowInstance sysFlowInstance) throws Exception {
        boolean flagModify = false;
        boolean flagReturn = false;

        HashMap<String, String> mapResult = new HashMap<String, String>();

        if (sysFlowInstance != null) {
            String userName = Eval.getLoginName();
            String departmentId = Eval.getLoginDepartmentId();

            SysFlowExec sysFlowExec = FlowService.getSysFlowExec(userName, departmentId, sysFlowInstance.getFlowInstanceId());

            if (sysFlowExec != null) {
                flagModify = true;
                if (StringUtils.isNotEmpty(sysFlowExec.getPrevFlowExecId()) && StringUtils.isNotEmpty(sysFlowExec.getPrevFlowNodeId())) {
                    String flowNodeId = sysFlowExec.getFlowNodeId();
                    SysFlowNode sysFlowNode = (SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), flowNodeId);
                    if (sysFlowNode != null && !"1".equals(sysFlowNode.getIsEmptynode())) {
                        flagReturn = true;
                    }
                }
            }
        }

        mapResult.put("flagModify", flagModify ? "1" : "0");
        mapResult.put("flagReturn", flagReturn ? "1" : "0");

        return mapResult;
    }

    /*
     * 数据录入时,上传文件处理
     * 参数:
     * req :带文件对象的request
     * fieldName:要保存到数据库的字段名,以及文件上传的变量名
     * savePath:要保存到的根目录
     * entry:如果为对象,则设置相关的值
     *
     * 返回数据:
     *{
            result:0/1,
            msg:提示消息
        };
     */
    public static String uploadFile(HttpServletRequest req, String fieldName, String savePath,
                                    String attaSrctype, String attaSrcid) {
        MultipartHttpServletRequest mulReq = (MultipartHttpServletRequest) req;
        MultipartFile mulFile = mulReq.getFile(fieldName);

        String yearMonth = CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()), "yyyyMM");
        String uuid = UUID.randomUUID().toString();

        if (mulFile != null && StringUtils.isNotEmpty(mulFile.getOriginalFilename())) {
            String fileName = mulFile.getOriginalFilename();
            String fileFormat = fileName.substring(fileName.lastIndexOf("."));

            String saveFileName = uuid + fileFormat;
            String dir = savePath + "/" + yearMonth;
            String saveFullName = savePath + "/" + yearMonth + "/" + saveFileName;

            try {
                File fileD = new File(dir);
                if (!fileD.exists()) {
                    fileD.mkdirs();
                }

                if (fileD.exists()) {
                    File targetFile = new File(saveFullName);
                    System.out.println("targetFile:" + targetFile);
                    FileUtils.writeByteArrayToFile(targetFile, mulFile.getBytes());
                }

                //保存至相关对象
                try(JdbcDao jdbcDao = new JdbcDao()) {
                    SysAttachmentDAO sysAttachmentDAO = new SysAttachmentDAO();
                    SysAttachment sysAttachment = new SysAttachment();
                    SysDepartments sysDepartments = (SysDepartments) new SysDepartmentsDAO().queryById(new SysDepartments(), Eval.getLoginDepartmentId());
                    String departmentname = "";
                    if (sysDepartments != null) {
                        departmentname = sysDepartments.getDepartmentname();
                    }

                    String attaId = jdbcDao.getSeqNextVal();
                    long size = mulFile.getSize();
                    String now = CommonFunc.unix2Date(CommonFunc.unixTimeStr());

                    sysAttachment.setAttaId(attaId);
                    sysAttachment.setAttaSrctype(attaSrctype);
                    sysAttachment.setAttaSrcid(attaSrcid);
                    sysAttachment.setAttachmentName(fileName);
                    sysAttachment.setAttachmentDir(dir);
                    sysAttachment.setAttachmentSize(String.valueOf(size));
                    sysAttachment.setUploadUsername(Eval.getLoginName());
                    sysAttachment.setUploadUserid(Eval.getLoginUserId());
                    sysAttachment.setUploadUserdept(departmentname);
                    sysAttachment.setContent(saveFullName);
                    sysAttachment.setUploadUserdeptid(Eval.getLoginDepartmentId());
                    sysAttachment.setUploadTime(now);
                    sysAttachment.setEditTime(now);

                    sysAttachmentDAO.saveNew(sysAttachment);
                    JSONArray jsonArray = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("id", attaId);
                    json.put("name", fileName);
                    jsonArray.add(json);

                    jdbcDao.close();
                    return jsonArray.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    /*
   确认流程节点,考虑分支情况
   传入:流程ID,flowId
   返回:
    */
    public static String[] getSwitchNodeId(String switchCode, Map<String, String> mapData) throws Exception {
        JSONObject json = JSON.parseObject(switchCode);
        if (json.containsKey("type")) {
            String type = json.getString("type");
            if ("10".equals(type)) {
                String fieldName = json.getString("field").toUpperCase(Locale.ROOT);
                JSONArray arrItem = json.getJSONArray("values");
                String fieldValue = mapData.get(fieldName).trim();

                for (int i = 0; i < arrItem.size(); i++) {
                    String item = (String) arrItem.get(i);
                    String[] arrTmp = item.trim().split(":", 3);
                    if (arrTmp.length < 2) {
                        continue;
                    }

                    if (fieldValue.equals(arrTmp[0].trim())) {
                        if (arrTmp.length == 2) {
                            return new String[]{arrTmp[1].trim()};
                        } else {
                            //会签节点和其子节点
                            return new String[]{arrTmp[1].trim(), arrTmp[2].trim()};
                        }
                    }
                }
            }
        }

        return null;
    }

    /*
    确认流程节点,考虑分支情况
    传入:流程ID,flowId
    返回:
     */
    public static boolean acceptFlow(SysFlow sysFlow,Map<String, String> mapData, SysFlowNode sysFlowNode, SysFlowExec sysFlowExec,
                                     SysFlowInstance sysFlowInstance, SysFlowNode SysFlowNodeParent, String nodekey,
                                     Map<String, String> mapVar) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {
            String now = CommonFunc.unix2Date(CommonFunc.unixTimeStr());
            CommonLogger commonLogger = CommonLogger.getInstance();

            //1.得到下一步流程节点的信
            String nodeType = sysFlowNode.getNodeType();
            String flowId = sysFlowNode.getFlowId();
            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            SysFlowNode sysFlowNodeNext = null;
            String flowInstanceId = sysFlowInstance.getFlowInstanceId();
            boolean endFlag = false, moreNodeEnd = false;
            String flowNodeIdNextMore = "";

            boolean flagChildNode = false;
            if ("1".equals(sysFlowNode.getIsChildnode())) {

                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow: is childnode");

                flagChildNode = true;

                //会签子节点处理：是否全部通过
                String sqlCount = "select count(*) mycount from sys_flow_exec a left join sys_flow_node b on a.flow_node_id=b.flow_node_id  where " +
                        " nvl(is_emptynode,'0')='0' and flow_instance_id='" + flowInstanceId + "' and flow_exec_id<>'" + sysFlowExec.getFlowExecId() + "' and exec_action='10'";
                int notEndCount = Integer.valueOf(jdbcDao.getFieldValue(sqlCount, "MYCOUNT"));

                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow: notEndCount:" + String.valueOf(notEndCount));

                if (notEndCount == 0) {
                    String sortindex = String.valueOf(SysFlowNodeParent.getSortindex());
                    sysFlowNodeDAO.setWhere(String.format("FLOW_ID='%s' and nvl(is_childnode,'0')='0' and SORTINDEX>'%s'", flowId, sortindex));
                    sysFlowNodeDAO.setOrder(" SORTINDEX ASC ");
                    sysFlowNodeNext = (SysFlowNode) sysFlowNodeDAO.queryFirstRow(new SysFlowNode());
                    if (sysFlowNodeNext == null) {
                        endFlag = true;
                    }
                    moreNodeEnd = true;

                    //更新其余
                /*
                String sqlEmptyNode="update sys_flow_exec set exec_action='"+FlowService.FLOW_ACTION_ACCEPT+"' " +
                        " where flow_instance_id='"+flowInstanceId+"' and exec_action='10' and flow_exec_id<>'"+sysFlowExec.getFlowExecId()+"'";
                jdbcDao.execute(sqlEmptyNode);
                */
                }
            } else {
                //普通节点处理
                if (FlowService.NODETYPE_SWITCH.equals(nodeType)) {
                    commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow: is nodetype_switch");

                    //分支:根据分支条件获取下一结点
                    String arrNextNodeId[] = FlowService.getSwitchNodeId(sysFlowNode.getSwitchCode(), mapData);
                    if (arrNextNodeId != null) {
                        String flowNodeIdNext = arrNextNodeId[0];
                        if (arrNextNodeId.length > 1) {
                            flowNodeIdNextMore = arrNextNodeId[1];
                        }
                        sysFlowNodeNext = (SysFlowNode) sysFlowNodeDAO.queryById(new SysFlowNode(), flowNodeIdNext);
                    }
                } else {
                    String sortindex = String.valueOf(sysFlowNode.getSortindex());
                    sysFlowNodeDAO.setWhere(String.format("FLOW_ID='%s' and nvl(is_childnode,'0')='0' and SORTINDEX>'%s'", flowId, sortindex));
                    sysFlowNodeDAO.setOrder(" SORTINDEX ASC ");
                    sysFlowNodeNext = (SysFlowNode) sysFlowNodeDAO.queryFirstRow(new SysFlowNode());
                }
                if (sysFlowNodeNext == null) {
                    endFlag = true;
                }
            }

            //更新实例
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow: endFlag:" + String.valueOf(endFlag));

            //////////////////////////////////////////////////
            //单独处理
            //////////////////////////////////////////////////

            if (endFlag) {
                //最后一个结点
                sysFlowInstance.setStatus(FlowService.FLOW_STATUS_END);
                sysFlowInstance.setEndTime(now);
                //自动归档结点
                sysFlowInstance.setFlowNodeId(FlowService.NODE_END);
                endFlag = true;
            } else {
                if (sysFlowNodeNext != null) {
                    sysFlowInstance.setFlowNodeId(sysFlowNodeNext.getFlowNodeId());
                }
            }

            sysFlowInstance.setFlagReturn("0");
            boolean result = new SysFlowInstanceDAO().update(sysFlowInstance);
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow: update sysFlowInstance result:" + String.valueOf(result));

            String sendtoUser = "";

            if (result) {
                if (!endFlag && sysFlowNodeNext != null) {
                    //非最后一个节点
                    //如果是会签结点
                    if (FlowService.NODETYPE_MORE.equals(sysFlowNodeNext.getNodeType())) {
                        //下一个节点是会签结点
                        //获取会签结点的所有子节点
                        String whereNextMore = "";
                        if (StringUtils.isNotEmpty(flowNodeIdNextMore)) {
                            whereNextMore = " and flow_node_id in ('" + flowNodeIdNextMore.replace(" ", "").replace(",", "','") + "')";
                        }
                        sysFlowNodeDAO.setWhere("FLOW_ID='" + flowId + "' and nvl(is_childnode,'0')='1' and parent_node_id='" +
                                sysFlowNodeNext.getFlowNodeId() + "' " + whereNextMore);

                        ArrayList<SysFlowNode> listFlowNodeNext = sysFlowNodeDAO.query(new SysFlowNode());
                        for (int i = 0; i < listFlowNodeNext.size(); i++) {
                            String userids = "";
                            if (StringUtils.isNotEmpty(nodekey)) {
                                String varname = "seluser_" + nodekey + "_" + listFlowNodeNext.get(i).getFlowNodeId();
                                if (mapVar.containsKey(varname)) {
                                    userids = mapVar.get(varname);
                                }
                            }
                            String sendtoUserItem = addSysFlowExec(sysFlow,listFlowNodeNext.get(i), jdbcDao,
                                    flowId, flowInstanceId, sysFlowExec, "0", userids, mapData);
                            sendtoUser = (StringUtils.isEmpty(sendtoUserItem)) ? sendtoUserItem : sendtoUser + sendtoUserItem;
                        }
                    } else {
                        String userids = "";
                        if (StringUtils.isNotEmpty(nodekey)) {
                            String varname = "seluser_" + nodekey + "_" + sysFlowNodeNext.getFlowNodeId();
                            if (mapVar.containsKey(varname)) {
                                userids = mapVar.get(varname);
                            }
                        } else {
                            //判断当前节点是否有默认提交人并且
                            //2025-08-04:sys_flow_node的节点，is_selectuser设置为0,
                            // 并且submit_defuser设置不为空时，会自动发送到些人
                            String submitDefuser = sysFlowNode.getSubmitDefuser().trim();
                            String isSelectuser = sysFlowNode.getIsSelectuser();
                            isSelectuser=(StringUtils.isEmpty(isSelectuser))?"0":isSelectuser;
                            if ("0".equals(isSelectuser) && StringUtils.isNotEmpty(submitDefuser)) {
                                /////////////////////////////////////////////////////
                                //2026-08-20:增加部门领导支持（动态查询，固定为DEPT_LEADER）
                                if ("DEPT_LEADER".equals(submitDefuser.toUpperCase(Locale.ROOT))){
                                    //获取部门领导
                                    userids = GrantService.getDeptLeader(Eval.getLoginDepartmentId(),"USERID");
                                }else {
                                    //2025-11-18：增加支持多人处理，程涛,admin
                                    userids = jdbcDao.getFieldValue(
                                            "select WM_CONCAT(userid) userid from sys_users where username in ('" +
                                                    submitDefuser.replaceAll(",", "','") + "')", "USERID").trim();
                                }
                            }
                        }
                        String sendtoUserItem = addSysFlowExec(sysFlow,sysFlowNodeNext, jdbcDao, flowId,
                                flowInstanceId, sysFlowExec, "0", userids, mapData);
                        sendtoUser = (StringUtils.isEmpty(sendtoUserItem)) ? sendtoUserItem : sendtoUser + sendtoUserItem;
                    }
                }
            }

            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow: sendtoUser:" + sendtoUser);

            if (!flagChildNode || endFlag) {
                sysFlowInstance.setSendtoUser(sendtoUser);
                String sqlUpdateUser = "update sys_flow_instance set sendto_user='" + sendtoUser + "' where flow_instance_id='" + sysFlowInstance.getFlowInstanceId() + "'";
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow: update sys_flow_instance sendto_user:" + sqlUpdateUser);
                jdbcDao.execute(sqlUpdateUser);
            }
            jdbcDao.close();

        }
        return true;
    }

    public static String getFlowAcceptElinkTpl(String tpl,Map<String,String> mapData,
                                               String nodeName,String flowName) throws Exception{
        String msg=tpl;
        try(JdbcDao jdbcDao = new JdbcDao()) {
            //当前处理人
            String sql = "select * from SYS_FLOW_INSTANCE where data_id=to_char(" + mapData.get("ID") + ")";
            Map<String, String> flowInstance = jdbcDao.getFirstRow(sql);
            String sendto_user = flowInstance.get("SENDTO_USER");

            //参考 ：
            //{now} {login_dept}发送了图档名称为{f_bh}的{flow_name}至{node_name}节点，请及时处理。
            msg = msg.replace("{login_dept}", Eval.getLoginDepartmentName());
            msg = msg.replace("{now}", CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
            msg = msg.replace("{flow_name}", flowName).replace("{node_name}", nodeName);
            msg = msg.replace("{sendto_user}", sendto_user);
            //替换业务数据
            for (Map.Entry<String, String> entry : mapData.entrySet()) {
                String src = "{" + entry.getKey().trim() + "}";
                msg = msg.replace(src.toLowerCase(Locale.ROOT), entry.getValue());
            }

            jdbcDao.close();
        }
        return msg;
    }

    private static String addSysFlowExec(SysFlow sysFlow,SysFlowNode sysFlowNodeNext, JdbcDao jdbcDao, String flowId, String flowInstanceId,
                                         SysFlowExec sysFlowExec, String flagReturn, String userids, Map<String, String> mapData) throws Exception {
        //下一个节点是普通结点
        String grants = sysFlowNodeNext.getGrants();
        JSONObject json = JSON.parseObject(grants);
        String departmentId = null;
        String usernames = "";
        String[] arrUserId = userids.split(",");
        ArrayList<String> listElinkid = new ArrayList<>();

        for (int i = 0; i < arrUserId.length; i++) {
            SysUsers sysUser = (SysUsers) new SysUsersDAO().queryById(new SysUsers(), arrUserId[i]);
            if (sysUser != null) {
                if (!StringUtils.isEmpty(sysUser.getElinkId())) {
                    listElinkid.add(sysUser.getElinkId());
                }
                usernames += sysUser.getUsername() + ",";
            }
        }

        /////////////////////////////////////////////////////////////////
        //05-21：此处单独处理：
        //ZDHCZDZZDGLLC(100014)，主调审核节点，发给最后节点会签时，厂站自动化节点人发给编制人，主调自动化执行节点人为空
        if (StringUtils.isEmpty(usernames) && (
                (sysFlowNodeNext.getFlowId().equals("100014") && sysFlowNodeNext.getFlowNodeId().equals("10001406"))
                || (sysFlowNodeNext.getFlowId().equals("100051") && sysFlowNodeNext.getFlowNodeId().equals("10005105"))
        )) {
            //获取实例第一个节点的人员
            String sqlFirstUsername = "select USERNAME from sys_flow_exec where flow_instance_id='" +
                    flowInstanceId + "' and exec_action='30' order by exec_time asc limit 0,1";
            usernames = jdbcDao.getFieldValue(sqlFirstUsername, "USERNAME");
            if (StringUtils.isNotEmpty(usernames)) {
                usernames += ",";
            }
            //更新流程实例的当前处理人
            String sqlUpdateUser = "update sys_flow_instance set sendto_user='" + usernames + "' where flow_instance_id='" + flowInstanceId + "'";
            jdbcDao.execute(sqlUpdateUser);

        }
        /////////////////////////////////////////////////////////////////

        CommonLogger commonLogger=CommonLogger.getInstance();
        commonLogger.info("addSysFlowExec,departmentId:"+departmentId);

        if (json != null) {
            if (json.containsKey("departmentId")) {
                departmentId = json.getString("departmentId");
            }
        }

        commonLogger.info("addSysFlowExec,departmentId:"+departmentId);
        //////////////////////////////////////////////////
        //个性化处理：提交到10005903时，根据字段判断
        if ("10005903".equals(sysFlowNodeNext.getFlowNodeId())){
            commonLogger.info("addSysFlowExec,10005903 found ");
            departmentId="主网自动化".equals(mapData.get("SHBM").trim())
                ?CommonFunc.getConfigByKey("sys.deptid_zdhyb")
                    :CommonFunc.getConfigByKey("sys.deptid_zdheb");
        }

        //////////////////////////////////////////////////
        //核心场所门禁模块：提交到10006602时，根据起单人部门判断下发至自动化还是综合
        if("10006603".equals(sysFlowNodeNext.getFlowNodeId())){
            commonLogger.info("addSysFlowExec,10006603 found ");
            String sqlDepartmentId = "select departmentid from SYS_users where userid=(" +
                    "select userid from sys_flow_exec where flow_node_id=10006601 and " +
                    "flow_instance_id='"+flowInstanceId+"' order by exec_time asc limit 0,1)";
            departmentId = jdbcDao.getFieldValue(sqlDepartmentId, "departmentid");
            departmentId = "25156".equals(departmentId) || "27790".equals(departmentId) ||
                    "25157".equals(departmentId)?"28493":"25157";
        }

        commonLogger.info("addSysFlowExec,departmentId:"+departmentId);
        /////////////////////////////////////////////////

        //新增一个流程执行信息
        SysFlowExec sysFlowExecNew = new SysFlowExec();
        String flowExecIdNew = jdbcDao.getSeqNextVal();
        sysFlowExecNew.setFlowExecId(flowExecIdNew);
        sysFlowExecNew.setFlowId(flowId);
        sysFlowExecNew.setFlowNodeId(sysFlowNodeNext.getFlowNodeId());
        sysFlowExecNew.setFlowInstanceId(flowInstanceId);
        sysFlowExecNew.setCreateTime(CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
        sysFlowExecNew.setExecAction(FlowService.FLOW_ACTION_START);
        sysFlowExecNew.setPrevFlowNodeId(sysFlowExec.getFlowNodeId());
        sysFlowExecNew.setPrevFlowExecId(sysFlowExec.getFlowExecId());
        sysFlowExecNew.setDepartmentid(departmentId);
        sysFlowExecNew.setFlagReturn(flagReturn);
        sysFlowExecNew.setSendtoUser(usernames);

        boolean result = new SysFlowExecDAO().saveNew(sysFlowExecNew);

        ///////////////////////////////////////////////////////////////
        //自动发送ELINK信息
        //发送原则：
        String msg = sysFlowNodeNext.getElinkAccept();
        commonLogger.info("addSysFlowExec,msg:"+msg);

        if (StringUtils.isNotEmpty(msg)) {
            String deptIdZdhyb = CommonFunc.getConfigByKey("sys.deptid_zdhyb").trim();
            String departmentIdTmp=departmentId==null?"":departmentId.trim();
            String[] arrDeptId =departmentIdTmp.split(",");
            String flowName = jdbcDao.getFieldValue("select flow_name from sys_flow where flow_id='" + flowId + "'", "FLOW_NAME");

            msg=getFlowAcceptElinkTpl(msg,mapData,sysFlowNodeNext.getNodeName(),flowName);

            KafkaClient kafkaClient = new KafkaClient("ZFOMS_流程类_"+sysFlow.getFlowName());

            //发送给相关的部门
            commonLogger.info("addSysFlowExec,listElinkid.size:"+listElinkid.size());
            commonLogger.info("addSysFlowExec,listElinkid:"+listElinkid.toString());
            if(listElinkid.size()==0){
                commonLogger.info("addSysFlowExec,arrDeptId.length:"+arrDeptId.length);
                commonLogger.info("addSysFlowExec,arrDeptId:"+arrDeptId.toString());
                for (int i = 0; i < arrDeptId.length; i++) {
                    String currDeptId = arrDeptId[i].trim();

                    SysDepartments sysDepartments = (SysDepartments) new SysDepartmentsDAO().queryById(new SysDepartments(), currDeptId);
                    if (sysDepartments == null) {
                        commonLogger.info("addSysFlowExec,sysDepartments is null ,continue");
                        continue;
                    }

                    String chatId = sysDepartments.getElinkChatId();
                    if (StringUtils.isEmpty(chatId)) {
                        commonLogger.info("addSysFlowExec,chatId is empty ,continue");
                        continue;
                    }

                    //2025-09-09
                    //发送

                    if (!CommonFunc.isValidJson(chatId)) {
                        //不是JSON格式
                        commonLogger.info("addSysFlowExec,chatId is not json ,send msg");
                        kafkaClient.sendElinkMsg(msg, "", chatId);
                    }else{
                        //是JSON格式
                        JSONObject jsonElinkId=JSONObject.parseObject(chatId);
                        //判断JSON是否包括当前流程节点ID
                        String tmpNodeid="node_"+sysFlowNodeNext.getFlowNodeId();
                        commonLogger.info("addSysFlowExec,chatId :"+chatId);
                        if (jsonElinkId.containsKey(tmpNodeid)) {
                            if (StringUtils.isEmpty(jsonElinkId.getString(tmpNodeid))){
                                continue;
                            }
                            kafkaClient.sendElinkMsg(msg, "", jsonElinkId.getString(tmpNodeid));
                        }else{
                            if (StringUtils.isEmpty(jsonElinkId.getString("common"))){
                                continue;
                            }
                            if (jsonElinkId.containsKey("common")){
                                kafkaClient.sendElinkMsg(msg, "", jsonElinkId.getString("common"));
                            }
                        }
                    }
                }
            }else{
                //发送给相关的业务人员
                for (int i = 0; i < listElinkid.size(); i++) {
                    kafkaClient.sendElinkMsg(msg, listElinkid.get(i), "");
                }
            }
        }
        return usernames;
    }

    /*
    回退流程节点,不考虑不可回退情况
    传入:流程ID,flowId
    返回:
     */
    public static boolean returnFlow(SysFlow sysFlow,SysFlowExec sysFlowExec, SysFlowInstance sysFlowInstance,
                                     SysFlowNode sysFlowNode, SysFlowNode sysFlowNodeParent,
                                     boolean flagToFirst) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            String prevFlowNodeId = sysFlowExec.getPrevFlowNodeId();
            String prevFlowExecId = sysFlowExec.getPrevFlowExecId();
            String now = CommonFunc.unix2Date(CommonFunc.unixTimeStr());

            boolean result = false;
            SysFlowExec sysFlowExecPrev;
            SysFlowExecDAO sysFlowExecDAO = new SysFlowExecDAO();

            if (flagToFirst) {
                //回退到超始节点
                SysFlowNode flowNodeFirst = FlowService.getFirstNode(sysFlowExec.getFlowId());
                prevFlowNodeId = flowNodeFirst.getFlowNodeId();
            }
            String where = String.format("flow_node_id='%s' and flow_instance_id='%s' and exec_action='30'",
                    prevFlowNodeId, sysFlowInstance.getFlowInstanceId());
            sysFlowExecDAO.setWhere(where);
            sysFlowExecPrev = (SysFlowExec) sysFlowExecDAO.queryFirstRow(new SysFlowExec());


            if (sysFlowExecPrev == null) {
                return result;
            }


            //1.更新流程实例表:将flow_node_id设置为prevFlowNodeId字段
            String sendtoUser = (StringUtils.isEmpty(sysFlowExecPrev.getUsername())) ? "" : sysFlowExecPrev.getUsername() + ",";

            sysFlowInstance.setFlowNodeId(prevFlowNodeId);
            sysFlowInstance.setSendtoUser(sendtoUser);

            //设置回退标记
            sysFlowInstance.setFlagReturn("1");
            new SysFlowInstanceDAO().update(sysFlowInstance);

            //如果是会签节点，清除其余初始状态的信息
            if ("1".equals(sysFlowNode.getIsChildnode())) {
                String sql = String.format("delete from sys_flow_exec where flow_instance_id='%s' and exec_action='10' and flow_exec_id<>'%s'",
                        sysFlowInstance.getFlowInstanceId(), sysFlowExec.getFlowExecId());
                jdbcDao.execute(sql);
            }

            //2.新增流程执行信息:prev_node_id必须是上一节点的node_id值
            SysFlowExec sysFlowExecNew = new SysFlowExec();
            String flowExecIdNew = jdbcDao.getSeqNextVal();
            sysFlowExecNew.setFlowExecId(flowExecIdNew);
            sysFlowExecNew.setFlowId(sysFlowExec.getFlowId());
            sysFlowExecNew.setFlowNodeId(prevFlowNodeId);
            sysFlowExecNew.setFlowInstanceId(sysFlowInstance.getFlowInstanceId());
            sysFlowExecNew.setCreateTime(now);
            sysFlowExecNew.setExecAction(FlowService.FLOW_ACTION_START);
            sysFlowExecNew.setPrevFlowNodeId(sysFlowExecPrev.getPrevFlowNodeId());
            sysFlowExecNew.setPrevFlowExecId(sysFlowExecPrev.getPrevFlowExecId());
            sysFlowExecNew.setDepartmentid(sysFlowExecPrev.getDepartmentid());

            sysFlowExecNew.setSendtoUser(sendtoUser);
            sysFlowExecNew.setFlagReturn("1");
            result = new SysFlowExecDAO().saveNew(sysFlowExecNew);

            /////////////////
            //自动发送ELINK信息
            ///////////////////////
            //业务数据处理
            String sql = "select * from sys_flow_node where flow_node_id='" + prevFlowNodeId + "'";
            Map<String, String> prevFlowNode = jdbcDao.getFirstRow(sql);
            String msg = prevFlowNode.get("ELINK_RETURN");
            if (StringUtils.isNotEmpty(msg)) {
                String flowName = jdbcDao.getFieldValue("select flow_name from sys_flow where flow_id='" + sysFlowNode.getFlowId() + "'", "FLOW_NAME");
                msg = msg.replace("{login_dept}", Eval.getLoginDepartmentName());
                msg = msg.replace("{now}", CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
                msg = msg.replace("{flow_name}", flowName).replace("{node_name}", prevFlowNode.get("NODE_NAME"));
                msg = msg.replace("{sendto_user}", sendtoUser);

                //替换业务数据
                String table_name = jdbcDao.getFieldValue("select table_code from sys_table where table_id=(select table_id from " +
                        "sys_flow where flow_id='" + sysFlowExec.getFlowId() + "')", "TABLE_CODE");
                sql = "select * from " + table_name + " where id=" + sysFlowInstance.getDataId();
                Map<String, String> mapData = jdbcDao.getFirstRow(sql);
                for (Map.Entry<String, String> entry : mapData.entrySet()) {
                    String src = "{" + entry.getKey().trim() + "}";
                    msg = msg.replace(src.toLowerCase(Locale.ROOT), entry.getValue());
                }

                //ELINK发送参数
                KafkaClient kafkaClient = new KafkaClient("ZFOMS_流程类_"+sysFlow.getFlowName());

                //判定是发群还是个人
                if ("1".equals(prevFlowNode.get("IS_SELECTUSER"))) {
                    String Elink_id = jdbcDao.getFieldValue(
                            "select elink_id from sys_users where username='"
                                    + sendtoUser.replace(",", "") + "'", "ELINK_ID");
                    if (StringUtils.isNotEmpty(Elink_id)) {
                        kafkaClient.sendElinkMsg(msg, Elink_id, "");
                    }
                } else {
                    String Elink_id = jdbcDao.getFieldValue(
                            "select elink_chat_id from sys_departments where departmentid=" +
                                    "(select departmentid from sys_users where username='"
                                    + sendtoUser.replace(",", "") + "')", "ELINK_CHAT_ID");
                    if (StringUtils.isNotEmpty(Elink_id)) {

                        kafkaClient.sendElinkMsg(msg, "", Elink_id);
                    /*
                    if (!CommonFunc.isValidJson(Elink_id)) {
                        //不是JSON格式

                    }else{
                        //是JSON格式
                        JSONObject jsonElinkId=JSONObject.parseObject(Elink_id);
                        //判断是否有当前流程节点
                        kafkaClient.sendElinkMsg(msg, "", Elink_id);
                    }
                    */
                    }

                }

            }
            jdbcDao.close();
//        }
            ///////////////////////////////////////////////////////////////
            //自动发送ELINK信息
            //发送对象：自动化一班
            ///////////////////////////////////////////////////////////////
            //自动发送ELINK信息
            //发送对象：自动化一班
        /*
        String deptIdZdhyb=CommonFunc.getConfigByKey("sys.deptid_zdhyb").trim();
        String[] arrDeptId=sysFlowExecPrev.getDepartmentid().trim().split(",");
        if (CommonFunc.inArray(deptIdZdhyb,arrDeptId)){
            String flowName=jdbcDao.getFieldValue("select flow_name from sys_flow where flow_id='"+sysFlowExec.getFlowId()+"'","FLOW_NAME");
            String nodeName=jdbcDao.getFieldValue("select node_name from sys_flow_node where flow_node_id='"+prevFlowNodeId+"'","NODE_NAME");
            String msg=String.format("%s收到%s回退到【%s】的【%s】的业务信息，请及时处理。",
                    sysFlowExecNew.getCreateTime(),Eval.getLoginName(),flowName,nodeName);
            KafkaClient kafkaClient=new KafkaClient("ZFOMS_FLOW_"+sysFlow.getFlowName());
            kafkaClient.sendElinkMsg(msg,"",CommonFunc.getConfigByKey("sys.kafka_chatid_zdhyb"));
        }
        */


            return result;
        }
    }

    /*
    更新流程对应的执行信息的业务数据
    传入:流程ID,flowId
    返回:
     */
    public static Map<String, String> updateFlowData(HttpServletRequest req, SysConfig sysConfig, String dataId,
                                                     Map<String, String> mapVar, SysFlowNode sysFlowNode) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {
            String flowId = sysFlowNode.getFlowId();
            String groupId = sysFlowNode.getTableColumnGroupId();

            SysFlow sysFlow = (SysFlow) new SysFlowDAO().queryById(new SysFlow(), flowId);
            String tableId = sysFlow.getTableId();

            SysTable sysTable = (SysTable) new SysTableDAO().queryById(new SysTable(), tableId);
            String tableCode = sysTable.getTableCode();

            HashMap<String, Object> mapColumn = FlowService.getTableColumn(tableId);
            SysTableColumn sysTableColumnPK = (SysTableColumn) mapColumn.get("pkField");
            String pkField = sysTableColumnPK.getColumnCode();

            String[] arrGroupId = groupId.split(",");
            ArrayList<String> listUpdate = new ArrayList<String>();

            for (int x = 0; x < arrGroupId.length; x++) {
                String[] arrItemGroupId = arrGroupId[x].split(":");
                String itemGroupId = arrItemGroupId[0];
                ArrayList<SysTableColumn> listColumn = (ArrayList<SysTableColumn>) mapColumn.get(itemGroupId);

                if (listColumn!=null) {
                    for (int i = 0; i < listColumn.size(); i++) {
                        String columnCode = listColumn.get(i).getColumnCode();
                        String inputType = listColumn.get(i).getInputType();

                        if ("10".equals(inputType)) {
                            String value = FlowService.uploadFile(req, columnCode, sysConfig.getEfileSavepath(), tableCode, dataId);
                            if (StringUtils.isNotEmpty(value)) {
                                listUpdate.add(String.format("%s='%s'", columnCode, value));
                            }
                        } else if ("3".equals(inputType)) {
                            if (mapVar.containsKey(columnCode)) {
                                String value = mapVar.get(columnCode).trim().replace("T", " ");
                                listUpdate.add(String.format("%s='%s'", columnCode, value));
                            }
                        } else {
                            if (mapVar.containsKey(columnCode)) {
                                String value = mapVar.get(columnCode).trim();
                                listUpdate.add(String.format("%s='%s'", columnCode, value));
                            }
                        }
                    }
                }
            }

            String sql = String.format("update %s set %s where %s='%s'", tableCode, String.join(",", listUpdate), pkField, dataId);
            boolean result = jdbcDao.execute(sql);

            if (!result) {
                return null;
            }

            sql = String.format("select * from %s where %s='%s'", tableCode, pkField, dataId);
            Map<String, String> mapData = jdbcDao.getFirstRow(sql);

            jdbcDao.close();

            return mapData;
        }
    }

    /*
    根据流程ID寻找 流程的所有节点
    传入:流程ID,flowId
    dataId:主变ID
    返回:
     */
    public static ArrayList<Object> getAllFlowNode(String flowId, HashMap<String, String> mapData, HashMap<String, Object> mapListColumn,
                                                   String tableGroupId, String dataId, String flowCode) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            String userId = Eval.getLoginUserId();
            String departmentId = Eval.getLoginDepartmentId();
            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            //sysFlowNodeDAO.setWhere("flow_id='"+flowId+"' and nvl(is_childnode,'0')='0'");
            sysFlowNodeDAO.setWhere("flow_id='" + flowId + "'");//
            sysFlowNodeDAO.setOrder(" sortindex asc ");
            ArrayList<SysFlowNode> listNode = (ArrayList<SysFlowNode>) sysFlowNodeDAO.query(new SysFlowNode());

            ArrayList<Object> listFlowNode = new ArrayList<Object>();

            boolean grantFlag = false;
            boolean firstFlag = false;

            for (int i = 0; i < listNode.size(); i++) {
                String flowNodeId = listNode.get(i).getFlowNodeId();
                String groupId = listNode.get(i).getTableColumnGroupId();
                String grants = listNode.get(i).getGrants();
                String[] arrTableGroupId = groupId.split(",");

                HashMap<String, Object> mapFlowNode = new HashMap<String, Object>();

                if (groupId.equals(tableGroupId)) {
                    grantFlag = FlowService.checkFlowNodeGrant(userId, departmentId, grants);
                }

                ArrayList<ArrayList<FlowFieldInfo>> listGroupInfo = new ArrayList<ArrayList<FlowFieldInfo>>();
                ArrayList<ArrayList<FlowFieldInfo>> listGroupInfoLast = new ArrayList<ArrayList<FlowFieldInfo>>();
                ArrayList<String> listGroupName = new ArrayList<String>();
                ArrayList<String> listGroupRemark = new ArrayList<String>();

                for (int x = 0; x < arrTableGroupId.length; x++) {
                    String[] arrGroupIdTmp = arrTableGroupId[x].split(":", 2);
                    String tableColumnGroupId = arrGroupIdTmp[0];
                    SysTableColumnGroup sysTableColumnGroup = (SysTableColumnGroup) new SysTableColumnGroupDAO().queryById(
                            new SysTableColumnGroup(), tableColumnGroupId);
                    if (sysTableColumnGroup != null) {
                        listGroupName.add(sysTableColumnGroup.getGroupName());
                        listGroupRemark.add(sysTableColumnGroup.getRemark());
                        ArrayList<FlowFieldInfo> listFieldInfo = FlowService.getFlowFieldInfo(tableColumnGroupId, mapData, mapListColumn, grantFlag && (!firstFlag), flowCode);
                        if ((arrGroupIdTmp.length == 2) && ("last".equals(arrGroupIdTmp[1]))) {
                            //放子表后面
                            listGroupInfoLast.add(listFieldInfo);
                        } else {
                            //放子表前面
                            listGroupInfo.add(listFieldInfo);
                        }
                    }
                }

                //获取子表信息
                String tableSubinfo = listNode.get(i).getTableSubinfo().trim();
                LinkedHashMap<String, Object> mapSubTable = new LinkedHashMap<String, Object>();
                if (!StringUtils.isEmpty(tableSubinfo)) {
                    //有子表
                    String[] arrTableid = tableSubinfo.split(",");
                    for (int xx = 0; xx < arrTableid.length; xx++) {
                        SysTable sysTablesub = (SysTable) new SysTableDAO().queryById(new SysTable(), arrTableid[xx]);
                        if (sysTablesub == null) {
                            continue;
                        }

                        //获取子表字段信息
                        JSONObject jsonTableSub = new JSONObject();
                        SysTableColumnDAO sysTableColumnDAO = new SysTableColumnDAO();
                        sysTableColumnDAO.setWhere("ishide='0' and table_id='" + sysTablesub.getTableId() + "'");
                        sysTableColumnDAO.setOrder("sortindex");
                        ArrayList<SysTableColumn> listTableColSub = sysTableColumnDAO.query(new SysTableColumn());
                        //特殊信息处理
                        for (int xxx=0;xxx<listTableColSub.size();xxx++){
                            SysTableColumn sysTableColumnSub=listTableColSub.get(xxx);
                            String inputType=sysTableColumnSub.getInputType().trim();
                            if ("5".equals(inputType) || "25".equals(inputType)){
                                sysTableColumnSub.setExtraInfo(CommonFunc.parseMapList(sysTableColumnSub.getMaplist().trim()));
                            }else if ("41".equals(inputType)){
                                //层级多选
                                String tmp=sysTableColumnSub.getMaplist().trim();
                                if (JSONObject.isValidArray(tmp)) {
                                    sysTableColumnSub.setExtraInfo(JSONArray.parseArray(tmp));
                                }
                            }
                        }
                        jsonTableSub.put("sysTablesub", sysTablesub);
                        jsonTableSub.put("listTableColSub", listTableColSub);


                        //获取子表数据信息
                        String sqlSubData = String.format("select * from %s where refid='%s' order by id",
                                sysTablesub.getTableCode(), dataId);
                        ArrayList<HashMap<String, String>> listSubData = jdbcDao.getResult(sqlSubData);
                        JSONArray jsonSubData = new JSONArray();
                        if (listSubData.size() > 0) {
                            for (int kk = 0; kk < listSubData.size(); kk++) {
                                HashMap<String, String> mapSubItem = listSubData.get(kk);
                                JSONArray jsonArrItem = new JSONArray();
                                if (listTableColSub != null) {
                                    for (int mm = 0; mm < listTableColSub.size(); mm++) {
                                        String tmpValue = mapSubItem.get(listTableColSub.get(mm).getColumnCode().trim());
                                        if ((!grantFlag) && "41".equals(listTableColSub.get(mm).getInputType())){
                                            tmpValue=CommonFunc.getDisplayValueOf41(tmpValue,listTableColSub.get(mm).getMaplist());
                                        }
                                        jsonArrItem.add(tmpValue);
                                    }
                                }
                                jsonSubData.add(jsonArrItem);
                            }
                        } else {
                            JSONArray jsonArrItem = new JSONArray();
                            if (listTableColSub != null) {
                                for (int mm = 0; mm < listTableColSub.size(); mm++) {
                                    //2026-08-14:此处让子表添加默认值功能
                                    if (StringUtils.isNotEmpty(listTableColSub.get(mm).getDefaultvalue())){
                                        jsonArrItem.add(new Eval().eval(listTableColSub.get(mm).getDefaultvalue()).toString());
                                    }else {
                                        jsonArrItem.add("");
                                    }
                                }
                            }
                            jsonSubData.add(jsonArrItem);
                        }
                        jsonTableSub.put("tabledata", jsonSubData);

                        mapSubTable.put(arrTableid[xx], jsonTableSub);
                    }
                }

                mapFlowNode.put("flowNode", listNode.get(i));
                mapFlowNode.put("listGroupInfo", listGroupInfo);
                mapFlowNode.put("listGroupInfoLast", listGroupInfoLast);
                mapFlowNode.put("listGroupName", listGroupName);
                mapFlowNode.put("listGroupRemark", listGroupRemark);
                mapFlowNode.put("mapSubTable", mapSubTable);
                mapFlowNode.put("childNode", FlowService.getChildFlowNode(flowId, flowNodeId));
                mapFlowNode.put("grantFlag", grantFlag && (!firstFlag) ? "1" : "0");


                if (grantFlag) {
                    firstFlag = true;
                }

                listFlowNode.add(mapFlowNode);
            }

            jdbcDao.close();

            return listFlowNode;
        }
    }

    public static ArrayList<FlowFieldInfo> getFlowFieldInfo(String groupId, HashMap<String, String> mapData,
                                                            HashMap<String, Object> mapListColumn, boolean grantFlag, String flowCode) throws Exception {
        ArrayList<FlowFieldInfo> listField = new ArrayList<FlowFieldInfo>();

        ArrayList<SysTableColumn> listColumn = (ArrayList<SysTableColumn>) mapListColumn.get(groupId);

        SysTableDAO sysTableDAO = new SysTableDAO();
        ArrayList<SysTable> sysTables = sysTableDAO.query(new SysTable());
        Map<String, String> tableIdCodeMap = sysTables.stream().collect(Collectors.toMap(SysTable::getTableId, SysTable::getTableCode));
        if (listColumn != null) {
            for (int i = 0; i < listColumn.size(); i++) {
                FlowFieldInfo flowFieldInfo = new FlowFieldInfo();

                SysTableColumn sysTableColumn = listColumn.get(i);
                String fieldName = sysTableColumn.getColumnName();
                String fieldCode = sysTableColumn.getColumnCode();
                String value = mapData.get(fieldCode);
                String inputType = sysTableColumn.getInputType();
                String tableName = tableIdCodeMap.get(sysTableColumn.getTableId());

                flowFieldInfo.setGroupId(groupId);
                flowFieldInfo.setFieldCode(fieldCode);
                flowFieldInfo.setFieldName(fieldName);
                flowFieldInfo.setTableName(tableName);

                //有修改权限获取默认值
                if (grantFlag) {
                    if (StringUtils.isEmpty(value) && StringUtils.isNotEmpty(sysTableColumn.getDefaultvalue())) {
                        value = new Eval().eval(sysTableColumn.getDefaultvalue()).toString();
                    }
                }

                if ("10".equals(inputType)) {
                    JSONArray jsonArr = null;
                    try {
                        jsonArr = JSONObject.parseArray(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (jsonArr == null) {
                        jsonArr = new JSONArray();
                    }
                    flowFieldInfo.setValue(jsonArr);
                    flowFieldInfo.setValueShow(jsonArr);
                } else if ("5".equals(inputType)) {
                    flowFieldInfo.setValue(value);
                    flowFieldInfo.setValueShow(value);

                    String mapStr = sysTableColumn.getMaplist();
                    String[] arrTmp = mapStr.split(";");
                    for (int xx = 0; xx < arrTmp.length; xx++) {
                        String[] arrItem = arrTmp[xx].split("=>", 2);
                        if (arrItem.length == 2) {
                            if (arrItem[0].equals(value)) {
                                flowFieldInfo.setValueShow(arrItem[1]);
                                break;
                            }
                        }
                    }

                } else if ("3".equals(inputType)) {
                    if (grantFlag || ("XLZHZJXD".equals(flowCode))) {
                        //编辑时，加上T（特殊业务如线路重合闸 需加T）
                        //flowFieldInfo.setValue(value.replace(" ","T"));
                        //flowFieldInfo.setValueShow(value.replace(" ","T"));

                        flowFieldInfo.setValue(value);
                        flowFieldInfo.setValueShow(value);

                    } else {

                        flowFieldInfo.setValue(value);
                        flowFieldInfo.setValueShow(value);
                    }
                } else if ((!grantFlag) && "41".equals(inputType)) {
                    flowFieldInfo.setValue(value);
                    flowFieldInfo.setValueShow(CommonFunc.getDisplayValueOf41(value,sysTableColumn.getMaplist()));
                }else {
                    flowFieldInfo.setValue(value);
                    flowFieldInfo.setValueShow(value);
                }
                flowFieldInfo.setInputType(sysTableColumn.getInputType());
                flowFieldInfo.setShowType("1");
                flowFieldInfo.setIsHide(sysTableColumn.getIshide());
                flowFieldInfo.setNullable(sysTableColumn.getNullable());
                flowFieldInfo.setColumnLength(sysTableColumn.getColumnLength());
                flowFieldInfo.setCss(sysTableColumn.getColspan());


                if ("5".equals(sysTableColumn.getInputType()) ||
                        "25".equals(sysTableColumn.getInputType())) {
                    LinkedHashMap<String, String> mapTmp = CommonFunc.parseMapList(sysTableColumn.getMaplist().trim());
                    flowFieldInfo.setDatamap(mapTmp);
                }else if ("41".equals(sysTableColumn.getInputType())){
                    //多选
                    String tmp=sysTableColumn.getMaplist().trim();
                    if (JSONObject.isValidArray(tmp)) {
                        flowFieldInfo.setJsonData(JSONArray.parseArray(tmp));
                    }
                }

                listField.add(flowFieldInfo);
            }
        }

        return listField;
    }

    /*
    根据流程ID寻找 流程的子节点
    传入:流程ID,flowId
    返回:
     */
    public static ArrayList<ArrayList<SysFlowNode>> getChildFlowNode(String flowId, String parentNodeId) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            sysFlowNodeDAO.setWhere("FLOW_ID='" + flowId + "' and nvl(is_childnode,'0')='1' and PARENT_NODE_ID='" + parentNodeId + "'");
            sysFlowNodeDAO.setOrder(" CHILD_SORTINDEX asc,sortindex asc ");
            ArrayList<SysFlowNode> listNode = (ArrayList<SysFlowNode>) sysFlowNodeDAO.query(new SysFlowNode());

            jdbcDao.close();

            if (listNode == null) {
                return null;
            }

            ArrayList<ArrayList<SysFlowNode>> listFlowNode = new ArrayList<ArrayList<SysFlowNode>>();
            LinkedHashMap<String, Object> mapTmp = new LinkedHashMap<String, Object>();

            for (int i = 0; i < listNode.size(); i++) {
                String flowNodeId = listNode.get(i).getFlowNodeId();
                String childSortindex = String.valueOf(listNode.get(i).getChildSortindex());

                if (!mapTmp.containsKey(childSortindex)) {
                    ArrayList<SysFlowNode> listTmp = new ArrayList<SysFlowNode>();
                    listTmp.add(listNode.get(i));
                } else {
                    ((ArrayList<SysFlowNode>) mapTmp.get(childSortindex)).add(listNode.get(i));
                }
            }

            for (String key : mapTmp.keySet()) {
                listFlowNode.add((ArrayList<SysFlowNode>) mapTmp.get(key));
            }

            return listFlowNode;
        }
    }


    /*
    根据实例ID查找某个流程实例最后的一个未执行的执行任务
    传入:流程ID,flowId
    返回:
     */
    public static SysFlowExec getLastSysFlowExec(String flowInstanceId) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            SysFlowExecDAO sysFlowExecDAO = new SysFlowExecDAO();
            sysFlowExecDAO.setWhere(String.format("(FLOW_INSTANCE_ID='%s') and (EXEC_ACTION='%s')",
                    flowInstanceId, FLOW_ACTION_START));
            sysFlowExecDAO.setOrder(" CREATE_TIME asc ");
            SysFlowExec sysFlowExec = (SysFlowExec) sysFlowExecDAO.queryFirstRow(new SysFlowExec());

            jdbcDao.close();

            return sysFlowExec;
        }
    }

    /*
    根据用户ID查找某个流程对应的节点
    传入:流程ID,flowId
    返回:
     */
    public static SysFlowExec getSysFlowExec(String userName, String departmentId, String flowInstanceId) throws Exception {
        if (StringUtils.isEmpty(userName)) {
            return null;
        }

        try(JdbcDao jdbcDao = new JdbcDao()) {

            SysFlowExecDAO sysFlowExecDAO = new SysFlowExecDAO();
            sysFlowExecDAO.setWhere(String.format("(FLOW_INSTANCE_ID='%s') and (EXEC_ACTION='%s') and" +
                            " ((sendto_user like '%%%s,%%') or (nvl(sendto_user,'')='' and (DEPARTMENTID='%s' or departmentid like '%s,%%' or departmentid like '%%,%s' or departmentid like '%%,%s,%%')))",
                    flowInstanceId, FLOW_ACTION_START, userName, departmentId, departmentId, departmentId, departmentId));
            sysFlowExecDAO.setOrder(" CREATE_TIME asc ");
            SysFlowExec sysFlowExec = (SysFlowExec) sysFlowExecDAO.queryFirstRow(new SysFlowExec());

            jdbcDao.close();


            return sysFlowExec;
        }
    }

    /*
    根据表ID寻找 表的所有字段,并寻找出主键字段
    传入:流程ID,flowId
    返回:
     */
    public static HashMap<String, Object> getTableColumn(String tableId) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            SysTableColumnDAO sysTableColumnDAO = new SysTableColumnDAO();
            sysTableColumnDAO.setWhere("table_id='" + tableId + "'");
            sysTableColumnDAO.setOrder(" sortindex asc ");
            ArrayList<SysTableColumn> listColumn = (ArrayList<SysTableColumn>) sysTableColumnDAO.query(new SysTableColumn());

            jdbcDao.close();

            HashMap<String, Object> mapListColumn = new HashMap<String, Object>();
            for (int i = 0; i < listColumn.size(); i++) {
                String groupId = listColumn.get(i).getTableColumnGroupId();
                String isPk = listColumn.get(i).getIspk();
                if ("1".equals(isPk) && !mapListColumn.containsKey("pkField")) {
                    mapListColumn.put("pkField", listColumn.get(i));
                }

                if (mapListColumn.containsKey(groupId)) {
                    ((ArrayList<SysTableColumn>) mapListColumn.get(groupId)).add(listColumn.get(i));
                } else {
                    ArrayList<SysTableColumn> listTmp = new ArrayList<SysTableColumn>();
                    listTmp.add(listColumn.get(i));
                    mapListColumn.put(groupId, listTmp);
                }
            }
            return mapListColumn;
        }
    }

    /*
    根据表ID寻找 表的所有分组
    传入:流程ID,flowId
    返回:
     */
    public static ArrayList<SysTableColumnGroup> getTableGroup(String tableId) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            SysTableColumnGroupDAO sysTableColumnGroupDAO = new SysTableColumnGroupDAO();
            sysTableColumnGroupDAO.setWhere("table_id='" + tableId + "'");
            sysTableColumnGroupDAO.setOrder(" sortindex asc ");
            ArrayList<SysTableColumnGroup> listGroup = (ArrayList<SysTableColumnGroup>) sysTableColumnGroupDAO.query(new SysTableColumnGroup());

            jdbcDao.close();

            return listGroup;
        }
    }

    /*
    根据流程ID寻找 流程的第一个节点
    传入:流程ID,flowId
    返回:
     */
    public static SysFlowNode getFirstNode(String flowId) throws Exception {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            sysFlowNodeDAO.setWhere("flow_id='" + flowId + "'");
            sysFlowNodeDAO.setOrder(" sortindex asc ");
            SysFlowNode sysFlowNode = (SysFlowNode) sysFlowNodeDAO.queryFirstRow(new SysFlowNode());

            jdbcDao.close();

            return sysFlowNode;
        }
    }

    /*
    根据用户ID和部门ID,判断是否有某个流程节点的权限
    传入:流程ID,flowId
    返回:
     */
    public static boolean checkFlowNodeGrant(String userId, String departmentId, String grants) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(departmentId)) {
            return false;
        }

        JSONObject json = JSON.parseObject(grants);

        if (json == null) {
            return false;
        }

        if (json.containsKey("userId")) {
            String userId1 = json.getString("userId");
            String[] arrUserId = userId1.split(",");
            if (CommonFunc.inArray(userId, arrUserId)) {
                return true;
            }
        }

        if (json.containsKey("departmentId")) {
            String departmentId1 = json.getString("departmentId");
            String[] arrDepartmentId = departmentId1.split(",");
            if (CommonFunc.inArray(departmentId, arrDepartmentId)) {
                return true;
            }
        }

        return false;
    }

    /*
    启动一个新流程服务
    传入:流程ID,flowId
    返回:
     */
    public void startFlow(String flowId) {
        try(JdbcDao jdbcDao = new JdbcDao()) {

            SysFlowDAO sysFlowDAO = new SysFlowDAO();

            jdbcDao.close();
        }
    }

    /*
    获取某个节点的所有用户
    传入:流程ID,flowId
    返回:
     */
    public static ArrayList<UserInfoShort> getFlowNodeUsersItem(SysFlowNode sysFlowNode) {
        String grants = sysFlowNode.getGrants();
        JSONObject json = JSONObject.parseObject(grants);
        ArrayList<UserInfoShort> listUserInfoShort = new ArrayList<UserInfoShort>();
        String sql ="";
        if (json.containsKey("userId") && StringUtils.isNotEmpty(json.getString("userId").trim())){
            String userId = "'"+json.getString("userId").replaceAll(",","','")+"'";
            sql = "select a.userid,a.username,a.departmentId,b.departmentname_full departmentname  " +
                    "from sys_users a left join sys_departments b on a.departmentId=b.departmentId " +
                    " where a.userid in (" + userId + ")";
        } else if (json.containsKey("departmentId")) {
            String deptId = json.getString("departmentId");
            sql = "select a.userid,a.username,a.departmentId,b.departmentname_full departmentname  " +
                    "from sys_users a left join sys_departments b on a.departmentId=b.departmentId " +
                    " where a.departmentId in (" + deptId + ")";
        }else{
            return listUserInfoShort;
        }

        try(JdbcDao jdbcDao = new JdbcDao()) {
            ArrayList<HashMap<String, String>> listUser = jdbcDao.getResult(sql);
            for (int i = 0; i < listUser.size(); i++) {
                UserInfoShort userInfoShort = new UserInfoShort();
                userInfoShort.setUserId(listUser.get(i).get("USERID").trim());
                userInfoShort.setUserName(listUser.get(i).get("USERNAME").trim());
                userInfoShort.setDeptId(listUser.get(i).get("DEPARTMENTID").trim());
                userInfoShort.setDeptName(listUser.get(i).get("DEPARTMENTNAME").trim());
                listUserInfoShort.add(userInfoShort);
            }
            jdbcDao.close();
        }

        return listUserInfoShort;
    }


    /*
    getFlowNodeUsers:获取一个节点的所有有权限的用户信息，如果节点是会签，则获取会签下面的所有
     */
    public static ArrayList<FlowNodeUsers> getFlowNodeUsers(SysFlowNode sysFlowNode) throws Exception {
        ArrayList<FlowNodeUsers> listFlowNodeUsers = new ArrayList<FlowNodeUsers>();
        if (sysFlowNode.getNodeType().equals(FlowService.NODETYPE_MORE)) {
            //
            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            sysFlowNodeDAO.setWhere("IS_CHILDNODE='1' and parent_node_id='" + sysFlowNode.getFlowNodeId() + "'");
            sysFlowNodeDAO.setOrder("sortindex asc");
            ArrayList<SysFlowNode> listNode = sysFlowNodeDAO.query(new SysFlowNode());
            for (int i = 0; i < listNode.size(); i++) {
                FlowNodeUsers flowNodeUsers = new FlowNodeUsers();
                flowNodeUsers.setNodeName(listNode.get(i).getNodeName());
                flowNodeUsers.setNodeType(listNode.get(i).getNodeType());
                flowNodeUsers.setFlowNodeId(listNode.get(i).getFlowNodeId());
                flowNodeUsers.setListUser(FlowService.getFlowNodeUsersItem(listNode.get(i)));
                listFlowNodeUsers.add(flowNodeUsers);
            }
        } else {
            FlowNodeUsers flowNodeUsers = new FlowNodeUsers();
            flowNodeUsers.setNodeName(sysFlowNode.getNodeName());
            flowNodeUsers.setNodeType(sysFlowNode.getNodeType());
            flowNodeUsers.setFlowNodeId(sysFlowNode.getFlowNodeId());
            flowNodeUsers.setListUser(FlowService.getFlowNodeUsersItem(sysFlowNode));
            listFlowNodeUsers.add(flowNodeUsers);
        }

        return listFlowNodeUsers;
    }

    /*
    getFlowNodeUsers:获取多个节点的所有有权限的用户信息，如果节点是会签，则获取会签下面的所有
     */
    public static ArrayList<FlowNodeUsers> getFlowNodeUsers(String nodeIds) throws Exception {
        String[] arrIds = nodeIds.trim().split(",");

        ArrayList<FlowNodeUsers> listFlowNodeUsers = new ArrayList<FlowNodeUsers>();

        for (int i = 0; i < arrIds.length; i++) {
            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            SysFlowNode sysFlowNode = (SysFlowNode) sysFlowNodeDAO.queryById(new SysFlowNode(), arrIds[i].trim());
            if (sysFlowNode == null) {
                continue;
            }

            FlowNodeUsers flowNodeUsers = new FlowNodeUsers();
            flowNodeUsers.setNodeName(sysFlowNode.getNodeName());
            flowNodeUsers.setNodeType(sysFlowNode.getNodeType());
            flowNodeUsers.setFlowNodeId(sysFlowNode.getFlowNodeId());
            flowNodeUsers.setListUser(FlowService.getFlowNodeUsersItem(sysFlowNode));
            listFlowNodeUsers.add(flowNodeUsers);
        }

        return listFlowNodeUsers;
    }

    /*
    getFlowExecAuditContent:获取执行节点的所有有权限的用户信息，如果节点是会签，则获取会签下面的所有
     */
    public static String getFlowExecAuditContent(
            String groupId, Map<String, String> mapData
    ) throws Exception {
        String[] arrTmp=groupId.trim().split(":");
        String tmpGroupId=arrTmp[0].trim();
        String result="";

        if (StringUtils.isEmpty(tmpGroupId)){
            return result;
        }

        String sql="select column_name,column_code,input_type from sys_table_column where table_column_group_id in ("
                +tmpGroupId+") and flag_node_exec='1'";

        try(JdbcDao jdbcDao=new JdbcDao()) {
            ArrayList<HashMap<String, String>> listColumn = jdbcDao.getResult(sql);

            JSONObject json = new JSONObject();
            for (int i = 0; i < listColumn.size(); i++) {
                String columnCode = listColumn.get(i).get("COLUMN_CODE").trim();
                if (!mapData.containsKey(columnCode)) {
                    continue;
                }
                String value = mapData.get(columnCode).trim();

                if (listColumn.get(i).get("INPUT_TYPE").trim().equals("10")) {
                    JSONArray jsonArrFile = JSONArray.parseArray(value);
                    if (jsonArrFile != null) {
                        String sFile = "";
                        for (int j = 0; j < jsonArrFile.size(); j++) {
                            JSONObject jsonFile = jsonArrFile.getJSONObject(j);
                            if (jsonFile == null) {
                                continue;
                            }

                            String sItem = String.format("<a target=_blank href='%s/Flow/AttaDownload?atta_id=%s'>%s</a>&nbsp;&nbsp;&nbsp;&nbsp;",
                                    CommonFunc.getConfigByKey("server.servlet.context-path"),
                                    jsonFile.getString("id"), jsonFile.getString("name"));
                            sFile = sFile + sItem;
                        }
                        result = result + listColumn.get(i).get("COLUMN_NAME").trim() + "：" + sFile + "<br>";
                    }
                } else {
                    result = result + listColumn.get(i).get("COLUMN_NAME").trim() + "：" + value + "<br>";
                }
            }

            jdbcDao.close();
        }

        return result;
    }

    /**
     * @Author fuxiaodong
     * @Description //获取流程实例的通知摘要信息
     * @Version: v1.8
     * @Date 9:22 2026/6/17
     * @Param [notifyTemplate, tableName, pkField, pkValue]
     * @return java.lang.String
     */
    public static String getFlowInstanceNotifyContent(
            String notifyTemplate,String tableName,String pkField,String pkValue
    ){

        try(JdbcDao jdbcDao=new JdbcDao()){
            Set<String> setField=extractUniqueHashTags(notifyTemplate);

            if (setField.isEmpty()){
                return notifyTemplate;
            }

            String sql=String.format("select %s from %s where %s='%s'",
                    String.join(",",setField),tableName,pkField,pkValue);
            Map<String, String> mapData=jdbcDao.getFirstRow(sql);

            String notifyConent=notifyTemplate;
            for (String fieldName:setField) {
                notifyTemplate=notifyTemplate.replaceAll("#"+fieldName+"#",mapData.get(fieldName));
            }

            jdbcDao.close();

            return notifyTemplate;
        }
    }

    /**
     * 提取文本中所有符合 #...# 格式的字符串（前后都有 #，中间不包含 #），并去重。
     *
     * @param text 输入文本
     * @return 去重后的子串集合
     */
    public static Set<String> extractUniqueHashTags(String text) {
        Set<String> result = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return result;
        }

        // 正则：以 # 开头，后面跟至少一个非 # 字符，再以 # 结尾
        Pattern pattern = Pattern.compile("#([^#]+)#");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            // 返回整个匹配的子串（包含两端的 #）
            String matched = matcher.group();
            result.add(matched.substring(1,matched.length()-1));
        }

        return result;
    }

    /**
     * @Author fuxiaodong
     * @Description //用于检查一个流程执行节点是否可以主动撤回
     * 逻辑：
     * 1.prev_flow_exec_id不为空（不是第一个节点）
     * 2.当前节点不是会签并行节点？（is_childnode=1)
     * 3.当前节点的prev_flow_exec_id节点是不是会签节点？（is_childnode=1)
     * 4.prev_flow_exec_id节点的处理人是不是当前登录用户？(username)
     * @Version: v1.8
     * @Date 14:30 2026/6/18
     * @Param [flowNodeId, prevFlowNodeId]
     * @return boolean
     */
    public static boolean checkFlowExecCanUndo(String status,
                                               String flowNodeId,String prevFlowExecId){

        //0.判断流程实例状态，只有在执行中的流程才可以主动撤回
        if (!FlowService.FLOW_STATUS_EXECUTING.equals(status)){
            return false;
        }

        //1.prev_flow_exec_id不为空（不是第一个节点）
        if (StringUtils.isEmpty(prevFlowExecId)){
            return false;
        }

        try(JdbcDao jdbcDao=new JdbcDao()){
            //2.当前节点不是会签并行节点？（is_childnode=1)
            Map<String,String> mapFlowNode=jdbcDao.getFirstRow(
                    "select * from sys_flow_node where flow_node_id='"+flowNodeId+"'");
            if (MapUtils.isEmpty(mapFlowNode)){
                return false;
            }
            if ("1".equals(mapFlowNode.get("IS_CHILDNODE"))){
                return false;
            }

            //3.当前节点的prev_flow_exec_id节点是不是会签节点？（is_childnode=1)
            String sqlPrev="select a.username, b.* from sys_flow_exec a left join sys_flow_node b " +
                    "on a.FLOW_NODE_ID=B.FLOW_NODE_ID where a.FLOW_EXEC_ID='"+
                    prevFlowExecId+"'";
            Map<String,String> mapPrevFlowNode=jdbcDao.getFirstRow(sqlPrev);
            if (MapUtils.isEmpty(mapPrevFlowNode)){
                return false;
            }
            if ("1".equals(mapPrevFlowNode.get("IS_CHILDNODE"))){
                return false;
            }

            //4.prev_flow_exec_id节点的处理人是不是当前登录用户？(username)
            if (!Eval.getLoginName().equals(mapPrevFlowNode.get("USERNAME"))){
                return false;
            }

            jdbcDao.close();

            return true;
        }
    }

    public static void main(String[] args) {
        String s=getFlowInstanceNotifyContent(
                "发起的【调度编号图】(#F_CZM##F_TDMC#) 进入【编制】环节,请处理",
                "w_DDBHGL","ID","10000004692870");

        System.out.println(s);
    }
}
