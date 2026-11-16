package com.zhuofan.core.orm;

/**
 * ClassName:BaseRestModel
 *
 * @author   fuxiaodong
 * @Date     2015-05-19
 */

public class BaseRestModel {
    protected String sysTitle;
    protected String pageTitle;
    protected String sysVersion;

    public void setSysTitle(String sysTitle){
        this.sysTitle=sysTitle;
    }

    public String getSysTitle(){
        return this.sysTitle;
    }

    public void setPageTitle(String pageTitle){
        this.pageTitle=pageTitle;
    }

    public String getPageTitle(){
        return this.pageTitle;
    }

    public void setSysVersion(String sysVersion){
        this.sysVersion=sysVersion;
    }

    public String getSysVersion(){
        return this.sysVersion;
    }
}
