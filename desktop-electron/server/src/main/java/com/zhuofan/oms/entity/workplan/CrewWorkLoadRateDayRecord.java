package com.zhuofan.oms.entity.workplan;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 班组饱和度日记录
 */
@Getter
@Setter
@TableName("CREW_WORK_LOAD_RATE_DAY_RECORD")
public class CrewWorkLoadRateDayRecord {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private LocalDate date;

    private Long crewId;

    /**
     * 班组成员会变动，使用名称保存记录<br/>
     * 1、工作负责人、作业人员，表示班组工作负责人饱和度、作业人员饱和度<br/>
     * 2、成员名称，表示成员饱和度
     */
    private String memberName;

    /**
     * 当{@code memberName}为工作负责人、作业人员时，表示班组负责的计划作业系数之和<br/>
     * 当{@code memberName}为成员名称时，表示成员参与的计划作业系数之和
     */
    private BigDecimal sumRatio;

    /**
     * 当{@code memberName}为工作负责人示班组工作负责人数<br/>
     * 当{@code memberName}为作业人员示班组人数<br/>
     * 当{@code memberName}为成员名称时，为1
     */
    private Integer workerNum;

    /**
     * 饱和度=sumRatio/workerNum，保留两位小鼠
     */
    private BigDecimal rate;

    public BigDecimal getRate() {
        return sumRatio == null || workerNum == 0 ? BigDecimal.ZERO : sumRatio.divide(new BigDecimal(workerNum), 2, RoundingMode.HALF_UP);
    }

}
