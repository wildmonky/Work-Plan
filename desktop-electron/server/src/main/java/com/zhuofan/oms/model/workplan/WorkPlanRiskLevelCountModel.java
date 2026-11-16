package com.zhuofan.oms.model.workplan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkPlanRiskLevelCountModel {

    /**
     * {@link com.zhuofan.oms.enums.workplan.RiskLevelEnum}
     */
    private int riskLevel = 0;

    private String risk;

    private int count = 0;

}
