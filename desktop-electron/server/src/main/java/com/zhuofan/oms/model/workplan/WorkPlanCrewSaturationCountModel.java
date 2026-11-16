package com.zhuofan.oms.model.workplan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 班组饱和度model, 各级风险作业统计 前端使用
 */
@Getter
@Setter
public class WorkPlanCrewSaturationCountModel {

    private LocalDate date;

    private Long crewId;

    private String crewName;

    /**
     * 负责人饱和度
     */
    private BigDecimal responsiblePersonSaturation;

    /**
     * 工作人员饱和度
     */
    private BigDecimal memberSaturation;

    /**
     * 对应风险等级,作业数量统计
     */
    private List<WorkPlanRiskLevelCountModel> riskAndNumCount;

    private Integer totalCount;

    private Integer veryHighCount;

    private Integer highCount;

    private Integer mediumCount;

    private Integer lowCount;

    private Integer acceptableCount;

}
