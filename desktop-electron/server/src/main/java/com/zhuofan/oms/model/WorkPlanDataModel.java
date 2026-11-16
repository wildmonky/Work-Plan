package com.zhuofan.oms.model;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@ExcelIgnoreUnannotated
public class WorkPlanDataModel {

    @ExcelProperty("作业计划编号")
    private String workPlanCode;

    // 本单位 外单位
    @ExcelProperty("作业主体")
    private String workMajor;

    @ExcelProperty("所属供电局")
    private String powerSupplyBureau;

    @ExcelProperty("项目管理单位名称")
    private String projectManageUnit;

    @ExcelProperty("工作单位名称")
    private String workUnit;

    @ExcelProperty("工作内容")
    private String workContent;

    @ExcelProperty("工作地点")
    private String workLocation;

    @ExcelProperty("计划开始时间")
    private LocalDateTime planStartTime;

    @ExcelProperty("计划结束时间")
    private LocalDateTime planEndTime;

    @ExcelProperty("实际开始时间")
    private LocalDateTime planActualStartTime;

    @ExcelProperty("实际结束时间")
    private LocalDateTime planActualEndTime;

    @ExcelProperty("作业人数")
    private Short workerNum;

    @ExcelProperty("作业状态")
    private String workStatus;

    @ExcelProperty("是否发布")
    private String isPublish;

    @ExcelProperty("工作班组")
    private String workCrew;

    @ExcelProperty(value = "运行班组", index = 15)
    private String runningCrew;

    @ExcelProperty("专业管理部门")
    private String professionManageDepartment;

    @ExcelProperty("工作地点-经度")
    private String workLocationX;

    @ExcelProperty("工作地点-纬度")
    private String workLocationY;

    @ExcelProperty("风险等级")
    private String riskLevel;

    @ExcelProperty("计划类型")
    private String planType;

    // 停电 不停电
    @ExcelProperty("工作方式")
    private String workMode;

    // 是 否
    @ExcelProperty("是否办票")
    private String needTicket;

    // 调度
    @ExcelProperty("作业专业")
    private String workProfession;

    // 继保、自动化
    @ExcelProperty("作业类别")
    private String workClass;

    // 维护 修理
    @ExcelProperty("作业类型")
    private String workType;

    // 无需勘察 需勘察
    @ExcelProperty("勘察方式")
    private String prospectMode;

    @ExcelProperty("现场第一责任人")
    private String firstResponsiblePersonInScene;

    @ExcelProperty("现场第一责任人联系电话")
    private String firstResponsiblePersonInScenePhone;

    @ExcelProperty("工作负责人")
    private String workResponsiblePerson;

    @ExcelProperty("工作负责人联系电话")
    private String workResponsiblePersonPhone;

    @ExcelProperty("发布时间")
    private String publishTime;

    @ExcelProperty("项目编码")
    private String projectCode;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("电压等级")
    private String voltageLevel;

    @ExcelProperty("项目性质")
    private String projectCharacter;

    // 是 否
    @ExcelProperty("是否纳入视频监控")
    private String needVideoSurveillance;

    @ExcelProperty("创建人")
    private String creatorName;

//    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("更新时间")
    private String updateTime;

//    @ExcelProperty("存储时间(短信)")
    private String saveTime;

    // 计算结果值
    @ExcelProperty("管理部门")
    private String manageDepartment;

    @ExcelProperty("风险值")
    private String risk;

    @ExcelProperty(value = "计算运行班组")
    private String calcRunningCrew;

    @ExcelProperty("计算是否有班组")
    private String calcRunningCrewExisted;

    @ExcelProperty("计算风险公式输出值")
    private String calcRiskFormulaOutputValue;

    @ExcelProperty("实际本单位工作人数")
    private String actualLocalUnitWorkerNum;
}
