package com.zhuofan.oms.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName = "OMS_SUBSTATION")
public class OmsSubstation extends BaseModel {
    @TableField(name = "SUBSTATION_ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
    private String substationId;

    @TableField(name = "SUBSTATION_NAME", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String substationName;

    @TableField(name = "VOL_LEVEL", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String volLevel;

    @TableField(name = "XWZX", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String xwzx;


    @TableField(name = "BDS", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String bds;

    public String getSubstationId() {
        return substationId;
    }

    public void setSubstationId(String substationId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.substationId = substationId;
    }

    public String getSubstationName() {
        return substationName;
    }

    public void setSubstationName(String substationName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.substationName = substationName;
    }

    public String getVolLevel() {
        return volLevel;
    }

    public void setVolLevel(String volLevel) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.volLevel = volLevel;
    }

    public String getXwzx() {
        return xwzx;
    }

    public void setXwzx(String xwzx) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.xwzx = xwzx;
    }

    public String getBds() {
        return bds;
    }

    public void setBds(String bds) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.bds = bds;
    }
}
