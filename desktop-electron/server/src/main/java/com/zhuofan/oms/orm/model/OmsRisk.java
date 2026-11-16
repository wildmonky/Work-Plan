package com.zhuofan.oms.orm.model;

import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

@Table(tableName = "OMS_RISK")
public class OmsRisk extends BaseModel {
    @TableField(name = "RISK_ID", type = TableField.TF_VARCHAR, isPrimaryKey = true)
    private String riskId;

    @TableField(name = "TIME_FROM", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String timeFrom;

    @TableField(name = "TIME_TO", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String timeTo;

    @TableField(name = "RISK_LEVEL", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String riskLevel;

    @TableField(name = "RISK_NO", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String riskNo;

    @TableField(name = "TITLE", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String title;

    @TableField(name = "CONTENT", type = TableField.TF_VARCHAR, isPrimaryKey = false)
    private String content;

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.riskId = riskId;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.timeTo = timeTo;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.riskLevel = riskLevel;
    }

    public String getRiskNo() {
        return riskNo;
    }

    public void setRiskNo(String riskNo) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.riskNo = riskNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.content = content;
    }
}
