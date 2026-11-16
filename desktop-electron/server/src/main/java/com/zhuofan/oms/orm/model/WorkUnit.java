package com.zhuofan.oms.orm.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * 工作单位 实体
 * 下辖 变电站
 * @see TransformerSubstation
 */
@TableName(value = "WORK_UNIT", excludeProperty = {"changeInfo", "extraInfo", "extraInfo1"})
@Table(tableName = "WORK_UNIT")
public class WorkUnit extends BaseModel {

    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    @TableField(name = "ID", type = TableField.TF_NUMBER, isPrimaryKey = true)
    private Long id;

    //如：变电管理一所
    @TableField(name = "UNIT")
    private String unit;

    //如：变一所
    @TableField(name = "SHORT_NAME")
    private String shortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.unit = unit;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.shortName = shortName;
    }
}
