package com.zhuofan.oms.orm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import lombok.ToString;

/**
 * 变电站负责班组 实体
 */
@ToString
@TableName(value = "CREW", excludeProperty = {"changeInfo", "extraInfo", "extraInfo1"})
@Table(tableName = "CREW")
public class Crew extends BaseModel {

    @TableId(type = IdType.AUTO)
    @TableField(name = "ID", type = TableField.TF_NUMBER, isPrimaryKey = true)
    private Long id;

    /**
     * 班组所在单位
     * {@link WorkUnit#getId()}
     */
    @TableField(name = "UNIT_ID", type = TableField.TF_NUMBER)
    private Long unitId;

    //班组名称
    @TableField(name = "NAME")
    private String name;

    // 班组类型，如：继保
    @TableField(name = "TYPE")
    private String type;

    // 简称,如：继一
    @TableField(name = "SHORT_NAME")
    private String shortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.type = type;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.shortName = shortName;
    }
}
