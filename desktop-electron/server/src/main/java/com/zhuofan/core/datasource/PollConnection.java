/**
 * com.zhuofan.core.orm
 *
 *
 *   ver     date           author
 *           2015-05-25      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.datasource;

import java.sql.Connection;

/**
 * ClassName:JoinOrmSupport
 * 多表关联ORM
 *
 * @author   fuxiaodong
 * @Date     2015-05-25
 */
public  class PollConnection {
    //连接对象
    private Connection connection;
    //初始连接时间(unix)
    private long initTime;
    //是否正在使用
    private boolean busy;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

}
