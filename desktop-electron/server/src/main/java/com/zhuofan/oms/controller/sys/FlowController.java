package com.zhuofan.oms.controller.sys;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.common.ServerKeyIv;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.flow.*;
import com.zhuofan.core.orm.dao.*;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.util.RequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/*
 */
@RestController
@CrossOrigin
public class FlowController  extends BaseController{

    private String userId;
    private String userName;
    private String departmentId;

    private void getUserId()  throws Exception{
        userId=Eval.getLoginUserId();
        userName=Eval.getLoginName();
        departmentId=Eval.getLoginDepartmentId();
    }

    /**
     * @Author fuxiaodong
     * @Description //主动撤回功能：
     * @Version: v1.8
     * @Date 11:29 2026/6/23
     * @Param [params, req]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping(value = "/Flow/Undo",method = RequestMethod.GET)
    public Map<String, Object> Undo(@RequestParam Map<String,Object> params, HttpServletRequest req) throws Exception {
        String flowInstanceId=CommonFunc.getNotNullString(params.get("flow_instance_id"));
        String flowExecId=CommonFunc.getNotNullString(params.get("flow_exec_id"));

        SysFlowInstance sysFlowInstance=(SysFlowInstance) new SysFlowInstanceDAO().queryById(new SysFlowInstance(),flowInstanceId);
        if (sysFlowInstance==null){
            return CommonFunc.makeErrResult("400001");
        }

        SysFlowExec sysFlowExec=(SysFlowExec) new SysFlowExecDAO().queryById(new SysFlowExec(),flowExecId);
        if (sysFlowExec==null){
            return CommonFunc.makeErrResult("400001");
        }

        //1.检查是否有权限
        boolean grantFlag=FlowService.checkFlowExecCanUndo(sysFlowInstance.getStatus(),
                sysFlowExec.getFlowNodeId(),sysFlowExec.getPrevFlowExecId());

        if (!grantFlag){
            return CommonFunc.makeErrResult("600003");
        }

        //2.主动撤回操作
        //2.1获取上一个sys_flow_exec_prev
        SysFlowExec sysFlowExecPrev=(SysFlowExec) new SysFlowExecDAO().queryById(
                new SysFlowExec(),sysFlowExec.getPrevFlowExecId());
        if (sysFlowExecPrev==null){
            return CommonFunc.makeErrResult("400001");
        }

        //2.2将sys_flow_exec结束
        sysFlowExec.setExecAction(FlowService.FLOW_ACTION_END);
        sysFlowExec.setUserid(Eval.getLoginUserId());
        sysFlowExec.setUsername(Eval.getLoginName());
        sysFlowExec.setExecTime(CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
        sysFlowExec.setAuditContent(sysFlowExec.getUsername()+"主动撤回");
        new SysFlowExecDAO().update(sysFlowExec);

        //2.3复制一份sys_flow_exec_prev到新的sys_flow_exec
        try(JdbcDao jdbcDao=new JdbcDao()) {
            SysFlowExec sysFlowExecNew = new SysFlowExec();
            String flowExecIdNew =jdbcDao.getSeqNextVal();

            sysFlowExecNew.setFlowExecId(flowExecIdNew);
            sysFlowExecNew.setFlowId(sysFlowInstance.getFlowId());
            sysFlowExecNew.setFlowNodeId(sysFlowExecPrev.getFlowNodeId());
            sysFlowExecNew.setFlowInstanceId(flowInstanceId);
            sysFlowExecNew.setCreateTime(CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
            sysFlowExecNew.setExecAction(FlowService.FLOW_ACTION_START);
            sysFlowExecNew.setPrevFlowNodeId(sysFlowExecPrev.getFlowNodeId());
            sysFlowExecNew.setPrevFlowExecId(flowExecId);
            sysFlowExecNew.setDepartmentid(Eval.getLoginDepartmentId());
            sysFlowExecNew.setFlagReturn("0");
            sysFlowExecNew.setSendtoUser(sysFlowExecPrev.getSendtoUser());

            boolean result = new SysFlowExecDAO().saveNew(sysFlowExecNew);

            //2.4将sys_flow_instance的流程节点信息设置sys_flow_exec_prev相关
            //修改：
            sysFlowInstance.setFlowNodeId(sysFlowExecPrev.getFlowNodeId());
            sysFlowInstance.setSendtoUser(sysFlowExecPrev.getSendtoUser());
            new SysFlowInstanceDAO().update(sysFlowInstance);
            
            jdbcDao.close();
        }




        return null;
    }

    /*
     * DeleteAttaFile:删除附件
     * 参数:flow_instance_id
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Flow/DeleteAttaFile",method = RequestMethod.GET)
    public Map<String, Object> DeleteAttaFile(@RequestParam Map<String,Object> params, HttpServletRequest req) throws Exception {
        String attaId=CommonFunc.getNotNullString(params.get("atta_id"));
        String flowInstanceId=CommonFunc.getNotNullString(params.get("flow_instance_id"));
        String tablename=CommonFunc.getNotNullString(params.get("tablename"));
        String fieldname=CommonFunc.getNotNullString(params.get("fieldname"));

        SysFlowInstance sysFlowInstance=(SysFlowInstance) new SysFlowInstanceDAO().queryById(new SysFlowInstance(),flowInstanceId);
        if (sysFlowInstance==null){
            return CommonFunc.makeErrResult("400001");
        }

        try(JdbcDao jdbcDao=new JdbcDao()) {
            String dataId = sysFlowInstance.getDataId();
            String sql = String.format("select %s from %s where id='%s'", fieldname, tablename, dataId);

            String fieldValue = jdbcDao.getFieldValue(sql, fieldname.toUpperCase(Locale.ROOT));
            JSONArray json = JSONArray.parseArray(fieldValue);

            if (json == null) {
                return CommonFunc.makeErrResult("999999");
            }

            for (int i = json.size() - 1; i >= 0; i--) {
                JSONObject jsonItem = json.getJSONObject(i);
                if (jsonItem == null) {
                    continue;
                }
                json.remove(i);
            }

            fieldValue = json.toJSONString();
            sql = String.format("update %s set %s='%s' where id='%s'", tablename, fieldname, fieldValue, dataId);
            if (!jdbcDao.execute(sql)) {
                return CommonFunc.makeErrResult("600099");
            }

            jdbcDao.close();
        }

        return CommonFunc.makeSussResult(new HashMap<String,Object>());
    }

    /*
     * AttaDownload:下载附件
     * 参数:flow_instance_id
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Flow/AttaDownload",method = RequestMethod.GET)
    public Map<String, Object> AttaDownload(@RequestParam Map<String,Object> params, HttpServletRequest req) throws Exception{
        HashMap<String,String> mapExtFilename=new HashMap<String,String>(){{
            put("W_ZDHCZDZZDGLLC","F_ZFBXH");
        }};

        String attaId=CommonFunc.getNotNullString(params.get("atta_id"));

        if (StringUtils.isEmpty(attaId)){
            return CommonFunc.makeErrResult("400001");
        }

        //获取附件资料
        SysAttachment sysAttachment=(SysAttachment) new SysAttachmentDAO().queryById(new SysAttachment(),attaId);
        if (sysAttachment==null){
            return CommonFunc.makeErrResult("500003");
        }

        String fileName=sysAttachment.getContent();
        File file=new File(fileName);
        if (!file.exists()){
            return CommonFunc.makeErrResult("700001");
        }

        //开始下载
        try {
            // 获取文件名
            String filename = file.getName();
            String filenameSrc=sysAttachment.getAttachmentName();
            if (StringUtils.isEmpty(filenameSrc)){
                filenameSrc=filename;
            }
            String uploadTime=sysAttachment.getUploadTime().trim();
            if (uploadTime.length()==10){
                uploadTime=CommonFunc.unix2Date(uploadTime,"yyyy-mm-dd hh:mm");
            }
            uploadTime=uploadTime.replace(":","").replace(" ","").replace("-","").trim();

            //获取特殊字段
            String srcType=sysAttachment.getAttaSrctype().trim();
            if (mapExtFilename.containsKey(srcType)){
                try(JdbcDao jdbcDao=new JdbcDao()){
                    String sql=String.format("select %s from %s where id='%s'",mapExtFilename.get(srcType),srcType,sysAttachment.getAttaSrcid().trim());
                    String extValue=jdbcDao.getFieldValue(sql,mapExtFilename.get(srcType));
                    if (StringUtils.isNotEmpty(extValue)){
                        filenameSrc=extValue+"-"+filenameSrc;
                    }
                    jdbcDao.close();
                }
            }

            if (StringUtils.isNotEmpty(uploadTime)) {
                filenameSrc = uploadTime + "-"+filenameSrc;
            }


            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filenameSrc, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * DeleteDraft:清除草稿
     * 参数:flow_instance_id
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Flow/DeleteDraft",method = RequestMethod.GET)
    public Map<String, Object> DeleteDraft(@RequestParam Map<String,Object> params, HttpServletRequest req) throws Exception {
        getUserId();


        //获取流程信息
        String flowInstanceId=CommonFunc.getNotNullString(params.get("flow_instance_id"));
        SysFlowInstance sysFlowInstance=(SysFlowInstance) new SysFlowInstanceDAO().queryById(new SysFlowInstance(),flowInstanceId);

        if (sysFlowInstance==null){
            return CommonFunc.makeSussResult(null);
        }

        if (!sysFlowInstance.getStatus().equals(FlowService.FLOW_STATUS_DRAFT)){
            return CommonFunc.makeSussResult(null);
        }


        try(JdbcDao jdbcDao=new JdbcDao()) {
            //删除数据表
            String dataId = sysFlowInstance.getDataId();
            SysFlow sysFlow = (SysFlow) new SysFlowDAO().queryById(new SysFlow(), sysFlowInstance.getFlowId());
            if (sysFlow != null) {
                String tableId = sysFlow.getTableId();
                SysTable sysTable = (SysTable) new SysTableDAO().queryById(new SysTable(), tableId);
                if (sysTable != null) {
                    String fieldName = jdbcDao.getFieldValue("select COLUMN_CODE FROM sys_table_column where ispk='1' and table_id='" + tableId + "'", "COLUMN_CODE");
                    if (StringUtils.isNotEmpty(fieldName)) {
                        jdbcDao.execute(String.format("delete from %s where %s='%s'", sysTable.getTableCode(), fieldName, dataId));
                    }
                }
            }
            //删除流程实例：
            jdbcDao.execute("delete from sys_flow_instance where flow_instance_id='" + flowInstanceId + "'");
            //删除流程执行信息
            jdbcDao.execute("delete from sys_flow_exec where flow_instance_id='" + flowInstanceId + "'");

            jdbcDao.close();
        }
        return CommonFunc.makeSussResult(null);
    }

    /*
     * ViewFlowImg:流程图查看
     * 参数:flowId
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Flow/ViewFlowImg",method = RequestMethod.GET)
    public Map<String, Object> ViewFlowImg(@RequestParam Map<String,Object> params, HttpServletRequest req) throws Exception {
        getUserId();

        try(JdbcDao jdbcDao=new JdbcDao()) {

            //获取流程信息
            String flowId = CommonFunc.getNotNullString(params.get("flow_id"));
            if (StringUtils.isEmpty(flowId)) {
                return CommonFunc.makeErrResult("");
            }

            SysFlow sysFlow = (SysFlow) new SysFlowDAO().queryById(new SysFlow(), flowId);
            if (sysFlow == null) {
                return CommonFunc.makeErrResult("");
            }

            //获取流程节点信息
            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            sysFlowNodeDAO.setWhere(" flow_id='" + flowId + "' ");
            sysFlowNodeDAO.setOrder(" sortindex asc ");
            ArrayList<SysFlowNode> listSysFlowNodeTmp = (ArrayList<SysFlowNode>) sysFlowNodeDAO.query(new SysFlowNode());
            ArrayList<SysFlowNode> listSysFlowNode = new ArrayList<SysFlowNode>();
            LinkedHashMap<String, SysFlowNode> mapNode = new LinkedHashMap<String, SysFlowNode>();
            for (int i = 0; i < listSysFlowNodeTmp.size(); i++) {
                if (listSysFlowNodeTmp.get(i).getIsChildnode().equals("1")) {
                    continue;
                }

                listSysFlowNode.add(listSysFlowNodeTmp.get(i));
                listSysFlowNode.get(listSysFlowNode.size() - 1).setExtraInfo1(listSysFlowNode.size());
                mapNode.put(listSysFlowNodeTmp.get(i).getFlowNodeId(), listSysFlowNode.get(listSysFlowNode.size() - 1));
            }
            //输出流程图：
            int imgWidth = 1000, imgHeight = listSysFlowNodeTmp.size() * 150 + 300;
            int currTop = 100;

            BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_BGR);
            Graphics graphics = image.getGraphics();

            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, imgWidth, imgHeight);
            graphics.setColor(Color.red);

            //输出标题
            Font fontTitle = new Font("黑体", Font.BOLD, 36);
            graphics.setFont(fontTitle);
            graphics.drawString(sysFlow.getFlowName(), (imgWidth - graphics.getFontMetrics(fontTitle).stringWidth(sysFlow.getFlowName())) / 2, currTop);
            currTop += graphics.getFontMetrics(fontTitle).getHeight() + 50;

            Font font = new Font("宋体", Font.BOLD, 18);
            graphics.setFont(font);

            for (int i = 0; i < listSysFlowNode.size(); i++) {
                SysFlowNode sysFlowNode = listSysFlowNode.get(i);
                String nodeName = sysFlowNode.getNodeName();
                String nodeType = sysFlowNode.getNodeType();

                if ("10".equals(nodeType)) {
                    //普通节点
                    //矩形，中间文字
                    graphics.drawRect((imgWidth - graphics.getFontMetrics(font).stringWidth(nodeName)) / 2 - 25,
                            currTop - graphics.getFontMetrics(font).getHeight() - 10,
                            graphics.getFontMetrics(font).stringWidth(nodeName) + 50, graphics.getFontMetrics(font).getHeight() + 30);
                    graphics.drawString(nodeName, (imgWidth - graphics.getFontMetrics(font).stringWidth(nodeName)) / 2, currTop);
                    //箭头连线
                    if (i < listSysFlowNode.size() - 1) {
//                        FlowPaint.PaintArrow(graphics, imgWidth / 2, currTop + graphics.getFontMetrics(font).getHeight(),
//                                90, -90);
                    }
                    currTop += 150;
                } else if ("20".equals(nodeType)) {
                    String switchCode = sysFlowNode.getSwitchCode();
                    JSONObject json = JSONObject.parseObject(switchCode);
                    String field = json.get("field").toString();
                    String values = json.get("values").toString();
                    JSONArray jsonArr = JSONObject.parseArray(values);
                    String switchTrue = "", switchFalse = "", nodeIdFalse = "", nodeNameFalse = "";
                    int nodeIndexFalse = 0;
                    for (int j = 0; j < jsonArr.size(); j++) {
                        String[] arrItem = jsonArr.get(j).toString().split(":");
                        if (arrItem.length < 2) {
                            continue;
                        }
                        if (arrItem[1].trim().equals(listSysFlowNode.get(i + 1).getFlowNodeId()) && StringUtils.isEmpty(switchTrue)) {
                            //条件
                            switchTrue = arrItem[0].trim();
                        } else {
                            switchFalse = arrItem[0].trim();
                            nodeIdFalse = arrItem[1].trim();
                            if (mapNode.get(nodeIdFalse) != null) {
                                nodeIndexFalse = (int) (mapNode.get(nodeIdFalse).getExtraInfo1());
                            }
                            nodeNameFalse = mapNode.containsKey(nodeIdFalse) ? mapNode.get(nodeIdFalse).getNodeName() : "";
                        }
                    }

                    //分支节点
                    //获取条件字段中文名
                    String sql = "select column_name from sys_table_column where column_code='" + field + "' and table_id='" + sysFlow.getTableId() + "'";
                    String columnName = jdbcDao.getFieldValue(sql, "COLUMN_NAME");
                    if (StringUtils.isEmpty(columnName)) {
                        columnName = field;
                    }

                    //尖形
//                    FlowPaint.drawRectSwitch(graphics, imgWidth / 2, currTop - graphics.getFontMetrics(font).getHeight() - 10,
//                            graphics.getFontMetrics(font).getHeight() + 30, graphics.getFontMetrics(font).stringWidth(nodeName),
//                            columnName, font, switchTrue, switchFalse, 150 * (nodeIndexFalse - i));
                    //中间文字
                    graphics.drawString(nodeName, (imgWidth - graphics.getFontMetrics(font).stringWidth(nodeName)) / 2, currTop);
                    //否分支
                    currTop += 150;
                } else if ("30".equals(nodeType)) {
                    graphics.drawRect(100,
                            currTop - graphics.getFontMetrics(font).getHeight() - 10,
                            imgWidth - 200, graphics.getFontMetrics(font).getHeight() + 30);
                    graphics.drawString(nodeName, (imgWidth - graphics.getFontMetrics(font).stringWidth(nodeName)) / 2, currTop);

                    graphics.drawRect(100,
                            currTop + 20,
                            imgWidth - 200, graphics.getFontMetrics(font).getHeight() + 30 + 200);

                    //子节点

                    ArrayList<SysFlowNode> listSysFlowNodeChild = new ArrayList<SysFlowNode>();
                    for (int xx = 0; xx < listSysFlowNodeTmp.size(); xx++) {
                        if (listSysFlowNodeTmp.get(xx).getIsChildnode().equals("1")) {
                            if (listSysFlowNodeTmp.get(xx).getParentNodeId().equals(listSysFlowNode.get(i).getFlowNodeId())) {
                                listSysFlowNodeChild.add(listSysFlowNodeTmp.get(xx));
                            }
                        }
                    }

                    int count = listSysFlowNodeChild.size();
                    int firstLeft = (imgWidth - count * 200 + 50) / 2;
                    for (int xx = 0; xx < count; xx++) {
                        String nodeNameChild = listSysFlowNodeChild.get(xx).getNodeName();
                        graphics.drawRect(firstLeft + xx * 200,
                                currTop + 100, 150
                                , graphics.getFontMetrics(font).getHeight() + 30);
                        graphics.drawString(nodeNameChild, firstLeft + xx * 200 + (150 - graphics.getFontMetrics(font).stringWidth(nodeNameChild)) / 2,
                                currTop + 100 + (graphics.getFontMetrics(font).getHeight() + 40) / 2);
                    }

                    currTop += 250;
                    //箭头连线
                    if (i < listSysFlowNode.size() - 1) {
//                        FlowPaint.PaintArrow(graphics, imgWidth / 2, currTop + graphics.getFontMetrics(font).getHeight(),
//                                90, -90);
                    }
                    currTop += 150;


                }
            }

            graphics.dispose();

            try {
                // 将内存中的图片通过流动形式输出到客户端
                ImageIO.write(image, "BMP", response.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }


            jdbcDao.close();
        }
        return null;
    }

    /*
     * 流程执行:保存草稿
     * 参数:流程实例ID,flowInstanceId
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Flow/SaveData",method = RequestMethod.POST)
    public Map<String, Object> SaveData(@RequestParam Map<String,Object> params, HttpServletRequest req) throws Exception {
        getUserId();

        Map<String,Object> mapPost=CommonFunc.checkServerPost(params,sysConfig);

        if (mapPost.get("result").equals("0")){
            //失败
            return mapPost;
        }

        //解析成功,开始处理登录验证
        Map<String,String> mapVar= (Map<String, String>) mapPost.get("vars");

        //1.查找流程实例
        String flowInstanceId=params.get("flow_instance_id").toString();
        String flowExecId=mapVar.get("flow_exec_id").toString();
        SysFlowInstance sysFlowInstance=(SysFlowInstance) new SysFlowInstanceDAO().queryById(new SysFlowInstance(),flowInstanceId);
        SysFlowExecDAO sysFlowExecDAO=new SysFlowExecDAO();
        SysFlowExec sysFlowExecCurr=(SysFlowExec) sysFlowExecDAO.queryById(new SysFlowExec(),flowExecId);

        if (sysFlowInstance==null || sysFlowExecCurr==null){
            return CommonFunc.makeErrResult("600010");
        }

        //2.获取当前节点/EXEC_ACTION执行动作
        String flowNodeIdCurr=sysFlowInstance.getFlowNodeId();
        SysFlowNode sysFlowNodeCurr=(SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), flowNodeIdCurr);
        if (sysFlowNodeCurr.getNodeType().equals(FlowService.NODETYPE_MORE)) {
            String flowNodeIdChild = params.get("flow_node_id").toString();
            sysFlowNodeCurr = sysFlowNodeCurr = (SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), flowNodeIdChild);
        }

        String dataId=sysFlowInstance.getDataId();

        //3.更新数据表数据
        Map<String, String> mapData=FlowService.updateFlowData(req,sysConfig,dataId,mapVar,sysFlowNodeCurr);
        if (mapData==null){
            return CommonFunc.makeErrResult("600099");
        }

        //4.更新子表数据
        String allSubtabledata=mapVar.get("all_subtabledata").toString();
        FlowService.updateFlowSubData(allSubtabledata,dataId,sysFlowNodeCurr);

        //更新流程实例状态
        if (sysFlowInstance.getStatus().equals(FlowService.FLOW_STATUS_DRAFT)) {
            sysFlowInstance.setStatus(FlowService.FLOW_STATUS_EXECUTING);
            new SysFlowInstanceDAO().update(sysFlowInstance);
        }

        return CommonFunc.makeSussResult(null);
    }

    /*
     * 流程执行:
     * 参数:流程实例ID,flowInstanceId
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Flow/Execute",method = RequestMethod.POST)
    public Map<String, Object> Execute(@RequestParam Map<String,Object> params,HttpServletRequest req) throws Exception {
        getUserId();
        CommonLogger commonLogger=CommonLogger.getInstance();

        Map<String,Object> mapPost=CommonFunc.checkServerPost(params,sysConfig);

        if (mapPost.get("result").equals("0")){
            //失败
            return mapPost;
        }

        //解析成功,开始处理登录验证
        Map<String,String> mapVar= (Map<String, String>) mapPost.get("vars");

        //Map<String,String> mapVar=params;
        Map<String,Object> mapResultData=new HashMap<String,Object>();
        try(JdbcDao jdbcDao=new JdbcDao()) {

            //选择人员相关
            String nodekey = mapVar.containsKey("nodekey") ? mapVar.get("nodekey") : "";

            //1.查找流程实例
            String flowInstanceId = params.get("flow_instance_id").toString();
            String flowExecId = mapVar.get("flow_exec_id").toString();
            SysFlowInstance sysFlowInstance = (SysFlowInstance) new SysFlowInstanceDAO().queryById(new SysFlowInstance(), flowInstanceId);
            SysFlowExecDAO sysFlowExecDAO = new SysFlowExecDAO();
            SysFlowExec sysFlowExecCurr = (SysFlowExec) sysFlowExecDAO.queryById(new SysFlowExec(), flowExecId);

            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：execute flow begin");
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：flowInstanceId:" + flowInstanceId + ",flowExecId:" + flowExecId);

            if (sysFlowInstance == null || sysFlowExecCurr == null) {
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：return error code:600010");
                return CommonFunc.makeErrResult("600010");
            }

            SysFlow sysFlow = (SysFlow) new SysFlowDAO().queryById(new SysFlow(), sysFlowInstance.getFlowId());

            //2.获取当前节点/EXEC_ACTION执行动作
            String flowNodeIdCurr = sysFlowExecCurr.getFlowNodeId();//sysFlowInstance.getFlowNodeId();
            SysFlowNode sysFlowNodeCurr = (SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), flowNodeIdCurr);
            SysFlowNode sysFlowNodeCurrParent = null;
            String flowId = sysFlowInstance.getFlowId();
            String now = CommonFunc.unix2Date(CommonFunc.unixTimeStr());
            String dataId = sysFlowInstance.getDataId();
            String execAction = params.get("exec_action").toString();

            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：flowId:" + flowId + ",dataId:" + dataId + ",execAction:" + execAction);

            if (!FlowService.FLOW_ACTION_ACCEPT.equals(execAction) && !FlowService.FLOW_ACTION_RETURN.equals(execAction)) {
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：return error code:600013");
                return CommonFunc.makeErrResult("600013");
            }

            Map<String, String> mapData;
            //如果是会签节点，则从表单中取ID
            if (sysFlowNodeCurr.getIsChildnode().equals("1")) {
                String parentNodeId = sysFlowNodeCurr.getParentNodeId();
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：is childnode,parentNodeId:" + parentNodeId);
                sysFlowNodeCurrParent = (SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), parentNodeId);
            }
            //3.更新数据表数据
            mapData = FlowService.updateFlowData(req, sysConfig, dataId, mapVar, sysFlowNodeCurr);

            if (mapData == null) {
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：return error code:600099");
                return CommonFunc.makeErrResult("600099");
            }

            //4.更新子表数据
            String allSubtabledata = mapVar.get("all_subtabledata").toString();
            FlowService.updateFlowSubData(allSubtabledata, dataId, sysFlowNodeCurr);

            //4.更新当前节点执行信息
            sysFlowExecCurr.setExecAction(execAction);
            sysFlowExecCurr.setExecTime(now);
            sysFlowExecCurr.setUserid(userId);
            sysFlowExecCurr.setUsername(userName);
            //更新执行信息表中的审批意见：
            sysFlowExecCurr.setAuditContent(FlowService.getFlowExecAuditContent(
                    sysFlowNodeCurr.getTableColumnGroupId(), mapData));

            if (!sysFlowExecDAO.update(sysFlowExecCurr)) {
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：return error code:500009");
                return CommonFunc.makeErrResult("500009");
            }


            //5.获取跳转到的下一节点
            if ("1".equals(sysFlowNodeCurr.getIsEmptynode())) {
                //
            } else {
                if (FlowService.FLOW_ACTION_ACCEPT.equals(execAction)) {
                    //5.1:同意
                    commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow begin");
                    boolean flag = FlowService.acceptFlow(sysFlow,mapData, sysFlowNodeCurr, sysFlowExecCurr, sysFlowInstance, sysFlowNodeCurrParent, nodekey, mapVar);
                    commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：acceptFlow end");

                    if (!flag) {
                        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：return error code:600011");
                        return CommonFunc.makeErrResult("600011");
                    }

                    //////////////////////////////////////////////////////////
                    //调度编号图专用：开启新的线程，调用AI智能审核
                    if ("10000801".equals(flowNodeIdCurr)){
                        new Thread(() -> {
                            //后台调用
                            try {
//                                ZdhService.DdbhtDiff(dataId);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }).start();

                    }else if ("10006201".equals(flowNodeIdCurr)){
                        //馈线控制管理：开启新的线程，调用AI智能审核
                        new Thread(() -> {
                            //后台调用
                            try {
//                                ZdhService.KxkzglAi(dataId);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }).start();

                    }
                } else {
                    //5.2:回退
                    String prevFlowNodeId = sysFlowExecCurr.getPrevFlowNodeId();
                    if (StringUtils.isEmpty(prevFlowNodeId)) {
                        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：return error code:600011");
                        return CommonFunc.makeErrResult("600011");
                    }

                    commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：returnFlow begin");

                    String flagToFirst = CommonFunc.getNotNullString(params.get("flag_tofirst"));
                    boolean bFlagToFirst = "1".equals(flagToFirst) ? true : false;

                    boolean flag = FlowService.returnFlow(sysFlow,sysFlowExecCurr, sysFlowInstance,
                            sysFlowNodeCurr, sysFlowNodeCurrParent, bFlagToFirst);

                    commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：returnFlow end");

                    if (!flag) {
                        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：return error code:600011");
                        return CommonFunc.makeErrResult("600011");
                    }

                    //5.12记录到回退操作的日志
                }
            }

            /////////////////////////////////////////////////
            //更新流程实例状态
            //2026-06-18修改：增加通知 模板功能
            if (sysFlowInstance.getStatus().equals(FlowService.FLOW_STATUS_DRAFT)) {
                sysFlowInstance.setStatus(FlowService.FLOW_STATUS_EXECUTING);
            }

            String notifyContent=FlowService.getFlowInstanceNotifyContent(
                    sysFlow.getNoticeFormat(),"W_"+sysFlow.getFlowCode(),
                        "ID",dataId
            );
            sysFlowInstance.setNoticeContent(notifyContent);
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：update sysFlowInstance begin");
            new SysFlowInstanceDAO().update(sysFlowInstance);
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：update sysFlowInstance end");
            /////////////////////////////////////////////////

            //5.1记录到确认操作的日志
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：add to zdh log begin");

//            ZdhService.addZdhFlowLog(flowInstanceId, sysFlowNodeCurr, sysFlow, execAction, mapData);

            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：add to zdh log end");

            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + "：execute flow suss");

            return CommonFunc.makeSussResult(null);
        }
    }

    /*
     * 流程展示和操作界面
     * 参数:流程编码,flow_code
     * 返回数据:
     *{
			result:0/1,
			data:list
		};
     */
    @RequestMapping(value = "/Flow/WorkForm",method = RequestMethod.GET)
    public Map<String, Object> WorkForm(@RequestParam Map<String,String> params) throws Exception {
        Map<String,Object> mapResultData=new HashMap<String,Object>();
        Map<String, Object> mapResult = new HashMap<String, Object>();
        mapResult.put("processStartTime",CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
        getUserId();


        try(JdbcDao jdbcDao=new JdbcDao()) {

            //1.查找流程实例
            String flowInstanceId = params.get("flow_instance_id");
            String dataId = params.get("data_id");
            SysFlowInstanceDAO sysFlowInstanceDAO = new SysFlowInstanceDAO();
            SysFlowInstance sysFlowInstance = (SysFlowInstance) sysFlowInstanceDAO.queryById(new SysFlowInstance(), flowInstanceId);

            if (sysFlowInstance == null) {
                sysFlowInstanceDAO.setWhere("data_id='" + dataId + "'");
                sysFlowInstance = (SysFlowInstance) sysFlowInstanceDAO.queryFirstRow(new SysFlowInstance());
            }

            SysFlow sysFlow;

            if (sysFlowInstance == null) {
                //return CommonFunc.makeErrResult("600010");
                //通过flow_code来确定flowId和sysflow
                String flowCode = CommonFunc.getNotNullString(params.get("flow_code"));
                SysFlowDAO sysFlowDAO = new SysFlowDAO();
                sysFlowDAO.setWhere("flow_code='" + flowCode + "'");
                sysFlow = (SysFlow) sysFlowDAO.queryFirstRow(new SysFlow());
            } else {
                //2.查找流程配置信息/流程节点信息/业务数据信息
                flowInstanceId = sysFlowInstance.getFlowInstanceId();
                dataId = sysFlowInstance.getDataId();
                sysFlow = (SysFlow) new SysFlowDAO().queryById(new SysFlow(), sysFlowInstance.getFlowId());
            }

            if (sysFlow == null) {
                return CommonFunc.makeErrResult("600001");
            }

            SysFlowExec sysFlowExecCurr = FlowService.getSysFlowExec(userName, departmentId, flowInstanceId);

            String flowId = sysFlow.getFlowId();
            String tableId = sysFlow.getTableId();
            SysTable sysTable = (SysTable) new SysTableDAO().queryById(new SysTable(), tableId);

            if (sysTable == null) {
                return CommonFunc.makeErrResult("600004");
            }

            String tableCode = sysTable.getTableCode();
            ArrayList<SysTableColumnGroup> listGroup = FlowService.getTableGroup(tableId);
            HashMap<String, Object> mapListColumn = FlowService.getTableColumn(tableId);
            if (!mapListColumn.containsKey("pkField")) {
                return CommonFunc.makeErrResult("600005");
            }

            SysTableColumn sysTableColumnPk = (SysTableColumn) mapListColumn.get("pkField");

            if (sysTableColumnPk == null) {
                return CommonFunc.makeErrResult("600005");
            }

            String fieldPk = sysTableColumnPk.getColumnCode();
            String sql = String.format("SELECT * from %s where %s='%s'", tableCode, fieldPk, dataId);

            HashMap<String, String> mapData = (HashMap<String, String>) jdbcDao.getFirstRow(sql);

            if (mapData == null) {
                return CommonFunc.makeErrResult("600012");
            }

            //3.获取当前登录用户的该业务的流程执行信息
            SysFlowExec sysFlowExec = sysFlowExecCurr;
            String tableGroupId = "";
            if (sysFlowExec != null) {
                String flowNodeId = sysFlowExec.getFlowNodeId();
                SysFlowNode sysFlowNode = (SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), flowNodeId);
                if (sysFlowNode == null) {
                    return CommonFunc.makeErrResult("600002");
                }
                tableGroupId = sysFlowNode.getTableColumnGroupId();
            }

            ArrayList<Object> listFlowNode = FlowService.getAllFlowNode(flowId, mapData, mapListColumn, tableGroupId, dataId, sysFlow.getFlowCode());

            //判断当前用户是否有提交/保存/回退权限
            HashMap<String, String> mapModifyGrant = FlowService.checkFlowExecGrant(sysFlowInstance);
        /*
        if ("1".equals(mapModifyGrant.get("flagModify"))){
            //有编辑权限
            for (int i=0;i<listFlowNode.size();i++){
                HashMap<String,Object> mapItem=(HashMap<String, Object>) listFlowNode.get(i);
                if (!"1".equals(mapItem.get("grantFlag"))){
                    continue;
                }

                ArrayList<FlowFieldInfo>  listGroupInfo=(ArrayList<FlowFieldInfo>) mapItem.get("listGroupInfo");
                for (int j=0;j<ArrayList)
            }
        }

        */

            //4.获取下一节点的所有人员资料
            SysFlowNode sysFlowNodeCurr = (sysFlowExec != null) ? (SysFlowNode) new SysFlowNodeDAO().queryById(new SysFlowNode(), sysFlowExec.getFlowNodeId()) : new SysFlowNode();
            SysFlowNodeDAO sysFlowNodeDAO = new SysFlowNodeDAO();
            ArrayList<MapListFlowNodeUsers> listMapFlowNodeUsers = new ArrayList<MapListFlowNodeUsers>();
            if (StringUtils.isNotEmpty(sysFlowNodeCurr.getFlowNodeId()) && sysFlowNodeCurr.getIsSelectuser().equals("1")) {
                String nodeType = sysFlowNodeCurr.getNodeType();
                if (nodeType.equals(FlowService.NODETYPE_SEQ)) {
                    //顺序节点
                    String sortindex = String.valueOf(sysFlowNodeCurr.getSortindex());
                    sysFlowNodeDAO.setWhere(String.format("FLOW_ID='%s' and nvl(is_childnode,'0')='0' and SORTINDEX>'%s'", flowId, sortindex));
                    sysFlowNodeDAO.setOrder(" SORTINDEX ASC ");
                    SysFlowNode sysFlowNodeNext = (SysFlowNode) sysFlowNodeDAO.queryFirstRow(new SysFlowNode());
                    if (sysFlowNodeNext != null) {
                        MapListFlowNodeUsers mapItem = new MapListFlowNodeUsers();
                        mapItem.setKey("normal");
                        mapItem.setFlagHideUser("0");
                        mapItem.setListFlowNodeUsers(FlowService.getFlowNodeUsers(sysFlowNodeNext));
                        listMapFlowNodeUsers.add(mapItem);
                    }
                } else if (nodeType.equals(FlowService.NODETYPE_SWITCH)) {
                    //分支节点
                    String switchCode = sysFlowNodeCurr.getSwitchCode();
                    JSONObject json = JSONObject.parseObject(switchCode);
                    if (json.containsKey("values")) {
                        JSONArray jsonArr = json.getJSONArray("values");
                        JSONObject jsonFlag = json.containsKey("flagHideUser")
                                ? json.getJSONObject("flagHideUser") : new JSONObject();

                        for (int i = 0; i < jsonArr.size(); i++) {
                            String tmp = jsonArr.get(i).toString().trim();
                            String[] arrTmp = tmp.split(":");
                            String nodeIds = arrTmp.length == 2 ? arrTmp[1] : arrTmp.length == 3 ? arrTmp[2] : "";
                            MapListFlowNodeUsers mapItem = new MapListFlowNodeUsers();
                            mapItem.setKey("swtich" + String.valueOf(i));
                            mapItem.setSwitchName(json.getString("field").toString());
                            mapItem.setSwitchVal(arrTmp[0]);
                            mapItem.setFlagHideUser(jsonFlag.containsKey(arrTmp[0]) ? jsonFlag.getString(arrTmp[0]) : "0");
                            mapItem.setListFlowNodeUsers(FlowService.getFlowNodeUsers(nodeIds));
                            listMapFlowNodeUsers.add(mapItem);
                        }
                    }
                }
            }

            //5.获取提交时选择的默认人员
            // 返回业务表单数据
            FlowWorkForm flowWorkForm = new FlowWorkForm();
            String defuser = sysFlowNodeCurr.getSubmitDefuser();

            if ("#loginName#".equals(defuser)) {
                //当前登录人
                flowWorkForm.setDefUserid(Eval.getLoginUserId());
                flowWorkForm.setDefUsername(Eval.getLoginName());
                flowWorkForm.setDefDeptname(Eval.getLoginDepartmentName());
            }

            flowWorkForm.setSysFlow(sysFlow);
            flowWorkForm.setSysFlowInstance(sysFlowInstance);
            flowWorkForm.setSysTable(sysTable);
            flowWorkForm.setSysFlowExec(sysFlowExec == null ? new SysFlowExec() : sysFlowExec);
            flowWorkForm.setListFlowNode(listFlowNode);
            flowWorkForm.setMapModifyGrant(mapModifyGrant);
            flowWorkForm.setListMapFlowNodeUsers(listMapFlowNodeUsers);
            //获取当前
            flowWorkForm.setSysFlowNode(sysFlowNodeCurr);

            ////////////////////////////////////////////
            //6.判断当前登录用户是否具备“主动撤回”权限
            SysFlowExec sysFlowExecLast=sysFlowExec;
            if (sysFlowExecLast==null){
                sysFlowExecLast=FlowService.getLastSysFlowExec(flowInstanceId);
            }

            mapResult.put("grantUndo",(sysFlowExecLast==null)?"0":
                    (FlowService.checkFlowExecCanUndo(sysFlowInstance.getStatus(),
                            sysFlowExecLast.getFlowNodeId(), sysFlowExecLast.getPrevFlowExecId())?"1":"0")
            );
            ////////////////////////////////////////////

            mapResult.put("flowExecIdLast",(sysFlowExecLast!=null)?sysFlowExecLast.getFlowExecId():"");
            mapResult.put("dataId", dataId);
            mapResult.put("flowId", flowId);
            mapResult.put("listFlowNode", listFlowNode);
            mapResult.put("flowWorkForm", flowWorkForm);
            mapResult.put("sys_title", sysConfig.getTitle());
            mapResult.put("page_title", sysFlow.getFlowName());
            mapResult.put("deptId", Eval.getLoginDepartmentId());
            mapResult.put("loginName", Eval.getLoginName());
            mapResult.put("DepartmentName", Eval.getLoginDepartmentName());
            mapResult.put("DepartmentId",Eval.getLoginDepartmentId());

            return CommonFunc.makeSussResult(mapResult, new ServerKeyIv(sysConfig.getTempfilePath()));
        }
    }

    /*
     * 启动一个新的流程
     * 参数:流程编码,flow_code
     * 返回数据:
     *{
			result:0/1,
			data:list
		};
     */
    @RequestMapping(value = "/Flow/Start",method = RequestMethod.GET)
    public Map<String, Object> Start(@RequestParam Map<String,String> params) throws Exception {
        getUserId();

        String flowCode = CommonFunc.nullToSpace(params.get("flow_code"));
        //复制ID(优先使用)
        String copyFlowInstanceId=CommonFunc.nullToSpace(params.get("flow_instance_id"));

        try(JdbcDao jdbcDao=new JdbcDao()) {
            SysFlowDAO sysFlowDAO = new SysFlowDAO();
            SysFlow sysFlow = null;
            boolean flagCopy = false;
            SysFlowInstance sysFlowInstanceSrc = new SysFlowInstance();

            if (StringUtils.isNotEmpty(copyFlowInstanceId)) {
                //从已有ID复制
                sysFlowInstanceSrc = (SysFlowInstance) new SysFlowInstanceDAO().queryById(new SysFlowInstance(), copyFlowInstanceId);
                if (sysFlowInstanceSrc != null) {
                    sysFlow = (SysFlow) sysFlowDAO.queryById(new SysFlow(), sysFlowInstanceSrc.getFlowId());
                    if (sysFlow != null) {
                        flowCode = sysFlow.getFlowCode();
                        flagCopy = true;
                    }
                }
            }

            //第1步:从流程表中获取数据
            if (sysFlow == null) {
                sysFlowDAO.setWhere("flow_code='" + flowCode + "'");
                sysFlow = (SysFlow) sysFlowDAO.queryFirstRow(new SysFlow());
            }

            if (sysFlow == null) {
                return CommonFunc.makeErrResult("600001");
            }

            //第2步:检查当前登录用户是否具备权限启动流程(检查第一个节点的权限)
            String flowId = sysFlow.getFlowId();
            SysFlowNode sysFlowNode = FlowService.getFirstNode(flowId);
            if (sysFlowNode == null) {
                return CommonFunc.makeErrResult("600002");
            }

            //第3步:检查启动流程的节点权限
            if (!FlowService.checkFlowNodeGrant(userId, departmentId, sysFlowNode.getGrants())) {
                return CommonFunc.makeErrResult("600003");
            }

            //第4步:获取流程对应的数据表
            SysTable sysTable = (SysTable) new SysTableDAO().queryById(new SysTable(), String.valueOf(sysFlow.getTableId()));
            if (sysTable == null) {
                return CommonFunc.makeErrResult("600004");
            }

            //第5步:获取所有分组与字段信息
            String tableId = sysTable.getTableId();
            String whereTableId = "table_id='" + tableId + "'";
            ArrayList<SysTableColumnGroup> listGroup = FlowService.getTableGroup(tableId);
            HashMap<String, Object> mapListColumn = FlowService.getTableColumn(tableId);
            if (!mapListColumn.containsKey("pkField")) {
                return CommonFunc.makeErrResult("600005");
            }
            SysTableColumn sysTableColumnPk = (SysTableColumn) mapListColumn.get("pkField");


            //第5步:获取表的信息和字段信息
            //字段的保存:JSON,组ID,字段ID
            String grantsField = sysFlowNode.getGrantsField();

            // 在数据表和流程记录表中添加新记录数据
            String tableCode = sysTable.getTableCode();
            String pkField = sysTableColumnPk.getColumnCode();
            String pkValue = jdbcDao.getSeqNextVal();
            //获取第一个节点，有默认值的字段，并取值保存
            String groupId = sysFlowNode.getTableColumnGroupId();
            String defaultFieldName = "";
            String defaultFieldValue = "";

            if (mapListColumn.containsKey(groupId)) {
                ArrayList<SysTableColumn> listColumn = (ArrayList<SysTableColumn>) mapListColumn.get(groupId);
                for (int i = 0; i < listColumn.size(); i++) {
                    String defaultValue = listColumn.get(i).getDefaultvalue().trim();
                    if (StringUtils.isEmpty(defaultValue)) {
                        continue;
                    }
                    //有默认值
                    String value = new Eval().eval(defaultValue).toString();
                    if (StringUtils.isEmpty(value)) {
                        continue;
                    }

                    defaultFieldName += "," + listColumn.get(i).getColumnCode();
                    defaultFieldValue += ",'" + value + "'";
                }
            }
            String sqlData = String.format("insert into %s (%s  %s) values ('%s'  %s)", tableCode, pkField, defaultFieldName, pkValue, defaultFieldValue);
            if (!jdbcDao.execute(sqlData)) {
                return CommonFunc.makeErrResult("600099");
            }

            //写入流程实例表
            SysFlowInstance sysFlowInstance = new SysFlowInstance();
            SysFlowInstanceDAO sysFlowInstanceDAO = new SysFlowInstanceDAO();
            String flowInstanceId = jdbcDao.getSeqNextVal();
            String now = CommonFunc.unix2Date(CommonFunc.unixTimeStr());

            sysFlowInstance.setFlowInstanceId(flowInstanceId);
            sysFlowInstance.setFlowId(flowId);
            sysFlowInstance.setFlowNodeId(sysFlowNode.getFlowNodeId());
            sysFlowInstance.setCreateTime(now);
            sysFlowInstance.setUserid(userId);
            sysFlowInstance.setDepartmentid(departmentId);
            sysFlowInstance.setDataId(pkValue);
            //默认为草稿
            if (flagCopy) {
                sysFlowInstance.setStatus(FlowService.FLOW_STATUS_EXECUTING);
            } else {
                sysFlowInstance.setStatus(FlowService.FLOW_STATUS_DRAFT);
            }
            sysFlowInstance.setFlagReturn("0");
            sysFlowInstanceDAO.saveNew(sysFlowInstance);

            SysFlowExecDAO sysFlowExecDAO = new SysFlowExecDAO();
            SysFlowExec sysFlowExec = new SysFlowExec();
            sysFlowExec.setFlowExecId(jdbcDao.getSeqNextVal());
            sysFlowExec.setFlowId(flowId);
            sysFlowExec.setFlowNodeId(sysFlowNode.getFlowNodeId());
            sysFlowExec.setCreateTime(now);
            sysFlowExec.setDepartmentid(departmentId);
            sysFlowExec.setExecAction(FlowService.FLOW_ACTION_START);
            sysFlowExec.setFlowInstanceId(flowInstanceId);
            sysFlowExec.setFlagReturn("0");
            sysFlowExecDAO.saveNew(sysFlowExec);

            Map<String, Object> mapResult = new HashMap<String, Object>();

            //如果是复制，则更新复制的第一个节点的信息
            //10-24更新：复制第一个节点所有分组的信息
            if (flagCopy) {
                String[] arrGroupId = sysFlowNode.getTableColumnGroupId().trim().split(",");
                for (int i = 0; i < arrGroupId.length; i++) {
                    String arrTmp[] = arrGroupId[i].trim().split(":");
                    ArrayList<SysTableColumn> listColumn = (ArrayList<SysTableColumn>) mapListColumn.get(arrTmp[0]);
                    for (int j = 0; j < listColumn.size(); j++) {
                        SysTableColumn sysTableColumn = listColumn.get(j);
                        if (sysTableColumn.getIshide().equals("1") || sysTableColumn.getIspk().equals("1")
                                || StringUtils.isNotEmpty(sysTableColumn.getDefaultvalue())) {
                            continue;
                        }

                        String sqlUpdate = String.format("update %s set %s=(select %s from %s where id='%s') where id='%s'",
                                tableCode, sysTableColumn.getColumnCode(), sysTableColumn.getColumnCode(),
                                tableCode, sysFlowInstanceSrc.getDataId(), pkValue);
                        jdbcDao.execute(sqlUpdate);
                    }
                }

                //复制子表信息
                String tableSubInfo = sysFlowNode.getTableSubinfo().trim();
                if (StringUtils.isNotEmpty(tableSubInfo)) {
                    String[] arrSubTable = tableSubInfo.trim().split(",");
                    for (int kk = 0; kk < arrSubTable.length; kk++) {
                        String subTableId = arrSubTable[kk].trim();
                        SysTable sysTableSub = (SysTable) new SysTableDAO().queryById(new SysTable(), subTableId);
                        if (sysTableSub == null) {
                            continue;
                        }
                        String subTableCode = sysTableSub.getTableCode();
                        String sqlTmp = "select wm_concat(COLUMN_NAME) COLUMN_NAMES from user_col_comments where table_name='" +
                                subTableCode + "' and column_name<>'ID' and column_name<>'REFID' ";
                        String fieldNames = jdbcDao.getFieldValue(sqlTmp, "COLUMN_NAMES");
                        if (StringUtils.isEmpty(fieldNames)) {
                            continue;
                        }

                        String sqlSub = String.format("insert into %s (ID,REFID,%s) " +
                                        "select seq.nextval,'%s',%s FROM %s where REFID='%s'",
                                subTableCode, fieldNames, pkValue, fieldNames, subTableCode,
                                sysFlowInstanceSrc.getDataId()
                        );
                        jdbcDao.execute(sqlSub);
                    }
                }

                String gotoUrl = "";
                String baseUrl = RequestUtils.getBaseUrl(request);

                if (sysFlow.getFlowCode().equals("XLZHZJXD")) {
                    //线路重合闸检修单，单独展示
                    gotoUrl = baseUrl + sysConfig.getClientUrl() + "/module/bussines/zwdd/XLZHZJXD.html??flow_code=XLZHZJXD&data_id=" + pkValue;

                } else {
                    gotoUrl = baseUrl + sysConfig.getClientUrl() + "/flowform.html?flow_instance_id=" + flowInstanceId;
                }
                response.sendRedirect(gotoUrl);
                return null;
            }

            jdbcDao.close();

            mapResult.put("flowInstanceId", flowInstanceId);

            return CommonFunc.makeSussResult(mapResult);
        }
    }

}
