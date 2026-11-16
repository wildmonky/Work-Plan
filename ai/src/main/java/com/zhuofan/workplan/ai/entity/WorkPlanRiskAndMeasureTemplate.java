package com.zhuofan.workplan.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * 作业风险和控制措施模板
 */
@Getter
@Setter
@TableName("WORK_PLAN_RISK_AND_MEASURE_TEMPLATE")
public class WorkPlanRiskAndMeasureTemplate {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 专业：继保、自动化、通信
     */
    @ExcelProperty("专业")
    private String majorWide;

    /**
     * 班组：
     *      自动化作业：主调自动化、配调自动化、二次智能运维班、继保自动化
     *      通信作业：通信检修、通信网络、空白
     *      继保作业：所有的变电继保班组（区别与继保自动化班组）
     */
    @ExcelProperty("班组")
    private String crew;

    /**
     * 作业内容
     */
    @ExcelProperty("作业任务")
    private String workContent;

    /**
     * 作业步骤，一个作业内容会被拆分为多个作业步骤
     */
    @ExcelProperty("作业步骤")
    private String workStep;

    /**
     * 现有控制措施
     */
    @ExcelProperty("现有的控制措施")
    private String usingControlMeasure;

    /**
     * 建议采取的控制措施
     */
    @ExcelProperty("建议采取的控制措施")
    private String suggestControlMeasure;

    /**
     * 风险等级：可接受、低风险、中风险、高风险
     */
    @ExcelProperty("风险等级")
    private String risk;

}
