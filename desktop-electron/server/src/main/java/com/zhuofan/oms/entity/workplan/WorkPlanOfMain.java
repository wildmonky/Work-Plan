package com.zhuofan.oms.entity.workplan;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 主调工作计划主表
 */
@Getter
@Setter
@TableName("WORK_PLAN_OF_MAIN")
public class WorkPlanOfMain {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 主调工作计划日期
     */
    private String date;

    /**
     * 值班安排计划，如：早班：陈守滨（5日）    中班：宋想富（5日）    晚班：张世斌（5日）
     */
    private String crewSchedule;

    /**
     * 总结，如：7月5日 共12项主要操作项目，4项停电，6项送电（0项启动），1项方式调整，1项方式恢复，值班力量满足（早班6人、中班5人、晚班6人）。
     */
    private String statistics;

    /**
     * 上传文件名称
     */
    private String uploadFileName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
