package com.zhuofan.oms.orm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;
import com.zhuofan.oms.service.workplan2.WorkPlanDataModel;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 作业计划饱和度系数
 */
@Getter
//@Setter
@TableName(value = "WORK_PLAN_LOAD_RATIO", excludeProperty = {"changeInfo", "extraInfo", "extraInfo1"})
@Table(tableName = "WORK_PLAN_LOAD_RATIO")
public class WorkPlanLoadRatio extends BaseModel {

    @TableField(name = "ID", type = TableField.TF_NUMBER, isPrimaryKey = true)
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作类别 {@link WorkPlanDataModel#getWorkType()}
     */
    @TableField(name = "WORK_TYPE", type = TableField.TF_VARCHAR)
    private String workType;

    /**
     * 饱和度系数
     */
    @TableField(name = "RATIO", type = TableField.TF_BIGDECIMAL)
    private BigDecimal ratio;

    public void setId(Long id) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.id = id;
    }

    public void setWorkType(String workType) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.workType = workType;
    }

    public void setRatio(BigDecimal ratio) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.ratio = ratio;
    }
}
