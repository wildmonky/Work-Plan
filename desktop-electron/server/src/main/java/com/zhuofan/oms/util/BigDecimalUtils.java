package com.zhuofan.oms.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BigDecimalUtils {

    /**
     * 最大值
     *
     * @param nums 数字组
     * @return 最大值
     */
    public static BigDecimal max(BigDecimal... nums) {
        List<BigDecimal> sort = sort(nums);
        return sort.isEmpty() ? null : sort.get(sort.size() - 1);
    }

    /**
     * 最小值
     *
     * @param nums 数字组
     * @return 最小值
     */
    public static BigDecimal min(BigDecimal... nums) {
        List<BigDecimal> sort = sort(nums);
        return sort.isEmpty() ? null : sort.get(0);
    }

    /**
     * 排序
     *
     * @param nums 数字列表
     * @return 排序后的数字列表，自然顺序， 从小到大
     */
    public static List<BigDecimal> sort(BigDecimal... nums) {
        return Stream.of(nums).filter(Objects::nonNull).sorted().collect(Collectors.toList());
    }


}
