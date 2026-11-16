package com.zhuofan.workplan.ai.configuration;

import com.zhuofan.workplan.ai.util.SnowFlakeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

@Configuration
public class BaseConfiguration {

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public SnowFlakeGenerator bean() {
        return new SnowFlakeGenerator(1,2,0);
    }

}
