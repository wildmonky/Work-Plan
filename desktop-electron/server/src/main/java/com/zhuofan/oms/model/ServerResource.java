package com.zhuofan.oms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerResource {

    private String name;

    private String url;

    // 是否为程序创建
    private Boolean builtin;

}
