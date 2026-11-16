package com.zhuofan.core.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName="SYS_QUESTION",comment="角色权限信息")
public class SysQuestion extends BaseModel {

    @TableField(name="ID",isPrimaryKey=true)
    private String id;      
    
    @TableField(name="USERNAME")
    private String userName; 
    
    @TableField(name="TEL")
    private String tel;      
    
    @TableField(name="BAK")
    private String bak;      
    
    @TableField(name="STIME")
    private String stime;      
    
    @TableField(name="ETIME")
    private String etime;      
    
    @TableField(name="BETIME")
    private String betime;      
    
    @TableField(name="USERID")
    private String userid;      
    
    @TableField(name="ZYDJ")
    private String zydj;      
    
    @TableField(name="HF")
    private String hf;
    
    @TableField(name="MOUBLE")
    private String mouble;
    
    @TableField(name="ISCOMPLETE")
    private String iscomplete;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.userName = userName;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.tel = tel;
    }
    public String getBak() {
        return bak;
    }
    public void setBak(String bak) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.bak = bak;
    }
    public String getStime() {
        return stime;
    }
    public void setStime(String stime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.stime = stime;
    }
    public String getEtime() {
        return etime;
    }
    public void setEtime(String etime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.etime = etime;
    }
    public String getBetime() {
        return betime;
    }
    public void setBetime(String betime) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.betime = betime;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.userid = userid;
    }
    public String getZydj() {
        return zydj;
    }
    public void setZydj(String zydj) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.zydj = zydj;
    }
    public String getHf() {
        return hf;
    }
    public void setHf(String hf) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());       
        this.hf = hf;
    }
    public String getMouble() {
        return mouble;
    }
    public void setMouble(String mouble) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());     
        this.mouble = mouble;
    }
    public String getIscomplete() {
        return iscomplete;
    }
    public void setIscomplete(String iscomplete) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());     
        this.iscomplete = iscomplete;
    }
}
