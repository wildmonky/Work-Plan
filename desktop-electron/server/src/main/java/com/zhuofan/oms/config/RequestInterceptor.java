package com.zhuofan.oms.config;

import com.zhuofan.oms.model.RequestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.annotation.Resource;

@Slf4j
@Component
public class RequestInterceptor implements WebRequestInterceptor {

    @Resource
    private RequestModel requestModel;

    @Override
    public void preHandle(WebRequest request) throws Exception {
        requestModel.setSessionId(requestModel.getSessionId());
        requestModel.setToken(request.getHeader("Access-Token"));
        log.debug("requestModel: {}", requestModel);
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {}

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {}

}
