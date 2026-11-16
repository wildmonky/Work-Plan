package com.zhuofan.oms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zhuofan.oms.config.json.ClobSerializer;
import com.zhuofan.oms.util.SnowFlakeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
public class ApplicationConfigurer implements WebMvcConfigurer {

    @Resource
    private RequestInterceptor requestInterceptor;

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public GenericConversionService addConverter(GenericConversionService conversionService) {
        conversionService.removeConvertible(String.class, LocalDate.class);
        conversionService.addConverter(String.class, LocalDate.class, new StringToLocalDateConverter());
        return conversionService;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return restTemplateBuilder.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(requestInterceptor);
        log.debug("spring mvc 拦截器加载成功");
    }

    @Bean
    public SnowFlakeGenerator snowFlakeGenerator() {
        return new SnowFlakeGenerator(1,1,0);
    }

    /**
     * 添加Clob序列化器
     */
    @PostConstruct
    public ObjectMapper objectMapper() {
        SimpleModule customModule = new SimpleModule();
        customModule.addSerializer(new ClobSerializer());
        objectMapper.registerModule(customModule);
        return objectMapper;
    }

}
