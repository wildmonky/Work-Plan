package com.zhuofan.oms.entity.workplan;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.oms.service.workplan2.WorkPlanDataModel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("WORK_PLAN_AI_ANALYSIS_RESULT")
public class WorkPlanAiAnalysisResult {

    /**
     * 计划作业编号
     * {@link WorkPlanDataModel#getWorkPlanCode()}
     */
    @TableId
    private String workPlanCode;

    /**
     * 风险等级分析结果
     */
    private String riskAnalysis;

    /**
     * 管控措施分析结果
     */
    private String measureAnalysis;

}
