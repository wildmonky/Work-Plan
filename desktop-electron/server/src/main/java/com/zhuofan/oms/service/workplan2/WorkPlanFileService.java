package com.zhuofan.oms.service.workplan2;

import com.zhuofan.oms.enums.workplan.ContentType;
import com.zhuofan.oms.model.workplan.WorkPlanFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkPlanFileService {


    @Value("${sys.tempfile_path}")
    private String tempDirPath;

    private final String workPlanDirName = "workplan";

    private File rootDir;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

    // 每周以周一开始，每周都有7天
    private final WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 7);
    // 年周时间字符串格式化：2024-W53-1 2024年第53周的周一
    private final DateTimeFormatter yearWeekFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(weekFields.weekBasedYear(), 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral("-W")
            .appendValue(weekFields.weekOfWeekBasedYear(), 1, 2, SignStyle.NORMAL)
            .appendLiteral('-')
            .appendValue(weekFields.dayOfWeek(), 1)
            .optionalStart()
            .appendOffsetId().parseStrict().toFormatter();

    @PostConstruct
    public void init() {
        rootDir = Paths.get(tempDirPath, workPlanDirName).toFile();
    }

    /**
     * 获取指定日期的作业计划分析结果
     * @param date 日期
     * @return 作业计划分析结果
     */
    public List<WorkPlanFile> searchDayFile(LocalDate date, ContentType contentType) {
        return searchFileByDate(date, file -> {
            if (contentType != null && !file.getName().startsWith(contentType.getType())) {
                return Collections.emptyList();
            }

            File dateDir = file.getParentFile();
            File yearDir = dateDir.getParentFile();

            WorkPlanFile workPlanFile = fileToWorkPlanFile(LocalDate.parse(yearDir.getName() + "-" + dateDir.getName()), file, contentType);
            return workPlanFile == null ? Collections.emptyList() : Collections.singletonList(workPlanFile);
        }).stream().sorted((e1,e2) ->
            e1.getDate().isAfter(e2.getDate()) ? -1 : (e1.getDate().isEqual(e2.getDate()) ? 0 : 1)
        ).collect(Collectors.toList());
    }

    /**
     * 获取周管控材料
     * @param date 日期
     * @return 作业计划分析结果
     */
    public List<WorkPlanFile> searchWeekFile(LocalDate date, ContentType contentType) {
        Integer year = date == null ? null : date.get(WeekFields.ISO.weekBasedYear());
        Integer weekOfYear = date == null ? null : date.get(WeekFields.ISO.weekOfYear());
        return searchFileByYear(year, file -> {
            if (!file.isFile()) {
                return Collections.emptyList();
            }

            String currentYear = year == null ? file.getParentFile().getName() : year + "";

            if (contentType == null) {
                return Collections.singletonList(fileToWorkPlanFile(currentYear, file, ContentType.RISK_ALARM));
            }

            boolean isMatch = file.getName().startsWith(contentType.getType()+ "_" + (weekOfYear == null ? "" : weekOfYear));
            if (!isMatch) {
                return Collections.emptyList();
            }

            return Collections.singletonList(fileToWorkPlanFile(currentYear, file, ContentType.RISK_ALARM));
        }).stream().sorted((e1,e2) -> {
            try {
                LocalDate date1 = parseWeekToDate(e1.getYear() + "-W" + e1.getWeek() + "-1");
                LocalDate date2 = parseWeekToDate(e2.getYear() + "-W" + e2.getWeek() + "-1");
                return date1.isAfter(date2) ? -1 : (date2.isEqual(date1) ? 0 : 1);
            } catch (RuntimeException e) {
//                throw new RuntimeException(e);
                return 0;
            }
        }).collect(Collectors.toList());
    }

    /**
     * 获取月督查报告
     * @param date 日期
     * @return 作业计划分析结果
     */
    public List<WorkPlanFile> searchMonthFile(LocalDate date, ContentType contentType) {
        Integer year = date == null ? null : date.get(WeekFields.ISO.weekBasedYear());
        Integer month = date == null ? null : date.getMonthValue();
        return searchFileByYear(year, file -> {
            if (!file.isFile()) {
                return Collections.emptyList();
            }

            String currentYear = year == null ? file.getParentFile().getName() : year + "";

            if (contentType == null) {
                return Collections.singletonList(fileToWorkPlanFile(currentYear, file, contentType));
            }

            boolean isMatch = file.getName().startsWith(contentType.getType()+ "_" + (month == null ? "" : month));
            if (!isMatch) {
                return Collections.emptyList();
            }

            return Collections.singletonList(fileToWorkPlanFile(currentYear, file, contentType));
        }).stream().sorted((e1,e2) -> {
            try {
                LocalDate date1 = LocalDate.parse(e1.getYear() + "-" + String.format("%02d", Integer.parseInt(e1.getMonth())) + "-01");
                LocalDate date2 = LocalDate.parse(e2.getYear() + "-" + String.format("%02d", Integer.parseInt(e2.getMonth())) + "-01");
                return date1.isAfter(date2) ? -1 : (date2.isEqual(date1) ? 0 : 1);
            } catch (RuntimeException e) {
//                throw new RuntimeException(e);
                return 0;
            }
        }).collect(Collectors.toList());
    }

    /**
     * @param weekYearStr 2024-W53-1
     * @return 2024年第53周的周一
     */
    public LocalDate parseWeekToDate(String weekYearStr) {
        return LocalDate.parse(weekYearStr, yearWeekFormatter);
    }

    public void download(WorkPlanFile workPlanFile, HttpServletResponse response) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            long writeBytesLength = download(workPlanFile, outputStream);
            response.setContentLengthLong(writeBytesLength);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public long download(WorkPlanFile workPlanFile, OutputStream outputStream) {
        if (workPlanFile == null || StringUtils.isBlank(workPlanFile.getUri())) {
            return 0;
        }

        String uri = workPlanFile.getUri();
        File file = new File(uri);
        if (!file.exists()) {
            log.debug("{}文件不存在", workPlanFile);
            return 0;
        }

        try {
            FileUtils.copyFile(file, outputStream);
            return file.length();
        } catch (Exception e) {
            log.warn("下载文件异常", e);
            return 0;
        }
    }

    public List<WorkPlanFile> searchFileByYear(Integer year, Function<File, List<WorkPlanFile>> fileFilter) {
        // 年目录
        List<File> dirs = getYearDir(year);
        if (CollectionUtils.isEmpty(dirs)) {
            return Collections.emptyList();
        }

        List<WorkPlanFile> reFiles = new ArrayList<>();
        for (File dir : dirs) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0 || fileFilter == null) {
                continue;
            }

            for (File file : files) {
                reFiles.addAll(fileFilter.apply(file));
            }
        }

        return reFiles;
    }

    public List<WorkPlanFile> searchFileByDate(LocalDate date, Function<File, List<WorkPlanFile>> fileFilter) {
        // 日期目录
        List<File> dateDirs = getDateDir(date);
        if (CollectionUtils.isEmpty(dateDirs)) {
            return Collections.emptyList();
        }

        List<WorkPlanFile> reFiles = new ArrayList<>();
        for (File dateDir : dateDirs) {
            File[] files = dateDir.listFiles();
            if (files == null || files.length == 0 || fileFilter == null) {
                continue;
            }

            for (File file : files) {
                reFiles.addAll(fileFilter.apply(file));
            }
        }

        return reFiles;
    }

    public List<File> getYearDir(LocalDate date) {
        return getYearDir(date  == null ? null : date.getYear());
    }

    public List<File> getYearDir(Integer year) {
        File[] files = rootDir.listFiles(file -> {
            if (!file.isDirectory()) {
                return false;
            }

            if (year == null) {
                return true;
            }

            return Objects.equals(file.getName(), year + "");
        });

        return files == null ? Collections.emptyList() : Arrays.stream(files).sorted((f1, f2) -> {
            String year1 = f1.getName();
            String year2 = f2.getName();
            return year1.compareTo(year2);
        }).collect(Collectors.toList());
    }

    public List<File> getDateDir(LocalDate date) {
        if (!rootDir.exists()) {
            return Collections.emptyList();
        }

        List<File> yearDirs = getYearDir(date);
        if (yearDirs.isEmpty()) {
            return Collections.emptyList();
        }

        List<File> dateDirs = new ArrayList<>();
        for (File yearDir : yearDirs) {
            File[] dateDirsWithYear = yearDir.listFiles(file -> {
                if (!file.isDirectory()) {
                    return false;
                }

                return date == null || file.getName().equals(date.format(dateFormatter));
            });

            if (dateDirsWithYear != null && dateDirsWithYear.length > 0) {
                dateDirs.addAll(Arrays.asList(dateDirsWithYear));
            }
        }

        return dateDirs;
    }

    public WorkPlanFile fileToWorkPlanFile(LocalDate localDate, File file, ContentType contentType) {
        if (file == null || file.isDirectory()) {
            return null;
        }

        String filename = file.getName();

        WorkPlanFile workPlanFile = new WorkPlanFile();
        workPlanFile.setDate(localDate);
        if (contentType == null) {
            contentType = Arrays.stream(ContentType.values()).filter(e -> filename.startsWith(e.getType())).findFirst().orElseGet(() -> null);
        }
        workPlanFile.setContentType(contentType);
        String name = filename;
        if (contentType != null) {
            name = contentType.getDesc() + "." + contentType.getSuffix();
        }
        workPlanFile.setFilename(name);
        workPlanFile.setUri(file.getAbsolutePath());
        return workPlanFile;
    }

    public WorkPlanFile fileToWorkPlanFile(String year, File file, ContentType contentType) {
        if (file == null || file.isDirectory()) {
            return null;
        }

        String filename = file.getName();

        WorkPlanFile workPlanFile = new WorkPlanFile();
        workPlanFile.setYear(year);

        if (contentType == null) {
            contentType = Arrays.stream(ContentType.values()).filter(e -> filename.startsWith(e.getType())).findFirst().orElseGet(() -> null);
        }

        if (contentType == null) {
            return null;
        }

        String name = contentType.getDesc() + "." + contentType.getSuffix();
        workPlanFile.setFilename(name);

        workPlanFile.setContentType(contentType);
        if (contentType == ContentType.RISK_ALARM) {
            String replaceFilename = filename.replaceFirst(contentType.getType(), "");
            String week = replaceFilename.substring(1, replaceFilename.lastIndexOf("."));
            workPlanFile.setWeek(week);
        }

        if (contentType == ContentType.MONTH_REPORT) {
            String replaceFilename = filename.replaceFirst(contentType.getType(), "");
            int indexOf = replaceFilename.indexOf("_", 1);
            String month = replaceFilename.substring(1, indexOf);
            workPlanFile.setMonth(month);
            workPlanFile.setFilename(replaceFilename.substring(indexOf + 1));
        }

        workPlanFile.setUri(file.getAbsolutePath());
        return workPlanFile;
    }

}
