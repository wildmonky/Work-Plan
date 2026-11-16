package com.zhuofan.oms.controller.oms;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.oms.controller.sys.BaseController;
import com.zhuofan.oms.entity.workplan.WorkPlanOfMainDetail;
import com.zhuofan.oms.enums.workplan.ContentType;
import com.zhuofan.oms.model.Page;
import com.zhuofan.oms.model.workplan.*;
import com.zhuofan.oms.service.workplan.WorkPlanService;
import com.zhuofan.oms.service.workplan2.WorkPlanAutoDataService;
import com.zhuofan.oms.service.workplan2.WorkPlanDataAutoModel;
import com.zhuofan.oms.service.workplan2.WorkPlanFileService;
import com.zhuofan.oms.service.workplan2.WorkPlanOfMainAndSubService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RequestMapping("/work/plan/auto")
@RestController
public class WorkPlanAutoController extends BaseController {

    @Resource
    private WorkPlanService workPlanService;

    @Resource
    private WorkPlanAutoDataService workPlanAutoDataService;

    @Resource
    private WorkPlanFileService workPlanFileService;

    @Resource
    private WorkPlanOfMainAndSubService workPlanOfMainAndSubService;

    @PostMapping("/{pageNo}/{pageSize}")
    public Page<WorkPlanDataAutoModel> getWorkPlansByDate(@RequestBody WorkPlanDataAutoModel query, @PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize) {
        return workPlanAutoDataService.searchWorkPlanPageByApi(query, pageNo, pageSize);
    }

    @PostMapping("/monitor/panel")
    public WorkPlanAnalysisModel workPlanAnalysis(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return workPlanAutoDataService.workPlanAnalysis(beginTime, endTime);
    }

    @PostMapping("/monitor/panel/every/day/count")
    public WorkPlanNumModel workPlanEverydayCount(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return workPlanAutoDataService.workPlanNumWithRecord(beginTime, endTime);
    }

    @PostMapping("/risk/day")
    public List<WorkPlanDataAutoModel> getWorkPlansByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return workPlanAutoDataService.getRiskBetweenMiddleAndHighWorkPlansByDate(date);
    }

    @PostMapping("/crew/saturation")
    public WorkPlanCrewSaturationCountStatistics crewSaturationAnalysis(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return workPlanAutoDataService.crewSaturationAnalysisStatistics(startDate, endDate);
    }

    /**
     * 日协调会材料查询
     * @param date
     * @param filename
     * @return
     */
    @PostMapping("/day")
    public List<WorkPlanFile> searchDay(@RequestParam(required = false) String date,
                                     String filename) {
        LocalDate localdate = StringUtils.isBlank(date) ? null : LocalDate.parse(date);
        return workPlanFileService.searchDayFile(localdate, ContentType.ofType(filename));
    }

    /**
     * 周管控会材料查询
     * @param date
     * @return
     */
    @PostMapping("/week")
    public List<WorkPlanFile> searchWeek(@RequestParam String date) {
        LocalDate localdate = StringUtils.isBlank(date) ? null : LocalDate.parse(date + "-1", DateTimeFormatter.ISO_WEEK_DATE);
        return workPlanFileService.searchWeekFile(localdate, ContentType.RISK_ALARM);
    }

    /**
     * 月督查报告查询
     * @param date
     * @return
     */
    @PostMapping("/month")
    public List<WorkPlanFile> searchMonth(@RequestParam String date) {
        LocalDate localdate = StringUtils.isBlank(date) ? null : LocalDate.parse(date + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
        return workPlanFileService.searchMonthFile(localdate, ContentType.MONTH_REPORT);
    }

    @PostMapping("/download")
    public void download(@RequestBody WorkPlanFile workPlanFile, HttpServletResponse response) {
        setResponseHeader(response, workPlanFile.getFilename());
        workPlanFileService.download(workPlanFile, response);
    }

    @PostMapping("/main")
    public List<WorkPlanOfMainDetail> searchMain(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return workPlanOfMainAndSubService.searchMain(date);
    }

    @PostMapping("/main/analysis")
    public void analysisMain(MultipartFile file) throws IOException {
        workPlanOfMainAndSubService.saveAndAnalysisMain(file);
    }

    @PostMapping("/sub")
    public WorkPlanOfSubModel searchSub(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return workPlanOfMainAndSubService.searchSub(date);
    }

    @PostMapping("/sub/analysis")
    public void analysisSub(MultipartFile file) throws IOException {
        workPlanOfMainAndSubService.saveAndAnalysisSub(file);
    }

    @GetMapping("/analysis/{date}")
    public Map<String, Object> autoProcess(@PathVariable("date") LocalDate date) {
//        if (!CommonFunc.checkIsLogined(request)) {
//            return CommonFunc.makeErrResult("300099");
//        }
        try {
            workPlanAutoDataService.processAuto(date);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonFunc.makeErrResult(e.getMessage());
        }
        return null;
    }

    /**
     * 移除文件
     *
     * @param date     日期
     * @param filename 文件名
     * @return 返回信息
     */
    @GetMapping("/remove/{date}/{filename}")
    public Map<String, Object> remove(@PathVariable("date") LocalDate date, @PathVariable("filename") String filename) {
//        if (!CommonFunc.checkIsLogined(request)) {
//            return CommonFunc.makeErrResult("300099");
//        }
        ContentType contentType = ContentType.ofType(filename);
        if (contentType == null) {
            throw new RuntimeException("未知的文件内容类型");
        }
        workPlanService.removeFile(date, contentType == ContentType.RISK_ALARM ? null : "auto", contentType, filename);
        return null;
    }

    @GetMapping("/grab")
    public void workPlanDataSendToElink(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                        @RequestParam String elinkIds
    ) {
        workPlanAutoDataService.grabDataWhenAndSaveAsFile(date, date, elinkIds);
    }

}