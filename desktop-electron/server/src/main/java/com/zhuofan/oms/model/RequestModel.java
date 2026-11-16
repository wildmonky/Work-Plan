package com.zhuofan.oms.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * 注入使用
 */
@Data
@RequestScope
@Component
public class RequestModel {

    private String token;

    private String sessionId;

}
