package com.zhuofan.oms.model.workplan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhuofan.oms.enums.workplan.ContentType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 作业计划文件model
 */
@ToString
@Getter
@Setter
public class WorkPlanFile {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String year;

    private String month;

    private String week;

    private String filename;

    private ContentType contentType;

    private String uri;

    public static WorkPlanFile ofDay(LocalDate date, String filename, String uri, ContentType contentType) {
        WorkPlanFile planFile = new WorkPlanFile();
        planFile.setContentType(contentType);
        planFile.setUri(uri);
        planFile.setDate(date);
        planFile.setFilename(filename);
        return planFile;
    }

    public static WorkPlanFile ofWeek(String year, String week, String filename, String uri, ContentType contentType) {
        WorkPlanFile planFile = new WorkPlanFile();
        planFile.setYear(year);
        planFile.setWeek(week);
        planFile.setFilename(filename);
        planFile.setUri(uri);
        planFile.setContentType(contentType);
        return planFile;
    }



}
