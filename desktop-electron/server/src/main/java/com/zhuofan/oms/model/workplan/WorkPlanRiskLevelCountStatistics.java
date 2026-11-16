package com.zhuofan.oms.model.workplan;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zhuofan.oms.enums.workplan.TypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作业风险统计总结
 * 模板
 * <div class="summary-title">📊 风险分析总结</div>
 *               <div class="summary-content">
 *                 <div class="summary-item">
 *                   <span class="summary-label">总体情况:</span>
 *                   <span class="summary-value">当前共有 <strong style="color: #00d4ff;">98</strong> 个作业任务，其中低风险作业占比最高（<strong style="color: #1890ff;">69.4%</strong>）</span>
 *                 </div>
 *                 <div class="summary-item">
 *                   <span class="summary-label">高风险预警:</span>
 *                   <span class="summary-value">发现 <strong style="color: #f5222d;">3</strong> 个中高风险作业，需重点关注继电保护类作业</span>
 *                 </div>
 *                 <div class="summary-item">
 *                   <span class="summary-label">作业类型分布:</span>
 *                   <span class="summary-value">技改（23个）、维护（12个）、验收（10个）为主要作业类型</span>
 *                 </div>
 * </div>
 */
@Getter
@Setter
public class WorkPlanRiskLevelCountStatistics {

    private List<WorkPlanRiskLevelCountModel> riskList;

    private List<WorkPlanTypeCountModel> typeList;

    /**
     * 替换变量1： 作业总数最高占比分析<br/>
     * 替换变量3： 高风险预警{@link highRiskTemplate}， 中高风险数，需重点关注作业类型<br/>
     * 替换变量4： 作业类型分布{@link top3TypeTemplate}， 作业类型最多的三个作业类型作业数量统计，如：技改（23个）、维护（12个）、验收（10个）为主要作业类型<br/>
     */
    private final String template = "<div class=\"summary-title\">\uD83D\uDCCA 风险分析总结</div>\n" +
            "<div class=\"summary-content\">\n" +
            " %s" +
            " %s" +
            " %s" +
            "</div>";

    /**
     * 替换变量1： 作业总数量，如：98<br/>
     * 替换变量2： {@link maxLevelStrTemplate}，如：，其中低风险作业占比最高（<strong style="color: #1890ff;">69.4%</strong>）<br/>
     */
    private final String statisticsTemplate = " <div class=\"summary-item\">\n" +
            "  <span class=\"summary-label\">总体情况:</span>\n" +
            "  <span class=\"summary-value\">当前共有 <strong style=\"color: #00d4ff;\">%d</strong> 个作业任务%s</span>\n" +
            " </div>\n";

    /**
     * 替换变量：
     * 1-风险等级：高风险、中风险、低风险、可接受；
     * 2-占比：如50.0%，保留一位小数
     */
    private final String maxLevelStrTemplate = "，其中%s作业占比最高（<strong style=\"color: #1890ff;\">%s</strong>）";

    /**
     * 替换变量：
     *  1-{@link highRiskStrTemplate}
     */
    private final String highRiskTemplate = "<div class=\"summary-item\">\n" +
            "  <span class=\"summary-label\">中高风险预警:</span>\n" +
            "  <span class=\"summary-value\">%s</span>\n" +
            " </div>\n";

    /**
     * 替换变量：<br/>
     * 1- 中高风险作业数，如：10 <br/>
     * 2- ，需重点关注{workType}作业。如：，需重点关注继电保护类作业
     */
    private final String highRiskStrTemplate = "发现 <strong style=\"color: #f5222d;\">%d</strong> 个中高风险作业%s";

    /**
     * 替换变量：<br/>
     * 作业类型(作业数量)为主要作业类型。如：技改（23个）、维护（12个）、验收（10个）为主要作业类型
     */
    private final String top3TypeTemplate = " <div class=\"summary-item\">\n" +
            "  <span class=\"summary-label\">作业类型分布:</span>\n" +
            "  <span class=\"summary-value\">%s</span>\n" +
            " </div>\n";

    @JsonValue
    @Override
    public String toString() {
        String param1 = riskStatistics();
        Map<String, String> paramMap = typeStatistics();
        String param2 = paramMap.get("param2");
        String param3 = paramMap.get("param3");

        return String.format(template, param1, param2, param3);
    }

    /**
     * 统计作业总数和风险占比
     * @return 页面展示字符
     */
    private String riskStatistics() {
        int total = 0;
        String template = this.statisticsTemplate;

        if (CollectionUtils.isEmpty(riskList)) {
            return String.format(template, total, "");
        }

        WorkPlanRiskLevelCountModel maxCountModel = null;
        for (WorkPlanRiskLevelCountModel model : riskList) {
            total += model.getCount();
            if (maxCountModel == null) {
                maxCountModel = model;
                continue;
            }

            if (maxCountModel.getCount() == model.getCount()) {
                if (maxCountModel.getRiskLevel() > model.getRiskLevel()) {
                    maxCountModel = model;
                }
            }

            if (maxCountModel.getCount() < model.getCount()) {
                maxCountModel = model;
            }
        }

        BigDecimal maxRiskPercent = BigDecimal.valueOf(maxCountModel.getCount())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);

        String maxRiskStr = String.format(maxLevelStrTemplate, maxCountModel.getRisk(), maxRiskPercent + "%");
        return String.format(template, total, maxRiskStr);
    }

    /**
     * 作业类型统计
     * @return 替换变量名， 替换字符串
     */
    private Map<String, String> typeStatistics() {
        HashMap<String, String> reMap = new HashMap<>();
        reMap.put("param2", "");
        reMap.put("param3", "");

        if (CollectionUtils.isEmpty(typeList)) {
            return reMap;
        }

        List<Integer> allTypeCodes = Arrays.stream(TypeEnum.values()).map(TypeEnum::getCode).collect(Collectors.toList());
        // 存在中高风险项
        List<WorkPlanTypeCountModel> mediumAndHighList = typeList.stream().filter(e ->
                allTypeCodes.contains(e.getWorkTypeCode()) && (e.getMediumRiskCount() != 0 || e.getHighRiskCount() != 0)
        ).collect(Collectors.toList());
        String param2 = "无";
        if (!mediumAndHighList.isEmpty()) {
            // 中高风险作业总数
            long sum = mediumAndHighList.stream().mapToLong(e -> {
                int high = Optional.ofNullable(e.getHighRiskCount()).orElse(0);
                int medium = Optional.ofNullable(e.getMediumRiskCount()).orElse(0);
                return high + medium;
            }).sum();
            // 中高风险类型
            String types = mediumAndHighList.stream().map(WorkPlanTypeCountModel::getWorkType).collect(Collectors.joining("、"));

            param2 = String.format(this.highRiskStrTemplate, sum, sum == 0L ? "" : String.format("，需重点关注%s作业", types));
        }
        reMap.put("param2",String.format(this.highRiskTemplate, param2));

        // 作业类型分布
        List<WorkPlanTypeCountModel> sortedList = typeList.stream()
                .filter(e -> !"总计".equals(e.getWorkType()))
                .sorted(Comparator.comparing(WorkPlanTypeCountModel::getCount).reversed())
                .collect(Collectors.toList());

        String param3 = "无";
        if (!sortedList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, sortedList.size()); i++) {
                WorkPlanTypeCountModel model = sortedList.get(i);
                sb.append(model.getWorkType())
                        .append("（").append(model.getCount()).append("）");
            }
            param3 = sb.append("为主要作业类型").toString();
        }
        param3 = String.format(this.top3TypeTemplate, param3);
        reMap.put("param3", param3);
        return reMap;
    }

}
