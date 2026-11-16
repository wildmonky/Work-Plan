package com.zhuofan.oms.entity.workplan.sub;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.oms.entity.workplan.WorkPlanOfSub;
import lombok.Getter;
import lombok.Setter;

/**
 * 计划检修单
 */
@Getter
@Setter
@TableName("MAINTENANCE_PLAN")
public class MaintenancePlan {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 配调工作计划id
     * {@link WorkPlanOfSub#getId()}
     */
    private String subId;

    /**
     * 检修单号
     */
    private String code;

    /**
     * 申请单位
     */
    private String requestUnit;

    /**
     * 停电设备
     */
    private String electricOffEquipments;

    /**
     * 检修类别
     */
    private String maintenanceType;

    /**
     * 批准停电开始时间
     */
    private String electricOffStartTime;

    /**
     * 批准复电结束时间
     */
    private String electricOffEndTime;

    /**
     * 工作内容
     */
    private String content;

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
