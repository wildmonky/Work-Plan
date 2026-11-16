package com.zhuofan.oms.model.workplan;

import com.fasterxml.jackson.annotation.JsonValue;
import com.zhuofan.oms.enums.workplan.RiskLevelEnum;
import com.zhuofan.oms.orm.model.TransformerSubstation;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 站点建议
 *
 * <div class="summary-title">🏭 厂站作业总结</div>
 *               <div class="summary-content">
 *                 <div class="summary-item">
 *                   <span class="summary-label">监控范围:</span>
 *                   <span class="summary-value">共监控 <strong style="color: #00d4ff;">12</strong> 个关键重要厂站，包括500kV站7个、220kV站4个、110kV站1个</span>
 *                 </div>
 *                 <div class="summary-item">
 *                   <span class="summary-label">作业热点:</span>
 *                   <span class="summary-value"><strong style="color: #faad14;">500kV东莞站</strong>作业最多（5个），<strong style="color: #f5222d;">220kV伯治站</strong>可接受风险作业最多（8个）</span>
 *                 </div>
 *                 <div class="summary-item">
 *                   <span class="summary-label">风险分布:</span>
 *                   <span class="summary-value">高风险作业1个（东莞站），中风险作业7个，主要集中在500kV站点</span>
 *                 </div>
 *                 <div class="summary-item">
 *                   <span class="summary-label">重点关注:</span>
 *                   <span class="summary-value">东莞站标注为重点关注对象，需加强现场安全监督</span>
 *                 </div>
 *               </div>
 */

@Getter
@Setter
public class WorkPlanStationRiskLevelStatistics {

    private List<WorkPlanStationRiskLevelCountModel> stationList;

    // 重要厂站
    private List<TransformerSubstation> importantStationList;

    private final String template = "<div class=\"summary-title\">\uD83C\uDFED 关键重要厂站作业总结</div>\n" +
            " <div class=\"summary-content\">\n" +
            "%s" +
            "%s" +
            "%s" +
            "</div>";

    /**
     * 替换变量：共监控<strong style="color: #00d4ff;">12</strong> 个关键重要厂站，包括500kV站7个、220kV站4个、110kV站1个
     */
    private final String param1 = "<div class=\"summary-item\">\n" +
            "     <span class=\"summary-label\">监控范围:</span>\n" +
            "     <span class=\"summary-value\">%s</span>\n" +
            "   </div>\n";

    /**
     * 替换变量：<strong style="color: #faad14;">500kV东莞站</strong>作业最多（5个），<strong style="color: #f5222d;">220kV伯治站</strong>可接受风险作业最多（8个）
     */
    private final String param2 = "<div class=\"summary-item\">\n" +
            "      <span class=\"summary-label\">作业热点:</span>\n" +
            "      <span class=\"summary-value\">%s</span>\n" +
            "   </div>\n";

    /**
     * 替换变量：高风险作业1个（东莞站），中风险作业7个，主要集中在500kV站点
     */
    private final String param3 = "<div class=\"summary-item\">\n" +
            "      <span class=\"summary-label\">风险分布:</span>\n" +
            "      <span class=\"summary-value\">%s</span>\n" +
            "   </div>\n";

    @JsonValue
    @Override
    public String toString() {
        return String.format(template, param1(), param2(), param3());
    }

    private String param1() {
        String param1 = "无";

        if (CollectionUtils.isEmpty(importantStationList)) {
            return String.format(this.param1, param1);
        }

        int total = importantStationList.size();

        List<String> voltages = Arrays.asList("500", "220", "110");
        Map<String, List<String>> substationNumMap = importantStationList.stream().collect(Collectors.groupingBy(e -> {
            String substationName = e.getVoltage();
            for (String voltage : voltages) {
                if (substationName.contains(voltage)) {
                    return voltage;
                }
            }
            return "";
        }, Collectors.mapping(e -> e.getName() + "站", Collectors.toList())));

        List<String> arr = new ArrayList<>();
        for (String voltage : voltages) {
            List<String> stations = substationNumMap.getOrDefault(voltage, Collections.emptyList());
            if (stations.isEmpty()) {
                continue;
            }
            arr.add(voltage + "kV变电站" + stations.size() + "个（" + String.join("、", stations) + "）");
        }

        String mes = arr.isEmpty() ? "" : "，包括" + String.join("，", arr);
        param1 = String.format("共监控<strong style=\"color: #00d4ff;\">%s</strong> 个关键重要厂站%s",
                total,
                mes
        );

        return String.format(this.param1, param1);
    }

    private String param2() {
        String param = "无";

        if (CollectionUtils.isEmpty(stationList)) {
            return String.format(this.param2, param);
        }

        param = "<strong style=\"color: #faad14;\">%s</strong>作业（%s个）";

        Map<Long, List<String>> reMap = new HashedMap<>();
        // 变电站作业数统计
        Map<String, Long> stationPlanCountMap = stationList.stream().collect(Collectors.groupingBy(WorkPlanStationRiskLevelCountModel::getSubstationName, Collectors.summingLong(WorkPlanRiskLevelCountModel::getCount)));
        for (Map.Entry<String, Long> entry : stationPlanCountMap.entrySet()) {
            String substationName = entry.getKey();
            Long count = Optional.ofNullable(entry.getValue()).orElse(0L);
            List<String> strArr = reMap.computeIfAbsent(count, k -> new ArrayList<>());
            strArr.add(String.format(param, substationName, count));
        }

        List<String> reList = new ArrayList<>();
        List<Long> sorted = reMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for (Long num : sorted) {
            List<String> list = Optional.ofNullable(reMap.get(num)).orElseGet(Collections::emptyList);
            reList.addAll(list);
        }

        return String.format(this.param2, String.join("、", reList));
    }

    private String param3() {
        String param = "无";

        if (CollectionUtils.isEmpty(stationList)) {
            return String.format(this.param3, param);
        }

        // 风险 -- <变电站，作业数>
        Map<Integer, Map<String, Integer>> stationRiskCountMap = stationList.stream()
                .filter(e -> e.getRiskLevel() <= RiskLevelEnum.MEDIUM.getCode())
                .collect(Collectors.groupingBy(WorkPlanStationRiskLevelCountModel::getRiskLevel,
                        Collectors.toMap(WorkPlanStationRiskLevelCountModel::getSubstationName, WorkPlanRiskLevelCountModel::getCount)
                ));

        StringBuilder sb = new StringBuilder();
        appendMessage(sb, stationRiskCountMap, RiskLevelEnum.HIGH);
        appendMessage(sb, stationRiskCountMap, RiskLevelEnum.MEDIUM);
        return String.format(this.param3, sb);
    }

    private void appendMessage(StringBuilder sb, Map<Integer, Map<String, Integer>> staionRiskCountMap, RiskLevelEnum riskLevelEnum) {
        Map<String, Integer> mediumRiskMap = staionRiskCountMap.get(riskLevelEnum.getCode());
        if (MapUtils.isNotEmpty(mediumRiskMap)) {
            int total = mediumRiskMap.values().stream().mapToInt(e -> e).sum();
            sb.append(riskLevelEnum.getDesc()).append("作业").append(total).append("个");
            Map<Integer, List<String>> detailMap = new HashedMap<>();
            for (Map.Entry<String, Integer> entry : mediumRiskMap.entrySet()) {
                String substation = entry.getKey();
                Integer numInRisk = Optional.ofNullable(entry.getValue()).orElse(0);
                List<String> detail = detailMap.computeIfAbsent(numInRisk, k -> new ArrayList<>());
                detail.add(substation + numInRisk + "个");
            }
            if (!detailMap.isEmpty()) {
                List<Integer> sorted = detailMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                sb.append("(");
                sorted.forEach(e -> {
                    List<String> strArr = detailMap.get(e);
                    if (CollectionUtils.isEmpty(strArr)) {
                        return;
                    }
                    sb.append(String.join("、", strArr));
                });
                sb.append(")");
            }
            sb.append("，");
        }
    }

}
