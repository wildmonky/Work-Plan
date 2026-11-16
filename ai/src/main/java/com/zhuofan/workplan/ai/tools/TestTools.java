package com.zhuofan.workplan.ai.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;

@Resource
public class TestTools {

    @Tool
    public String test() {
        return "ok";
    }

}
