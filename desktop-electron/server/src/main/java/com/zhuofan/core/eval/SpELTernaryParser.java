package com.zhuofan.core.eval;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

public class SpELTernaryParser {

    public static Object evaluateWithSpEL(String expression, Map<String, Object> variables) throws Exception {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        //设置初始固定变量
        variables.put("loginName", Eval.getLoginName());
        variables.put("loginUserId", Eval.getLoginUserId());
        variables.put("loginDepartmentId", Eval.getLoginDepartmentId());
        variables.put("loginDepartmentName",Eval.getLoginDepartmentName());

        // 设置变量
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        Expression exp = parser.parseExpression(expression);
        Object result = exp.getValue(context);

        // 如果结果是字符串，则进行变量替换
        if (result instanceof String) {
            String resultString = (String) result;
            // 替换字符串中的占位符，例如 #departmentid 替换为变量的值
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "#" + entry.getKey();
                if (resultString.contains(placeholder)) {
                    resultString = resultString.replace(placeholder,
                            entry.getValue() != null ? entry.getValue().toString() : "");
                }
            }
            return resultString;
        }

        return result;
    }

    public static void main(String[] args)  throws  Exception{
        Map<String, Object> variables = new HashMap<>();
        String expression = "#loginName == '张三' || #loginName=='李四' ? '' : \"AND (FUNC_SPLIT_EXISTS(departmentid,'#loginDepartmentId',',')>0)\"";
        Object result = evaluateWithSpEL(expression, variables);
        System.out.println("结果: " + result); // 输出: 结果: A
    }

}
