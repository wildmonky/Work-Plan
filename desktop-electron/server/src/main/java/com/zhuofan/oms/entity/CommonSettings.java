package com.zhuofan.oms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("COMMON_SETTINGS")
public class CommonSettings {

    @TableId
    private String name;

    private String value;

    private String commentInfo;

}
