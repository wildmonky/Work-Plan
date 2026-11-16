package com.zhuofan.oms.model.workplan;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 作业计划-计划数-日期
 */
@Getter
@Setter
public class WorkPlanNumModel {

    private LocalDate beginDate;

    private LocalDate endDate;

    private List<WorkPlanEverydayCountModel> everydayCount;

    private Integer total = 0;

    private Integer average = 0;

    private String averageName;

    // 上三月平均值
    private Integer lastThreeMonthAverage = 0;

    private String lastThreeMonthAverageName;

    private Map<LocalDate, WorkPlanEverydayCountModel> everydayCountMap;

}
