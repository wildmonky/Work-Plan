package com.zhuofan.oms.entity.workplan.sub;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.oms.entity.workplan.WorkPlanOfSub;
import lombok.Getter;
import lombok.Setter;

/**
 * 转供电方式单
 */
@Getter
@Setter
@TableName("SECONDARY_POWER_SUPPLY_MODE")
public class SecondaryPowerSupplyMode {

    private String id;

    /**
     * 配调工作计划id
     * {@link WorkPlanOfSub#getId()}
     */
    private String subId;

    /**
     * 方式单号
     */
    private String code;

    /**
     * 区局
     */
    private String unit;

    /**
     * 转电操作任务
     */
    private String operateTask;

    /**
     * 执行状态
     */
    private String taskStatus;

    /**
     * 批复转出开始时间
     */
    private String powerOutStartTime;

    /**
     * 批准恢复开始时间
     */
    private String powerRecoverStartTime;

    /**
     * 是否合环
     */
    private String taskLoopClose;

    /**
     * 方式审批意见
     */
    private String approvalOpinion;

    /**
     * 是否涉及重要用户
     */
    private String isInvolveImportantUser;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 管控标准/要求
     */
    private String controlStandard;

    /**
     * 备注
     */
    private String comments;

}
