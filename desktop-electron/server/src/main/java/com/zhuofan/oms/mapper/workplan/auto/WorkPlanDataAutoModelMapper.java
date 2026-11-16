package com.zhuofan.oms.mapper.workplan.auto;

import com.zhuofan.oms.mapper.CommonMapper;
import com.zhuofan.oms.model.workplan.WorkPlanRiskLevelCountModel;
import com.zhuofan.oms.model.workplan.WorkPlanStatusModel;
import com.zhuofan.oms.service.workplan2.WorkPlanDataAutoModel;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkPlanDataAutoModelMapper extends CommonMapper<WorkPlanDataAutoModel> {

    long selectCountByPage(@Param("query") WorkPlanDataAutoModel query);

    List<WorkPlanRiskLevelCountModel> selectRiskLevelCountByPage(@Param("query") WorkPlanDataAutoModel query);

    List<WorkPlanDataAutoModel> selectByPage(WorkPlanDataAutoModel query, Integer pageNo, Integer pageSize);

    List<WorkPlanDataAutoModel> selectAll(@Param("query") WorkPlanDataAutoModel query);

    List<WorkPlanDataAutoModel> selectAllForStatistics(@Param("query") WorkPlanDataAutoModel query);

    List<WorkPlanStatusModel> selectWorkStatusNum(LocalDateTime beginTime, LocalDateTime endTime);

}
