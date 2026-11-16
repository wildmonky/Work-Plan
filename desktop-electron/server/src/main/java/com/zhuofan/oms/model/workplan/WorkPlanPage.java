package com.zhuofan.oms.model.workplan;

import com.zhuofan.oms.model.Page;
import com.zhuofan.oms.service.workplan2.WorkPlanDataAutoModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class WorkPlanPage extends Page<WorkPlanDataAutoModel> {

    private RiskLevelCountModel riskLevelCountModel;

    public WorkPlanPage(List<WorkPlanDataAutoModel> rows, long total, List<WorkPlanRiskLevelCountModel> workPlanRiskLevelCountModels) {
        super(rows, total);
        this.riskLevelCountModel = parse(workPlanRiskLevelCountModels);
    }

    public static WorkPlanPage page(List<WorkPlanDataAutoModel> rows, long total, List<WorkPlanRiskLevelCountModel> workPlanRiskLevelCountModels) {
        return new WorkPlanPage(rows, total, workPlanRiskLevelCountModels);
    }

    public static RiskLevelCountModel parse(List<WorkPlanRiskLevelCountModel> workPlanRiskLevelCountModels) {
        if (CollectionUtils.isEmpty(workPlanRiskLevelCountModels)) {
            return new RiskLevelCountModel();
        }

        Map<Integer, Integer> map = workPlanRiskLevelCountModels.stream().collect(Collectors.toMap(WorkPlanRiskLevelCountModel::getRiskLevel, WorkPlanRiskLevelCountModel::getCount, (e1, e2) -> e2));
        RiskLevelCountModel model = new RiskLevelCountModel();

        model.setHigh(map.getOrDefault(2, 0));
        model.setMid(map.getOrDefault(3, 0));
        model.setLow(map.getOrDefault(4, 0));
        model.setAcceptable(map.getOrDefault(5, 0));
        return model;
    }

    @Getter
    @Setter
    public static class RiskLevelCountModel {

        private int high = 0, mid = 0, low = 0, acceptable = 0;

    }

}
