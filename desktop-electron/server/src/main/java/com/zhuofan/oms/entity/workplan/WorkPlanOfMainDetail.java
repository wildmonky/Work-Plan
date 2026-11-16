package com.zhuofan.oms.entity.workplan;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 主调工作计划子表
 */
@Getter
@Setter
@TableName("WORK_PLAN_OF_MAIN_DETAIL")
public class WorkPlanOfMainDetail {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * {@link WorkPlanOfMain#getId()}
     */
    private String mainId;

    /**
     * 操作内容（关键设备停、送电及方式调整）
     */
    private String operateContent;

    /**
     * 项目管理单位/分部
     */
    private String manageDepartment;

    /**
     * 计划开始时间（送电/方式恢复）
     */
    private String planStartTime;

    /**
     * 计划结束时间（送电/方式恢复）
     */
    private String planEndTime;

    /**
     * 检修内容
     */
    private String maintenanceContent;

    /**
     * 主要方式调整
     */
    private String mainMethodAdjustment;

    /**
     * 保护、安置投退/定值修改
     */
    private String modify;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 操作风险点
     */
    private String riskPoint;

    /**
     * 检修单管控
     */
    private String maintenanceListControl;

    /**
     * 监督管理人员
     */
    private String manager;

}
