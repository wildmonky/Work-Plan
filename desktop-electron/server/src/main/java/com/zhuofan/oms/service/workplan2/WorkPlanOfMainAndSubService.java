package com.zhuofan.oms.service.workplan2;

import com.alibaba.excel.util.BooleanUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhuofan.oms.config.mybatis.DbTransactionManager;
import com.zhuofan.oms.entity.workplan.WorkPlanOfMain;
import com.zhuofan.oms.entity.workplan.WorkPlanOfMainDetail;
import com.zhuofan.oms.entity.workplan.WorkPlanOfSub;
import com.zhuofan.oms.entity.workplan.sub.LiveWorkPermit;
import com.zhuofan.oms.entity.workplan.sub.MaintenancePlan;
import com.zhuofan.oms.entity.workplan.sub.SecondaryPowerSupplyMode;
import com.zhuofan.oms.enums.workplan.ContentType;
import com.zhuofan.oms.mapper.workplan.*;
import com.zhuofan.oms.model.workplan.WorkPlanOfSubModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主配调服务
 */
@Service
public class WorkPlanOfMainAndSubService {

    @Resource
    private WorkPlanService2 workPlanService2;

    @Resource
    private WorkPlanOfMainMapper workPlanOfMainMapper;

    @Resource
    private WorkPlanOfMainDetailsMapper workPlanOfMainDetailsMapper;

    @Resource
    private WorkPlanOfSubMapper workPlanOfSubMapper;

    @Resource
    private MaintenancePlanMapper maintenancePlanMapper;

    @Resource
    private SecondaryPowerSupplyModeMapper secondaryPowerSupplyModeMapper;

    @Resource
    private LiveWorkPermitMapper liveWorkPermitMapper;

    @Resource
    private DbTransactionManager dbTransactionManager;

    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<WorkPlanOfMainDetail> searchMain(LocalDate date) {
        WorkPlanOfMain workPlanOfMain = workPlanOfMainMapper.selectOne(Wrappers.<WorkPlanOfMain>lambdaQuery().eq(WorkPlanOfMain::getDate, date));
        if (workPlanOfMain == null) {
            File[] uploadFile = workPlanService2.getUploadFile(date, ContentType.MASTER);
            return Collections.emptyList();
        }

        return workPlanOfMainDetailsMapper.selectList(Wrappers.<WorkPlanOfMainDetail>lambdaQuery()
                .eq(WorkPlanOfMainDetail::getMainId, workPlanOfMain.getId())
        );
    }

    public void saveAndAnalysisMain(MultipartFile file) throws IOException {
        // 解析日期
        LocalDate date = analysisMain(file.getInputStream());
        // 保存文件
        workPlanService2.saveFile(date, null, ContentType.MASTER, Collections.singletonList(file));
    }

    /**
     * 解析主调计划
     * @param inputStream 上传主调计划输入流
     */
    public LocalDate analysisMain(InputStream inputStream) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            WorkPlanOfMain workPlanOfMain = new WorkPlanOfMain();

            workPlanOfMain.setCreateTime(LocalDateTime.now());
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            // 解析日期
            XSSFRow firstRow = firstSheet.getRow(0);
            if (firstRow == null) {
                throw new RuntimeException("主调文件格式异常");
            }

            String value = firstRow.getCell(0).getStringCellValue();
            workPlanOfMain.setUploadFileName(value);
            Pattern pattern = Pattern.compile("(?<year>\\d{4})年(?<month>\\d{1,2})月(?<day>\\d{1,2})日");
            Matcher matcher = pattern.matcher(value);
            if (!matcher.find()) {
                throw new RuntimeException("主调文件首行不存在日期，如xxxx年xx月xx日");
            }
            String year = matcher.group("year");
            String month = matcher.group("month");
            String day = matcher.group("day");
            LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
            workPlanOfMain.setDate(localDate.format(DateTimeFormatter.ISO_DATE));

            // 值班安排
            XSSFRow thirdRow = firstSheet.getRow(2);
            if (thirdRow != null) {
                XSSFCell cell = thirdRow.getCell(0);
                workPlanOfMain.setCrewSchedule(cell != null ? cell.getStringCellValue() : null);
            }
            // 总结
            XSSFRow fourthRow = firstSheet.getRow(3);
            if (fourthRow != null) {
                XSSFCell cell = fourthRow.getCell(0);
                workPlanOfMain.setStatistics(cell == null ? null : cell.getStringCellValue());
            }

            // 主调作业计划解析
            List<WorkPlanOfMainDetail> workPlanOfMainDetails = new ArrayList<>();
            for (int i = 5; i <= firstSheet.getLastRowNum(); i++) {
                XSSFRow row = firstSheet.getRow(i);
                if (row == null) {
                    continue;
                }

                WorkPlanOfMainDetail detail = new WorkPlanOfMainDetail();
                cellValueHandle(row, 1, Cell::getStringCellValue, detail::setOperateContent);
                if (StringUtils.isBlank(detail.getOperateContent())) {
                    continue;
                }
                cellValueHandle(row, 2, Cell::getStringCellValue, detail::setManageDepartment);
                cellValueHandle(row, 3, cel -> {
                    try {
                        LocalDateTime timeValue = cel.getLocalDateTimeCellValue();
                        return timeValue == null ? null : timeValue.format(dateTimeFormatter);
                    } catch (RuntimeException e) {
                        return null;
                    }
                }, detail::setPlanStartTime);
                cellValueHandle(row, 4, cel -> {
                    try {
                        LocalDateTime timeValue = cel.getLocalDateTimeCellValue();
                        return timeValue == null ? null : timeValue.format(dateTimeFormatter);
                    } catch (RuntimeException e) {
                        return null;
                    }
                }, detail::setPlanEndTime);
                cellValueHandle(row, 5, Cell::getStringCellValue, detail::setMaintenanceContent);
                cellValueHandle(row, 6, Cell::getStringCellValue, detail::setMainMethodAdjustment);
                cellValueHandle(row, 7, Cell::getStringCellValue, detail::setModify);
                cellValueHandle(row, 8, Cell::getStringCellValue, detail::setRiskLevel);
                cellValueHandle(row, 9, Cell::getStringCellValue, detail::setRiskPoint);
                cellValueHandle(row, 10, Cell::getStringCellValue, detail::setMaintenanceListControl);
                cellValueHandle(row, 11, Cell::getStringCellValue, detail::setManager);
                workPlanOfMainDetails.add(detail);
            }

            WorkPlanOfMain existedMain = workPlanOfMainMapper.selectOne(Wrappers.<WorkPlanOfMain>lambdaQuery()
                    .eq(WorkPlanOfMain::getDate, workPlanOfMain.getDate())
            );
            dbTransactionManager.transaction(() -> {
                if (existedMain != null) {
                    workPlanOfMainDetailsMapper.delete(Wrappers.<WorkPlanOfMainDetail>lambdaQuery().eq(WorkPlanOfMainDetail::getMainId, existedMain.getId()));
                    workPlanOfMainMapper.delete(Wrappers.<WorkPlanOfMain>lambdaQuery().eq(WorkPlanOfMain::getId, existedMain.getId()));
                }
                workPlanOfMainMapper.insert(workPlanOfMain);
                if (!workPlanOfMainDetails.isEmpty()) {
                    workPlanOfMainDetails.forEach(e -> e.setMainId(workPlanOfMain.getId()));
                    workPlanOfMainDetailsMapper.insertBatchSomeColumn(workPlanOfMainDetails);
                }
                return null;
            });
            return localDate;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public WorkPlanOfSubModel searchSub(LocalDate date) {

        WorkPlanOfSubModel model = new WorkPlanOfSubModel();
        WorkPlanOfSub workPlanOfSub = workPlanOfSubMapper.selectOne(Wrappers.<WorkPlanOfSub>lambdaQuery().eq(WorkPlanOfSub::getDate, date));
        if (workPlanOfSub == null) {
            return model;
        }

        model.setWorkPlanOfSub(workPlanOfSub);

        List<MaintenancePlan> maintenancePlans = maintenancePlanMapper.selectList(Wrappers.<MaintenancePlan>lambdaQuery().eq(MaintenancePlan::getSubId, workPlanOfSub.getId()));
        model.setMaintenancePlanList(maintenancePlans);

        List<SecondaryPowerSupplyMode> secondaryPowerSupplyModes = secondaryPowerSupplyModeMapper.selectList(Wrappers.<SecondaryPowerSupplyMode>lambdaQuery().eq(SecondaryPowerSupplyMode::getSubId, workPlanOfSub.getId()));
        model.setSecondaryPowerSupplyModeList(secondaryPowerSupplyModes);

        List<LiveWorkPermit> liveWorkPermits = liveWorkPermitMapper.selectList(Wrappers.<LiveWorkPermit>lambdaQuery().eq(LiveWorkPermit::getSubId, workPlanOfSub.getId()));
        model.setLiveWorkPermitList(liveWorkPermits);

        return model;
    }

    public void saveAndAnalysisSub(MultipartFile file) throws IOException {
        if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
            return;
        }
        Pattern pattern = Pattern.compile("(?<year>\\d+)年(?<month>\\d+)月(?<date>\\d+)日");
        Matcher matcher = pattern.matcher(file.getOriginalFilename());
        if (!matcher.find()) {
            throw new RuntimeException("上传配调文件名没有日期，格式：xxxx年xx月xx日");
        }

        LocalDate date = LocalDate.of(Integer.parseInt(matcher.group("year")),
                Integer.parseInt(matcher.group("month")),
                Integer.parseInt(matcher.group("date"))
        );
        // 保存配调文件
        workPlanService2.saveFile(date, null, ContentType.SUB, Collections.singletonList(file));
        analysisSub(file.getInputStream(), date);
    }
    /**
     * 解析配调计划
     * @param inputStream 上传配调计划输入流
     */
    public void analysisSub(InputStream inputStream, LocalDate date) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            WorkPlanOfSub workPlanOfSub = new WorkPlanOfSub();
            workPlanOfSub.setCreateTime(LocalDateTime.now());
            workPlanOfSub.setDate(date.format(DateTimeFormatter.ISO_DATE));
            XSSFSheet firstSheet = null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (!workbook.isSheetHidden(i)) {
                    firstSheet = workbook.getSheetAt(i);
                    break;
                }
            }
            if (firstSheet == null) {
                return;
            }
            // 计划检修单-0；转供电方式单-1；带电作业单-2
            int flag = 0;
            // 配调作业计划解析
            List<MaintenancePlan> maintenancePlans = new ArrayList<>();
            List<SecondaryPowerSupplyMode> secondaryPowerSupplyModes = new ArrayList<>();
            List<LiveWorkPermit> liveWorkPermits = new ArrayList<>();
            for (int i = 0; i <= firstSheet.getLastRowNum(); i++) {
                XSSFRow row = firstSheet.getRow(i);
                if (row == null) {
                    continue;
                }

                XSSFCell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }

                CellType cellType = cell.getCellType();
                if (cellType == CellType.STRING) {
                    String value = Optional.ofNullable(cell.getStringCellValue()).orElse("");
                    if (value.contains("计划检修")) {
                        flag = 0;
                        workPlanOfSub.setOfMaintenance(value);
                    } else if (value.contains("转供电方式")){
                        flag = 1;
                        workPlanOfSub.setOfSecondary(value);
                    } else if (value.contains("带电作业")) {
                        flag = 2;
                        workPlanOfSub.setOfLiveWork(value);
                    }
                    // 跳过表头
                    i++;
                    continue;
                }

                // 计划检修单
                if (flag == 0) {
                    MaintenancePlan maintenancePlan = new MaintenancePlan();
                    AtomicBoolean hasVal = new AtomicBoolean(false);
                    cellValueHandle(row, 1, cel -> {
                        String value = cel.getStringCellValue();
                        hasVal.set(StringUtils.isNotBlank(value));
                        return value;
                    }, maintenancePlan::setCode);
                    if (BooleanUtils.isFalse(hasVal.get())) {
                        continue;
                    }
                    cellValueHandle(row, 2, Cell::getStringCellValue, maintenancePlan::setRequestUnit);
                    cellValueHandle(row, 3, Cell::getStringCellValue, maintenancePlan::setElectricOffEquipments);
                    cellValueHandle(row, 4, Cell::getStringCellValue, maintenancePlan::setMaintenanceType);
                    cellValueHandle(row, 5, Cell::getStringCellValue, maintenancePlan::setElectricOffStartTime);
                    cellValueHandle(row, 6, Cell::getStringCellValue, maintenancePlan::setElectricOffEndTime);
                    cellValueHandle(row, 7, Cell::getStringCellValue, maintenancePlan::setContent);
                    cellValueHandle(row, 8, Cell::getStringCellValue, maintenancePlan::setApprovalOpinion);
                    cellValueHandle(row, 9, Cell::getStringCellValue, maintenancePlan::setIsInvolveImportantUser);
                    cellValueHandle(row, 10, Cell::getStringCellValue, maintenancePlan::setRiskLevel);
                    cellValueHandle(row, 11, Cell::getStringCellValue, maintenancePlan::setControlStandard);
                    cellValueHandle(row, 12, Cell::getStringCellValue, maintenancePlan::setComments);
                    maintenancePlans.add(maintenancePlan);
                }

                // 转供电方式单
                if (flag == 1) {
                    SecondaryPowerSupplyMode secondaryPowerSupplyMode = new SecondaryPowerSupplyMode();
                    cellValueHandle(row, 1, Cell::getStringCellValue, secondaryPowerSupplyMode::setCode);
                    cellValueHandle(row, 2, Cell::getStringCellValue, secondaryPowerSupplyMode::setUnit);
                    cellValueHandle(row, 3, Cell::getStringCellValue, secondaryPowerSupplyMode::setOperateTask);
                    cellValueHandle(row, 4, Cell::getStringCellValue, secondaryPowerSupplyMode::setTaskStatus);
                    cellValueHandle(row, 5, Cell::getStringCellValue, secondaryPowerSupplyMode::setPowerOutStartTime);
                    cellValueHandle(row, 6, Cell::getStringCellValue, secondaryPowerSupplyMode::setPowerRecoverStartTime);
                    cellValueHandle(row, 7, Cell::getStringCellValue, secondaryPowerSupplyMode::setTaskLoopClose);
                    cellValueHandle(row, 8, Cell::getStringCellValue, secondaryPowerSupplyMode::setApprovalOpinion);
                    cellValueHandle(row, 9, Cell::getStringCellValue, secondaryPowerSupplyMode::setIsInvolveImportantUser);
                    cellValueHandle(row, 10, Cell::getStringCellValue, secondaryPowerSupplyMode::setRiskLevel);
                    cellValueHandle(row, 11, Cell::getStringCellValue, secondaryPowerSupplyMode::setControlStandard);
                    cellValueHandle(row, 12, Cell::getStringCellValue, secondaryPowerSupplyMode::setComments);
                    secondaryPowerSupplyModes.add(secondaryPowerSupplyMode);
                }

                // 带电作业单
                if (flag == 2) {
                    LiveWorkPermit liveWorkPermit = new LiveWorkPermit();
                    cellValueHandle(row, 1, Cell::getStringCellValue, liveWorkPermit::setCode);
                    cellValueHandle(row, 2, Cell::getStringCellValue, liveWorkPermit::setUnit);
                    cellValueHandle(row, 3, Cell::getStringCellValue, liveWorkPermit::setLines);
                    cellValueHandle(row, 4, Cell::getStringCellValue, liveWorkPermit::setStatus);
                    cellValueHandle(row, 5, Cell::getStringCellValue, liveWorkPermit::setReason);
                    cellValueHandle(row, 6, Cell::getStringCellValue, liveWorkPermit::setSwitchOffTime);
                    cellValueHandle(row, 7, Cell::getStringCellValue, liveWorkPermit::setSwitchOnTime);
                    cellValueHandle(row, 8, Cell::getStringCellValue, liveWorkPermit::setApprovalOpinion);
                    cellValueHandle(row, 9, Cell::getStringCellValue, liveWorkPermit::setIsInvolveImportantUser);
                    cellValueHandle(row, 10, Cell::getStringCellValue, liveWorkPermit::setRiskLevel);
                    cellValueHandle(row, 11, Cell::getStringCellValue, liveWorkPermit::setControlStandard);
                    cellValueHandle(row, 12, Cell::getStringCellValue, liveWorkPermit::setComments);
                    liveWorkPermits.add(liveWorkPermit);
                }
            }

            WorkPlanOfSub existedSub = workPlanOfSubMapper.selectOne(Wrappers.<WorkPlanOfSub>lambdaQuery()
                    .eq(WorkPlanOfSub::getDate, workPlanOfSub.getDate())
            );
            dbTransactionManager.transaction(() -> {
                if (existedSub != null) {
                    maintenancePlanMapper.delete(Wrappers.<MaintenancePlan>lambdaQuery().eq(MaintenancePlan::getSubId, existedSub.getId()));
                    secondaryPowerSupplyModeMapper.delete(Wrappers.<SecondaryPowerSupplyMode>lambdaQuery().eq(SecondaryPowerSupplyMode::getSubId, existedSub.getId()));
                    liveWorkPermitMapper.delete(Wrappers.<LiveWorkPermit>lambdaQuery().eq(LiveWorkPermit::getSubId, existedSub.getId()));
                    workPlanOfSubMapper.delete(Wrappers.<WorkPlanOfSub>lambdaQuery().eq(WorkPlanOfSub::getDate, existedSub.getDate()));
                }
                workPlanOfSubMapper.insert(workPlanOfSub);
                if (!maintenancePlans.isEmpty()) {
                    maintenancePlans.forEach(e -> e.setSubId(workPlanOfSub.getId()));
                    maintenancePlanMapper.insertBatchSomeColumn(maintenancePlans);
                }

                if (!secondaryPowerSupplyModes.isEmpty()) {
                    secondaryPowerSupplyModes.forEach(e -> e.setSubId(workPlanOfSub.getId()));
                    secondaryPowerSupplyModeMapper.insertBatchSomeColumn(secondaryPowerSupplyModes);
                }

                if (!liveWorkPermits.isEmpty()) {
                    liveWorkPermits.forEach(e -> e.setSubId(workPlanOfSub.getId()));
                    liveWorkPermitMapper.insertBatchSomeColumn(liveWorkPermits);
                }
                return null;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void cellValueHandle(Row row, int cellIndex, Function<Cell, T> cellFunc, Consumer<T> consumer) {
        if (consumer == null) {
            return;
        }

        if (row == null || cellFunc == null) {
            consumer.accept(null);
            return;
        }
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            consumer.accept(null);
            return;
        }
        T val = cellFunc.apply(cell);
        consumer.accept(val);
    }

}
