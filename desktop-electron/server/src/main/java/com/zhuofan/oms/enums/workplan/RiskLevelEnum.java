package com.zhuofan.oms.enums.workplan;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 计划作业风险等级枚举
 */
@Getter
public enum RiskLevelEnum {

    VERY_HIGH(1, "特高风险", "特高"),
    HIGH(2, "高风险", "高"),
    MEDIUM(3, "中风险", "中"),
    LOW(4, "低风险", "低"),
    ACCEPTABLE(5, "可接受","可接受"),
    UNKNOWN(-999, "未知", "未知");

    final int code;
    final String desc;
    final String simpDesc;

    RiskLevelEnum(int code, String desc, String simpDesc) {
        this.code = code;
        this.desc = desc;
        this.simpDesc = simpDesc;
    }

    public static RiskLevelEnum ofCode(int code) {
        return Arrays.stream(values()).filter(e -> e.code == code).findFirst().orElse(RiskLevelEnum.UNKNOWN);
    }

    public static RiskLevelEnum ofDesc(String desc) {
        return Arrays.stream(values()).filter(e ->
                Objects.equals(desc, e.getDesc()) || Objects.equals(desc, e.getSimpDesc())
        ).findFirst().orElse(RiskLevelEnum.UNKNOWN);
    }

    public static RiskLevelEnum getRiskLevel(String riskLevel) {
        if (!NumberUtils.isCreatable(riskLevel)) {
            return RiskLevelEnum.ofDesc(riskLevel);
        }

        return RiskLevelEnum.ofCode(Integer.parseInt(riskLevel));
    }

    public static String getDesc(String riskLevel) {
        RiskLevelEnum risk = getRiskLevel(riskLevel);
        if (risk == UNKNOWN) {
            return riskLevel;
        }
        return risk.getDesc();
    }

}
