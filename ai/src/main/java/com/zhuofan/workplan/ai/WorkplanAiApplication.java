package com.zhuofan.workplan.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com.zhuofan.workplan.ai.mapper"})
@SpringBootApplication
public class WorkplanAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkplanAiApplication.class, args);
    }

}
