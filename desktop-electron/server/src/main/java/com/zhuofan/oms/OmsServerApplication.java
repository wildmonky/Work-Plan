package com.zhuofan.oms;

import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.util.SpringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@MapperScan({"com.zhuofan.oms.mapper"})
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties({DatasourceConfig.class, SysConfig.class})
@ComponentScan(basePackages = {"com.zhuofan.core", "com.zhuofan.oms"})
@SpringBootApplication
public class OmsServerApplication {
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("PRC"));
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext=
                SpringApplication.run(OmsServerApplication.class, args);
        SpringUtils.setApplicationContext(applicationContext);
    }

}
