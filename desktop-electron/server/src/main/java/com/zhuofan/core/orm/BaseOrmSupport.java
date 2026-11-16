/*
 com.zhuofan.core.orm


   ver     date           author
           2015-05-11      fuxiaodong

 Copyright (c) 2015, TNT All Rights Reserved.
 */

package com.zhuofan.core.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.datasource.JdbcDao;

/**
 * ClassName:BaseOrmSupport
 *
 * @author fuxiaodong
 * @Date 2015-05-11
 */
public abstract class BaseOrmSupport<T> {
    public static final String FT_VARCHAR = "VARCHAR";
    public static final String FT_NUMBER = "NUMBER";
    protected static CommonLogger logger = CommonLogger.getInstance();

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
    // 选择字段
    private String selectedFldn = "*";

    // 是否自动提交
    private final boolean autoCommit=true;

    //错误信息
    private String lastError;

    public String getLastError(){
        return lastError;
    }


    /**
     * 获取实体的表名
     * @param entity
     * @return String
     */
    public String getTableName(Object entity) {
        Table table = entity.getClass().getAnnotation(Table.class);
        if(table != null) {
            return table.tableName();
        } else {
            String className = entity.getClass().getName();
            return className.substring(className.lastIndexOf(".") + 1);
        }
    }

    /**
     * 获取实体的ID值
     * @param entity
     * @return String
     */
    public static String getIdField(Object entity) {
        String idField = null;
        try {
            Field[] fields = entity.getClass().getDeclaredFields();

            System.out.println(fields.length);

            for (Field field : fields) {
                TableField tf = field.getAnnotation(TableField.class);
                if(tf.isPrimaryKey()) {
                    return tf.name();
                }
            }
        } catch (Exception e) {
            logger.debug("", Thread.currentThread().getStackTrace()[1]
                    .getMethodName(),e.getMessage());
            e.printStackTrace();
        }
        return idField;
    }

    /**
     * 获取实体的ID的记录值
     * @param entity
     * @return String
     */
    public String getIdValue(Object entity) {
        String value = null;

        try {
            Field[] fields = entity.getClass().getDeclaredFields();

            for (Field field : fields) {
                TableField tf = field.getAnnotation(TableField.class);
                if(tf.isPrimaryKey()) {
                    String methodName = field.getName();
                    String getMethodName = "get"
                            + methodName.substring(0, 1).toUpperCase()
                            + methodName.substring(1);
                    Method getMethod = entity.getClass().getMethod(getMethodName, new Class[] {});
                    if(tf.type().equals(FT_VARCHAR)) {
                        value = CommonFunc.getNotNullString(getMethod
                                .invoke(entity, new Object[] {}));
                    } else if(tf.type().equals(FT_NUMBER)) {
                        Object obj = getMethod.invoke(entity, new Object[] {});
                        value = (obj == null) ? "" : obj.toString();
                    } else {
                        value = "";
                    }
                    return value;
                }
            }// for
        } catch (Exception e) {
            String methodName =
                    Thread.currentThread().getStackTrace()[1].getMethodName();
            logger.error(this, methodName, e.getMessage());
            e.printStackTrace();
        }
        return value;
    }

    /**
     * getFieldMap 获取实体的字段对应关系表
     * @param entity
     * @return HashMap<String,String>
     */
    private HashMap<String, String> getFieldMap(Object entity) {
        HashMap<String, String> mapField = new HashMap<>();

        try {
            Field[] fields = entity.getClass().getDeclaredFields();

            for (Field field : fields) {
                TableField tf = field.getAnnotation(TableField.class);

                if (tf == null) {
                    continue;
                }

                if(!StringUtils.isEmpty(tf.name())) {
                    mapField.put(tf.name(), field.getName() + ":" + tf.type());
                }
            }
        } catch (Exception e) {
            String methodName =
                    Thread.currentThread().getStackTrace()[1].getMethodName();
            logger.debug(this, methodName, e.getMessage());
            e.printStackTrace();
        }

        return mapField;
    }

    /**
     * setQueryParam 设置对象查询的参数
     * @paramString selectedFldn
     * @paramString where
     * @paramString order
     * @paramString group
     * @paramint pageNumber
     * @paramint pageNo
     */
    public void setQueryParam(String selectedFldn, String where, String order,
                              String group, int pageNumber, int pageNo) {
        this.selectedFldn = selectedFldn;
        this.where = where;
        this.order = order;
        this.group = group;
        this.pageNumber = pageNumber;
        this.pageNo = pageNo;
    }

    /**
     * 对象查询
     * @paramentity
     * @return String
     */
    public String getQuerySql(T o) {
        String tableName = this.getTableName(o);
        String sql = "";
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(this.selectedFldn).append(" FROM ");
        sb.append(tableName);
        if(!StringUtils.isEmpty(this.where)) {
            sb.append(" WHERE ").append(this.where);
        }

        if(!StringUtils.isEmpty(this.group)) {
            sb.append(" GROUP BY ").append(this.group);
        }

        if(!StringUtils.isEmpty(this.order)) {
            sb.append(" ORDER BY ").append(this.order);
        }
        sql = sb.toString();

        // 如果pageNumber大于0，则分页，否则不分页
        if(this.pageNumber > 0) {
            int max = this.pageNumber * this.pageNo;
            int min = (this.pageNumber - 1) * this.pageNo;
            String sba = "SELECT * FROM ( SELECT row_.*,rownum ROWNUM_ FROM (" + sql +
                    ") row_ where rownum <=" + max +
                    ") where rownum_>" + min;
            sql = sba;
        }
        System.out.println("the final sql :" + sql);

        return sql;

    }

    public ArrayList<T> query(T o) throws Exception {
        String sql=this.getQuerySql(o);
        return this.query(o,sql);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<T> query(T o, String sql) throws Exception {
        try(JdbcDao jdbcDao=new JdbcDao()) {
            HashMap<String, String> fieldMap = this.getFieldMap(o);
            List<HashMap<String, String>> list = jdbcDao.getResult(sql);

            if (list == null) {
                return null;
            }
            if (list.isEmpty()) {
                return null;
            }

            // 返回对象容器
            ArrayList<T> listResult = new ArrayList<>();

            // 结果列数
            int rowCount = list.size();
            // 通过反射类，实例化对象
            Object object = o.getClass().newInstance();
            // 返回类中所有方法，不包括继承的方法
            Method[] methods = object.getClass().getDeclaredMethods();

            for (int i = 0; i < rowCount; i++) {
                if (i > 0) {
                    object = o.getClass().newInstance();
                }

                for (Map.Entry<String, String> entry : list.get(i).entrySet()) {
                    String fieldName = entry.getKey();
                    if (!fieldMap.containsKey(fieldName)) {
                        continue;
                    }

                    String fieldValue = entry.getValue();

                    // methodname:fieldtype
                    String fieldParam = fieldMap.get(fieldName);
                    if (StringUtils.isEmpty(fieldParam)) continue;

                    String[] arrayParam = StringUtils.split(fieldParam, ":");
                    if (arrayParam.length < 2) continue;

                    String methodName = "set"
                            + arrayParam[0].replaceFirst(arrayParam[0].substring(0, 1), arrayParam[0].substring(0, 1).toUpperCase());
                    String fieldType = arrayParam[1].toUpperCase();

                    for (Method method : methods) {
                        if (methodName.equals(method.getName())) {
                            // 若方法存在，则使用invoke调用该方法
                            if (TableField.TF_NUMBER.equals(fieldType)) {
                                if (CommonFunc.isNumeric(fieldValue)) {
                                    if (method.getParameterTypes()[0].equals(short.class)) {
                                        method.invoke(object, Short.valueOf(fieldValue));
                                    } else if (Integer.class.equals(method.getParameterTypes()[0])) {
                                        method.invoke(object, Integer.valueOf(fieldValue));
                                    } else if (Short.class.equals(method.getParameterTypes()[0])) {
                                        method.invoke(object, Short.valueOf(fieldValue));
                                    } else {
                                        method.invoke(object, Math.round(Double.parseDouble(fieldValue)));
                                    }
                                }
                            } else if (TableField.TF_BIGDECIMAL.equals(fieldType)) {
                                if (BigDecimal.class.equals(method.getParameterTypes()[0])) {
                                    method.invoke(object, new BigDecimal(fieldValue));
                                }
                            } else {
                                try {
                                    method.invoke(object,
                                            CommonFunc.getNotNullString(fieldValue));
                                } catch (IllegalArgumentException e) {
//                                Parameter[] parameters = method.getParameters();
//                                Class<?> type = parameters[0].getType();
//                                if (!Objects.equals(type, String.class)) {
                                    continue;
//                                }
                                }
                            }
                            break;
                        } // if methodname
                    } // for method
                }// for Map.Entry;

                listResult.add((T) object);
            }// for i

            jdbcDao.close();
            return listResult;
        }
    }

    /**
     * 获取分页
     * @param o
     * @paramsql
     * @return
     * @throws Exception
     */
    public ArrayList<T> queryPage(T o) throws Exception {
        String finalSql = this.getQuerySql(o);
        return query(o, finalSql);
    }

    public int queryPageCount(String sql) {
        int r;
        String finalSql = "SELECT COUNT(*) AS C FROM (" + sql + ")";
        try(JdbcDao jdbcDao=new JdbcDao()){
            String a = jdbcDao.getFieldValue(finalSql, "C");
            jdbcDao.close();
            r = CommonFunc.strToInt(a);
            return r;
        }
    }

    /**
     * 获取分页
     * @param o
     * @param sql
     * @return
     * @throws Exception
     */
    public ArrayList<T> queryPage(T o, String sql, int pageCurrent, int pageSize) throws Exception {
        String sqlPage;
        int max = pageCurrent * pageSize;
        int min = (pageCurrent - 1) * pageSize;
        String sba = "SELECT * FROM ( SELECT row_.*,rownum ROWNUM_ FROM (" + sql +
                ") row_ where rownum <=" + max +
                ") where rownum_>" + min;
        sqlPage = sba;

        try( JdbcDao jdbcDao=new JdbcDao()) {
            HashMap<String, String> fieldMap = this.getFieldMap(o);
            List<HashMap<String, String>> list = jdbcDao.getResult(sqlPage);
            /** 返回对象容器 **/
            ArrayList<T> listResult = new ArrayList<>();

            if (list == null) {
                return null;
            }

            if (list.size() == 0) {
                return null;
            }

            /** 结果列数 **/
            int rowCount = list.size();
            /** 通过反射类，实例化对象 **/
            Object object = o.getClass().newInstance();
            /** 返回类中所有方法，不包括继承的方法 **/
            Method[] methods = object.getClass().getDeclaredMethods();

            for (int i = 0; i < rowCount; i++) {
                if (i > 0) {
                    object = o.getClass().newInstance();
                }

                for (Map.Entry<String, String> entry : list.get(i).entrySet()) {
                    String fieldName = entry.getKey();
                    if (!fieldMap.containsKey(fieldName)) {
                        continue;
                    }

                    String fieldValue = entry.getValue();

                    String fieldParam = fieldMap.get(fieldName);
                    if (StringUtils.isEmpty(fieldParam)) continue;

                    String[] arrayParam = StringUtils.split(fieldParam, ":");
                    if (arrayParam.length < 2) continue;

                    String methodName = "set"
                            + arrayParam[0].replaceFirst(arrayParam[0].substring(0, 1), arrayParam[0].substring(0, 1).toUpperCase());
                    String fieldType = arrayParam[1].toUpperCase();

                    for (Method method : methods) {
                        if (methodName.equals(method.getName())) {
                            // 若方法存在，则使用invoke调用该方法
                            if (fieldType.equals("NUMBER")) {
                                if (CommonFunc.isNumeric(fieldValue)) {
                                    method.invoke(object, Long.valueOf(fieldValue));
                                }
                            } else {
                                method.invoke(object,
                                        CommonFunc.getNotNullString(fieldValue));
                            }
                            break;
                        } // if methodname
                    } // for method
                }// for Map.Entry;

                listResult.add((T) object);
            }// for i

            jdbcDao.close();

            return listResult;
        }
    }

    /**
     * 对象查询
     * @param o
     * @param sql
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ArrayList<T> queryBySql(T o, String sql) throws Exception {
        HashMap<String, String> fieldMap = this.getFieldMap(o);
        List<HashMap<String, String>> list;

        try(JdbcDao jdbcDao=new JdbcDao()) {
            list=jdbcDao.getResult(sql);
        }

        /** 返回对象容器 **/
        ArrayList<T> listResult = new ArrayList<>();

        if(list == null) {
            return null;
        }

        if(list.size() == 0) {
            return null;
        }



        /** 结果列数 **/
        int rowCount = list.size();
        /** 通过反射类，实例化对象 **/
        Object object = o.getClass().newInstance();
        /** 返回类中所有方法，不包括继承的方法 **/
        Method[] methods = object.getClass().getDeclaredMethods();

        for (int i = 0; i < rowCount; i++) {
            if(i > 0) {
                object = o.getClass().newInstance();
            }

            for (Map.Entry<String, String> entry : list.get(i).entrySet()) {
                String fieldName = entry.getKey();
                if(!fieldMap.containsKey(fieldName)) {
                    continue;
                }

                String fieldValue = entry.getValue();

                String fieldParam = fieldMap.get(fieldName);
                if(StringUtils.isEmpty(fieldParam)) continue;

                String[] arrayParam = StringUtils.split(fieldParam, ":");
                if(arrayParam.length < 2) continue;

                String methodName = "set"
                        + arrayParam[0].replaceFirst(arrayParam[0].substring(0, 1),arrayParam[0].substring(0, 1).toUpperCase());
                String fieldType = arrayParam[1].toUpperCase();

                for (Method method : methods) {
                    if(methodName.equals(method.getName())) {
                        // 若方法存在，则使用invoke调用该方法
                        if(fieldType.equals("NUMBER")) {
                            if(CommonFunc.isNumeric(fieldValue)) {
                                method.invoke(object, Long.valueOf(fieldValue));
                            }
                        } else if (TableField.TF_LOCAL_DATE_TIME.equals(fieldType)) {
                            method.invoke(object,
                                    LocalDateTime.parse(CommonFunc.getNotNullString(fieldValue), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
                        } else if (TableField.TF_BIGDECIMAL.equals(fieldType)) {
                            String notNullString = CommonFunc.getNotNullString(fieldValue);
                            method.invoke(object,
                                    StringUtils.isBlank(notNullString) ? BigDecimal.ZERO : new BigDecimal(notNullString));
                        } else {
                            method.invoke(object,
                                    CommonFunc.getNotNullString(fieldValue));
                        }
                        break;
                    } // if methodname
                } // for method
            }// for Map.Entry;

            listResult.add((T) object);
        }// for i

        return listResult;
    }

    /**
     * saveNew 插入新的记录
     * @param o
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public boolean saveNew(T o) {
        String methodName = Thread.currentThread().getStackTrace()[1]
                .getMethodName();
        lastError="";

        Class<? extends Object> entityClass = o.getClass();

        Method getMethod = null;
        HashMap<String, String> mapChangeInfo = new HashMap<String, String>();

        try {
            getMethod = entityClass.getMethod("getChangeInfo", new Class[] {});
            mapChangeInfo = (HashMap<String, String>) getMethod.invoke(o,
                    new Object[] {});
        } catch (Exception e) {
            logger.error(this, methodName, e.getMessage());
            e.printStackTrace();
        }

        StringBuilder sql = new StringBuilder();

        String tableName = this.getTableName(o);
        sql.append("insert into ").append(tableName).append(" (");

        Field[] fields = entityClass.getDeclaredFields();
        String temp = "";
        StringBuilder valueSql = new StringBuilder();

        for (Field f : fields) {
            String firstLetter = f.getName().substring(0, 1).toUpperCase();

            // 判断字段是否更新过
            String setMethodName = "set" + firstLetter
                    + f.getName().substring(1);
            if(!mapChangeInfo.containsKey(setMethodName)) continue;
            if(!mapChangeInfo.get(setMethodName).equals("1")) continue;

            String getMethodName = "get" + firstLetter
                    + f.getName().substring(1);
            TableField tf = f.getAnnotation(TableField.class);
            Object value = null;
            try {
                getMethod = entityClass
                        .getMethod(getMethodName, new Class[]{});
                value = getMethod.invoke(o, new Object[]{});
                if (tf.type().equals(FT_NUMBER)) {
                    valueSql.append(temp).append(value.toString());
                } else if (value instanceof LocalDateTime) {
                    String format = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    valueSql.append(temp).append("TO_TIMESTAMP('").append(format).append("', 'YYYY-MM-DD HH24:MI:SS')");
                } else if (value instanceof Boolean) {
                    valueSql.append(temp).append(BooleanUtils.isTrue((Boolean) value) ? 1 : 0);
                }else {
                    String sValue = "";
                    if(value != null){
                        sValue = ((String) value).replace("'", "''");
                    }

                    valueSql.append(temp).append("'").append(sValue).append("'");
                }
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                logger.error(this, methodName, e.getMessage());
                e.printStackTrace();
            }

            if(tf != null) {
                sql.append(temp).append(tf.name());
            } else {
                sql.append(temp).append(f.getName());
            }
            temp = ",";
        }

        sql.append(")values(");
        sql.append(valueSql);
        sql.append(")");

        System.out.println(sql.toString());

        // 执行SQL
        try(JdbcDao jdbcDao=new JdbcDao()) {
            if (jdbcDao.execute(sql.toString())) {
                logger.debug(this, methodName, "执行SQL成功：" + sql.toString());
                if (this.autoCommit) {
                    try {
                        jdbcDao.commit();
                    } catch (SQLException e) {
                        lastError = jdbcDao.getLastError();
                        logger.error(this, methodName, e.getMessage());
                        e.printStackTrace();
                    }
                    jdbcDao.close();
                }
                return true;
            } else {
                lastError = jdbcDao.getLastError();
                logger.error(this, methodName, "执行SQL失败：" + sql.toString());
                jdbcDao.close();
                return false;
            }
        }
    }

    /**
     * update 更新现有的记录
     *
     * @param o
     * @return boolean
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public boolean update(T o) throws Exception {
        String idField = BaseOrmSupport.getIdField(o);
        String idValue = this.getIdValue(o);
        lastError="";

        // 判断是否有设置where和设置ID值，如果都未设置，则禁止更新，以防止更新全部记录
        if(StringUtils.isEmpty(this.where) && StringUtils.isEmpty(idValue)) {
            return false;
        }

        // 设置更新的过滤条件
        if(!StringUtils.isEmpty(idValue)) {
            this.where = (StringUtils.isEmpty(this.where)) ?
                    idField + "='" + idValue + "'" :
                    this.where + " AND (" + idField + "='" + idValue + "')";
        }

        String methodName = Thread.currentThread().getStackTrace()[1]
                .getMethodName();

        Class<? extends Object> entityClass = o.getClass();

        Method getMethod;
        HashMap<String, String> mapChangeInfo = new HashMap<String, String>();

        try {
            getMethod = entityClass.getMethod("getChangeInfo", new Class[] {});
            mapChangeInfo =
                    (HashMap<String, String>) getMethod.invoke(o, new Object[] {});
        } catch (Exception e) {
            logger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+this.getClass().toString()+" update: getChangeInfo error");
            logger.error(this, methodName, e.getMessage());
            e.printStackTrace();
        }

        String tableName = this.getTableName(o);
        String sql = new String();

        ArrayList<String> arrayFieldValue = new ArrayList<String>();

        Field[] fields = entityClass.getDeclaredFields();

        for (Field f : fields) {
            String firstLetter = f.getName().substring(0, 1).toUpperCase();

            // 判断字段是否更新过
            String setMethodName = "set" + firstLetter
                    + f.getName().substring(1);
            if(!mapChangeInfo.containsKey(setMethodName)) continue;
            if(!mapChangeInfo.get(setMethodName).equals("1")) continue;

            String getMethodName = "get" + firstLetter
                    + f.getName().substring(1);
            TableField tf = f.getAnnotation(TableField.class);

            try {
                getMethod = entityClass
                        .getMethod(getMethodName, new Class[] {});

                String fieldName = (tf != null) ? tf.name() : f.getName();
                Object value = getMethod.invoke(o, new Object[] {});

                if(value != null) {
                    if(tf.type().equals(TableField.TF_NUMBER)) {
                        arrayFieldValue.add(fieldName + "="
                                + String.valueOf(value));
                    }else if (value instanceof Boolean) {
                        arrayFieldValue.add(fieldName + "=" + (BooleanUtils.isTrue((Boolean)value) ? 1 : 0));
                    } else if (value instanceof BigDecimal) {
                        arrayFieldValue.add(fieldName + "=" + value);
                    } else{
                        value = ((String) value).replace("'", "''");
                        arrayFieldValue.add(fieldName + "='" + value + "'");
                    }
                }
            } catch (Exception e) {
                logger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+this.getClass().toString()+" update: getMethod error,fieldName:"+((tf != null) ? tf.name() : f.getName()));
                logger.error(this, methodName, e.getMessage());
                e.printStackTrace();
            }
        }

        sql = "update " + tableName + " SET "
                + StringUtils.join(arrayFieldValue, ",") + " WHERE "
                + this.where;

        // 执行SQL
        try(JdbcDao jdbcDao=new JdbcDao()) {
            if (jdbcDao.execute(sql.toString())) {
                logger.debug(this, methodName, "执行SQL成功：" + sql.toString());

                if (this.autoCommit) {
                    jdbcDao.commit();
                    jdbcDao.close();
                }
                return true;
            } else {
                lastError = jdbcDao.getLastError();
                logger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + " execute sql error,sql:" + sql.toString());
                logger.error(this, methodName, "执行SQL失败：" + sql.toString());
                jdbcDao.close();
                return false;
            }
        }
    }

    /**
     * delete 删除现有的记录
     *
     * @param o return boolean
     * @throws Exception
     */
    public boolean delete(T o) throws Exception {
        /** 判断是否有设置where和设置ID值，如果都未设置，则禁止删除，以防止删除全部记录 **/
        if ((StringUtils.isEmpty(this.where)) && (o==null)){
            return false;
        }

        return deleteImpl(o);
    }

    /**
     * deleteImpl 删除现有的记录 ，私有实现方法
     *
     * @param o return boolean
     * @throws Exception
     */
    private boolean deleteImpl(T o) throws Exception {
        String tableName = this.getTableName(o);
        String sql = "DELETE FROM " + tableName + " WHERE " ;

        if (StringUtils.isNotEmpty(this.where)){
            sql=sql+this.where;
        }else{
            if (o!=null) {
                sql = sql + this.getIdField(o)+"='"+this.getIdValue(o)+"'";
            }
        }


        System.out.println(sql.toString());

        // 执行SQL
        String methodName = Thread.currentThread().getStackTrace()[1]
                .getMethodName();

        try(JdbcDao jdbcDao=new JdbcDao()) {
            if (jdbcDao.execute(sql.toString())) {
                logger.info(this, methodName, "执行SQL成功：" + sql.toString());
                if (this.autoCommit) {
                    jdbcDao.commit();
                    jdbcDao.close();
                }
                return true;
            } else {
                logger.info(this, methodName, "执行SQL失败：" + sql.toString());
                jdbcDao.close();
                return false;
            }
        }
    }

    /**
     * deleteByID 删除现有的记录
     * @param o return boolean
     * @throws Exception
     */
    public boolean deleteById(T o, String idValue) {
        String idField = BaseOrmSupport.getIdField(o);
        setWhere(idField + "='" + idValue + "'");
        try {
            return deleteImpl(o);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public T queryById(T o, String idValue) throws Exception {
        String idField = BaseOrmSupport.getIdField(o);
        this.setWhere(idField + "='" + idValue + "'");
        return this.queryFirstRow(o);
    }

    /**
     *
     * @return
     */
    public String getNewIdValue() {
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String result= jdbcDao.getSeqNextVal();
            jdbcDao.close();
            return result;
        }
    }

    /**
     * queryFirstRow 获取查询列表的第1行
     * @return
     */
    public T queryFirstRow(T o) throws Exception {
        ArrayList<T> list = this.queryPage(o);
        if(list == null) {
            return null;
        }

        return list.get(0);
    }

    /**
     * 获取记录总数 getRowCount
     * @return
     */
    public long getRowCount(T o) throws Exception {
        try(JdbcDao jdbcDao=new JdbcDao()) {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT COUNT(*) MYCOUNT FROM ");
            sb.append(getTableName(o));
            if (!StringUtils.isEmpty(this.where)) {
                sb.append(" WHERE " + this.where);
            }
            String sCount = jdbcDao.getFieldValue(sb.toString(), "MYCOUNT");

            jdbcDao.close();

            if (CommonFunc.isNumeric(sCount)) {
                return CommonFunc.strToInt(sCount);
            }
        }

        return 0;
    }

    /**
     * @Title: queryToTree
     * @Description:执行查询，并将查询结果返回到树形列表中
     * @param o
     * @param fieldName
     *            :字段名，但此处必须是对象的字段名，而不是数据库的字段名
     * @param rootNode
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    @SuppressWarnings("rawtypes")
    public ArrayList queryToTree(T o, String fieldName, String rootNode) throws Exception {
        return getTreeList(queryPage(o), fieldName, rootNode);
    }

    /**
     * @Title: getTreeList
     * @Description:获取一个递归的对象列表
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ArrayList getTreeList(ArrayList<T> list, String fieldName,
                                 String rootNode) {

        ArrayList listResult = new ArrayList();

        if(list == null) return null;

        String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
        Method getMethod = null;

        for (int i = 0; i < list.size(); i++) {
            HashMap mapItem = new HashMap();
            Object object = list.get(i);
            String value = "";

            try {
                if(i == 0) {
                    getMethod = object.getClass().getMethod(getMethodName,
                            new Class[] {});
                }

                String idValue = getIdValue(object);
                value = CommonFunc.getNotNullString(getMethod.invoke(object,
                        new Object[] {}));

                if(!value.equals(rootNode)) continue;

                mapItem.put("self", object);
                mapItem.put("child", (StringUtils.isEmpty(value)) ? null
                        : getTreeList(list, fieldName, idValue));

                listResult.add(mapItem);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return listResult;
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

    public BaseOrmSupport() {

    }

}
