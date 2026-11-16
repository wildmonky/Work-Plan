package com.zhuofan.oms.config;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * yyyy-MM-dd 字符串格式转换
 */
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate convert(String source) {
        return LocalDate.parse(source, formatter);
    }
}
