package com.zhuofan.oms.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 文件类型枚举类
 */
@Getter
public enum FileTypeEnum {

    XLSX("xlsx"), XLS("xls"), XLSXM("xlsxm"),
    DOC("doc"), DOCX("docx"),
    TXT("txt");

    private final String suffix;

    FileTypeEnum(String type) {
        this.suffix = type;
    }

    public static FileTypeEnum getBySuffix(String suffix) {
        return Arrays.stream(values()).filter(e -> e.suffix.equals(suffix)).findFirst().orElseThrow(() -> new RuntimeException("没有\"" + suffix + "\"对应的文件后缀"));
    }

}
