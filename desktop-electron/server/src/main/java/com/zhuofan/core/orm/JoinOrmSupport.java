/**
 * com.zhuofan.core.orm
 *
 *
 *   ver     date           author
 *           2015-05-25      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.datasource.JdbcDao;

/**
 * ClassName:JoinOrmSupport 多表关联ORM
 *
 * @author fuxiaodong
 * @Date 2015-05-25
 */
public abstract class JoinOrmSupport<T> {
    protected CommonLogger logger = CommonLogger.getInstance();

    private static final String LEFT_JOIN = "LEFT JOIN";
    private static final String RIGHT_JOIN = "RIGHT JOIN";
    private static final String JOIN = "JOIN";

    // 连接类型
    private String joinType;
    // 连接方式
    private ArrayList joinOn;
    // 表名
    private String tableName;
    // 字段名
    private ArrayList selectedFldn;
    // 处理后的字段名
    private String strSelectedFldn;

    // 过滤条件
    private String where = "";
    // 排序条件
    private String order = "";
    // 分组条件
    private String group = "";
    // 分页数
    private int pageNumber = 0;
    // 页码
    private int pageNo = 1;
    // 数据库操作对象
    private JdbcDao jdbcDao;
    // 表名列表
    private HashMap<String, String> mapTable;
    // table alias
    private HashMap<String, String> mapTableAlias;
    // 字段映射关系
    private HashMap<String, String> mapField;

    /**
     * =======================================================
     * =======================================================
     */

    public String getJoinType() {
        return this.joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public void setJoinOn(ArrayList joinOn) {
        this.joinOn = joinOn;
    }

    public ArrayList getJoinOn() {
        return this.joinOn;
    }

    public String getTableName() {
        return this.tableName;
    }

    public ArrayList getSelectedFldn() {
        return this.selectedFldn;
    }

    public void setSelectedFldn(ArrayList selectedFldn) {
        this.selectedFldn = selectedFldn;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getWhere() {
        return this.where;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrder() {
        return this.order;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    /**
     * =======================================================
     * =======================================================
     */

    public JoinOrmSupport() {
        joinType = LEFT_JOIN;
        joinOn = new ArrayList();
        selectedFldn = new ArrayList();
    }

    /*
     * initParam 初始化相关属性
     */
    public void initParam(Object o, ArrayList joinOn, ArrayList selectedFldn) {
        this.joinOn = joinOn;
        this.selectedFldn = selectedFldn;
        initParam(o);
    }

    /*
     * initParam 初始化相关属性
     */
    public void initParam(Object o) {
        boolean selecteAllFldn = (this.selectedFldn == null);

        try {
            Field[] fields = o.getClass().getDeclaredFields();
            int i = 0;

            // 检查joinon对象
            if(this.joinOn.size() != (fields.length - 1)) {
                throw new Exception("初始化查询对象发生错误：joinOn的设置不正确，必须是长度为"
                        + String.valueOf((fields.length - 1)) + "的ArrayList");
            }

            mapTable = new HashMap<String, String>();
            mapTableAlias = new HashMap<String, String>();
            mapField = new HashMap<String, String>();
            strSelectedFldn = "";

            for (Field field : fields) {
                String className = field.getType().getName();
                Object entity = Class.forName(className).newInstance();

                // 获取表名
                Table table = (Table) entity.getClass().getAnnotation(
                        Table.class);
                String tabn = (table != null) ? table.tableName() : className
                        .substring(className.lastIndexOf(".") + 1);
                String tabnAlias = "T" + String.valueOf(i + 1);
                String tabnFull = tabn + " " + tabnAlias;

                tableName = (i == 0) ? tabnFull : tableName + " "
                        + this.getJoinType() + " " + tabnFull + " ON "
                        + this.joinOn.get(i - 1);
                mapTable.put(field.getName(), className);
                mapTableAlias.put(className, tabnAlias);

                // 获取当前表的所有字段名
                Field[] fs = entity.getClass().getDeclaredFields();
                if(selecteAllFldn) {
                    for (Field f : fs) {
                        TableField tableField = f
                                .getAnnotation(TableField.class);

                        if(!StringUtils.isEmpty(tableField.name())) {
                            String sFldnItem = tabnAlias + "."
                                    + tableField.name() + " " + tabnAlias + "_"
                                    + tableField.name();
                            strSelectedFldn = (StringUtils
                                    .isEmpty(strSelectedFldn)) ? sFldnItem
                                    : strSelectedFldn + "," + sFldnItem;
                            mapField.put(tabnAlias + "_" + tableField.name(),
                                    f.getName() + ":" + tableField.type());
                        }
                    }// for f:fs
                } else {
                    if(selectedFldn.get(i) != null) {
                        String[] arrayFldn = selectedFldn.get(i).toString()
                                .toUpperCase().split(",");

                        for (Field f : fs) {
                            TableField tableField = f
                                    .getAnnotation(TableField.class);

                            for (int j = 0; j < arrayFldn.length; j++) {
                                if(!arrayFldn[j].trim().equals(
                                        tableField.name())) continue;

                                String sFldnItem = tabnAlias + "."
                                        + tableField.name() + " " + tabnAlias
                                        + "_" + tableField.name();
                                strSelectedFldn = (StringUtils
                                        .isEmpty(strSelectedFldn)) ? sFldnItem
                                        : strSelectedFldn + "," + sFldnItem;
                                mapField.put(
                                        tabnAlias + "_" + tableField.name(),
                                        f.getName() + ":" + tableField.type());
                                break;
                            }// for j;
                        }// for f:fs
                    }
                }// if

                i++;
            }// for field:fields

        } catch (Exception e) {
            String methodName = Thread.currentThread().getStackTrace()[1]
                    .getMethodName();
            logger.debug(this, methodName, e.getMessage());
            e.printStackTrace();
        }

        System.out.println("tableName:" + tableName);
        System.out.println("strSelectedFldn:" + strSelectedFldn);
    }

    /*
     * getJdbcDao 获取jdbcDao对象
     *
     * @return JdbcDAO
     */
    private JdbcDao getJdbcDao() {
        if (this.jdbcDao == null || this.jdbcDao.isClosed()) {
            this.jdbcDao = new JdbcDao();
        }

        return this.jdbcDao;
    }

    /**
     * setQueryParam 设置对象查询的参数
     *
     * @paramString
     *            where
     * @paramString
     *            order
     * @paramString
     *            group
     * @paramint pageNumber
     * @paramint pageNo
     */
    public void setQueryParam(String where, String order, String group,
                              int pageNumber, int pageNo) {
        this.where = where;
        this.order = order;
        this.group = group;
        this.pageNumber = pageNumber;
        this.pageNo = pageNo;
    }

    /**
     * 对象查询
     *
     * @param:entity
     * @return String
     * @throws Exception
     */
    public String getQuerySql() {
        String sql = "SELECT " + this.strSelectedFldn + " FROM " + tableName;

        String sqlWhere = "";
        String sqlOrder = "";
        String sqlGroup = "";

        if(!StringUtils.isEmpty(this.where)) {
            sql = sql + " WHERE " + this.where;
        }

        if(!StringUtils.isEmpty(this.group)) {
            sql = sql + " GROUP BY " + this.group;
        }

        if(!StringUtils.isEmpty(this.order)) {
            sql = sql + " ORDER BY " + this.order;
        }

        // 如果pageNumber大于0，则分页，否则不分页
        if(this.pageNumber > 0) {
            int max = this.pageNumber * this.pageNo;
            int min = (this.pageNumber - 1) * this.pageNo;
            sql = "SELECT * FROM ( SELECT row_.*,rownum ROWNUM_ FROM (" + sql
                    + ") row_ where rownum <=" + String.valueOf(max)
                    + ") where rownum_>" + String.valueOf(min);
        }

        System.out.println("the final sql :" + sql);

        return sql;

    }

    /**
     * 对象查询
     *
     * @param o
     * @param:sql
     * @return
     * @throws Exception
     */
    public ArrayList<T> query(T o) throws Exception {
        String sql=this.getQuerySql();

        getJdbcDao();
        List<HashMap<String, String>> list = jdbcDao.getResult(sql);
        jdbcDao.close();

        if(list == null) {
            return null;
        }

        if(list.size() == 0) {
            return null;
        }

        // 返回对象容器
        ArrayList<T> listResult = new ArrayList();

        // 结果列数
        Method[] methodsMain = null;
        int rowCount = list.size();

        for (int i = 0; i < rowCount; i++) {
            Object object = o.getClass().newInstance();

            if(i == 0) {
                methodsMain = object.getClass().getMethods();
            }

            HashMap<String, String> tmpMapTable = mapTable;

            for (Entry<String, String> entryTable : tmpMapTable.entrySet()) {
                String classField = entryTable.getKey();
                String className = entryTable.getValue();
                String tabnAlias = mapTableAlias.get(className);

                // 创建对象
                Object objectSub = Class.forName(className).newInstance();
                Method[] methods = objectSub.getClass().getMethods();

                // 给对象逐一赋值
                for (Entry<String, String> entryCol : list.get(i).entrySet()) {
                    String fieldName = entryCol.getKey();
                    if(!fieldName.substring(0, tabnAlias.length()).equals(
                            tabnAlias)) continue;

                    if(!mapField.containsKey(fieldName)) continue;

                    String fieldValue = entryCol.getValue();

                    // methodname:fieldtype
                    String fieldParam = mapField.get(fieldName);
                    if(StringUtils.isEmpty(fieldParam)) continue;

                    String[] arrayParam = StringUtils.split(fieldParam, ":");
                    if(arrayParam.length < 2) continue;

                    String methodName = "set"
                            + arrayParam[0]
                            .replaceFirst(
                                    arrayParam[0].substring(0, 1),
                                    arrayParam[0].substring(0, 1)
                                            .toUpperCase());
                    String fieldType = arrayParam[1].toUpperCase();

                    for (Method method : methods) {
                        if(methodName.equals(method.getName())) {
                            // 若方法存在，则使用invoke调用该方法
                            if(fieldType.equals("NUMBER")) {
                                if(CommonFunc.isNumeric(fieldValue)) {
                                    method.invoke(objectSub,
                                            Long.valueOf(fieldValue));
                                }
                            } else {
                                method.invoke(objectSub, fieldValue);
                            }
                        } // if methodname
                    } // for method
                }// for entryCol;

                // 将对象逐一添加到主对象中
                String setMethodName = "set"
                        + classField.substring(0, 1).toUpperCase()
                        + classField.substring(1);
                for (Method methodMain : methodsMain) {
                    if(methodMain.getName().equals(setMethodName)) {
                        methodMain.invoke(object, objectSub);
                    }
                }// for methodMain

            }// entryTable

            listResult.add((T) object);
        }// for i

        return listResult;
    }

    /**
     * 获取记录总数 getRowCount
     *
     */
    public long getRowCount(T o) throws Exception {
        getJdbcDao();

        String sql = "SELECT COUNT(*) MYCOUNT FROM " + tableName;

        if(!StringUtils.isEmpty(this.where)) {
            sql = sql + " WHERE " + this.where;
        }

        String sCount = this.jdbcDao.getFieldValue(sql, "MYCOUNT");
        jdbcDao.close();
        
        if(CommonFunc.isNumeric(sCount)) {
            return Long.valueOf(sCount);
        }

        return 0;
    }

}
