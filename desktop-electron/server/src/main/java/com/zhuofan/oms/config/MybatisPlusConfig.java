package com.zhuofan.oms.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zhuofan.oms.config.mybatis.ExpandSqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public ExpandSqlInjector expandSqlInjector() {
        return new ExpandSqlInjector();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 关键：添加分页插件，并指定数据库类型
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.DM));
        // 如果有其他插件（如乐观锁），也在这里添加
        return interceptor;
    }


}
