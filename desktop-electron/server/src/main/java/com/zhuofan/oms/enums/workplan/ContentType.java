package com.zhuofan.oms.enums.workplan;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ContentType {

    CHECK("check", "xlsx", "调度类二次作业文件"),
    EXTRA_CHECK("extraCheck", "xlsx", "非调度类二次作业文件"),
    MASTER("master", "xlsx", "主调文件"),
    SUB("sub", "xlsx", "配调文件"),
    RISK_ALARM("risk_alarm", "docx", "系统运行专业作业计划周管控会汇报材料"),
    WEEK_RISK("week_risk", "docx", "电网主要检修及电网运行风险预警及重载设备清单"),
    EXPORT_IMG("export_img", "png", "生成的图片"),
    EXPORT_TOMORROW("export_tomorrow", "xlsx", "生成的计划"),
    MONTH_REPORT("month_report", "docx", "月度督查报告"),
    TEMPLATE("data1", "xlsm", "模板");

    private final String type;
    private final String suffix;
    private final String desc;

    ContentType(String type, String suffix, String desc) {
        this.type = type;
        this.suffix = suffix;
        this.desc = desc;
    }

    public static ContentType ofType(String type) {
        String finalType = type == null ? "" : type;
        return Arrays.stream(values()).filter(e -> finalType.startsWith(e.type)).findFirst().orElse(null);
    }
}
