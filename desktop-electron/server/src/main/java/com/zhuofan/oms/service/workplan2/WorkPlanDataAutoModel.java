package com.zhuofan.oms.service.workplan2;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhuofan.oms.enums.workplan.TypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@TableName(schema = "XXZXHL", value = "SCY170_SP_PD_OPERMAINTENCE_SP_PD_PP_PLAN_03")
public class WorkPlanDataAutoModel {

    @TableId("id")
    private String id;

    @TableField(exist = false)
    @ExcelProperty("分子公司")
    private String company;

    @TableField(exist = false)
    @ExcelProperty("地市局")
    private String powerSupplyBureau;

    @ExcelProperty("部门/县区局")
    @TableField("WORK_DEPT_ONAME")
    private String projectManageUnit;

    @ExcelProperty("作业计划编号")
    @TableField("PLAN_CODE")
    private String workPlanCode;

    /**
     * 风险等级（1 特高 2 高 3 中 4 低 5 可接受）
     */
    @ExcelProperty("风险等级")
    @TableField("RISK_LEVEL")
    private String riskLevel;

    /**
     * 作业状态（1 新增 2未开工 3进行中 4已完成 5 取消 6 间断 7 改期）
     */
    @ExcelProperty("作业状态")
    @TableField("WORKING")
    private String workStatus;

    /**
     * 专业大类（1 生技 2 调度 3 营销 4 基建）
     */
    @TableField("SPECIALITY_BIG")
    @ExcelProperty("专业大类")
    private String workProfession;

    /**
     * 专业（1 变电 2 输电 3 配电 4 基建 5 营销 6 调度 7其他）
     */
    @TableField("SPECIALITY")
    @ExcelProperty("专业")
    private String workClass;

    /**
     * 专业细类 专业二级分类（1 检修 2 试验 3 运行）
     */
    @TableField("SPECIAL_WORK_ITEM")
    @ExcelProperty("专业细类")
    private String workClassDetail;

    /**
     * 作业类型/TASK_TYPE： 1:预试，2:定检,3:巡视,4:检修,5:维护,6:消缺,7:操作,8:验收;9:安装、更换
     * {@link TypeEnum}
     */
    @ExcelProperty("作业类型")
    @TableField("WORK_TYPE")
    private String workType;

    /**
     * 作业主体（1 本单位 2 外单位）
     */
    @TableField(exist = false)
    @ExcelProperty("作业主体")
    private String workMajor;

    @TableField(exist = false)
    @ExcelProperty("项目/工作单位")
    private String workUnit;

    @ExcelProperty("施工/工作班组")
    @TableField("WORK_TEAM_ONAME")
    private String workCrew;

    @ExcelProperty("工作内容")
    @TableField("JOB_CONTENT")
    private String workContent;

    @ExcelProperty("工作地点")
    @TableField("WORK_SITE_NAMES")
    private String workLocation;

    @ExcelProperty("工作负责人")
    @TableField("WORK_MASTER_UNAME")
    private String workResponsiblePerson;

    @TableField(exist = false)
    @ExcelProperty("工作负责人电话")
    private String workResponsiblePersonPhone;

    @ExcelProperty("计划开始时间")
    @TableField("PLAN_BEGIN_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planStartTime;

    @ExcelProperty("计划结束时间")
    @TableField("PLAN_END_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planEndTime;

    @ExcelProperty("实际开始时间")
    @TableField("REAL_BEGIN_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planActualStartTime;

    @ExcelProperty("实际结束时间")
    @TableField("REAL_END_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planActualEndTime;

    @TableField(exist = false)
    @ExcelProperty("发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 计划类型(1 计划 2 临时)
     */
    @ExcelProperty("任务类型")
    @TableField("PLAN_TYPE")
    private String planType;

    /**
     * 计划分类（1 年计划、2 月计划、3 周计划、4 日计划）
     */
    @TableField(exist = false)
    @ExcelProperty("计划分类")
    private String planClass;

    /**
     * 工作方式（1 停电 2 不停电 3 带电作业）
     */
    @TableField(exist = false)
    @ExcelProperty("工作方式")
    private String workMode;

    /**
     * 是否视频监控（1 是 2 否）
     */
    @TableField(exist = false)
    @ExcelProperty("是否需视频监管")
    private String needVideoSurveillance;

    /**
     * 是否绑定视频（1 是 2 否）
     */
    @TableField(exist = false)
    @ExcelProperty("是否已绑定视频")
    private String bindVideoSurveillance;

    /**
     * 是否监管类作业：（1 是,2:否）
     */
    @TableField(exist = false)
    @ExcelProperty("是否监管类作业")
    private String monitorWork;

    /**
     * 是否办票（1 是 2 否）
     */
    @TableField(exist = false)
    @ExcelProperty("是否办票")
    private String needTicket;

    /**
     * 勘察方式（1 无需勘察 2 需勘察）
     */
    @TableField(exist = false)
    @ExcelProperty("是否勘察")
    private String prospectMode;

    /**
     * 是否需要使用工器具(1：是，2：否）
     */
    @TableField(exist = false)
    @ExcelProperty("是否需要使用工器具")
    private String needTools;

    /**
     * 存在关键工序（1 :是,2:否）
     */
    @TableField(exist = false)
    @ExcelProperty("是否存在关键工序")
    private String existedKeyFlow;

    @TableField(exist = false)
    @ExcelProperty("作业人数")
    private Short workerNum;

    @TableField(exist = false)
    @ExcelProperty("电压等级")
    private String voltageLevel;

    /**
     * 作业任务来源（-1 手工录入 1：维护检修，2：技改修理，3：故障抢修，4：营销客户服务平台，5：营销施工管控平台，6：智慧工程）
     */
    @ExcelProperty("作业来源")
    @TableField("PLAN_SOURCE_TYPE")
    private String workOrigin;

    @TableField(exist = false)
    @ExcelProperty("标签")
    private String tags;

    @ExcelProperty("工作成员")
    @TableField("WORK_MEMBER_UNAMES")
    private String workMembers;

    @TableField(exist = false)
    @ExcelProperty("施工项目经理")
    private String constructionProjectManager;

    @TableField(exist = false)
    @ExcelProperty(value = "是否动火")
    private String isNeedUseFire;

    @ExcelProperty("创建人")
    @TableField("CREATOR_UNAME")
    private String creator;

    //--------------------------查询参数--------------------------------------
    @TableField(exist = false)
    private int[] workTypes;

    @TableField(exist = false)
    private int[] riskLevels;

    @TableField(exist = false)
    private int[] workStatuses;

    @TableField(exist = false)
    private int[] workProfessions;

    @TableField(exist = false)
    private String runningCrewName;

    // 到位安排
    @TableField(exist = false)
    private String arrangement;

    // 管控措施
    @TableField(exist = false)
    private String controlMeasure;

    public WorkPlanDataModel toWorkPlanDataModel() {
        WorkPlanDataModel model = new WorkPlanDataModel();
        model.setCompany(this.company);
        model.setPowerSupplyBureau(this.powerSupplyBureau);
        model.setWorkPlanCode(this.workPlanCode);
        model.setWorkCrew(this.workCrew);
        model.setWorkContent(this.workContent);
        model.setWorkStatus(this.workStatus);
        model.setWorkProfession(this.workProfession);
        model.setWorkClass(this.workClass);
        model.setWorkClassDetail(this.workClassDetail);
        model.setWorkType(this.workType);
        model.setWorkMajor(this.workMajor);
        model.setProjectManageUnit(this.projectManageUnit);
        model.setWorkUnit(this.workUnit);
        model.setWorkLocation(this.workLocation);
        model.setPlanStartTime(this.planStartTime == null ? null : this.planStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.setPlanEndTime(this.planEndTime == null ? null : this.planEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.setRiskLevel(this.riskLevel);
        model.setWorkResponsiblePerson(this.workResponsiblePerson);
        model.setWorkResponsiblePersonPhone(this.workResponsiblePersonPhone);
        model.setPlanActualStartTime(this.planActualStartTime == null ? null : this.planActualStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.setPlanActualEndTime(this.planActualEndTime == null ? null : this.planActualEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.setPublishTime(this.publishTime == null ? null : this.publishTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.setPlanType(this.planType);
        model.setPlanClass(this.planClass);
        model.setWorkMode(this.workMode);
        model.setNeedVideoSurveillance(this.needVideoSurveillance);
        model.setBindVideoSurveillance(this.bindVideoSurveillance);
        model.setMonitorWork(this.monitorWork);
        model.setNeedTicket(this.needTicket);
        model.setProspectMode(this.prospectMode);
        model.setNeedTools(this.needTools);
        model.setExistedKeyFlow(this.existedKeyFlow);
        model.setWorkerNum(this.workerNum);
        model.setVoltageLevel(this.voltageLevel);
        model.setWorkOrigin(this.workOrigin);
        model.setTags(this.tags);
        model.setWorkMembers(this.workMembers);
        model.setConstructionProjectManager(this.constructionProjectManager);
        model.setCreator(this.creator);
        return model;
     }

}
