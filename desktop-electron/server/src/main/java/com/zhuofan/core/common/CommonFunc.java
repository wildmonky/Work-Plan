/**
 * com.zhoufan.core.common
 *
 *
 *   ver     date           author
 *
 *           2015-05-15      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.datasource.DataSource;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.dao.SysNoticeDAO;
import com.zhuofan.core.orm.dao.SysUsersDAO;
import com.zhuofan.core.orm.model.SysNotice;
import com.zhuofan.core.orm.model.SysUsers;
import com.zhuofan.core.util.SessionParams;
import com.zhuofan.core.util.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;


/**
 * ClassName:CommonFunc
 *
 * @author fuxiaodong
 * @Date 2015-05-15
 */
public class CommonFunc {
    // javascript 标志
    public static final String JS = "javascript:";
    public static long tmpCount = 0;
    
    private static Logger logger= LoggerFactory.getLogger(CommonFunc.class);

    /**
     * @Author fuxiaodong
     * @Description //41类型：展示的转化
     * @Version: v1.8
     * @Date 9:36 2026/8/14
     * @Param [value, src]
     * @return java.lang.String
     */
    public static String getDisplayValueOf41(String sourceStr,String treeJson){
        if (!isValidJsonArray(treeJson)){
            return sourceStr;
        }

        if (!isValidJsonArray(sourceStr)){
            return sourceStr;
        }

        // 1. 解析源字符串，只取每个内层数组的最后一个元素（路径终点）
        List<String> endIds = new ArrayList<>();
        try {
            JSONArray outer = JSONArray.parseArray(sourceStr);
            for (int i = 0; i < outer.size(); i++) {
                JSONArray inner = outer.getJSONArray(i);
                if (inner.size() > 0) {
                    // 取最后一个元素（统一转为字符串）
                    String last = inner.get(inner.size() - 1).toString();
                    endIds.add(last);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("源字符串格式错误，需为二维嵌套数组", e);
        }

        // 2. 构建 value -> label 映射（递归遍历树，支持任意深度）
        Map<String, String> valueLabelMap = new HashMap<>();
        JSONArray treeArray = JSONArray.parseArray(treeJson);
        buildMap(treeArray, valueLabelMap);

        // 3. 按顺序获取 label，用 LinkedHashSet 去重（保留首次出现）
        Set<String> uniqueLabels = new LinkedHashSet<>();
        for (String id : endIds) {
            String label = valueLabelMap.get(id);
            if (label == null) {
                System.err.println("警告：未找到 value=" + id + " 对应的 label");
                continue;
            }
            uniqueLabels.add(label);
        }

        return String.join(",", uniqueLabels);
    }

    /**
     * 递归遍历 JSON 数组（包括嵌套 children），将每个节点的 value 和 label 存入 Map
     * 支持任意层级的 children 嵌套
     */
    private static void buildMap(JSONArray array, Map<String, String> map) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject node = array.getJSONObject(i);
            String value = node.get("value").toString();
            String label = node.getString("label");
            map.put(value, label);

            if (node.containsKey("children")) {
                JSONArray children = node.getJSONArray("children");
                if (children.size() > 0) {
                    buildMap(children, map);
                }
            }
        }
    }

    /**
     * @Author fuxiaodong
     * @Description //将文本内容解析成LinkedHashMap返回
     * @Version: v1.8
     * @Date 14:48 2026/8/5
     * @Param [s]:7天;15天;30天;60天;90天;
     * @return java.util.LinkedHashMap<java.lang.String,java.lang.String>
     */
    public static LinkedHashMap<String, String> parseMapList(String s){

        LinkedHashMap<String, String> mapTmp = new LinkedHashMap<String, String>();
        String sTmp = s.trim();
        String[] arrTmp = sTmp.split(";");
        for (int x = 0; x < arrTmp.length; x++) {
            String item = arrTmp[x].trim();
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            String[] arrItem = item.split("=>", 2);
            if (arrItem.length == 1) {
                mapTmp.put(arrItem[0], arrItem[0]);
            } else {
                mapTmp.put(arrItem[0], arrItem[1]);
            }
        }

        return mapTmp;
    }

    /**
     * @Author fuxiaodong
     * @Description ////去除连续的指定字符（只保留一个），并删除末尾的所有指定字符
     * 例如：1,2,4,,55,,,结果：1,2,4,55
     * @Version: v1.8
     * @Date 11:24 2025/11/12
     * @Param [str, targetChar]
     * @return java.lang.String
     */

    public static String removeConsecutiveChars(String str, char targetChar) {

        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        boolean lastWasTarget = false;

        // 遍历字符串，处理连续的指定字符
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);

            if (currentChar == targetChar) {
                // 如果是第一个遇到的指定字符，则添加
                if (!lastWasTarget) {
                    sb.append(currentChar);
                    lastWasTarget = true;
                }
                // 如果已经遇到过了，则跳过（不添加）
            } else {
                // 其他字符正常添加
                sb.append(currentChar);
                lastWasTarget = false;
            }
        }

        // 去除末尾的指定字符
        String result = sb.toString();
        if (result.length() > 0 && result.charAt(result.length() - 1) == targetChar) {
            // 找到最后一个非指定字符的位置
            int lastNonTargetIndex = result.length() - 1;
            while (lastNonTargetIndex >= 0 && result.charAt(lastNonTargetIndex) == targetChar) {
                lastNonTargetIndex--;
            }
            result = result.substring(0, lastNonTargetIndex + 1);
        }

        return result;
    }

    public static String getSendtoDept(Map<String,String> mapData){
        String sendToUser=mapData.get("SENDTO_USER").trim();
        if (StringUtils.isNotEmpty(sendToUser)){
            return sendToUser;
        }

        try(JdbcDao jdbcDao=new JdbcDao()) {
            String sql = String.format("select DEPARTMENTID from sys_flow_exec where  " +
                            "flow_node_id='%s' and flow_instance_id='%s' and exec_action='10'",
                    mapData.get("FLOW_NODE_ID_EXEC").trim(),
                    mapData.get("FLOW_INSTANCE_ID").trim());
            String deptId = jdbcDao.getFieldValue(sql, "DEPARTMENTID");
            String result = "";
            if (!StringUtils.isEmpty(deptId)) {
                sql = String.format("select  WM_CONCAT(departmentname) AS departmentname  from sys_DEPARTMENTs " +
                        "where DEPARTMENTID in (%s)", deptId);
                result = jdbcDao.getFieldValue(sql, "DEPARTMENTNAME");
            }

            jdbcDao.close();
            return result;
        }
    }

    /**
     * @Author fuxiaodong
     * @Description //判断一个字符串是否为合法JSON格式
     * @Version: v1.8
     * @Date 14:23 2025/9/9
     * @Param [jsonStr]
     * @return boolean
     */
    public static boolean isValidJson(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return false; // 空字符串或null直接返回false
        }
        try {
            Object result = JSONObject.parseObject(jsonStr); // 尝试解析
            return true; // 解析成功，是有效的JSON
        } catch (Exception e) {
            return false; // 解析失败，捕获异常并返回false
        }
    }

    /**
     * @Author fuxiaodong
     * @Description //难证是否为JSOANARRAY
     * @Version: v1.8
     * @Date 9:42 2026/8/14
     * @Param [jsonStr]
     * @return boolean
     */
    public static boolean isValidJsonArray(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return false; // 空字符串或null直接返回false
        }
        try {
            Object result = JSONArray.parseArray(jsonStr); // 尝试解析
            return true; // 解析成功，是有效的JSON Array
        } catch (Exception e) {
            return false; // 解析失败，捕获异常并返回false
        }
    }

    //获取不带电压等级的变电站名
    public static String getSiteNameNovol(String siteName){
        String result=siteName.toUpperCase(Locale.ROOT).replace("110KV","").replace("220KV","").replace("500KV","");
        result=result.replace("高步","高埗").
                replace("道窖","道滘");
        return result;
    }

    //母线名称替换
    public static String replaceBusbarName(String busbarName){
        return busbarName.replace("#1M母线","10kVI段")
                .replace("#2甲M母线","10kVII甲段")
                .replace("#2乙M母线","10kVII乙段")
                .replace("#3M母线","10kVIII段")
                .replace("ⅠM母线","10kVI段")
                .replace("Ⅱ甲M母线","10kVII甲段")
                .replace("Ⅱ乙M母线","10kVII乙段")
                .replace("1M母线","10kVI段")
                .replace("2M甲母线","10kVII甲段")
                .replace("2M乙母线","10kVII乙段");
    }

    public static double strToDouble(String s){
        if (StringUtils.isEmpty(s)){
            return 0;
        }

        try{
            double d=Double.valueOf(s);
            return d;
        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }

    }

    public static int strToInt(String s){
        return (int)CommonFunc.strToDouble(s);
    }

    public static int getSysYear() {
        Calendar date = Calendar.getInstance();
        return  date.get(Calendar.YEAR);
    }

    //2个字符串相加
    public static String additionStrToDouble(String s1,String s2){
        double d=CommonFunc.strToDouble(s1)+CommonFunc.strToDouble(s2);
        return String.valueOf(d);
    }

    //获取流程的默认值
    public static String getFlowAutoNo(String format,String tableName,String fieldName) {
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String result = "";
            String ymd = LocalDate.now().toString().replaceAll("-", "");
            String ym = ymd.substring(0, 6);
            String tmp = format.replace("{YMD}", ymd).replace("{YM}", ym).replace("{no}", "");

            String sql = String.format("select max(%s) %s from %s where %s like '%s%%'", fieldName, fieldName, tableName, fieldName, tmp);
            String lastNo = jdbcDao.getFieldValue(sql, fieldName);
            if (StringUtils.isEmpty(lastNo)) {
                //无编号，从第1个开始
                result = tmp + "001";
            } else {
                //有编号，续编
                String last = lastNo.substring(lastNo.length() - 3, lastNo.length());
                if (CommonFunc.isNumeric(last)) {
                    result = tmp + String.valueOf(Integer.valueOf(last) + 1001).substring(1, 4);
                } else {
                    result = tmp + "001";
                }
            }

            jdbcDao.close();

            return result;
        }
    }

    public static ArrayList<ArrayList<Object>> splitArrayList(ArrayList<Object> list,int num){
        ArrayList<ArrayList<Object>> listResult=new ArrayList<ArrayList<Object>>();

        ArrayList<Object> listItem=new ArrayList<Object>();
        for (int i=0;i<list.size();i++){
            listItem.add(list.get(i));
            if ((i+1)%num==0){
                listResult.add(listItem);
                listItem=new ArrayList<Object>();
            }
        }

        if (list.size()%num>0){
            listResult.add(listItem);
        }

        return listResult;
    }

    /*
    * 获取EFILE的路径前缀
    * 参数:String filepath,String sourceSystem:通知标题
    * String sysConfig:通知内容
    * 返回数据:
    *{
        路径
    };
    */
    public static String getEfilePathBySourceSystem(String filePath,String sourceSystem,SysConfig sysConfig){
        if (sourceSystem.equals("oms")){
            return filePath;
        }else if (sourceSystem.equals("dkm")){
            return sysConfig.getDkmUploadPath()+"/"+filePath;
        }else{
            return filePath;
        }
    }
    /*
    * 发送通知消息
    * 参数:String biaoti:通知标题
    * String neirong:通知内容
    * String leixing:通知类型
    * String manNoticeTo:被通知人的ID
    * String noticeInfoUrl:通知消息的URL
    * 返回数据:
    *{
        发送通知消息的结果,boolean
    };
    */
    public static boolean sendNotice(String biaoti,String neirong,String leixing,String manNoticeTo,String noticeInfoUrl){
        SysNoticeDAO sysNoticeDAO=new SysNoticeDAO();
        SysNotice sysNotice=new SysNotice();

        try(JdbcDao jdbcDao=new JdbcDao()) {

            sysNotice.setNoticeId(jdbcDao.getSeqNextVal());
            sysNotice.setCreatetime(CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
            sysNotice.setCreateuser(Eval.getLoginName());
            sysNotice.setBiaoti(biaoti);
            sysNotice.setNeirong(neirong);
            sysNotice.setShijian(CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
            sysNotice.setLeixing(leixing);
            sysNotice.setTongzhiduixiang(Eval.getLoginUserId());
            sysNotice.setManNoticeTo(manNoticeTo);
            sysNotice.setNoticeInfoUrl(noticeInfoUrl);

            boolean result = sysNoticeDAO.saveNew(sysNotice);

            jdbcDao.close();

            return result;
        }
    }

    /*
    * 格式化文件大小
    * 参数:formatFileSize
    * 返回数据:
    *{
        返回名称连接串
    };
    */
    public static String formatFileSize(String fileSize){
        if (!CommonFunc.isNumeric(fileSize)){
            return "";
        }

        long size=Long.valueOf(fileSize);

        if (size>1024*1024*1024){
            return new Formatter().format("%.1f",Math.round(size/1024/1024/1024))+"G" ;
        }else if (size>1024*1024){
            return String.valueOf(Math.round(size/1024/1024))+"M" ;
        }else if (size>1024){
            return String.valueOf(Math.round(size/1024))+"K" ;
        }else{
            return String.valueOf(size)+"B" ;
        }
    }

    /*
    * 将对应的部门ID转成部门名称
    * 参数:listItem
    * 返回数据:
    *{
        返回名称连接串
    };
    */
    public static String getDepartmentNames(String ids){
        if (StringUtils.isEmpty(ids)){
            return "";
        }

        String sql="select wm_concat(departmentname) NAMES from sys_departments where departmentid in ("+ids+")";
        try(JdbcDao jdbcDao=new JdbcDao()){
            String result= jdbcDao.getFieldValue(sql,"NAMES");
            jdbcDao.close();

            return result;
        }
    }

    /*
    * 将对应的部门ID转成部门名称
    * 参数:listItem
    * 返回数据:
    *{
        返回名称连接串
    };
    */
        public static String getUserNames(String ids){
            if (StringUtils.isEmpty(ids)){
                return "";
            }

            String sql="select wm_concat(username) NAMES from sys_users where userid in ("+ids+")";
            try(JdbcDao jdbcDao=new JdbcDao()) {
                String result= jdbcDao.getFieldValue(sql, "NAMES");
                jdbcDao.close();
                return result;
            }
        }

    /*
    * 将ArrayList<map>转为<map>arrayList
    * 参数:listItem
    * 返回数据:
    *{
        返回MAP列表
    };
    */
    public static Map<String,ArrayList<String>> sqlresultRowToCol(ArrayList<HashMap<String, String>> listItem){
        Map<String,ArrayList<String>> result=new HashMap<String,ArrayList<String>>();

        for(int i=0;i<listItem.size();i++){
            HashMap<String, String> mapItem=listItem.get(i);

            for(Map.Entry<String,String> entry:mapItem.entrySet()){
                if (i==0) {
                    result.put(entry.getKey(), new ArrayList<String>());
                }

                result.get(entry.getKey()).add(entry.getValue());
            }

        }

        return result;
    }

    /*
    * 从MAP中执行多条SQL语句,并返回到map中
    * 参数:MAP
    * 返回数据:
    *{
        返回MAP列表
    };
    */
    public static Map<String,Object> execSqlList(Map <String,String> mapSql){
        try(JdbcDao jdbcDao=new JdbcDao()) {
            Map<String, Object> mapResult = new HashMap<String, Object>();

            for (String key : mapSql.keySet()) {
                String sql = mapSql.get(key);
                if (StringUtils.isNotEmpty(sql)) {
                    ArrayList<HashMap<String, String>> listItem = jdbcDao.getResult(sql);
                    mapResult.put(key, listItem);
                } else {
                    mapResult.put(key, null);
                }

            }

            jdbcDao.close();

            return mapResult;
        }
    }

    //读取消息提示文件根据关键字获取配置值
    public static String getConfigByKey(String key) {
        try {
            if (SpringUtils.applicationContext!=null) {
                return SpringUtils.getProperty(key);
            }

            CommonLogger.getInstance().info("getConfigByKey:test!");
            //此处在未加载springboot上下文时使用
            //例如：一些main方法或@test方法在快速调试方法功能时，需读取配置文件时使用
            InputStream in = DataSource.class.getClassLoader().getResourceAsStream(
                    "application-"+SpringUtils.getCurrentProfile()+".yml");

            Properties prop = new Properties();
            prop.load(in);

            if (prop.containsKey(key.toLowerCase())) {
                String value=prop.getProperty(key.toLowerCase());
                value=new String(value.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
                return value;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    public static String getMapValueByKey(String mapStr,Object key){
        String[] arrList=mapStr.split(";");
        String sKey=key.toString();
        for (int i=0;i<arrList.length;i++){
            String[] arrTmp=arrList[i].split("=>",2);
            if (arrTmp[0].equals(sKey)){
                if (arrTmp.length==2){
                    return arrTmp[1];
                }

                return sKey;
            }
        }

        return sKey;
    }

    //读取消息提示文件根据关键字获取配置值
    public static String getMsgByKey(String key) throws Exception{
        try {
            InputStream in = DataSource.class.getClassLoader()
                    .getResourceAsStream("msg.properties");

            Properties prop = new Properties();
            prop.load(in);

            if (prop.containsKey(key.toLowerCase())) {

                String value=prop.getProperty(key.toLowerCase());
                value=new String(value.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);

                return value;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    //读取数据库SQL配置文件根据关键字获取SQL语句
    public static String getSqlByKey(String key) throws Exception{
        try{
            InputStream in = DataSource.class.getClassLoader()
                    .getResourceAsStream("sql.properties");

            Properties prop = new Properties();
            prop.load(in);

            if (prop.containsKey(key.toLowerCase())) {
                return prop.getProperty(key.toLowerCase());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    //获取目录下面的所有文件
    public static String getFileList(String path,String split){
        String result="";

        if (StringUtils.isEmpty(path)){
            return null;
        }

        File file=new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] listFile=file.listFiles();
                for(int i=0;i<listFile.length;i++){
                    String fullName=path+"/"+listFile[i].getName();
                    result=(result.isEmpty())?fullName:result+split+fullName;
                }
                return result;
            } else {
                return path;
            }
        }

        return null;
    }

    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (isEncoding(str, encode)) { // 判断是不是GB2312
                return encode;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (isEncoding(str, encode)) { // 判断是不是ISO-8859-1
                return encode;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (isEncoding(str, encode)) { // 判断是不是UTF-8
                return encode;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (isEncoding(str, encode)) { // 判断是不是GBK
                return encode;
            }
        } catch (Exception exception3) {
        }
        return ""; // 如果都不是，说明输入的内容不属于常见的编码格式。
    }

    public static boolean isEncoding(String str, String encode) {
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }


    //所取方法(包括父亲类)
    public static Method getDeclaredMethodWithParent(Class targetClass, String methodName, Class<?>... parameteTypes)  {
        Boolean doneFlag=false;
        Class curClass=targetClass;
        Method declaredMethod=null;
        while (!doneFlag && curClass!=Object.class){
            try {
                declaredMethod = curClass.getDeclaredMethod(methodName, parameteTypes);
            } catch (NoSuchMethodException e) {
                doneFlag=false;
                curClass=curClass.getSuperclass();
                continue;
            }
            doneFlag=true;
        }
        if(declaredMethod==null){
            try {
                throw new NoSuchMethodException( methodName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return declaredMethod;

    }

    //将类似于SYS_USERS转换成SysUsers
    public static String changeWord(String s){
        String[] arrayClassName=s.split("_");

        for (int k = 0; k < arrayClassName.length; k++) {
            String item=StringUtils.upperCase(arrayClassName[k].substring(0,1))+
                    StringUtils.lowerCase(arrayClassName[k].substring(1,arrayClassName[k].length()));
            arrayClassName[k]=item;
        }
        return StringUtils.join(arrayClassName,"");
    }

    //将类似于SYS_USERS转换成sysUsers
    public static String changeWord(String s,boolean firstToLower){
        String tmp=CommonFunc.changeWord(s);

        if (firstToLower){
            tmp= tmp.substring(0,1).toLowerCase()+tmp.substring(1);
        }

        return tmp;
    }

    //字符串null转为空
    public static String nullToSpace(String s){
        return Optional.ofNullable(s).orElse("");
    }

    //字符串null转为空
    public static String nullToSpace(Object s){
        if (s==null){
            return "";
        }
        return String.valueOf(s);
    }

        //根据类名创建新类
    public static Object createTableInstance(String className){
        try {
            String first=className.substring(0,3);
            String end=className.substring(className.length()-3,className.length());

            String type=(end.equals("DAO"))?"dao":"model";

            String classNameFull;

            if (first.equals("Sys")){
                classNameFull="com.zhuofan.core.orm."+type+"."+className;
            }else{
                classNameFull="com.zhuofan.oms.orm."+type+"."+className;
            }

            Class clz = Class.forName(classNameFull);
            Object obj = clz.newInstance();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //检查是否登录
    public static boolean checkIsLogined(HttpServletRequest request) {
        try {
            String username = request.getSession().getAttribute(SessionParams.CurrentUserName)
                    .toString();
            String password = request.getSession().getAttribute(SessionParams.CurrentUserPas)
                    .toString();


            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                System.out.println("username: " + username + ", password: " + password);
                return false;
            }

            try(JdbcDao jdbcDao = new JdbcDao()) {
                SysUsersDAO userDao = new SysUsersDAO();
                SysUsers sysUser = (SysUsers) userDao.getByUserNamePass(username,
                        password);

                jdbcDao.close();
                if (sysUser == null) {
                    System.out.println("用户表中不存在当前用户，username: " + username + ", password: " + password);
                    return false;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //返回成功的信息,不带server_key和server_iv
    public static Map<String,Object> makeSussResult(Map<String,Object> data){
        return makeSussResult(data,null);
    }

    //返回成功的信息,不带server_key和server_iv
    public static Map<String,Object> makeSussResultList(ArrayList<Map<String,Object>> data){
        Map<String,Object> mapResult=new HashMap<>();

        mapResult.put("result","1");
        mapResult.put("data",data);

        return mapResult;
    }

    //返回成功的信息,自动带server_iv和server_key
    public static Map<String,Object> makeSussResult(Map<String,Object> data,ServerKeyIv serverKeyIv){
        Map<String,Object> mapResult=new HashMap<>();

        if (serverKeyIv!=null){
            String serverKey=serverKeyIv.getServerKey();
            String serverIv=serverKeyIv.getServerIv();

            data.put("server_key",serverKey);
            data.put("server_iv",serverIv);
        }

        mapResult.put("result","1");
        if (data!=null){
            data.put("processEndTime",CommonFunc.unix2Date(CommonFunc.unixTimeStr()));
        }
        mapResult.put("data",data);

        return mapResult;
    }

    //返回错误信息
    public static Map<String,Object> makeErrResult(String errCode) {
        return makeErrResult(errCode,"");
    }

    //返回错误信息
    public static Map<String,Object> makeErrResult(String errCode,String extraInfo) {
        Map<String,Object> mapResult=new HashMap<>();

        try {
            String errMsg = "错误"+errCode+":"+CommonFunc.getMsgByKey("err." + errCode);
            if (StringUtils.isNotEmpty(extraInfo)){
                errMsg+="【"+extraInfo+"】";
            }
            mapResult.put("result","0");
            mapResult.put("err_code",errCode);
            mapResult.put("err_msg",errMsg);

        }catch (Exception e) {
            e.printStackTrace();
        }

        return mapResult;
    }

    //判断加密(POST)的数据,并解析返回
    public static Map<String,Object> checkServerPost(Map<String,Object> params,SysConfig sysConfig){

        String server_iv=params.get("_server_iv_").toString();
        String server_key=params.get("_server_key_").toString();
        String client_sign=params.get("_client_sign_").toString();

        logger.debug(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":_server_iv_:"+server_iv);
        logger.debug(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":_server_key_:"+server_key);
        logger.debug(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":_client_sign_:"+client_sign);

        Map<String, Object> result = new HashMap<String, Object>();
        String err_msg="";

        //1.先判断server_key文件是否存在,如果不存在,则返回错误
        String fileName=sysConfig.getTempfilePath()+"/"+server_key;
        logger.debug(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+": server key fileName:"+fileName);

        File writeName = new File(fileName);
        if(!writeName.exists()) {
            logger.debug(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+": server key fileName:"+fileName +" not exists ");

            result.put("result", "0");
            result.put("err_code", "20001");
            result.put("err_msg", err_msg);
        }else{
            //2.从server_key中计取server_iv,与传入的server_iv进行比较,如果不存在,则返回错误
            String server_iv_t=CommonFunc.readTxtFile(fileName);
            logger.debug(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+": server key fileName:"+fileName +" content: "+server_iv_t);

            if (!server_iv_t.equals(server_iv)){
                result.put("result","0");
                result.put("err_code","20002");
                result.put("err_msg",err_msg);
            }

            //3.用server_iv和key,解析client_sign,看是否可以解析成功
            String decrypted = AesUtil.decrypt(server_key,server_iv,client_sign);
            logger.debug(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":decrypted:"+decrypted);

            if (StringUtils.isBlank(decrypted)){
                result.put("result","0");
                result.put("err_code","20003");
                result.put("err_msg",err_msg);
            }

            //4.解析字符串
            String[] items = decrypted.split("&");
            Map<String,String> mapVar=new HashMap<String,String>();

            for(int i=0;i<items.length;i++){
                String[] keyValue = items[i].split("=",2);
                if (keyValue.length>1){
                    //此得需考虑多个兼容性问题
                    if (mapVar.containsKey(keyValue[0])){
                        String oldValue=mapVar.get(keyValue[0]);
                        String newValue= URLDecoder.decode(keyValue[1]);
                        newValue=(StringUtils.isEmpty(oldValue))?newValue:oldValue+","+newValue;
                        mapVar.replace(keyValue[0],newValue);
                    }else {
                        mapVar.put(keyValue[0], URLDecoder.decode(keyValue[1]));
                    }
                }else{
                    mapVar.put(keyValue[0],"");
                }
            }

            result.put("result","1");
            result.put("vars",mapVar);

        }

        return result;
    }

    //返回文件的最后修改时间
    public  static String getFileLastTime(String fileName){
        File file=new File(fileName);

        if (file.exists()){
            long lastTime=file.lastModified();
            Date date=new Date(lastTime);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(date);
        }

        return "";
    }

    //读取GBK格式的文本文件
    public static String readGbkFile(String fileName){
        File file = new File(fileName);
        Charset charset = Charset.forName("GBK");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);

            InputStreamReader reader = new InputStreamReader(inputStream, charset);
            int c;
            StringBuilder buf = new StringBuilder();
            while ((c = reader.read()) != -1) {
                buf.append((char) c);
            }
            return buf.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return "";
    }

    //从文本文件中读取字符串
    public static String readTxtFile(String fileName){
        String content ="";

        try {
            content=new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    //把字符串写入文本文件
    public static boolean writeTxtFile(String fileName,String data){
            File writeName = new File(fileName);
            if (writeName.exists()){
                writeName.delete();
            }
            if(!writeName.exists()) {
                try {
                    writeName.createNewFile();
                    FileWriter writer = new FileWriter(writeName);
                    BufferedWriter out = new BufferedWriter(writer);
                    out.write(data);
                    out.flush(); // 把缓存区内容压入文件
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

        return true;
    }


    public static String getRandom(int len) {
        Random rd = new Random();
        StringBuilder n= new StringBuilder();
        int getNum;
        for (int i =0; i<len;){
            getNum = rd.nextInt(10);
            String num = Integer.toString(getNum);
            //保证第一个数字大于零
            if (n.length() == 0){
                if (getNum>0){
                    n.append(num);
                    i++;
                }
            }else {
                n.append(num);
                i++;
            }
        }
        return n.toString();
    }

    /*
    //生成随机数
    public static String getRandom(){
        SecureRandom instance = null;
        try {
            instance = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] bytes = instance.generateSeed(8);
        Random random = new Random();

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        long aLong = buffer.getLong();
        random.setSeed(aLong);
        return String.valueOf(random.nextLong());
    }
    */

    // 判断是否为数字
    public static boolean isNumeric(String str) {
        if (str==null) {
            return false;
        }

        if(str.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String encodingString(String str, String from, String to) {
        String result = str;
        try {
            result = new String(str.getBytes(from), to);
        } catch (Exception e) {
            result = str;
        }
        return result;
    }

    /*
     * inArray 检查字符串是否在数组中
     *
     * @param Object obj:要检查的字符串
     *
     * @param Object[] array：数组
     *
     * @return boolean
     */
    public static boolean inArray(Object obj, Object[] array) {
        int length = array.length;

        for (int i = 0; i < length; i++) {
            if(obj.equals(array[i])) {
                return true;
            }
        }

        return false;
    }


    /**
     * addArrayToOption 将字符串数组添加到Option中
     * @return String
     */
    public static String addArrayToOption(String[] array, String value,
                                          String strFirst) {
        value=value==null?"":value;

        int length = array.length;
        StringBuffer sResult = new StringBuffer();

        if(strFirst.isEmpty()) {
            sResult.append("<option value=''>" + strFirst + "</option>");
        }

        for (int i = 0; i < length; i++) {
            String arrayItem[] = array[i].split(":", 2);
            String sValue;
            String sText;

            if(arrayItem.length < 2) {
                sValue = array[i];
                sText = sValue;
            } else {
                sValue = arrayItem[0];
                sText = arrayItem[1];
            }
            String selected = (value.equals(sValue)) ? "selected" : "";
            sResult.append("<option value='" + sValue + "' " + selected + ">"
                    + sText + "</option>");
        }

        return sResult.toString();
    }

    /**
     * <pre>
     * 將map以key=>value形式生成option
     * </pre>

     */
    public static String addMapToOption(HashMap<String, String> map,
                                        String value, String strFirst) {
        int length = map.size();
        StringBuffer sResult = new StringBuffer();

        if(strFirst.isEmpty()) {
            sResult.append("<option value=''>" + strFirst + "</option>");
        }

        for (Map.Entry<String, String> entryTable : map.entrySet()) {
            String sValue = entryTable.getKey();
            String sText = entryTable.getValue();

            String selected = (value.equals(sValue)) ? "selected" : "";
            sResult.append("<option value='" + sValue + "' " + selected + ">"
                    + sText + "</option>");
        }

        return sResult.toString();
    }

    /**
     * @Title: addSqlResultToOption
     * @Description: 执行SQL查询，并将结果以option形式返回
     * @param sql
     *            ：执行的SQL语句
     * @param fldnValue
     *            ：option.value字值
     * @param fldnText
     *            :option.text字段
     * @param value
     *            ：option.value字段
     * @param strFirst
     *            :第一个选择项
     * @return 设定文件
     * @return String 返回类型
     * @throws
     */
    public static String addSqlResultToOption(String sql, String fldnValue,
                                              String fldnText, String value, String strFirst) {
        StringBuffer sResult = new StringBuffer();

        if(strFirst.isEmpty()) {
            sResult.append("<option value=''>" + strFirst + "</option>");
        }

        try(JdbcDao jdbcDao = new JdbcDao()) {
            List<HashMap<String, String>> list = jdbcDao.getResult(sql);

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    HashMap<String, String> item = (HashMap<String, String>) list
                            .get(i);
                    String selected = (value.equals(item.get(fldnValue))) ? "selected"
                            : "";
                    sResult.append("<option value='" + item.get(fldnValue) + "' "
                            + selected + ">" + item.get(fldnText) + "</option>");
                }
            }

            jdbcDao.close();

            return sResult.toString();
        }
    }

    // 连接数组
    public static String join(String[] arr, String joinStr) {
        int length = arr.length;

        StringBuffer buf = new StringBuffer("");
        if(length > 0) {
            buf.append(arr[0]);
        }

        for (int i = 1; i < length; i++) {
            buf.append(joinStr);
            buf.append(arr[i]);
        }

        return buf.toString();
    }

    // 获取当前时间
    public static long unixTime() {
        return System.currentTimeMillis() / 1000;
    }

    // 获取当前时间
    public static String unixTimeStr() {
        return String.valueOf(CommonFunc.unixTime());
    }

    // 将number（unix时间)置换成java日期格式(dateTime)
    public static String unix2Date(String timestampString, String format) {
        if(!CommonFunc.isNumeric(timestampString)) return "";

        Long timestamp = Math.round(Double.parseDouble(timestampString)) * 1000;
        if(timestamp <= 0) return "";

        String date = new SimpleDateFormat(format).format(new Date(timestamp));
        return date;
    }

    // 将number（unix时间)置换成java日期格式(dateTime)
    public static String unix2Date(long timestamp, String format) {
        String date = new SimpleDateFormat(format).format(new Date(timestamp*1000));
        return date;
    }

    public static String date2String(Date time, String format) {
        return new SimpleDateFormat(format).format(time);
    }

    public static Date string2Date(String time, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(time);
    }

    // 将number（unix时间)置换成java日期格式(dateTime)
    public static String unix2Date(String timestampString) {
        return CommonFunc.unix2Date(timestampString, "yyyy-MM-dd HH:mm:ss");
    }

    // 将number（unix时间)置换成java日期格式(dateTime)
    public static String unix2Date(long timestamp) {
        return CommonFunc.unix2Date(String.valueOf(timestamp), "yyyy-MM-dd HH:mm:ss");
    }


    // 将日期格式转抱成unix时间
    public static long date2Unix(String timeString)  {
        try {
            long result=new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(timeString)
                    .getTime() / 1000;
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0L;
    }

    // 将日期格式转抱成unix时间
    public static long date2Unix(String timeString, String format) {
        try {
            return new SimpleDateFormat(format).parse(timeString).getTime() / 1000;
        } catch (ParseException e) {
            return 0;
        }
    }

    //根据日期获取周几
    public static String date2WeekDay(String timeString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(CommonFunc.parseStringToDate(timeString));

        // 获取星期几
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String[] chineseDaysOfWeek = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return chineseDaysOfWeek[dayOfWeek - 1];
    }

    /*
     * getRemortIP 获取访问者的真实IP地址
     *
     * @param HttpServletRequest request：请求对象
     *
     * @return String
     */
    public static String getRemortIP(HttpServletRequest request) {
        if(request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        }

        return request.getHeader("x-forwarded-for");
    }

    /*
     * equalsFirst 检查一个字符串是否在另一个字符串的开头
     *
     * @param String str
     *
     * @param String strFirst
     *
     * @return boolean
     */
    public static boolean equalsFirst(String str, String strFirst) {
        if(str.isEmpty()) return false;
        if(strFirst.isEmpty()) return false;

        return str.substring( 0, strFirst.length())
                .equals(strFirst);
    }

    /**
     * @Title: getHttpRequest
     * @Description:获取http request对象
     * @return HttpServletRequest
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static HttpServletRequest getHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            // 或者转成具体对象
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            return servletRequestAttributes.getRequest();

        }

        return null;
    }

    /**
     * @Title: getHttpVar
     * @Description:获取从http发送过来的变量值
     * @param varName
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getHttpVar(String varName) {
        HttpServletRequest request = CommonFunc.getHttpRequest();
        String sValue = request.getParameter(varName);
        if(StringUtils.isEmpty(sValue)) {
            sValue = (String) request.getAttribute(varName);
            if(StringUtils.isEmpty(sValue)) {
                return "";
            }
        }

        return sValue;
    }

    /**
     * @Title: getHttpVarList
     * @Description:获取表单传递过来的数组选项
     * @param varName
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String[] getHttpVarList(String varName) {
        HttpServletRequest request = getHttpRequest();

        String[] sValue = request.getParameterValues(varName);

        return sValue;
    }

    /**
     * @Title: getRequestURI
     * @Description:获取当前脚本路径
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getRequestURI() {
        HttpServletRequest request = getHttpRequest();
        return request.getScheme() + "://" + request.getHeader("host")
                + getHttpRequest().getRequestURI();
    }

    /**
     * @Title: getParameterMap
     * @Description:
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static HashMap getParameterMap() {
        HttpServletRequest request = getHttpRequest();
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        HashMap returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj) {
                value = "";
            } else if(valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * @Title: base64Encode
     * @Description:base64编码
     * @param
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String base64Encode(String str) {
        return new String(Base64.encodeBase64(str.getBytes()));
    }

    /**
     * @Title: base64Decode
     * @Description:base64解码
     * @param str
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String base64Decode(String str) {
        return new String(Base64.decodeBase64(str));
    }

    /**
     * @Title: getNotNullString
     * @Description:将字符串为null的，直接返回空字符串
     * @param str
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getNotNullString(Object str) {
        return (str == null) ? "" : str.toString();
    }

    /**
     * @Title: getNotNullString
     * @Description:将字符串为null的，直接返回空字符串
     * @param str
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getListIsNull(Object str) {
        return (str == null) ? "" : str.toString();
    }

    /**
     * @Title: getNextTmpValue
     * @Description:获取下一个临时计数值
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static synchronized long getNextTmpValue() {
        tmpCount = tmpCount + 1;
        return tmpCount;
    }

    /**
     * @Title: getSession
     * @Description:获取session信息
     * @param name
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getSession(String name) {
        HttpServletRequest request = getHttpRequest();
        HttpSession session = request.getSession();

        if(session == null) {
            return "";
        }

        return getNotNullString(session.getAttribute(name));

    }

    public static String getSum(HashMap<String, String> map, String str) {
        String result = "0";
        if(str.indexOf(",") > -1) {
            String[] stra = str.split(",");
            for (int i = 0; i < stra.length; i++) {
                String a = (map.containsKey(stra[i])) ? map.get(stra[i]) : "0";
                a = (a.length() > 0) ? a : "0";
                result = Integer.valueOf(result) + Integer.valueOf(a) + "";
            }
        }
        return result;
    }

    /**
     * <pre></pre>
     *
     * @param map
     * @param
     * @param actiontype add的情况下，params用来判断输入的类型， view 的情况下
     * @param type 0默认为0的输入框，1长400的输入框，2只读，3长70的输入框，4大文本
     * @return
     */
    public static String getInputValue(HashMap<String, String> map, String keyName, String actiontype, int type) {
        String readonly = (actiontype.equals("add")) ? "" : "readonly";
        String value = (map.containsKey(keyName)) ? map.get(keyName) : "";
        value = (value == null) ? "" : value;
        String css = "text-align:center;border-top-style:none;border-left-style:none;border-right-style:none;border-bottom-style:solid #cccccc 2px;box-shadow:none'";
        if(actiontype.equals("add")) {
            switch (type) {
                case 0: {// 0数字
                    StringBuffer sb = new StringBuffer();
                    sb.append(" onkeyPress='keyPress_Number(this);'");
                    sb.append(" onkeyUp='keyUp_Number(this);'");
                    sb.append(" ononBlur='onBlur_Number(this);'");
                    value = value.isEmpty() ? "0" : value;

                    value = "<input type='text' value='"
                            + value + "' style='width:100px;" + css + " name='"
                            + keyName + "' " + sb.toString() + " id='"+ keyName + "'/>";
                    break;
                }
                case 1: {
                    value = "<input type='text' value='" + value
                            + "'  style='width:400px;" + css + " name='" + keyName + "' "
                            + " id='"+ keyName + "'/>";
                    break;
                }
                case 4: {
                    value = "<textarea style='text-align:center;width:100%' rows='10' name='"
                            + keyName
                            + "' "
                            + " id='"
                            + keyName
                            + "'>"
                            + value
                            + "</textarea>";
                    break;
                }
            }

        } else {
            if(type == 0) {
                value = value.isEmpty() ? "0" : value;
            }

            if(!value.isEmpty()){
                value = "<Strong>" + value + "</Strong>";
            }

        }
        return value;
    }

    /**
     * <pre>
     * 用于总结与计划报表中，文字部分，数字录入
     * </pre>
     *
     * @param map
     *            取键名为str1，str2的值
     * @param str1
     *            键名
     * @param str2
     *            键名
     * @param type
     *            1：之和，2：除数
     * @return String
     */
    public static String getValues(HashMap<String, String> map, String str1,
                                   String str2, int type) {
        String stra = (map.containsKey(str1)) ? map.get(str1) : "0";
        String strb = (map.containsKey(str2)) ? map.get(str2) : "0";
        if(strb == null || strb == ""){
            strb = "0";
        }

        if(stra == null || stra == ""){
            stra = "0";
        }
        String result = null;
        if(type == 1) {
            result = Integer.valueOf(stra) + Integer.valueOf(strb) + "";
        } else {
            result = (Float.valueOf(strb) / (Float.valueOf(stra) + Float
                    .valueOf(strb))) * 100 + "";
            if(Integer.valueOf(strb) == 0 && Integer.valueOf(stra) == 0) {
                result = "0";
            } else {
                BigDecimal b = new BigDecimal(result);
                result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                        + "";
            }
            result = result + "%";
        }
        return result;
    }

    /**
     * 返回两个数的除值
     *
     * @param num
     * @param numCount
     * @return
     */
    public static double getBigDecimal(int num, int numCount) {
        if(numCount == 0) {
            return 0;
        } else {
            BigDecimal a = new BigDecimal(num * 100);
            BigDecimal b = new BigDecimal(numCount);

            BigDecimal c = new BigDecimal(a.divide(b, 2,
                    BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal d = new BigDecimal(1);
            return c.divide(d, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }

    /**
     * 返回两个数的差值
     *
     * @param num
     * @param numCount
     * @return
     */
    public static double getBigDecimal(double num, double numCount) {
        BigDecimal a = new BigDecimal(num);
        BigDecimal b = new BigDecimal(numCount);
        BigDecimal c = new BigDecimal(a.subtract(b).doubleValue());
        BigDecimal d = new BigDecimal(1);
        return c.divide(d, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 年月的格式和转换
     *
     * @param
     * @param type 1 当年当月，2 当年当月的下个月， 3去年当月, 4 当年的上个月
     * @param formate 1 yyyy年MM月；2 yyyyMM;3 yyyy-MM
     * @return
     */
    public static String getYearMonth(String ym, int type, int formate) {
        String y = ym.substring(0, 4);
        String m = ym.substring(4, 6);
        Calendar cal = Calendar.getInstance();

        if(type == 1) {// 当年当月
            cal.set(Integer.valueOf(y), Integer.valueOf(m) - 1, 1);
        } else if(type == 2) {// 当年当月的下个月
            cal.set(Integer.valueOf(y), Integer.valueOf(m), 1);
        } else if(type == 3) {// 去年当月
            cal.set(Integer.valueOf(y) - 1, Integer.valueOf(m) - 1, 1);
        } else if(type == 4) {// 去年当月
            cal.set(Integer.valueOf(y), Integer.valueOf(m) - 2, 1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月");
        if(formate == 2) {
            sdf = new SimpleDateFormat("yyyyMM");
        } else if(formate == 3){
            sdf = new SimpleDateFormat("yyyy-MM");
        }
        else if(formate == 4){
            sdf = new SimpleDateFormat("yyyy");
        }
        return sdf.format(cal.getTime());
    }

    /**
     * 将未指定格式的日期字符串转化成java.util.Date类型日期对象 <br>
     * Author：BluesLee <br>
     * CreateDate：2008-9-25 <br>
     * Modifier：BluesLee <br>
     * ModifyDate：2008-9-25 <br>
     * Version:1.1<br>
     * All right reserved.<br>
     *
     * @param date,待转换的日期字符串
     * @return
     * @throws ParseException
     */
    public static Date parseStringToDate(String date) throws ParseException{
        Date result=null;
        String parse=date;
        parse=parse.replaceFirst("^[0-9]{4}([^0-9])", "yyyy$1");
        parse=parse.replaceFirst("^[0-9]{2}([^0-9])", "yy$1");
        parse=parse.replaceFirst("([^0-9])[0-9]{1,2}([^0-9])", "$1MM$2");
        parse=parse.replaceFirst("([^0-9])[0-9]{1,2}( ?)", "$1dd$2");
        parse=parse.replaceFirst("( )[0-9]{1,2}([^0-9])", "$1HH$2");
        parse=parse.replaceFirst("([^0-9])[0-9]{1,2}([^0-9]?)", "$1mm$2");
        parse=parse.replaceFirst("([^0-9])[0-9]{1,2}([^0-9]?)", "$1ss$2");
        DateFormat format=new SimpleDateFormat(parse);
        result=format.parse(date);
        return result;
    }

    public void getCurrentPath() {
        File directory = new File("");// 参数为空
        String courseFile;
        try {
            courseFile = directory.getCanonicalPath();
            System.out.println(courseFile);
            System.out.println(courseFile.replaceAll("/", "\\"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileExtension(String fileName) {
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return  fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        else return "";
    }

    public static String getFileExtensionEx(String fileName,String fileName1) {
        String ext=getFileExtension(fileName);
        if (StringUtils.isEmpty(ext)){
            ext=getFileExtension(fileName1);
        }

        return ext;
    }

    public static void main(String[] args) {
        String source = "[[9,10],[9,11],[9,12]]";
        String treeJson = "[{\"value\":2,\"label\":\"四楼副楼东\",\"children\":[{\"value\":3,\"label\":\"主网主调副楼 402 门禁 1\"},{\"value\":4,\"label\":\"主网主调副楼四楼东玻璃门门禁 1\"},{\"value\":5,\"label\":\"主网主调副楼 407 门禁 1\"},{\"value\":6,\"label\":\"主网主调副楼 406 门禁 1\"},{\"value\":7,\"label\":\"主网主调副楼 406 门禁 2\"},{\"value\":8,\"label\":\"主网主调副楼 403 门禁 1\"}]},{\"value\":9,\"label\":\"四楼副楼西\",\"children\":[{\"value\":10,\"label\":\"主网主调副楼四楼西玻璃门门禁 1\"},{\"value\":11,\"label\":\"主网主调副楼 409 门禁 1\"},{\"value\":12,\"label\":\"主网主调副楼 412 门禁 1\"}]},{\"value\":13,\"label\":\"东莞供电局 -- 四楼主楼\",\"children\":[{\"value\":14,\"label\":\"主网主调主楼 401 门禁 1\"},{\"value\":15,\"label\":\"主网主调主楼 401 门禁 2\"},{\"value\":16,\"label\":\"主网主调主楼 401 门禁 3\"},{\"value\":17,\"label\":\"主网主调主楼 403 配网蓄电池室门禁 1\"},{\"value\":18,\"label\":\"主网主调主楼四楼电梯厅门禁 1\"},{\"value\":19,\"label\":\"主网主调主楼 409 门禁 1\"},{\"value\":20,\"label\":\"主网主调主楼 403 门禁 1\"},{\"value\":21,\"label\":\"主网主调主楼 409 门禁 2\"},{\"value\":22,\"label\":\"主网主调主楼 405 门禁 1\"}]},{\"value\":23,\"label\":\"东莞供电局 -- 五楼主楼\",\"children\":[{\"value\":24,\"label\":\"主网主调主楼 501 门禁 1\"},{\"value\":25,\"label\":\"主网主调主楼 511 门禁 3\"},{\"value\":26,\"label\":\"主网主调主楼 511 门禁 2\"},{\"value\":27,\"label\":\"主网主调主楼 511 门禁 1\"},{\"value\":28,\"label\":\"主网主调主楼 501 门禁 2\"},{\"value\":29,\"label\":\"主网主调主楼 503 门禁 1\"},{\"value\":30,\"label\":\"主网主调主楼 518 门禁 1\"},{\"value\":31,\"label\":\"主网主调主楼 517 门禁 1\"},{\"value\":32,\"label\":\"主网主调主楼 518 门禁 2\"}]}]";

        String result = getDisplayValueOf41(source, treeJson);
        System.out.println(result);
    }

    public static JsonType checkJsonType(String str) {
        if (str == null || str.trim().isEmpty()) {
            return JsonType.NEITHER;
        }

        String trimmed = str.trim();

        // 检查是否为 JSON 对象
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                JSONObject.parseObject(str);  // 尝试解析为对象
                return JsonType.OBJECT;
            } catch (JSONException e) {
                // 解析失败，继续检查数组
            }
        }

        // 检查是否为 JSON 数组
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                JSONArray.parseArray(str);    // 尝试解析为数组
                return JsonType.ARRAY;
            } catch (JSONException e) {
                // 解析失败
            }
        }

        // 检查是否是有效的 JSON 值（但不是对象或数组）
        try {
            Object parsed = JSONObject.parse(str);  // 尝试解析为任意 JSON
            if (parsed instanceof JSONObject) {
                return JsonType.OBJECT;
            } else if (parsed instanceof JSONArray) {
                return JsonType.ARRAY;
            }
        } catch (JSONException e) {
            // 不是有效的 JSON
        }

        return JsonType.NEITHER;
    }

    public enum JsonType {
        OBJECT("JSON对象"),
        ARRAY("JSON数组"),
        NEITHER("非法JSON");

        private final String description;

        JsonType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
