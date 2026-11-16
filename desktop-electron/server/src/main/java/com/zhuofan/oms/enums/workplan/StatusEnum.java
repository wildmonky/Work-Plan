package com.zhuofan.oms.enums.workplan;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;

@Getter
public enum StatusEnum {

    NEW(1, "新增"),
    NOT_START(2, "未开工"),
    IN_PROGRESS(3, "进行中"),
    COMPLETED(4, "已完成"),
    CANCELED(5, "取消"),
    INTERRUPTED(6, "间断"),
    RESCHEDULED(7, "改期"),
    DELETED(8, "删除"),
    UNKNOW(-999, "未知");

    final int code;
    final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StatusEnum ofCode(int code) {
        return Arrays.stream(values()).filter(e -> e.code == code).findFirst().orElseGet(() -> UNKNOW);
    }

    public static StatusEnum ofDesc(String desc) {
        return Arrays.stream(values()).filter(e -> e.desc.equals(desc)).findFirst().orElseGet(() -> UNKNOW);
    }

    public static StatusEnum getStatus(String status) {
        if (!NumberUtils.isCreatable(status)) {
            return StatusEnum.ofDesc(status);
        }
        return StatusEnum.ofCode(Integer.parseInt(status));
    }

    public static String getDesc(String status) {
        StatusEnum statusEnum = getStatus(status);
        if (statusEnum == UNKNOW) {
            return status;
        }
        return statusEnum.desc;
    }

}
