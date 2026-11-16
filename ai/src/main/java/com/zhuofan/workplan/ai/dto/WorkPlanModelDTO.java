package com.zhuofan.workplan.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkPlanModelDTO {

    private String id;

    /**
     * 作业编码
     */
    private String code;

    /**
     * 专业
     */
    private String profession;

    /**
     * 风险等级
     */
    private String risk;

    /**
     * 作业内容
     */
    private String plan;

    /**
     * 管控措施
     */
    private String controlMeasures;

}
