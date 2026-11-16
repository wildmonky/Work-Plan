package com.zhuofan.oms.enums.workplan;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;

@Getter
public enum MajorWideEnum {

    sj(1, "生技"),
    du(2, "调度"),
    yx(3, "营销"),
    jj(4, "基建"),
    UNKNOWN(-999, "未知");

    final int code;

    final String desc;

    MajorWideEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MajorWideEnum ofCode(int code) {
        return Arrays.stream(values()).filter(e -> e.code == code).findFirst().orElse(MajorWideEnum.UNKNOWN);
    }

    public static MajorWideEnum ofDesc(String desc) {
        return Arrays.stream(values()).filter(e -> e.desc.equals(desc)).findFirst().orElse(MajorWideEnum.UNKNOWN);
    }

    public static MajorWideEnum getMajorWide(String desc) {
        if (!NumberUtils.isCreatable(desc)) {
            return ofDesc(desc);
        }

        return ofCode(Integer.parseInt(desc));
    }

    public static String getDesc(String desc) {
        MajorWideEnum majorWide = getMajorWide(desc);
        if (majorWide == UNKNOWN) {
            return desc;
        }

        return majorWide.getDesc();
    }

}
