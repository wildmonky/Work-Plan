/**
 * com.zhoufan.core.datasource
 *
 *
 *   ver     date           author

 *           2015-05-15      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.util.SpringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ClassName:DataSource
 *
 * @author fuxiaodong
 * @Date 2015-05-15
 */
public class DataSource {
    protected CommonLogger logger = CommonLogger.getInstance();

    private static DataSource instance = null;

    private HikariDataSource hikariDataSource=null;

    private String url;
    private String user;
    private String password;
    private String driver;
    private int minConn = 1;
    private int maxConn = 100;
    private int initConn = 10;
    private int inUsed = 0;
    private String autoCommit;
    private int idleTime = 60*30*1000;

    private Timer timer;
    private static LinkedList<PollConnection> connPool = new LinkedList<PollConnection>();

    public String getUser(){
        return user;
    }

    private static synchronized void syncInit() {
        if(instance == null) {
            instance = new DataSource();
        }
    }

    // 获取实例
    public static DataSource getInstance() {
        if(instance == null) {
            syncInit();
        }
        return instance;
    }


    // 私有化构造函数
    private DataSource( ) {
        try {
            url = SpringUtils.getProperty("spring.datasource.dynamic.datasource.master.url");
            user = SpringUtils.getProperty("spring.datasource.dynamic.datasource.master.username");
            password = SpringUtils.getProperty("spring.datasource.dynamic.datasource.master.password");
            autoCommit = SpringUtils.getProperty("spring.datasource.auto-commit");
            driver = SpringUtils.getProperty("spring.datasource.driver-class-name");

            String sTmp;

            if(SpringUtils.containsProperty("spring.datasource.min-conn")) {
                sTmp = SpringUtils.getProperty("spring.datasource.min-conn");
                if(CommonFunc.isNumeric(sTmp)) {
                    minConn = Integer.parseInt(sTmp);
                }
            }

            if(SpringUtils.containsProperty("spring.datasource.max-conn")) {
                sTmp = SpringUtils.getProperty("spring.datasource.max-conn");
                if(CommonFunc.isNumeric(sTmp)) {
                    maxConn = Integer.parseInt(sTmp);
                }
            }

            if(SpringUtils.containsProperty("spring.datasource.init-conn")) {
                sTmp = SpringUtils.getProperty("spring.datasource.init-conn");
                if(CommonFunc.isNumeric(sTmp)) {
                    initConn = Integer.parseInt(sTmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try{
//            Class.forName("dm.jdbc.driver.DmDriver");
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setPoolName("OmsPoll");
        config.setMaximumPoolSize(maxConn);
        config.setIdleTimeout(1000*60*10);
        config.setMaxLifetime(1000*60*30);
        config.setMinimumIdle(20);
        config.setLeakDetectionThreshold(1000*60);
        config.setAutoCommit("1".equals(autoCommit));

        //connectionTimeout ：30000 ms (30秒)
        //idleTimeout:600000 ms (10分钟)
        //maxLifetime：1800000 ms (30分钟)

        config.setConnectionTimeout(1000*10);
        config.setIdleTimeout(600*1000);
        config.setMaxLifetime(1800*1000);


        hikariDataSource = new HikariDataSource(config);
        /*
        // 初始化连接池
        int i;

        for (i = 0; i < initConn; i++) {
            newConnection();
        }

        // 定时器
        timer = new Timer();
        timer.schedule(new checkConnService(), 1000 * 60 * 10, 1000 * 60 * 10);

        */
    }

    public HikariDataSource getHikariDataSource() {
        if (hikariDataSource==null){
            if(instance == null) {
                syncInit();
            }
        }
        return hikariDataSource;
    }

    // 创建新连接
    private synchronized boolean newConnection() {
        if(connPool.size() > maxConn) return false;

        String methodName = Thread.currentThread().getStackTrace()[1]
                .getMethodName();
        try {
            logger.debug(this, methodName, "init new connection begin");

            System.out.println(url+":"+user+":"+password);
            Class.forName(driver);
            logger.debug(this, methodName, "init new connection begin,class for name suss,begin getconnection");
            Connection connection = DriverManager.getConnection(url, user,
                    password);
            logger.debug(this, methodName, "init new connection begin,getConnection suss,begin setAutoCommit");
            connection.setAutoCommit(autoCommit.equals("1") ? true : false);
            logger.debug(this, methodName, "init new connection begin,setAutoCommit suss,begin add to poll");
            PollConnection pollConnection = new PollConnection();
            pollConnection.setConnection(connection);
            pollConnection.setBusy(false);
            pollConnection.setInitTime(CommonFunc.unixTime());
            connPool.add(pollConnection);

            logger.debug(this, methodName, "init new connection suss。");
            return true;
        } catch (SQLException e) {
            logger.error(this, methodName,
                    "init new connection error:" + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e1) {
            logger.error(this, methodName,
                    "init new connection error:" + e1.getMessage());
            e1.printStackTrace();
            return false;
        }
    }

    // 从连接池获取连接
    public synchronized PollConnection getConnection() {
        String methodName = Thread.currentThread().getStackTrace()[1]
                .getMethodName();

        if(connPool.size() > 0) {
            inUsed++;

            logger.debug(this, methodName, "get connection suss");

            PollConnection pollConnection = connPool.remove();
            pollConnection.setBusy(true);
            return pollConnection;
        }

        if (newConnection()) {
            return getConnection();
        }

        return null;
    }

    // 回收连接对象
    public synchronized void release(PollConnection pollConnection) {
        String methodName = Thread.currentThread().getStackTrace()[1]
                .getMethodName();

        System.out.println(pollConnection + "被回收");
        logger.debug(this, methodName, pollConnection + "被回收。");

        inUsed--;
        pollConnection.setBusy(false);
        connPool.addLast(pollConnection);
    }

    // 定时器
    // 定时检查连接是否连接上，如果未连接则删除
    public class checkConnService extends TimerTask {
        @Override
        public void run() {
            int i;
            if(connPool == null) return;

            int size = connPool.size();
            for (i = size - 1; i >= 0; i--) {
                Connection connection = connPool.get(i).getConnection();
                try {
                    if(connection.isClosed()) {
                        connPool.remove(i);
                    } else {
                        // 判断是否超时
                        PollConnection pollConnection = connPool.get(i);
                        if(pollConnection.isBusy() == false) {
                            if(pollConnection.getInitTime() >= CommonFunc
                                    .unixTime() - idleTime) {
                                pollConnection.getConnection().close();
                                connPool.remove(i);
                                System.out.println(pollConnection
                                        + "超时，被系统自动关闭。");
                                logger.debug(this, Thread.currentThread()
                                                .getStackTrace()[1].getMethodName(),
                                        pollConnection + "超时，被系统自动关闭。");
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
