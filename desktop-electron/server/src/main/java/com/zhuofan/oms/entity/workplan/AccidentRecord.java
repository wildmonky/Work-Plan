package com.zhuofan.oms.entity.workplan;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "accident_record", autoResultMap = true)
public class AccidentRecord {

    // ==================== 主键 ====================

    @TableId(type = IdType.AUTO)
    private Long id;

    // ==================== 事件基础信息 ====================

    @TableField("accident_name")
    private String accidentName;

    @TableField("occurrence_time")
    private String occurrenceTime;

    @TableField("occurrence_province")
    private String occurrenceProvince;

    @TableField("occurrence_city")
    private String occurrenceCity;

    @TableField("occurrence_district")
    private String occurrenceDistrict;

    @TableField("responsible_entity")
    private String responsibleEntity;

    @TableField("accident_level")
    private String accidentLevel;

    @TableField("accident_type")
    private String accidentType;

    @TableField("operation_task")
    private String operationTask;

    @TableField("weather_condition")
    private String weatherCondition;

    @TableField("geographic_env")
    private String geographicEnv;

    @TableField("work_period")
    private String workPeriod;

    @TableField("voltage_level")
    private String voltageLevel;

    @TableField("equipment_name")
    private String equipmentName;

    @TableField("equipment_model")
    private String equipmentModel;

    @TableField("equipment_age")
    private String equipmentAge;

    @TableField(value = "operation_method")
    private String operationMethod;

    @TableField(value = "tools_protection")
    private String toolsProtection;

    @TableField("personnel_count")
    private Integer personnelCount;

    @TableField(value = "personnel_qualification")
    private String personnelQualification;

    @TableField("outsourcing_ratio")
    private String outsourcingRatio;

    @TableField(value = "team_roles")
    private String teamRoles;

    // ==================== 关键时间轴 ====================

    @TableField("timeline_start")
    private String timelineStart;

    @TableField("timeline_violation")
    private String timelineViolation;

    @TableField("timeline_accident")
    private String timelineAccident;

    @TableField("timeline_discovery")
    private String timelineDiscovery;

    @TableField("timeline_rescue")
    private String timelineRescue;

    @TableField("event_description")
    private String eventDescription;

    // ==================== 事故致因分析 ====================

    @TableField(value = "direct_cause")
    private String directCause;

    @TableField(value = "indirect_cause")
    private String indirectCause;

    @TableField(value = "tech_equipment_cause")
    private String techEquipmentCause;

    // ==================== 事故后果与影响 ====================

    @TableField("casualty_count")
    private Integer casualtyCount;

    @TableField("casualty_detail")
    private String casualtyDetail;

    @TableField("equipment_damage")
    private String equipmentDamage;

    @TableField("power_outage_scope")
    private String powerOutageScope;

    @TableField("outage_duration")
    private String outageDuration;

    @TableField("load_loss")
    private String loadLoss;

    @TableField("environmental_impact")
    private String environmentalImpact;

    // ==================== 违规条款 ====================

    @TableField(value = "violated_regulations")
    private String violatedRegulations;

    // ==================== 整改与警示 ====================

    @TableField("exposed_problem")
    private String exposedProblem;

    @TableField(value = "rectification_measures")
    private String rectificationMeasures;

    @TableField("core_lessons")
    private String coreLessons;

    // ==================== 元数据与标签 ====================

    @TableField("risk_level_tag")
    private String riskLevelTag;

    @TableField(value = "operation_type_tag")
    private String operationTypeTag;

    @TableField(value = "equipment_type_tag")
    private String equipmentTypeTag;

    @TableField(value = "violation_type_tag")
    private String violationTypeTag;

    @TableField(value = "liability_tag")
    private String liabilityTag;

    @TableField(value = "origin_file_path")
    private String originFilePath;

    @TableField(value = "origin_file_name")
    private String originFileName;
}
