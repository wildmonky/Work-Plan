package com.zhuofan.oms.model.workplan;

import com.zhuofan.oms.entity.workplan.WorkPlanOfSub;
import com.zhuofan.oms.entity.workplan.sub.LiveWorkPermit;
import com.zhuofan.oms.entity.workplan.sub.MaintenancePlan;
import com.zhuofan.oms.entity.workplan.sub.SecondaryPowerSupplyMode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkPlanOfSubModel {

    private WorkPlanOfSub workPlanOfSub;

    private List<MaintenancePlan> maintenancePlanList;

    private List<SecondaryPowerSupplyMode> secondaryPowerSupplyModeList;

    private List<LiveWorkPermit> liveWorkPermitList;

}
