package com.zhuofan.oms.entity.workplan.sub;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.oms.entity.workplan.WorkPlanOfSub;
import lombok.Getter;
import lombok.Setter;

/**
 * 带电作业单
 */
@Getter
@Setter
@TableName("LIVE_WORK_PERMIT")
public class LiveWorkPermit {

    @TableId(type = IdType.ASSIGN_ID)
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
     * 区/县局
     */
    private String unit;

    /**
     * 退重合闸线路
     */
    private String lines;

    /**
     * 状态
     */
    private String status;

    /**
     * 退重合闸原因
     */
    private String reason;

    /**
     * 批复退出合闸时间
     */
    private String switchOffTime;

    /**
     * 批复投入合闸时间
     */
    private String switchOnTime;

    /**
     * 方式意见
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
