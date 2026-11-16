package com.zhuofan.oms.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 班组饱和度 Model
 */
@Getter
@Setter
public class CrewSaturationModel {

    @ExcelProperty("班组名称")
    private String crewName;

    @ExcelProperty(value = "工作任务数量\n（所需工作负责人数量）", index = 1)
    private Long workNeedManagerNum;

    @ExcelProperty("所需作业人员数量")
    private Long workNeedWorkerNum;

    @ExcelProperty(value = "班组日作业饱和度\n（工作负责人）", index = 3)
    private BigDecimal crewManagerSaturation;

    @ExcelProperty(value = "日作业饱和度\n（作业人员）", index = 4)
    private BigDecimal crewWorkerSaturation;

}
