package com.zhuofan.oms.model.workplan;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 班组饱和度统计
 *
 * <div class="summary-item">
 *               <span class="summary-label">班组概况:</span>
 *               <span class="summary-value">共监控 <strong style="color: #00d4ff;">20</strong> 个专业班组，涵盖变电继保、通信、自动化等专业</span>
 *             </div>
 *             <div class="summary-item">
 *               <span class="summary-label">负荷预警:</span>
 *               <span class="summary-value"><strong style="color: #f5222d;">变一继二</strong>工作负责人饱和度达警戒线(110%)，需合理调配人员</span>
 *             </div>
 *
 */
@Getter
@Setter
public class WorkPlanCrewSaturationCountStatistics {

    private List<WorkPlanCrewSaturationCountModel> loadRateModels;

    private String html = "";

    /**
     * 默认超过100需要调休，80要关注
     */
    @JsonIgnore
    private final int limit = 100;
    @JsonIgnore
    private final int close = 80;
    @JsonIgnore
    private final String template = "<div class=\"summary-item\">\n" +
            " <span class=\"summary-label\">班组概况:</span>\n" +
            " <span class=\"summary-value\">共监控 <strong style=\"color: #00d4ff;\">20</strong> 个专业班组，涵盖变电继保、通信、自动化等专业</span>\n" +
            "</div>\n" +
            "<div class=\"summary-item\">\n" +
            " <span class=\"summary-label\">负荷预警:</span>\n" +
            " <span class=\"summary-value\">%s</span>\n" +
            "</div>";

    @JsonIgnore
    private List<String> crewNameSortedList = Arrays.asList("变一继一", "变一继二", "变一继三", "变一继四", "变二继一", "变二继二", "变二继三", "变二继四", "变二继五", "变三继一", "变三继二", "变三继三", "变三继四", "通信一班", "通信二班", "通信三班", "通信网络班", "主调自动化", "配调自动化", "二次智能运维班");

    public String getHtml() {
        return toHtml();
    }

    private String toHtml() {
        String param = "无";

        if (CollectionUtils.isEmpty(this.loadRateModels)) {
            return String.format(this.template, param);
        }

        List<WorkPlanCrewSaturationCountModel> above = this.loadRateModels.stream()
                .filter(e -> e.getResponsiblePersonSaturation().compareTo(BigDecimal.valueOf(limit)) > -1)
                .collect(Collectors.toList());

        if (above.isEmpty()) {
            return String.format(this.template, param);
        }

        Map<String, WorkPlanCrewSaturationCountModel> overLimitLeaderMap = new HashMap<>();
        Map<String, WorkPlanCrewSaturationCountModel> overCloseLeaderMap = new HashMap<>();

        Map<String, WorkPlanCrewSaturationCountModel> overLimitWorkerMap = new HashMap<>();
        Map<String, WorkPlanCrewSaturationCountModel> overCloseWorkerMap = new HashMap<>();

        Map<String, List<WorkPlanCrewSaturationCountModel>> crewSaturationMap = this.loadRateModels.stream().collect(Collectors.groupingBy(WorkPlanCrewSaturationCountModel::getCrewName));
        for (Map.Entry<String, List<WorkPlanCrewSaturationCountModel>> entry : crewSaturationMap.entrySet()) {
            String crew = entry.getKey();
            List<WorkPlanCrewSaturationCountModel> saturation = entry.getValue();

            if (CollectionUtils.isEmpty(saturation)) {
                continue;
            }
            WorkPlanCrewSaturationCountModel lastData = saturation.get(saturation.size() - 1);

            if (lastData == null) {
                continue;
            }

            // 工作负责人饱和度超过100
            if (crewSaturationOverloadAnalysis(saturation, WorkPlanCrewSaturationCountModel::getResponsiblePersonSaturation, limit)) {
                overLimitLeaderMap.put(crew, lastData);
            } else if (crewSaturationOverloadAnalysis(saturation, WorkPlanCrewSaturationCountModel::getResponsiblePersonSaturation, close)){
                // 工作负责人饱和度超过80
                overCloseLeaderMap.put(crew, lastData);
            }

            // 作业人员饱和度超过100
            if (crewSaturationOverloadAnalysis(saturation, WorkPlanCrewSaturationCountModel::getMemberSaturation, limit)) {
                overLimitWorkerMap.put(crew, lastData);
            } else if (crewSaturationOverloadAnalysis(saturation, WorkPlanCrewSaturationCountModel::getMemberSaturation, close)) {
                // 作业人员饱和度超过80
                overCloseWorkerMap.put(crew, lastData);
            }
        }

        StringBuilder sb = new StringBuilder();
        if (!overLimitLeaderMap.isEmpty()) {
            List<String> sortedCrewNames = sortCrewNames(overLimitLeaderMap.keySet(), this.crewNameSortedList);
            sb.append("以下班组的工作负责人饱和度达警戒线("+ limit +"%)，需合理调配人员: ");
            sb.append(String.join("、", sortedCrewNames));
        }

        if (!overCloseLeaderMap.isEmpty()) {
            List<String> sortedCrewNames = sortCrewNames(overCloseLeaderMap.keySet(), this.crewNameSortedList);
            sb.append("<br/>以下班组的工作负责人饱和度达("+ close +"%)，需关注: ");
            sb.append(String.join("、", sortedCrewNames));
        }

        if (!overLimitWorkerMap.isEmpty()) {
            List<String> sortedCrewNames = sortCrewNames(overLimitWorkerMap.keySet(), this.crewNameSortedList);
            sb.append("<br/>以下班组的作业人员饱和度达警戒线("+ limit +"%)，需合理调配人员: ");
            sb.append(String.join("、", sortedCrewNames));
        }

        if (!overCloseWorkerMap.isEmpty()) {
            List<String> sortedCrewNames = sortCrewNames(overCloseWorkerMap.keySet(), this.crewNameSortedList);
            sb.append("<br/>以下班组的作业人员饱和度达("+ close +"%)，需关注: ");
            sb.append(String.join("、", sortedCrewNames));
        }

        param = sb.toString();
        return String.format(this.template, param);
    }

    /**
     * 班组饱和度是否超过100
     *
     * @param list
     * @return
     */
    public boolean crewSaturationOverloadAnalysis(List<WorkPlanCrewSaturationCountModel> list, Function<WorkPlanCrewSaturationCountModel, BigDecimal> func, int limit) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }

        WorkPlanCrewSaturationCountModel model = list.get(list.size() - 1);
        return func.apply(model).compareTo(BigDecimal.valueOf(limit)) > -1;
    }

    public List<String> sortCrewNames(Collection<String> crewNames, List<String> sortedCrewNames) {
        return crewNames.stream().sorted((e1, e2) -> {
            int index1 = sortedCrewNames.indexOf(e1);
            int index2 = sortedCrewNames.indexOf(e2);

            return Integer.compare(index1, index2);
        }).collect(Collectors.toList());
    }

    public String overloadTemplate(String crewName) {
        return String.format("<strong style=\"color: #f5222d;\">%s</strong>", "");
    }

}