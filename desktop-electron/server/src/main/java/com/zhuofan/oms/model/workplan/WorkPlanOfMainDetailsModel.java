package com.zhuofan.oms.model.workplan;

import com.alibaba.excel.annotation.ExcelProperty;
import com.zhuofan.oms.entity.workplan.WorkPlanOfMainDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * @see WorkPlanOfMainDetail
 */
@Getter
@Setter
public class WorkPlanOfMainDetailsModel {

    @ExcelProperty(index = 0)
    private String sortNum;

    /**
     * 操作内容（关键设备停、送电及方式调整）
     */
    @ExcelProperty(index = 1)
    private String operateContent;

    /**
     * 项目管理单位/分部
     */
    @ExcelProperty(index = 2)
    private String manageDepartment;

    /**
     * 计划开始时间（送电/方式恢复）
     */
    @ExcelProperty(index = 3)
    private String planStartTime;

    /**
     * 计划结束时间（送电/方式恢复）
     */
    @ExcelProperty(index = 4)
    private String planEndTime;

    /**
     * 检修内容
     */
    @ExcelProperty(index = 5)
    private String maintenanceContent;

    /**
     * 主要方式调整
     */
    @ExcelProperty(index = 6)
    private String mainMethodAdjustment;

    /**
     * 保护、安置投退/定值修改
     */
    @ExcelProperty(index = 7)
    private String a;

    /**
     * 风险等级
     */
    @ExcelProperty(index = 8)
    private String riskLevel;

    /**
     * 操作风险点
     */
    @ExcelProperty(index = 9)
    private String riskPoint;

    /**
     * 检修单管控
     */
    @ExcelProperty(index = 10)
    private String maintenanceListControl;

    /**
     * 监督管理人员
     */
    @ExcelProperty(index = 11)
    private String manager;

}
