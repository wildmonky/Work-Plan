package com.zhuofan.oms.orm.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;

/**
 * 变电站 实体
 */
@TableName(value = "transformer_substation", excludeProperty = {"changeInfo", "extraInfo", "extraInfo1"})
@Table(tableName = "transformer_substation")
public class TransformerSubstation extends BaseModel {

    @TableId(type = IdType.AUTO)
    @com.zhuofan.core.orm.TableField(name = "ID", type = com.zhuofan.core.orm.TableField.TF_NUMBER, isPrimaryKey = true)
    private Long id;

    /**
     * 所属工作单位
     * {@link WorkUnit#getId()}
     */
    @com.zhuofan.core.orm.TableField(name = "UNIT_ID", type = com.zhuofan.core.orm.TableField.TF_NUMBER)
    private Long unitId;

    // 电压
    @com.zhuofan.core.orm.TableField(name = "VOLTAGE")
    private String voltage;

    // 变电站名称
    @com.zhuofan.core.orm.TableField(name = "NAME")
    private String name;

    // 匹配正则
    @com.zhuofan.core.orm.TableField(name = "MATCH_PATTERN")
    private String matchPattern;

    // 所属镇区 如：樟木头镇
    @com.zhuofan.core.orm.TableField(name = "TOWN")
    private String town;

    /**
     * 负责继保班组
     * {@link Crew#getId()}
     */
    @com.zhuofan.core.orm.TableField(name = "CREW_JB_ID", type = com.zhuofan.core.orm.TableField.TF_NUMBER)
    private Long crewJbId;

    /**
     * 负责通信班组
     */
    @com.zhuofan.core.orm.TableField(name = "CREW_TX_ID", type = com.zhuofan.core.orm.TableField.TF_NUMBER)
    private Long crewTxId;

    @ExcelProperty("是否13+16")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @com.zhuofan.core.orm.TableField(name = "IS_THIRTEEN_ADD_SIXTEEN")
    private Boolean isThirteenAddSixteen;

    public Long getId() {
        return id;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.unitId = unitId;
    }

    public void setUnitId(long unitId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.unitId = unitId;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.voltage = voltage;
    }

    public String getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.matchPattern = matchPattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.name = name;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.town = town;
    }

    public Long getCrewJbId() {
        return crewJbId;
    }

    public void setCrewJbId(Long crewJBId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.crewJbId = crewJBId;
    }

    // BaseOrmSupport
    public void setCrewJbId(long crewJBId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.crewJbId = crewJBId;
    }

    public Long getCrewTxId() {
        return crewTxId;
    }

    public void setCrewTxId(Long crewTXId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.crewTxId = crewTXId;
    }

    // BaseOrmSupport
    public void setCrewTxId(long crewTXId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.crewTxId = crewTXId;
    }

    public Boolean getIsThirteenAddSixteen() {
        return isThirteenAddSixteen;
    }

    public void setIsThirteenAddSixteen(Boolean thirteenAddSixteen) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        isThirteenAddSixteen = thirteenAddSixteen;
    }

    public void setIsThirteenAddSixteen(String s) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        isThirteenAddSixteen = Boolean.parseBoolean(s) || "是".equals(s) || "yes".equalsIgnoreCase(s) || "1".equals(s);
    }
}
