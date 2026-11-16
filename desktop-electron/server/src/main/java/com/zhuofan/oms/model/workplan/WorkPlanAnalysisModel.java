package com.zhuofan.oms.model.workplan;

import com.zhuofan.oms.service.workplan2.WorkPlanDataAutoModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 前端首页数据模型
 */
@Getter
@Setter
public class WorkPlanAnalysisModel {

    // 作业状态分类统计
    private WorkPlanStatusCountModel workPlanStatusCountModel;

    // 日期分类统计
    private WorkPlanNumModel workPlanNumModel;

    // 风险分级分类统计
    private List<WorkPlanRiskLevelCountModel> workPlanRiskLevelCountModel;

    // 作业类型分类统计
    private List<WorkPlanTypeCountModel> workPlanTypeCountModel;

    private WorkPlanRiskLevelCountStatistics workPlanRiskLevelCountStatistics;

    // 重要厂站分类统计
    private List<WorkPlanStationRiskLevelCountModel> workPlanStationRiskLevelCountModel;

    private WorkPlanStationRiskLevelStatistics workPlanStationRiskLevelStatistics;

    // 重要厂站计划作业项
    private Map<String, List<WorkPlanDataAutoModel>> workPlanSubstationData;

}
