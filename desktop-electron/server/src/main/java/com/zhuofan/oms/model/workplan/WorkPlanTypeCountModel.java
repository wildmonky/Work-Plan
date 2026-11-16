package com.zhuofan.oms.model.workplan;

import com.zhuofan.oms.enums.workplan.TypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 计划作业类型{@link TypeEnum}统计
 */
@Getter
@Setter
public class WorkPlanTypeCountModel {

    private int workTypeCode;

    private String workType;

    private Long count;

    private List<WorkPlanRiskLevelCountModel> riskLevelCount;

    private Integer veryHighRiskCount;

    private Integer highRiskCount;

    private Integer mediumRiskCount;

    private Integer lowRiskCount;

    private Integer acceptableRiskCount;

}
