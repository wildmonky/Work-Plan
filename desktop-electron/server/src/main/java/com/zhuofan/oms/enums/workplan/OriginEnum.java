package com.zhuofan.oms.enums.workplan;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum OriginEnum {

    sglr(-1, "手工录入"),
    maintain(1, "维护检修"),
    repair(2, "技改修理"),
    fault(3, "故障抢修"),
    market(4, "营销客户服务平台"),
    construction(5, "营销施工管控平台"),
    smart(6, "智慧工程"),
    UNKNOW(-999, "未知");

    final int code;

    final String desc;

    OriginEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OriginEnum ofCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElse(UNKNOW);
    }

    public static OriginEnum ofDesc(String desc) {
        return Arrays.stream(values())
                .filter(e -> Objects.equals(e.desc, desc))
                .findFirst()
                .orElse(UNKNOW);
    }

    public static OriginEnum getOrigin(String origin) {
        if (!NumberUtils.isCreatable(origin)) {
            return ofDesc(origin);
        }

        return ofCode(Integer.parseInt(origin));
    }

    public static String getDesc(String origin) {
        OriginEnum originEnum = getOrigin(origin);
        if (originEnum == UNKNOW) {
            return origin;
        }

        return originEnum.desc;
    }

}
