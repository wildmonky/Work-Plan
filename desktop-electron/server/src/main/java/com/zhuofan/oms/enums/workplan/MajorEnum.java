package com.zhuofan.oms.enums.workplan;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum MajorEnum {

    bd(1, "变电"),
    sd(2, "输电"),
    pd(3, "配电"),
    jj(4, "基建"),
    yx(5, "营销"),
    du(6, "调度"),
    OTHER(7, "其他"),
    JZ(21, "继保"),
    AUTOMATION(22, "自动化"),
    TZ(23, "通信"),
    AZ(24, "安自"),
    WA(25, "网安"),
    UNKNOWN(-999, "未知");

    final int code;

    final String desc;

    MajorEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MajorEnum ofCode(int code) {
        return Arrays.stream(values()).filter(e -> e.code == code).findFirst().orElse(UNKNOWN);
    }

    public static MajorEnum ofDesc(String desc) {
        return Arrays.stream(values()).filter(e -> Objects.equals(e.desc, desc)).findFirst().orElse(UNKNOWN);
    }

    public static MajorEnum getMajor(String major) {
        if (!NumberUtils.isCreatable(major)) {
            return ofDesc(major);
        }

        return ofCode(Integer.parseInt(major));
    }

    public static String getDesc(String major) {
        MajorEnum majorEnum = getMajor(major);
        if (majorEnum == UNKNOWN) {
            return major;
        }

        return majorEnum.desc;
    }
}
