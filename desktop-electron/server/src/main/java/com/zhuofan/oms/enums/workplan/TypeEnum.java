package com.zhuofan.oms.enums.workplan;

import com.zhuofan.oms.service.workplan2.WorkPlanDataAutoModel;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 作业状态枚举类
 * 作业计划 {@link WorkPlanDataAutoModel#getWorkType()}
 */
@Getter
public enum TypeEnum {

    PRE_TEST(1, "预试"),
    TEST(2, "定检"),
    PATROL(3, "巡视"),
    OVERHAUL(4, "检修"),
    MAINTENANCE(5, "维护"),
    FIX(6, "消缺"),
    OPERATION(7, "操作"),
    ACCEPTANCE(8, "验收"),
    INSTALL_REPLACE(9, "安装、更换"),
    COOPERATION_PRODUCTION(13, "配合(生产)"),
    OTHER_PRODUCTION(14, "其他(生产)"),
    REPAIR(31, "修理"),
    TECHNICAL_IMPROVE(32, "技改"),
    DISTRIBUTION_NETWORK(404, "配网工程"),
    RELAY_PROTECTION(407, "继电保护"),
    AUTOMATION(408, "自动化"),
    COMMUNICATION(409, "通信工程"),
    UNKNOW(-999, "未知");

    final int code;
    final String desc;

    TypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TypeEnum ofCode(int code) {
        return Arrays.stream(values()).filter(e -> e.code == code).findFirst().orElse(UNKNOW);
    }

    public static TypeEnum ofDesc(String desc) {
        return Arrays.stream(values()).filter(e -> Objects.equals(e.desc, desc)).findFirst().orElse(UNKNOW);
    }

    public static TypeEnum getWorkType(String workType) {
        if (workType == null) {
            return UNKNOW;
        }

        if (!NumberUtils.isCreatable(workType)) {
            return TypeEnum.ofDesc(workType);
        }

        return TypeEnum.ofCode(Integer.parseInt(workType));
    }

    public static String getDesc(String workType) {
        TypeEnum workTypeEnum = getWorkType(workType);
        if (workTypeEnum == UNKNOW) {
            return workType;
        }

        return workTypeEnum.desc;
    }

}
