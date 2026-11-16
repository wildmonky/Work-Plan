package com.zhuofan.oms.config;

import com.zhuofan.oms.service.workplan.WorkPlanService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JodConverterAspect {

    @Pointcut("execution(* com.zhuofan.oms.service.workplan.WorkPlanService.process(..)) && target(obj)")
    public void pointCut(WorkPlanService obj) {}

    @Before(value = "pointCut(obj)", argNames = "obj")
    public void jodConverterCheck(WorkPlanService obj) {
        if (obj.getLocalOfficeManager() == null) {
            throw new RuntimeException("JodConverter 初始化失败，无法使用");
        }
    }

}
