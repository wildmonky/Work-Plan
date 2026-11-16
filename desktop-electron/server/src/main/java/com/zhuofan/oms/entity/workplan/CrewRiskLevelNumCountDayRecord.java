package com.zhuofan.oms.entity.workplan;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 班组各级风险分级作业数统计日记录
 */
@Getter
@Setter
@TableName("CREW_RISK_LEVEL_NUM_COUNT_DAY_RECORD")
public class CrewRiskLevelNumCountDayRecord {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private LocalDate date;

    /**
     * {@link com.zhuofan.oms.orm.model.Crew}
     */
    private Long crewId;

    private Integer totalCount;
    /**
     * 特高风险作业数
     */
    private Integer veryHighCount;

    /**
     * 高风险作业数
     */
    private Integer highCount;

    /**
     * 中高风险作业数
     */
    private Integer mediumCount;

    /**
     * 中风险作业数
     */
    private Integer lowCount;

    /**
     * 低风险作业数
     */
    private Integer acceptableCount;

}
