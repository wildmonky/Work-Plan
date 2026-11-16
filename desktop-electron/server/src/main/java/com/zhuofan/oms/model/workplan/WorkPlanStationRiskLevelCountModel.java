package com.zhuofan.oms.model.workplan;

import com.zhuofan.oms.enums.workplan.RiskLevelEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 变电站作业计划风险等级统计model
 */
@Getter
@Setter
public class WorkPlanStationRiskLevelCountModel extends WorkPlanRiskLevelCountModel{

    // 站点名称
    private String substationName;

    public WorkPlanStationRiskLevelCountModel() {}

    public WorkPlanStationRiskLevelCountModel(String substationName, RiskLevelEnum riskLevel, int count) {
        super();
        this.substationName = substationName;
        super.setRiskLevel(riskLevel.getCode());
        super.setRisk(riskLevel.getDesc());
        super.setCount(count);
    }

}
