/**
 * com.zhoufan.core.datasource
 *
 *
 *   ver     date           author
 *           2015-05-18      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.datasource;

import java.sql.*;

import java.util.*;


import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * ClassName:JdbcDao JDBC操作数据库的基类
 *
 * @author fuxiaodong
 * @Date 2015-05-18
 */
public class JdbcDao implements AutoCloseable {
    protected CommonLogger logger = CommonLogger.getInstance();
    private DataSource ds;
    private Connection connection;
    private PollConnection pollConnection;
    private String lastError;

    public DataSource getDs(){
        return ds;
    }

    /**
     * @Author fuxiaodong
     * @Description //使用非主数据源进行查询操作
     * @Version: v1.8
     * @Date 10:43 2026/6/26
     * @Param [dataSource, sql]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     */
    /*public static List<Map<String, Object>> queryByDataSource(String dataSource,String sql){
        DynamicDataSourceContextHolder.push(dataSource);
        try {
            return new JdbcTemplate(dataSource).queryForList(sql);
        } finally {
            // 必须手动弹出，清空当前数据源标识
            DynamicDataSourceContextHolder.poll();
        }
    }*/

    // 构造函数
    public JdbcDao()  {
        /*
        ds = DataSource.getInstance();
        this.pollConnection = ds.getConnection();
        if(this.pollConnection != null) {
            this.connection = pollConnection.getConnection();
        }
        */
        ds=DataSource.getInstance();
        try {
            logger.info("jdbcdao begin get connection");
            this.connection = ds.getHikariDataSource().getConnection();
            logger.info("jdbcdao get connection suss");
        }catch (Exception e){
            logger.info("jdbcdao get connection error:"+e.getMessage());
            e.printStackTrace();
        }
    }

    // 关闭连接
    @Override
    public void close() {
        String methodName = Thread.currentThread().getStackTrace()[1]
                .getMethodName();
        logger.info(this, methodName, "close connection");
        try {
            this.connection.close();
            logger.info(this, methodName, "close connection suss");
        }catch (Exception e ) {
            logger.info(this, methodName, "close connection,error:"+e.getMessage());
            e.printStackTrace();
        }
        //ds.release(this.pollConnection);
    }

    /**
     * 获取一个字段的值
     *
     * @param sql
     * @param fieldName
     * @return "null",查出的为空 ,"",查不到字段
     */
    public String getFieldValue(String sql, String fieldName) {
        String result = "";

        if(this.connection != null) {
            try(Statement stmt = this.connection.createStatement();) {
                logger.info(" getFieldValue,begin executeQuery:"+sql);
                try(ResultSet rs = stmt.executeQuery(sql);) {
                    logger.info(" getFieldValue,end executeQuery");
                    if (rs.next()) {
                        result = rs.getString(fieldName);
                    } else {
                        result = "";
                    }
                    if (null == result) {
                        result = "";
                    }
                    rs.close();
                }
                stmt.close();
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                logger.info(this, methodName, sql + ",E:" + e.getMessage());
                logger.info("getFieldValue error sql:"+sql);
                e.printStackTrace();
            }
        }

        return result;
    }

    // 返回是否连接
    public boolean isClosed() {
        if(this.connection == null) {
            return true;
        }

        try {
            return this.connection.isClosed();
        } catch (Exception e) {
            String methodName = Thread.currentThread().getStackTrace()[1]
                    .getMethodName();
            logger.info(this, methodName, "E:" + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    // 执行一条数据库语句，如update,delete等非select类型的语句
    public boolean execute(String sql) {
        boolean result = false;
        lastError="";
        
        if(this.connection != null) {
            try(Statement stmt = this.connection.createStatement();) {
                stmt.executeUpdate(sql);
                stmt.close();
                result = true;
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                logger.info(this, methodName, sql + "，E:" + e.getMessage());
                logger.info("jdbcdao.execute error sql:"+sql);
                lastError=e.getMessage();
                e.printStackTrace();
            }
        }

        return result;
    }

    // 执行一条数据库语句，如update,delete等非select类型的语句
    public boolean callProc(String procName) {
        boolean result = false;
        lastError="";

        if(this.connection != null) {
            try(CallableStatement stmt = this.connection.prepareCall("{call "+procName+"}")) {
                // 执行存储过程（无参数，无返回值）
                result = stmt.execute(); // 返回 false 表示没有结果集
                logger.info("jdbcdao.callProc:procName:"+procName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // 返回 首行记录
    public Map<String, String> getFirstRow(String sql) {
        Map<String, String> rowData = new HashMap<String, String>();
        if(this.connection != null) {
            try(Statement stmt = this.connection.createStatement();) {
                logger.info(" getFirstRow,begin executeQuery:"+sql);
                try(ResultSet rs = stmt.executeQuery(sql);) {
                    logger.info(" getFirstRow,end executeQuery");
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();

                    if (rs.next()) {
                        for (int i = 1; i <= count; i++) {
                            String value = rs.getString(i);
                            if (value == null) {
                                value = "";
                            }
                            rowData.put(rsmd.getColumnLabel(i).toUpperCase(), value);
                        }

                    }
                    rs.close();
                }
                stmt.close();
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                logger.info(this, methodName, sql + "，E:" + e.getMessage());
                logger.info("jdbcdao.getFirstRow error sql:"+sql);
                e.printStackTrace();
            }
        }
        return rowData;
    }

    /**
     * 执行SQL，获取单列多行或单行一个字段的值
     *
     * @param sqlList
     *            SQL组成的List
     * @param:flag
     *            true SQL执行后为三行，false SQL执行后为一行
     * @return
     */
    public Map<String, String> getListSqlValue(List<Map<String, String>> sqlList) {
        String result = "";
        Map<String, String> map = new HashMap<String, String>();
        if(this.connection != null) {
            Statement stmt = null;
            String sql = "";
            String fieldName = "";
            try {
                ResultSet rs = null;
                for (int i = 0; i < sqlList.size(); i++) {
                    sql = sqlList.get(i).get("SQL");
                    fieldName = sqlList.get(i).get("FIELDNAME");
                    stmt = this.connection.createStatement();
                    logger.info(" getListSqlValue,begin executeQuery:"+sql);
                    rs = stmt.executeQuery(sql);
                    logger.info(" getListSqlValue,end executeQuery");

                    if(rs.next()) {
                        result = rs.getString("COUNTS");
                    }
                    map.put(fieldName, result);

                }
                rs.close();
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                logger.error(this, methodName, sql + ",E:" + e.getMessage());
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * 返回记录集
     *
     * @param sql
     * @return
     */
    public ArrayList<HashMap<String, String>> getResult(String sql) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        if(this.connection != null) {
            try(Statement stmt = this.connection.createStatement();) {
                long execStartTime=CommonFunc.unixTime();
                logger.info(" getResult,begin executeQuery:"+sql);
                long execEndTime = CommonFunc.unixTime();

                try(ResultSet rs = stmt.executeQuery(sql);) {
                    logger.info(" getResult,end executeQuery");
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();
                    String[] arrColumn = new String[count];
                    for (int i = 1; i <= count; i++) {
                        arrColumn[i - 1] = rsmd.getColumnLabel(i).toUpperCase();
                    }

                    while (rs.next()) {
                        HashMap<String, String> rowData = new HashMap<String, String>();
                        for (int i = 1; i <= count; i++) {
                            rowData.put(arrColumn[i - 1], CommonFunc.getNotNullString(rs.getString(i)));
                        }
                        list.add(rowData);
                    }
                    rs.close();
                }

                long fetchEndTime=CommonFunc.unixTime();
                logger.info(" getResult,end fetch data. exectime:"+(execEndTime-execStartTime)
                        +",fetchtime:"+(fetchEndTime-execEndTime)
                        +",fetchsize:"+list.size());
                stmt.close();
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                logger.info("jdbcdao.getResult error sql:"+sql);
                logger.info(this, methodName, sql + "，E:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 返回记录集
     *
     * @param sql
     * @return
     */
    public LinkedHashMap<String,ArrayList<HashMap<String, String>>> getResultListByKey(String sql,String keyField) {
        LinkedHashMap<String,ArrayList<HashMap<String, String>>> mapResult = new LinkedHashMap<>();
        if(this.connection != null) {
            try(Statement stmt = this.connection.createStatement();) {
                logger.info(" getResultListByKey,begin executeQuery:"+sql);
                try(ResultSet rs = stmt.executeQuery(sql);) {
                    logger.info(" getResultListByKey,end executeQuery");

                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();

                    while (rs.next()) {
                        HashMap<String, String> rowData = new HashMap<String, String>();
                        for (int i = 1; i <= count; i++) {
                            String columnLabel = rsmd.getColumnLabel(i).toUpperCase();
                            String columnObject = rs.getString(i);
                            rowData.put(columnLabel, CommonFunc.getNotNullString(columnObject));
                        }

                        if (!mapResult.containsKey(rowData.get(keyField))) {
                            mapResult.put(rowData.get(keyField), new ArrayList<HashMap<String, String>>());
                        }

                        mapResult.get(rowData.get(keyField)).add(rowData);
                    }
                    rs.close();
                }
                stmt.close();
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                logger.info("jdbcdao.getResultListByKey error sql:"+sql);
                logger.info(this, methodName, sql + "，E:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return mapResult;
    }

    /**
     * 返回记录集
     *
     * @param sql
     * @return
     */
    public LinkedHashMap<String,HashMap<String, String>> getResultByKey(String sql,String keyField) {
        LinkedHashMap<String,HashMap<String, String>> mapResult = new LinkedHashMap<>();
        if(this.connection != null) {
            try(Statement stmt = this.connection.createStatement();) {

                logger.info(" getResultByKey,begin executeQuery:"+sql);
                try(ResultSet rs = stmt.executeQuery(sql);) {
                    logger.info(" getResultByKey,end executeQuery");

                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();

                    while (rs.next()) {
                        HashMap<String, String> rowData = new HashMap<String, String>();
                        for (int i = 1; i <= count; i++) {
                            String columnLabel = rsmd.getColumnLabel(i).toUpperCase();
                            String columnObject = rs.getString(i);
                            rowData.put(columnLabel, CommonFunc.getNotNullString(columnObject));
                        }
                        mapResult.put(rowData.get(keyField), rowData);
                    }
                    rs.close();
                }
                stmt.close();
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                System.out.println(sql);
                logger.error(this, methodName, sql + "，E:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return mapResult;
    }

    // 提交
    public void commit() throws SQLException {
        try {
            this.connection.commit();
        } catch (Exception e) {
            logger.info("jdbcdao.commit error,E:" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 回滚
    public void rollback() throws SQLException {
        try {
            this.connection.rollback();
        } catch (Exception e) {
            logger.info("jdbcdao.rollback error,E:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @Title: execTransaction
     * @Description:自动提交或回滚，并且关闭连接
     * @param commitFlag
     *            ：提交或回滚标志
     * @author 广州卓凡扶小冬
     * @throws
     */
    public void execTransaction(boolean commitFlag) {
        try {
            if(commitFlag) {
                this.commit();
            } else {
                this.rollback();
            }
            this.close();
        } catch (Exception e) {
            this.close();
            e.printStackTrace();
        }
    }

    public int queryPageCount(String sql) {
        int r = 0;
        String finalSql = "SELECT COUNT(*) AS C FROM (" + sql + ")";
        String a = this.getFieldValue(finalSql, "C");
        r = Integer.valueOf(a);
        return r;
    }

    public ArrayList<HashMap<String, String>> queryPage(String sql, int pageCurrent, int pageSize) throws Exception {
        String sqlPage = "";
        StringBuffer sba = new StringBuffer();
        int max = pageCurrent * pageSize;
        int min = (pageCurrent - 1) * pageSize;
        sba.append("SELECT * FROM ( SELECT row_.*,rownum ROWNUM_ FROM (" + sql);
        sba.append(") row_ where rownum <=" + String.valueOf(max));
        sba.append(") where rownum_>" + String.valueOf(min));
        sqlPage = sba.toString();
        return this.getResult(sqlPage);
    }

    /**
     * 获取序列ID
     * @return
     */
    public String getSeqNextVal() {
        String result= this.getFieldValue("SELECT SEQ.NEXTVAL NEXTVAL FROM DUAL", "NEXTVAL");
        if (StringUtils.isNotEmpty(result)){
            return result;
        }

        logger.info("jdbcdao.getSeqNextVal is empty");
        Random random = new Random();
        // 生成15位随机数，加上第一位（1-9）确保总共16位
        long firstDigit = random.nextInt(9) + 1; // 1-9
        long remainingDigits = random.nextLong() % 1000000000000000L; // 15位
        if (remainingDigits < 0) {
            remainingDigits = -remainingDigits; // 确保为正数
        }

        result=String.format("%d%015d", firstDigit, remainingDigits);
        return result;
    }

    /**
     * @Title: getLimitSql
     * @Description: 获取分页查询(oracle)
     * @param sql
     * @param from
     * @param to
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static String getLimitSql(String sql, int from, int to) {
        return "SELECT * FROM ( SELECT ROW_.*,rownum rownum_ FROM (" + sql
                + ") row_ where rownum <=" + String.valueOf(to)
                + ") where rownum_>" + String.valueOf(from);
    }

    // 执行一条数据库语句，如update,delete等非select类型的语句
    public boolean execute(String sql,ArrayList<String> listParam) {
        boolean result = false;
        CommonLogger commonLogger=CommonLogger.getInstance();

        if(this.connection != null) {
            try {
                try(PreparedStatement preparedStatement = this.connection.prepareStatement(sql);) {
                    for (int i = 0; i < listParam.size(); i++) {
                        preparedStatement.setString(i+1, listParam.get(i));
                    }
                    logger.info(" execute,begin executeQuery:" + sql);
                    preparedStatement.executeQuery();
                    logger.info(" execute,end executeQuery");

                    preparedStatement.close();
                }
                result = true;
            } catch (Exception e) {
                String methodName = Thread.currentThread().getStackTrace()[1]
                        .getMethodName();
                logger.info(this, methodName, sql + "，E:" + e.getMessage());
                logger.info("jdbcdao.execute,listParam:"+listParam.toString());
                e.printStackTrace();
            }
        }

        return result;
    }

    public String getLastError(){
        return lastError;
    }
}
