package com.zhuofan.oms.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName = "OMS_JZB")
public class OmsJzb  extends BaseModel {
    @TableField(name = "ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
    private String id;

    @TableField(name = "DYDJ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String dydj;

    @TableField(name = "NAME", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String name;

    @TableField(name = "EQUIPMENT", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String equipment;

    @TableField(name = "ZCXLZ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String zcxlz;

    @TableField(name = "HXZ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String hxz;

    @TableField(name = "DEPART", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String depart;

    @TableField(name = "JTYPE", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String jtype;

    @TableField(name = "REMARK", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String remark;

    @TableField(name = "EDDL", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String eddl;

    @TableField(name = "ID_P", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String idP;

    @TableField(name = "ID_Q", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String idQ;

    @TableField(name = "ID_I", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String idI;

    @TableField(name = "SGXLZ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String sgxlz;

    @TableField(name = "FLAG_HIDE", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String flagHide;

    @TableField(name = "SFJXDM", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String sfjxdm;

    @TableField(name = "YXQ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String yxq;

    @TableField(name = "KZZ", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String kzz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.id = id;
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

    public String getHxz() {
        return hxz;
    }

    public void setHxz(String hxz) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.hxz = hxz;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.depart = depart;
    }

    public String getJtype() {
        return jtype;
    }

    public void setJtype(String jtype) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.jtype = jtype;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.remark = remark;
    }

    public String getEddl() {
        return eddl;
    }

    public void setEddl(String eddl) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.eddl = eddl;
    }


    public String getSgxlz() {
        return sgxlz;
    }

    public void setSgxlz(String sgxlz) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sgxlz = sgxlz;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.equipment = equipment;
    }

    public String getIdP() {
        return idP;
    }

    public void setIdP(String idP) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.idP = idP;
    }

    public String getIdQ() {
        return idQ;
    }

    public void setIdQ(String idQ) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.idQ = idQ;
    }

    public String getIdI() {
        return idI;
    }

    public void setIdI(String idI) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.idI = idI;
    }

    public String getFlagHide() {
        return flagHide;
    }

    public void setFlagHide(String flagHide) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flagHide = flagHide;
    }


    public String getSfjxdm() {
        return sfjxdm;
    }

    public void setSfjxdm(String sfjxdm) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.sfjxdm = sfjxdm;
    }

    public String getYxq() {
        return yxq;
    }

    public void setYxq(String yxq) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.yxq = yxq;
    }

    public String getKzz() {
        return kzz;
    }

    public void setKzz(String kzz) {
        this.kzz = kzz;
    }
}
