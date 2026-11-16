package com.zhuofan.oms.service.workplan2;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.zhuofan.oms.orm.model.Crew;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@ExcelIgnoreUnannotated
public class WorkPlanDataModel {

    @ExcelProperty("分子公司")
    private String company;

    @ExcelProperty("地市局")
    private String powerSupplyBureau;

    @ExcelProperty("作业计划编号")
    private String workPlanCode;

    @ExcelProperty("施工/工作班组")
    private String workCrew;

    @ExcelProperty("工作内容")
    private String workContent;

    @ExcelProperty("作业状态")
    private String workStatus;

    // 调度
    @ExcelProperty("专业大类")
    private String workProfession;

    // 继保、自动化
    @ExcelProperty("专业")
    private String workClass;

    @ExcelProperty("专业细类")
    private String workClassDetail;

    // 维护 修理
    @ExcelProperty("作业类型")
    private String workType;

    // 本单位 外单位
    @ExcelProperty("作业主体")
    private String workMajor;

    @ExcelProperty("部门/县区局")
    private String projectManageUnit;

    @ExcelProperty("项目/工作单位")
    private String workUnit;

    @ExcelProperty("工作地点")
    private String workLocation;

    @ExcelProperty("计划开始时间")
    private String planStartTime;

    @ExcelProperty("计划结束时间")
    private String planEndTime;

    @ExcelProperty("风险等级")
    private String riskLevel;

    @ExcelProperty("工作负责人")
    private String workResponsiblePerson;

    @ExcelProperty("工作负责人电话")
    private String workResponsiblePersonPhone;

    @ExcelProperty("实际开始时间")
    private String planActualStartTime;

    @ExcelProperty("实际结束时间")
    private String planActualEndTime;

    @ExcelProperty("发布时间")
    private  String publishTime;

    @ExcelProperty("任务类型")
    private String planType;

    @ExcelProperty("计划分类")
    private String planClass;

    // 停电 不停电
    @ExcelProperty("工作方式")
    private String workMode;

    // 是 否
    @ExcelProperty("是否需视频监管")
    private String needVideoSurveillance;

    // 是 否
    @ExcelProperty("是否已绑定视频")
    private String bindVideoSurveillance;

    // 是否监管类作业
    @ExcelProperty("是否监管类作业")
    private String monitorWork;

    // 是 否
    @ExcelProperty("是否办票")
    private String needTicket;

    // 无需勘察 需勘察
    @ExcelProperty("是否勘察")
    private String prospectMode;

    @ExcelProperty("是否需要使用工器具")
    private String needTools;

    @ExcelProperty("是否存在关键工序")
    private String existedKeyFlow;

    @ExcelProperty("作业人数")
    private Short workerNum;

    @ExcelProperty("电压等级")
    private String voltageLevel;

    @ExcelProperty("作业来源")
    private String workOrigin;

    @ExcelProperty("标签")
    private String tags;

    @ExcelProperty("工作成员")
    private String workMembers;

    @ExcelProperty("施工项目经理")
    private String constructionProjectManager;

    @ExcelProperty("创建人")
    private String creator;

    // 计算结果值
    @ExcelProperty("管理部门")
    private String manageDepartment;

    @ExcelProperty("风险值")
    private String risk;

    @ExcelProperty(value = "计算运行班组")
    private String calcRunningCrew;

    /**
     * 运行班组id {@link Crew#getId()}
     */
    private Long calcRunningCrewId;

    @ExcelProperty("计算是否有班组")
    private String calcRunningCrewExisted;

    @ExcelProperty("计算风险公式输出值")
    private String calcRiskFormulaOutputValue;

    @ExcelProperty("实际本单位工作人数")
    private String actualLocalUnitWorkerNum;
}
