package com.zhuofan.oms.model.workplan;

import lombok.Getter;
import lombok.Setter;

/**
 * 作业状态{@link com.zhuofan.oms.enums.workplan.StatusEnum}数量统计
 */
@Getter
@Setter
public class WorkPlanStatusCountModel {

    private Long newNum;

    private Long notStartNum;

    private Long inProgressNum;

    private Long completedNum;

    private Long canceledNum;

    private Long interruptedNum;

    private Long reScheduledNum;

}
