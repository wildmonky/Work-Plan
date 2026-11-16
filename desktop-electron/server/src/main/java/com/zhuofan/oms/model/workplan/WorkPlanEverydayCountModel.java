package com.zhuofan.oms.model.workplan;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class WorkPlanEverydayCountModel {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer total;

    private List<WorkPlanRiskLevelCountModel> riskAndNumCount;

    private Integer average;

    private Integer previousWeekAverage;

    private String previousThreeMonthAverageName;

    private Integer previousThreeMonthAverage;

}
