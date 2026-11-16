package com.zhuofan.oms.entity.workplan;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 配调工作计划主表
 *
 * 子表：
 * @see com.zhuofan.oms.entity.workplan.sub.MaintenancePlan
 * @see com.zhuofan.oms.entity.workplan.sub.SecondaryPowerSupplyMode
 * @see com.zhuofan.oms.entity.workplan.sub.LiveWorkPermit
 */
@Getter
@Setter
@TableName("WORK_PLAN_OF_SUB")
public class WorkPlanOfSub {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 配调计划日期
     */
    private String date;

    /**
     * 上传文件名
     */
    private String uploadFileName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 如：计划检修单（6项，操作复杂0项）
     */
    private String ofMaintenance;

    /**
     * 如：转供电方式单（10项，配合配网9项、配合主网1项）
     */
    private String ofSecondary;

    /**
     * 如：带电作业单（2项）
     */
    private String ofLiveWork;

}
