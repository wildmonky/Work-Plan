package com.zhuofan.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix=DatasourceConfig.PREFIX)
@Component
public class DatasourceConfig {
    public static final String PREFIX="spring.datasource.dynamic.datasource.master";
    private String url;
    private String password;
    private String username;
    private String driverClassName;
    private String autoCommit;
    private int minConn;
    private int maxConn;
    private int initConn;

    public static String getPREFIX(){
        return PREFIX;
    }

    public int getMinConn(){
        return minConn;
    }

    public void setMinConn(int minConn){
        this.minConn=minConn;
    }

    public int getMaxConn(){
        return maxConn;
    }

    public void setMaxConn(int maxConn){
        this.maxConn=maxConn;
    }

    public int getInitConn(){
        return initConn;
    }

    public void setInitConn(int initConn){
        this.initConn=initConn;
    }

    public String getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(String autoCommit) {
        this.autoCommit = autoCommit;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getpassword() {
        return password;
    }

    public void setpassword(String user) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }



}
