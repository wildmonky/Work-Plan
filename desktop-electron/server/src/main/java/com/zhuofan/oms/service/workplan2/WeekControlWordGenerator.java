package com.zhuofan.oms.service.workplan2;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * 周管控会材料生成组件
 */
@Component
public class WeekControlWordGenerator {

    @Resource
    private WorkPlanAutoDataService workPlanAutoDataService;

    /**
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param lastWeekStartDate 上周开始日期
     * @param lastWeekEndDate 上周结束日期
     */
    public void generate(LocalDate startDate, LocalDate endDate, LocalDate lastWeekStartDate, LocalDate lastWeekEndDate) {
        // 上周作业计划统计
        lastWeekPlanStatistics(lastWeekStartDate, lastWeekEndDate);

        // 本周作业计划统计
        List<WorkPlanDataAutoModel> currentWeekPlans = searchPlans(startDate, endDate);
    }

    /**
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return [{@code startDate}, {@code endDate + 1})范围内的作业计划列表
     */
    private List<WorkPlanDataAutoModel> searchPlans(LocalDate startDate, LocalDate endDate) {
        return Collections.emptyList();
    }

    /**
     * 上周作业计划统计
     *
     * @param lastWeekStartDate 上周开始日期
     * @param lastWeekEndDate 上周结束日期
     */
    public void lastWeekPlanStatistics(LocalDate lastWeekStartDate, LocalDate lastWeekEndDate) {

        List<WorkPlanDataAutoModel> lastWeekPlans = searchPlans(lastWeekStartDate, lastWeekEndDate);
    }

    /**
     * 本周作业计划统计
     *
     * @param startDate 本周开始日期
     * @param endDate 本周结束日期
     */
    public void currentWeekPlanStatistics(LocalDate startDate, LocalDate endDate) {
        List<WorkPlanDataAutoModel> lastWeekPlans = searchPlans(startDate, endDate);
    }

}
