/**
 * com.zhuofan.core.eval
 *
 *
 *   ver     date           author
 * ======================================================
 *           2015-05-26      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.eval;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.orm.dao.SysDepartmentsDAO;
import com.zhuofan.core.orm.dao.SysUsersDAO;
import com.zhuofan.core.orm.model.SysDepartments;
import com.zhuofan.core.orm.model.SysUsers;
import com.zhuofan.core.util.SessionParams;
import com.zhuofan.core.util.SpringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 * ClassName:Eval 模拟编译器，动态执行JAVA语言类
 *
 * @author fuxiaodong
 * @Date 2015-05-25
 */
public class Eval {
    // eval 标志
    public static final String EVAL = "eval:";
    private HashMap mapRow;

    public Eval() {
        this.mapRow = new HashMap();
    }

    private ClassLoader classLoader;

    {
        String outDir = SpringUtils.getProperty("sys.eval-classpath");
        URL extraClassUrl;
        try {
            extraClassUrl = new File(outDir).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        classLoader = new URLClassLoader(new URL[]{extraClassUrl}, getClass().getClassLoader());
    }

    public Object eval(String str) throws Exception {
        String className = "C_" + DigestUtils.md5Hex(str);

        Class clazz;

        // 调用自定义类加载器加载编译在内存中class文件
        try {
            clazz = classLoader.loadClass(className);
        }catch (ClassNotFoundException e) {

            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append("import com.zhuofan.core.datasource.JdbcDao;\n");
            stringBuffer.append("import com.zhuofan.core.common.CommonFunc;\n");
            stringBuffer.append("import com.zhuofan.core.common.CommonObject;\n");
            stringBuffer.append("import java.util.HashMap;\n");
            stringBuffer.append("import java.util.LinkedHashMap;\n");
            stringBuffer.append("import com.zhuofan.core.eval.Eval;\n");
            stringBuffer.append("import com.zhuofan.core.orm.model.*;\n");
            stringBuffer.append("import com.zhuofan.core.orm.dao.*;\n");
            stringBuffer.append("public class " + className + "\n");
            stringBuffer.append("{\n");
            stringBuffer
                    .append("    public Object getEvalResult(HashMap mapRow) throws Exception\n");
            stringBuffer.append("    {\n");
            stringBuffer.append("        " + str + "\n");
            stringBuffer.append("    }\n");
            stringBuffer.append("}\n");

            System.out.println(stringBuffer.toString());

            clazz = new ZfClassLoader().findClass(className, stringBuffer.toString());
        }

        try{
            if(clazz == null) {
                return "";
            }

            Object newObj = clazz.newInstance();

            Method method = newObj.getClass().getMethod("getEvalResult",
                    new Class[]{ this.mapRow.getClass() });
            // 通过反射调用方法
            Object objResult = method.invoke(newObj, new Object[]
                    { this.mapRow });

            if(objResult == null) {
                return null;
            }

            return objResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Title: eval
     * @Description:
     * @param str
     * @param checkFlag
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public String eval(String str, boolean checkFlag) throws Exception {
        if(StringUtils.isEmpty(str)) return "";

        if(!StringUtils.substring(str, 0, EVAL.length()).equals(EVAL)) return str;

        return (String) eval(str.substring(EVAL.length()));
    }

    public String eval(String str, boolean checkFlag, HashMap mapRow)
            throws Exception {
        if(StringUtils.isEmpty(str)) return "";
        if(mapRow == null) return "";

        if(!StringUtils.substring(str, 0, EVAL.length()).equals(EVAL)) return str;

        this.mapRow = mapRow;

        return (String) eval(str.substring(EVAL.length()));
    }


    /**
     * @Title: getLoginName
     * @Description:获取当前登录的帐号姓名
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getLoginName(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            // 或者转成具体对象
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpSession session = request.getSession();
            return CommonFunc.getNotNullString(session.getAttribute(SessionParams.CurrentUserName));
        }

        return "";
    }

    /**
     * @Title: getEval_Row_Unix2Data
     * @Description:数据查询报表中，将一行的某个日期字段格式转成eval语法
     * @param fldn
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getEval_Row_Unix2Data(String fldn) {
        StringBuffer sbFunc = new StringBuffer();
        sbFunc.append("return CommonFunc.unix2Date(mapRow.get(\"" + fldn
                + "\").toString(),\"yyyy-MM-dd HH:mm\");");
        return Eval.EVAL + sbFunc.toString();
    }

    public static String getEval_Row_Unix2Data(String fldn, String format) {
        StringBuffer sbFunc = new StringBuffer();
        sbFunc.append("return CommonFunc.unix2Date(mapRow.get(\"" + fldn
                + "\",\"+format+\").toString());");
        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getHashMapValue
     * @描述 用于返回获取hashMap值的数据
     * @param mapName
     *            hashMap对象变量名
     * @param fieldName
     *            字段名
     * @return
     */
    public static String getHashMapValue(String mapName, String fieldName) {
        return Eval.EVAL + "return " + mapName + ".get(mapRow.get(\"" + fieldName + "\"));\n";
    }

    public static String getHashMapValueFromStr(String mapStr, String fieldName) {
        return Eval.EVAL + "return CommonFunc.getMapValueByKey(\""+mapStr+"\",mapRow.get(\"" + fieldName + "\"));\n";
    }

    /**
     *
     * @方法 getViewUrl
     * @描述 用于返回查看功能的java eval代码
     * @param pkName
     * @return
     */
    public static String getViewUrl(String pkName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:jsCommon.viewEfile('"+ "\"+mapRow.get(\"" + pkName + "\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getDownUrl
     * @描述 用于返回下载功能的java eval代码
     * @param dataname
     * @param param
     * @param pkName
     * @return
     */
    public static String getDownUrl(String dataname, String param, String pkName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:jsCommon.downloadEfile('"+ "\"+mapRow.get(\"" + pkName + "\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getAddUrl
     * @描述 用于返回新增数据功能的java eval代码
     * @param dataname
     * @param param
     * @return
     */
    public static String getAddUrl(String dataname, String param,String paramFldn) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:window.open('dataadd.html?dataname=" + dataname + "&actiontype=add&"+param+"=\"+mapRow.get(\"" + paramFldn + "\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getEditUrl
     * @描述 用于返回编辑功能的java eval代码
     * @param dataname
     * @param param
     * @param pkName
     * @return
     */
    public static String getViewDataUrl(String dataname, String param, String pkName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:window.open('dataview.html?dataname=" + dataname + "&"+param+"&actiontype=view&pk_id=\"+mapRow.get(\"" + pkName + "\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getEditUrl
     * @描述 用于返回编辑功能的java eval代码
     * @param dataname
     * @param param
     * @param pkName
     * @return
     */
    public static String getEditUrl(String dataname, String param, String pkName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:window.open('dataadd.html?dataname=" + dataname + "&"+param+"&actiontype=edit&pk_id=\"+mapRow.get(\"" + pkName + "\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    public static String getEditUrlWithHtml(String pageUrl, String dataname, String param, String pkName) {
        StringBuffer sbFunc = new StringBuffer();
        sbFunc.append("String url=\"javascript:window.open('" + (StringUtils.isNotBlank(pageUrl) ? pageUrl : "dataadd.html") +"?dataname=\"+mapRow.get(\"" + dataname + "\")+\"&"+param+"&actiontype=edit&pk_id=\"+mapRow.get(\"" + pkName + "\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getEditAddUrl
     * @描述 用于返回编辑功能的java eval代码,根据字段是否为空判定是add还是EDIT
     * @param dataname
     * @param param
     * @param pkName
     * @return
     */
    public static String getEditAddUrl(String getname,String dataname, String param, String pkName) {
        StringBuffer sbFunc = new StringBuffer();
        sbFunc.append("String pdname = (mapRow.get(\""+ getname + "\")==null || mapRow.get(\""+ getname + "\").toString().trim().isEmpty())?\"add\":\"edit\";\n");

        // 判断指定字段的值是否为空
        sbFunc.append("String url=\"javascript:window.open('dataadd.html?dataname=" + dataname + "&"+param+"&actiontype=\"+pdname+\"&pk_id=\"+mapRow.get(\"" + pkName + "\")+\"');\";\n");
        sbFunc.append("return url;\n");
        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getDeleteUrl
     * @描述 用于返回删除功能的java eval代码
     * @param dataname
     * @param pkName
     * @return
     */
    public static String getDeleteUrl(String dataname, String pkName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:jsCommon.deleteTableData('"+dataname+"','\"+mapRow.get(\""+pkName+"\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getDeleteUrl
     * @描述 用于返回删除功能的java eval代码
     * @param dataname
     * @param pkName
     * @return
     */
    public static String getDeleteUrl(String dataname, String pkName,String busTableName,String busPkName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:jsCommon.deleteTableData('"+dataname+"','\"+mapRow.get(\""+pkName+"\")+\"','"+busTableName+"','\"+mapRow.get(\""+busPkName+"\")+\"');\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getListUrl
     * @描述 用于返回列表功能的java eval代码
     * @param dataname
     * @param vars
     * @return
     */
    public static String getListUrl(String dataname, String vars,String fields,String fieldName) {
        StringBuffer sbFunc = new StringBuffer();
        String[] arrVar=vars.split(",");
        String[] arrField=fields.split(",");
        String queryString="";

        for (int i=0;i<arrVar.length;i++){
            String item=String.format("%s=\"+mapRow.get(\"%s\")",arrVar[i],arrField[i]);
            queryString=(i==0)?item:queryString+"&"+item;
        }

        sbFunc.append(String.format("String url=\"<a target=_blank href='#' onclick=javascript:window.showlayer('datalist.html?dataname=%s&%s+\"','800px','500px')>\"+mapRow.get(\"%s\")+\"</a>\";",
                dataname,queryString,fieldName));
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getOpenUrl
     * @描述 用于打开指定的URL
     * @param filename
     * @param vars
     * @return
     */
    public static String getOpenUrl(String filename, String vars,String fields,String fieldName,String actionType) {
        StringBuffer sbFunc = new StringBuffer();
        String[] arrVar=vars.split(",");
        String[] arrField=fields.split(",");
        String queryString="";

        for (int i=0;i<arrVar.length;i++){
            String item=String.format("%s=\"+mapRow.get(\"%s\")",arrVar[i],arrField[i]);
            queryString=(i==0)?item:queryString+"&"+item;
        }

        if ("showlayer".equals(actionType)) {
            sbFunc.append(String.format("String url=\"<a href='#' onclick=javascript:showlayer('%s%s+\"','800px','500px')>\"+mapRow.get(\"%s\")+\"</a>\";",
                    filename, queryString, fieldName));
        }else{
            sbFunc.append(String.format("String url=\"<a href='#' onclick=javascript:window.open('%s%s+\"')>\"+mapRow.get(\"%s\")+\"</a>\";",
                    filename, queryString, fieldName));
        }
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getOpenUrl
     * @描述 用于打开指定的URL
     * @param filename
     * @param vars
     * @return
     */
    public static String getOpenUrlAction(String filename, String vars,String fields,String fieldName,String actionType) {
        StringBuffer sbFunc = new StringBuffer();
        String[] arrVar=vars.split(",");
        String[] arrField=fields.split(",");
        String queryString="";

        for (int i=0;i<arrVar.length;i++){
            String item=String.format("%s=\"+mapRow.get(\"%s\")",arrVar[i],arrField[i]);
            queryString=(i==0)?item:queryString+"&"+item;
        }

        if ("showlayer".equals(actionType)) {
            sbFunc.append(String.format("String url=\"javascript:showlayer('%s%s+\"','800px','500px');\";",
                    filename, queryString, fieldName));
        }else{
            sbFunc.append(String.format("String url=\"javascript:window.open('%s%s+\"');\";",
                    filename, queryString, fieldName));
        }
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 getArchiveTypeSubList
     * @描述 用于返回删除功能的java eval代码
     * @param colName
     * @param pkName
     * @return
     */
    public static String getArchiveTypeSubList(String colName,String pkName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String colValue=String.valueOf(mapRow.get(\""+colName+"\"));\n");
        sbFunc.append("String pkValue=String.valueOf(mapRow.get(\""+pkName+"\"));\n");

        sbFunc.append("String url=\"datalist.html?dataname=erms_archive_type&parent_id=\"+pkValue;\n");

        sbFunc.append("return \"<a href='\"+url+\"'>\"+colValue+\"</a>\";\n");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     * @Title: getLoginUserId
     * @Description:获取当前登录的帐号ID
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getLoginUserId(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            // 或者转成具体对象
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpSession session = request.getSession();
            return CommonFunc.getNotNullString(session.getAttribute(SessionParams.CurrentUserID));
        }

        return "";
    }

    /**
     * @Title: getLoginInfo
     * @Description:获取当前登录的用户信息ID
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static SysUsers getLoginInfo() throws  Exception{
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            // 或者转成具体对象
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpSession session = request.getSession();
            String userId=CommonFunc.getNotNullString(session.getAttribute(SessionParams.CurrentUserID));
            SysUsers loginUser=(SysUsers)new SysUsersDAO().queryById(new SysUsers(),userId);
            return loginUser;
        }

        return null;
    }

    /**
     * @Title: getLoginDeptInfo
     * @Description:获取当前登录的部门信息
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static SysDepartments getLoginDeptInfo() throws  Exception{
        SysUsers sysUsers=Eval.getLoginInfo();
        if (sysUsers==null){
            return null;
        }

        return (SysDepartments) new SysDepartmentsDAO().queryById(new SysDepartments(),sysUsers.getDepartmentid());
    }

    /**
     * @Title: getLoginDepartmentId
     * @Description:获取当前登录的部门ID
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getLoginDepartmentId() throws Exception{
        SysUsers loginUser=Eval.getLoginInfo();
        return (loginUser==null)?"":loginUser.getDepartmentid();
    }

    /**
     * @Title: getLoginDepartmentName
     * @Description:获取当前登录的部门名称
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getLoginDepartmentName() throws Exception{
        SysDepartments sysDepartments=Eval.getLoginDeptInfo();
        if (sysDepartments==null){
            return "";
        }

        return sysDepartments.getDepartmentname();
    }
    public static String getLoginSuperiorDepartmentName() throws Exception{
        SysDepartments sysDepartments=Eval.getLoginDeptInfo();
        if (sysDepartments==null){
            return "";
        }
        SysDepartments SuperiorDepartment = new SysDepartmentsDAO().queryById(new SysDepartments(),sysDepartments.getParentid());
        if (SuperiorDepartment==null){
            return "";
        }

        return SuperiorDepartment.getDepartmentname();
    }

    /**
     * @Title: getDepartmentNames
     * @Description:获取多个部门ID对应的名称
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getDepartmentNames(String colName) throws Exception{
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String colValue=String.valueOf(mapRow.get(\""+colName+"\"));\n");
        sbFunc.append("return CommonFunc.getDepartmentNames(colValue);");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     * @Title: getUserNames
     * @Description:获取多个用户ID对应的用户名称
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getUserNames(String colName) throws Exception{
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String colValue=String.valueOf(mapRow.get(\""+colName+"\"));\n");
        sbFunc.append("return CommonFunc.getUserNames(colValue);");

        return Eval.EVAL + sbFunc.toString();
    }

    /**
     * @Title: formatFileSize
     * @Description:格式化文件大小
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String formatFileSize(String colName) throws Exception{
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String colValue=String.valueOf(mapRow.get(\""+colName+"\"));\n");
        sbFunc.append("return CommonFunc.formatFileSize(colValue);");

        return Eval.EVAL + sbFunc.toString();
    }


    public static String getEval_Fhdl_Modify(String fldn,String fldnFlag) {
        StringBuffer sbFunc = new StringBuffer();
        sbFunc.append("return \"1\".equals(mapRow.get(\"" + fldnFlag+ "\"))?\"<font color=red>\"+mapRow.get(\""+fldn+"\")+\"</font>\":mapRow.get(\""+fldn+"\");");
        return Eval.EVAL + sbFunc.toString();
    }

    /**
     *
     * @方法 js
     * @描述
     * @param jsFunctionName js方法名称
     * @return
     */
    public static String js(String jsFunctionName, String colName) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:" + jsFunctionName + "('\" + mapRow.get(\"" + colName + "\")" + "+ \"')\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    public static String js(String jsFunctionName, String colName, String type) {
        StringBuffer sbFunc = new StringBuffer();

        sbFunc.append("String url=\"javascript:" + jsFunctionName + "('" + type + "','\" + mapRow.get(\"" + colName + "\")" + "+ \"')\";\n");
        sbFunc.append("return url;\n");

        return Eval.EVAL + sbFunc.toString();
    }

    public static String jsCol(String jsFunctionName, String colName,String showText) {
        StringBuffer sbFunc = new StringBuffer();

        if (showText.isEmpty()) {
            sbFunc.append("return mapRow.get(\"" + colName + "\").toString().isEmpty()?\"\"" +
                    ":\"<span datavalue='\"+mapRow.get(\"" + colName + "\")+\"' onclick='" + jsFunctionName + "(this);'>" + showText + "</span>\";\n");
        }else{
            sbFunc.append("return \"<span datavalue='\"+mapRow.get(\"" + colName + "\")+\"' onclick='" + jsFunctionName + "(this);'>\"+mapRow.get(\""+colName+"\")+\"</span>\";\n");
        }

        return Eval.EVAL+sbFunc.toString();
    }

    public static String getParamFromUrl(String paramName){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();

            // 获取单个参数
            String paramValue = request.getParameter(paramName);
            return paramValue != null ? paramValue : "";
        }

        return "";
    }

    /**
     * @Title: getLoginDeptInfo
     * @Description:获取当前登录人的电话
     * @return
     * @author 广州卓凡冯承基
     * @throws
     */
    public static String getLoginMobile() throws Exception{
        SysUsers loginUser=Eval.getLoginInfo();
        return (loginUser==null)?"":loginUser.getMobile();
    }

    /**
     * @Title: getLoginDeptInfo
     * @Description:获取当前登录人的电话
     * @return
     * @author 广州卓凡冯承基
     * @throws
     */
    public static String getDisplayValueOf41(String sourceStr,String treeJson) throws Exception{
        StringBuffer sbFunc = new StringBuffer();
        String escapedJson = treeJson.replace("\\", "\\\\").replace("\"", "\\\"");
        sbFunc.append("return CommonFunc.getDisplayValueOf41(mapRow.get(\""+sourceStr+"\").toString(),\""+escapedJson+"\");");
        return Eval.EVAL+sbFunc.toString();
    }
}
