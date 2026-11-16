package com.zhuofan.oms.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName = "OMS_JZB_EDIT")
public class OmsJzbEdit  extends BaseModel {
    @TableField(name = "OMS_JZB_ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
    private String omsJzbId;

    @TableField(name = "DYDJ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String dydj;

    @TableField(name = "NAME", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String name;

    @TableField(name = "EQUIPMENT", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String equipment;

    @TableField(name = "ZCXLZ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String zcxlz;

    @TableField(name = "SFTJ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String sftj;

    @TableField(name = "KZZ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String kzz;

    @TableField(name = "SFYC", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String sfyc;

    @TableField(name = "EDDL", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String eddl;

    @TableField(name = "SFJXDM", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String sfjxdm;

    @TableField(name = "HXZ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String hxz;

    @TableField(name = "YXQ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String yxq;

    @TableField(name = "ID_I", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String idI;

    @TableField(name = "JTYPE", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String jtype;

    public String getOmsJzbId() {
        return omsJzbId;
    }

    public void setOmsJzbId(String omsJzbId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.omsJzbId = omsJzbId;
    }

    public String getDydj() {
        return dydj;
    }

    public void setDydj(String dydj) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.dydj = dydj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.name = name;
    }


    public String getZcxlz() {
        return zcxlz;
    }

    public void setZcxlz(String zcxlz) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.zcxlz = zcxlz;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.equipment = equipment;
    }

    public String getSfyc() {
        return sfyc;
    }

    public void setSfyc(String sfyc) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sfyc = sfyc;
    }

    public String getKzz() {
        return kzz;
    }

    public void setKzz(String kzz) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.kzz = kzz;
    }


    public String getSftj() {
        return sftj;
    }

    public void setSftj(String sftj) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sftj = sftj;
    }

    public String getEddl() {
        return eddl;
    }

    public void setEddl(String eddl) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.eddl = eddl;
    }

    public String getSfjxdm() {
        return sfjxdm;
    }

    public void setSfjxdm(String sfjxdm) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sfjxdm = sfjxdm;
    }

    public String getHxz() {
        return hxz;
    }

    public void setHxz(String hxz) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.hxz = hxz;
    }

    public String getYxq() {
        return yxq;
    }

    public void setYxq(String yxq) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.yxq = yxq;
    }

    public String getIdI() {
        return idI;
    }

    public void setIdI(String idI) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.idI = idI;
    }

    public String getJtype() {
        return jtype;
    }

    public void setJtype(String jtype) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.jtype = jtype;
    }
}
