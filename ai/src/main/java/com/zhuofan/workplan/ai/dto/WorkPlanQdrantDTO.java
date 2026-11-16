package com.zhuofan.workplan.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkPlanQdrantDTO extends WorkPlanModelDTO{

    private String voltage;

    /**
     * 抽象作业内容
     */
    private String abstractPlan;

    /**
     * 标签
     */
    private String tags;

    /**
     * 设备类型
     */
    private String deviceTypes;

    /**
     * 作业类型
     */
    private String workTypes;

    private Float score;

}
