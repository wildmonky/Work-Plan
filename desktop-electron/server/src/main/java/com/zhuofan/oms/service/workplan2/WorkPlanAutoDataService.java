package com.zhuofan.oms.service.workplan2;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.kafka.KafkaClient;
import com.zhuofan.oms.entity.CommonSettings;
import com.zhuofan.oms.entity.workplan.CrewRiskLevelNumCountDayRecord;
import com.zhuofan.oms.enums.workplan.*;
import com.zhuofan.oms.mapper.CommonSettingsMapper;
import com.zhuofan.oms.mapper.SecondPlanRecordMapper;
import com.zhuofan.oms.mapper.TransformerSubstationMapper;
import com.zhuofan.oms.mapper.workplan.CrewRiskLevelNumCountDayRecordMapper;
import com.zhuofan.oms.mapper.workplan.CrewWorkLoadRateDayRecordMapper;
import com.zhuofan.oms.mapper.workplan.auto.WorkPlanDataAutoModelMapper;
import com.zhuofan.oms.model.workplan.*;
import com.zhuofan.oms.orm.model.SecondPlanRecord;
import com.zhuofan.oms.orm.model.TransformerSubstation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 抓取 工作计划数据
 */
@Slf4j
@Service
public class WorkPlanAutoDataService {

    /**
     * 使用elink发送文件时，需要将文件保存到指定目录
     */
    @Value("${sys.kafka_path_file}")
    private String sendFileByElinkSaveFilePath;

    /**
     * 使用elink发送时，设置应用名
     */
    @Value("${sys.kafka_appname}")
    private String appNameWithinElink;

    @Value("${work.plan.data.url}")
    private String uri;

    @Resource
    private CommonSettingsMapper commonSettingsMapper;

    @Resource
    private WorkPlanService2 workPlanService2;

    @Resource
    private WorkPlanDataAutoModelMapper workPlanDataAutoModelMapper;

    @Resource
    private TransformerSubstationMapper transformerSubstationMapper;

    @Resource
    private CrewWorkLoadRateDayRecordMapper crewWorkLoadRateDayRecordMapper;

    @Resource
    private CrewRiskLevelNumCountDayRecordMapper crewRiskLevelNumCountDayRecordMapper;

    @Resource
    private SecondPlanRecordMapper secondPlanRecordMapper;

    @Resource
    private RestTemplate restTemplate;

//    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Map<String, Map<String, String>> map = new HashMap<>();

    public WorkPlanAutoDataService() {
        map.put("workClassDetail", new HashMap<String, String>() {{
            put("1", "检修");
            put("2", "试验");
            put("3", "运行");
        }});

        map.put("workMajor", new HashMap<String, String>() {{
            put("1", "本单位");
            put("2", "外单位");
        }});

        map.put("planType", new HashMap<String, String>() {{
            put("1", "计划");
            put("2", "临时");
        }});

        map.put("planClass", new HashMap<String, String>() {{
            put("1", "年计划");
            put("2", "月计划");
            put("3", "周计划");
            put("4", "日计划");
        }});

        map.put("workMode", new HashMap<String, String>() {{
            put("1", "停电");
            put("2", "不停电");
            put("3", "带电作业");
        }});

        HashMap<String, String> yesNoValueMap = new HashMap<String, String>() {{
            put("1", "是");
            put("2", "否");
        }};
        map.put("needVideoSurveillance", yesNoValueMap);
        map.put("bindVideoSurveillance", yesNoValueMap);
        map.put("monitorWork", yesNoValueMap);
        map.put("needTicket", yesNoValueMap);
        map.put("prospectMode", new HashMap<String, String>() {{
            put("1", "无需勘察");
            put("2", "需勘察");
        }});
        map.put("needTools", yesNoValueMap);
        map.put("existedKeyFlow", yesNoValueMap);
    }

    /**
     * 自动分析每日工作计划
     *
     * @param date 日期
     */
    public synchronized void processAuto(LocalDate date) {
        workPlanService2.setChangeSheetName(false);

        String dirName = "auto";
        // 从日工作计划数据库中获取作业数据
        List<WorkPlanDataModel> workPlanDataModels = getWorkPlansByDate(date, date);
        if (workPlanDataModels == null) {
            log.error("获取 {} 工作计划作业数据异常", date);
            return;
        }

        // 保存作业计划数据到文件
        if (!saveWorkPlanDataToFile(date, ContentType.CHECK, dirName, workPlanDataModels)) {
            throw new RuntimeException("作业计划数据保存失败");
        }
        workPlanService2.setCrewMembers(workPlanService2.flushCrewMembersWithSort());

        // 前置数据处理
        workPlanService2.preDataHandler(workPlanDataModels);
        // 根据模板文件 初始化要下载的临时文件
        File templateFile = workPlanService2.prepareModuleFile(date, dirName, workPlanDataModels);
        // 数据写入模板文件, 并设置列公式
        templateFile = workPlanService2.dataHandler(date, templateFile, workPlanDataModels);
        // 后置 数据处理
        templateFile = workPlanService2.postDataHandler(date, templateFile, workPlanDataModels);

        // 获取处理后的临时文件 处理好的数据并导出
        workPlanService2.exportExcel(date, dirName, templateFile, null, workPlanDataModels);
    }

    /**
     * 保存每日作业计划自动分析时，获取工作计划作业数据
     *
     * @param date 日期
     * @param contentType 内容类型
     * @param workPlans 作业计划数据
     */
    public synchronized boolean saveWorkPlanDataToFile(LocalDate date, ContentType contentType, String dirName, List<WorkPlanDataModel> workPlans) {
        String fileName = contentType.getType() ;
        String fileSuffix = "xlsx";
        try {
            File temporaryFile = workPlanService2.newTemporaryFile(date, dirName, fileName, fileSuffix);
//            log.info("上传文件保存路径：{}", temporaryFile.getPath());
            ClassPathResource templateFile = new ClassPathResource("workplan/auto_check_template.xlsx");
            EasyExcel.write(temporaryFile)
                    .withTemplate(templateFile.getInputStream())
                    .head(WorkPlanDataModel.class)
                    .needHead(false)
                    .sheet(0)
                    .doFill(workPlans);
            return true;
        } catch (RuntimeException e) {
            log.warn("临时文件创建失败，文件名：{}", fileName);
            return false;
        } catch (IOException e) {
            log.warn("自动获取日作业计划保存模板文件获取失败");
            return false;
        }
    }

    /**
     * 获取指定日期的 工作计划数据<br/>
     *  专业大类=调度<br/>
     *  +<br/>
     *  专业大类=基建 and 作业类型 in (自动化，继电保护，通信工程)
     * @param startDate 开始日期
     * @param endDate 结束日期 包含
     * @return 工作计划数据
     */
    public List<WorkPlanDataModel> getWorkPlansByDate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.plusDays(1L).atStartOfDay();
        List<StatusEnum> statuses = Arrays.asList(StatusEnum.NOT_START, StatusEnum.IN_PROGRESS, StatusEnum.INTERRUPTED, StatusEnum.RESCHEDULED);
        // 批量获取数据
        List<WorkPlanDataAutoModel> searchOfDu = search(startTime, endTime,
                Collections.singletonList(MajorWideEnum.du),
                Collections.emptyList(),
                Collections.emptyList(),
                statuses
        );
        List<WorkPlanDataAutoModel> searchOfJj = search(startTime, endTime,
                Arrays.asList(MajorWideEnum.jj, MajorWideEnum.sj, MajorWideEnum.yx),
                Collections.emptyList(),
                Arrays.asList(TypeEnum.AUTOMATION, TypeEnum.RELAY_PROTECTION, TypeEnum.COMMUNICATION),
                statuses
        );
        List<WorkPlanDataAutoModel> workPlansByDateBatch = new ArrayList<>(searchOfDu);
        workPlansByDateBatch.addAll(searchOfJj);
        return parseWorkPlanData(workPlansByDateBatch).stream().map(WorkPlanDataAutoModel::toWorkPlanDataModel).collect(Collectors.toList());
    }

    public List<WorkPlanDataAutoModel> searchWorkPlansBatch(WorkPlanDataAutoModel param, int batchSize) {
        Long total = searchWorkPlanCountByApi(param);
        if (total == 0) {
            return Collections.emptyList();
        }

        List<WorkPlanDataAutoModel> reList = new ArrayList<>();
        int pageNo = 1;
        long maxPageNum = total / batchSize + (total % batchSize == 0 ? 0 : 1);
        while (pageNo <= maxPageNum) {
            List<WorkPlanDataAutoModel> data = searchWorkPlanByApi(param, pageNo, batchSize);
            if (CollectionUtils.isEmpty(data)) {// 没数据了
                break;
            }
            // 数据转换 电压
            data.forEach(ele -> {
                String voltageLevel = ele.getVoltageLevel();
                if (NumberUtils.isCreatable(voltageLevel)) {
                    try {
                        Integer voltage = NumberUtils.createInteger(voltageLevel);
                        if (voltage == null) {
                            return;
                        }

                        if (voltage < 1000) {
                            ele.setVoltageLevel(voltage + "V");
                            return;
                        }

                        ele.setVoltageLevel(voltage / 1000 + "kV");
                    } catch (RuntimeException e) {
                        log.warn("电压转换失败：{}", voltageLevel);
                    }
                }
            });
            reList.addAll(data);
            pageNo++;
        }

        return reList;
    }

    public Long searchWorkPlanCountByApi(WorkPlanDataAutoModel param) {
        RequestEntity<WorkPlanDataAutoModel> requestEntity = new RequestEntity<>(param, HttpMethod.POST, URI.create(uri));
        ResponseEntity<Long> exchange = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Long>() {});
        if (exchange.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(exchange.getBody()).orElse(0L);
        }
        return 0L;
    }

    public List<WorkPlanDataAutoModel> searchWorkPlanByApi(WorkPlanDataAutoModel param, int pageNo, int batchSize) {
        RequestEntity<WorkPlanDataAutoModel> requestEntity = new RequestEntity<>(param, HttpMethod.POST, URI.create(uri + "/" + pageNo + "/" + batchSize));
        ResponseEntity<List<WorkPlanDataAutoModel>> exchange = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<WorkPlanDataAutoModel>>() {});
        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException(String.format("日作业计划获取失败，参数: %s, pageNo: %d, pageSize: %d", param, pageNo, batchSize));
        }

        if (!exchange.hasBody()) {
            return Collections.emptyList();
        }

        List<WorkPlanDataAutoModel> body = exchange.getBody();
        // 分子公司固定 广东 地市局固定 东莞供电局
        body.forEach(e -> {
            e.setCompany("广东");
            e.setPowerSupplyBureau("东莞供电局");
        });

        return body;
    }

    public List<WorkPlanStatusModel> searchWorkPlanStatusStatistics(WorkPlanDataAutoModel param) {
        RequestEntity<WorkPlanDataAutoModel> requestEntity = new RequestEntity<>(param, HttpMethod.POST, URI.create(uri + "/status/statistics"));
        ResponseEntity<List<WorkPlanStatusModel>> exchange = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<WorkPlanStatusModel>>() {});
        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException(String.format("日作业计划作业状态统计获取失败，参数: %s", param));
        }
        return exchange.getBody();
    }

    public List<WorkPlanStatusModel> searchWorkPlanStatusStatistics(List<WorkPlanDataAutoModel> data) {
        if (CollectionUtils.isEmpty(data)) {
            return Collections.emptyList();
        }

        Map<String, Long> statusMap = data.stream().collect(Collectors.groupingBy(WorkPlanDataAutoModel::getWorkStatus, Collectors.counting()));
        List<WorkPlanStatusModel> reList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : statusMap.entrySet()) {
            WorkPlanStatusModel model = new WorkPlanStatusModel();
            if (!NumberUtils.isParsable(entry.getKey())) {
                continue;
            }
            model.setStatus(Long.parseLong(entry.getKey()));
            model.setCount(entry.getValue());
            reList.add(model);
        }

        return reList;
    }

    public WorkPlanPage searchWorkPlanPageByApi(WorkPlanDataAutoModel query, Integer pageNo, Integer pageSize) {
        int[] professions = query.getWorkProfessions();
        if (professions == null || professions.length == 0) {
            professions = new int[]{2, 4};
            query.setWorkProfessions(professions);
        }
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 100 : pageSize;
        long count = searchWorkPlanCountByApi(query);
        List<WorkPlanDataAutoModel> workPlanDataAutoModels = searchWorkPlanByApi(query, pageNo, pageSize);
        workPlanDataAutoModels = parseWorkPlanData(workPlanDataAutoModels);
        List<WorkPlanRiskLevelCountModel> workPlanRiskLevelCountModels = searchWorkPlanRiskLevelStatistics(query);
        return WorkPlanPage.page(workPlanDataAutoModels, count, workPlanRiskLevelCountModels);
    }

    public List<WorkPlanRiskLevelCountModel> searchWorkPlanRiskLevelStatistics(WorkPlanDataAutoModel param) {
        RequestEntity<WorkPlanDataAutoModel> requestEntity = new RequestEntity<>(param, HttpMethod.POST, URI.create(uri + "/risk/level/statistics"));
        ResponseEntity<List<WorkPlanRiskLevelCountModel>> exchange = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<WorkPlanRiskLevelCountModel>>() {});
        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException(String.format("日作业计划风险等级统计获取失败，参数: %s", param));
        }
        return exchange.getBody();
    }

    public <T> List<T> searchByApi(String appendUri, WorkPlanDataAutoModel param, Class<T> clazz,
                                   BiConsumer<RequestEntity<WorkPlanDataAutoModel>, ResponseEntity<List<T>>> errorConsumer
    ) {
        String requestUri = uri + appendUri;
        RequestEntity<WorkPlanDataAutoModel> requestEntity = new RequestEntity<>(param, HttpMethod.POST, URI.create(requestUri));
        ResponseEntity<List<T>> exchange = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<T>>() {});
        if (exchange.getStatusCode() != HttpStatus.OK) {
            if (errorConsumer != null) {
                errorConsumer.accept(requestEntity, exchange);
            }
            throw new RuntimeException(String.format("接口请求失败，地址：%s, 参数: %s", requestUri, param));
        }
        return exchange.getBody();
    }

    public List<WorkPlanDataAutoModel> searchWorkPlansByDb(WorkPlanDataAutoModel query) {
        List<WorkPlanDataAutoModel> workPlanDataAutoModels = workPlanDataAutoModelMapper.selectAll(query);
        return parseWorkPlanData(workPlanDataAutoModels);
    }

    /**
     * 枚举类转换
     *
     * @param workPlanDataAutoModels 作业计划
     * @return 作业计划-枚举类已转换
     */
    public static List<WorkPlanDataAutoModel> parseWorkPlanData(List<WorkPlanDataAutoModel> workPlanDataAutoModels) {
        if (CollectionUtils.isEmpty(workPlanDataAutoModels)) {
            return Collections.emptyList();
        }

        List<WorkPlanDataAutoModel> workPlanDataModels = new ArrayList<>();
        workPlanDataAutoModels.forEach(origin -> {
            WorkPlanDataAutoModel target = new WorkPlanDataAutoModel();
            BeanUtils.copyProperties(origin, target);
            target.setRiskLevel(RiskLevelEnum.getDesc(target.getRiskLevel()));
            target.setWorkStatus(StatusEnum.getDesc(target.getWorkStatus()));
            target.setWorkProfession(MajorWideEnum.getDesc(target.getWorkProfession()));
            target.setWorkClass(MajorEnum.getDesc(target.getWorkClass()));
            target.setWorkType(TypeEnum.getDesc(target.getWorkType()));
            target.setWorkOrigin(OriginEnum.getDesc(target.getWorkOrigin()));

            String workClassDetail = target.getWorkClassDetail();
            target.setWorkClassDetail(map.get("workClassDetail").getOrDefault(workClassDetail, workClassDetail));

            String workMajor = target.getWorkMajor();
            target.setWorkMajor(map.get("workMajor").getOrDefault(workMajor, workMajor));

            String planType = target.getPlanType();
            target.setPlanType(map.get("planType").getOrDefault(planType, planType));

            String planClass = target.getPlanClass();
            target.setPlanClass(map.get("planClass").getOrDefault(planClass, planClass));

            String workMode = target.getWorkMode();
            target.setWorkMode(map.get("workMode").getOrDefault(workMode, workMode));

            String needVideoSurveillance = target.getNeedVideoSurveillance();
            target.setNeedVideoSurveillance(map.get("needVideoSurveillance").getOrDefault(needVideoSurveillance, needVideoSurveillance));

            String bindVideoSurveillance = target.getBindVideoSurveillance();
            target.setBindVideoSurveillance(map.get("bindVideoSurveillance").getOrDefault(bindVideoSurveillance, bindVideoSurveillance));

            String monitorWork = target.getMonitorWork();
            target.setMonitorWork(map.get("monitorWork").getOrDefault(monitorWork, monitorWork));

            String needTicket = target.getNeedTicket();
            target.setNeedTicket(map.get("needTicket").getOrDefault(needTicket, needTicket));

            String prospectMode = target.getProspectMode();
            target.setProspectMode(map.get("prospectMode").getOrDefault(prospectMode, prospectMode));

            String needTools = target.getNeedTools();
            target.setNeedTools(map.get("needTools").getOrDefault(needTools, needTools));

            String existedKeyFlow = target.getExistedKeyFlow();
            target.setExistedKeyFlow(map.get("existedKeyFlow").getOrDefault(existedKeyFlow, existedKeyFlow));

            // 电压格式
            String voltageLevel = target.getVoltageLevel();
            if (StringUtil.isNotBlank(voltageLevel) && NumberUtils.isParsable(voltageLevel)) {
                try {
                    int voltage = Integer.parseInt(voltageLevel);
                    if (voltage > 1000) {
                        voltageLevel = voltage / 1000 + "k";
                    }
                } catch (NumberFormatException  e) {
                    log.warn("电压转换失败", e);
                }
                target.setVoltageLevel(voltageLevel + "V");
            }
            workPlanDataModels.add(target);
        });

        return workPlanDataModels;
    }

    /**
     * 做业计划 作业状态统计
     *
     * @param beginTime 计划开始时间
     * @param endTime   计划结束时间
     * @return 作业状态统计结果
     */
    public WorkPlanStatusCountModel countByWorkStatus(LocalDateTime beginTime, LocalDateTime endTime) {
        List<WorkPlanStatusModel> workPlanStatusModels = workPlanDataAutoModelMapper.selectWorkStatusNum(beginTime, endTime);
        return countByWorkStatus(workPlanStatusModels);
    }

    public WorkPlanStatusCountModel countByWorkStatus(List<WorkPlanStatusModel> workPlanStatusModels) {
        Map<Integer, Long> workStatusNum = workPlanStatusModels.stream().collect(Collectors.toMap(e -> e.getStatus() == null ? null : e.getStatus().intValue(), WorkPlanStatusModel::getCount, (e1, e2) -> e2));
        WorkPlanStatusCountModel workPlanStatusCountModel = new WorkPlanStatusCountModel();

        Long newCount = workStatusNum.getOrDefault(StatusEnum.NEW.getCode(), 0L);
        workPlanStatusCountModel.setNewNum(newCount);

        Long notStartCount = workStatusNum.getOrDefault(StatusEnum.NOT_START.getCode(), 0L);
        workPlanStatusCountModel.setNotStartNum(notStartCount);

        Long inProgressCount = workStatusNum.getOrDefault(StatusEnum.IN_PROGRESS.getCode(), 0L);
        workPlanStatusCountModel.setInProgressNum(inProgressCount);

        Long completedCount = workStatusNum.getOrDefault(StatusEnum.COMPLETED.getCode(), 0L);
        workPlanStatusCountModel.setCompletedNum(completedCount);

        Long canceledCount = workStatusNum.getOrDefault(StatusEnum.CANCELED.getCode(), 0L);
        workPlanStatusCountModel.setCanceledNum(canceledCount);

        Long interruptedCount = workStatusNum.getOrDefault(StatusEnum.INTERRUPTED.getCode(), 0L);
        workPlanStatusCountModel.setInterruptedNum(interruptedCount);

        Long reScheduledCount = workStatusNum.getOrDefault(StatusEnum.RESCHEDULED.getCode(), 0L);
        workPlanStatusCountModel.setReScheduledNum(reScheduledCount);
        return workPlanStatusCountModel;
    }

    /**
     * 作业计划 每日计划数量统计
     *
     * @param models 作业计划列表
     * @return 每日计划数量统计结果
     */
    public WorkPlanNumModel countByDate(List<WorkPlanDataAutoModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return new WorkPlanNumModel();
        }

        PlanAnalysisTool.EveryDayCountTool planAnalysisTool = new PlanAnalysisTool.EveryDayCountTool();
        for (WorkPlanDataAutoModel model : models) {
            if (!planAnalysisTool.count(model)) {
                throw new RuntimeException(model.getWorkPlanCode() + "的作业计划时间待补充");
            }
        }
        return planAnalysisTool.getCountResult();
    }

    /**
     * 作业类型分类统计
     *
     * @param models 作业计划列表
     * @return 作业类型分类统计结果
     */
    public List<WorkPlanTypeCountModel> countByPlanTypeAnalysis(List<WorkPlanDataAutoModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }

        PlanAnalysisTool planAnalysisTool = new PlanAnalysisTool();
        for (WorkPlanDataAutoModel model : models) {
            if (!planAnalysisTool.workPlanTypeCount(model)) {
                throw new RuntimeException(model.getWorkPlanCode() + ",作业计划编码待补充");
            }
        }
        return planAnalysisTool.getWorkPlanTypeCount();
    }

    public List<WorkPlanStationRiskLevelCountModel> workPlanStationAnalysis(List<WorkPlanDataAutoModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }


        // 重要厂站
        List<TransformerSubstation> transformerSubstations = transformerSubstationMapper.selectList(Wrappers.<TransformerSubstation>lambdaQuery().eq(TransformerSubstation::getIsThirteenAddSixteen, true));
        PlanAnalysisTool.SubstationCountTool substationCountTool = new PlanAnalysisTool.SubstationCountTool(transformerSubstations);
        for (WorkPlanDataAutoModel model : models) {
            if (!substationCountTool.count(model)) {
                throw new RuntimeException(model.getWorkPlanCode() + ",作业计划时间待补充");
            }
        }
        return substationCountTool.getCountResult();
    }

    public List<WorkPlanCrewSaturationCountModel> crewSaturationAnalysis(List<WorkPlanDataAutoModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }

        PlanAnalysisTool.CrewSaturationCountTool crewSaturationCountTool = new PlanAnalysisTool.CrewSaturationCountTool();
        for (WorkPlanDataAutoModel model : models) {
            if (!crewSaturationCountTool.count(model)) {
                throw new RuntimeException(model.getWorkPlanCode() + ",作业计划时间待补充");
            }
        }
        return crewSaturationCountTool.getCrewSaturationCount();

    }

    /**
     * 计算运行班组
     *
     * @param model
     */
    public void calcRunningCrew(WorkPlanDataAutoModel model) {
        WorkPlanDataModel data = model.toWorkPlanDataModel();
        workPlanService2.preDataHandler(Collections.singletonList(data));
        model.setRunningCrewName(data.getCalcRunningCrew());
    }

    /**
     * 前端首页查询使用
     *
     * @param startTime 开始时间
     * @param endTime   返回时间
     * @return DTO
     */
    public WorkPlanAnalysisModel workPlanAnalysis(LocalDateTime startTime, LocalDateTime endTime) {
        if (!ObjectUtils.allNotNull(startTime, endTime)) {
            return new WorkPlanAnalysisModel();
        }

        List<StatusEnum> statuses = Arrays.asList(StatusEnum.NOT_START, StatusEnum.IN_PROGRESS, StatusEnum.INTERRUPTED, StatusEnum.RESCHEDULED, StatusEnum.COMPLETED);
        // 调度
        List<WorkPlanDataAutoModel> search = search(startTime, endTime,
                Collections.singletonList(MajorWideEnum.du),
                Collections.emptyList(),
                Collections.emptyList(),
                statuses
        );
        ArrayList<WorkPlanDataAutoModel> workPlans = new ArrayList<>(search);
        // 生计、基建、营销 自动化、继电保护、通信工程
        List<WorkPlanDataAutoModel> search2 = search(startTime, endTime,
                Arrays.asList(MajorWideEnum.jj, MajorWideEnum.sj, MajorWideEnum.yx),
                Collections.emptyList(),
                Arrays.asList(TypeEnum.AUTOMATION, TypeEnum.RELAY_PROTECTION, TypeEnum.COMMUNICATION),
                statuses
        );
        workPlans.addAll(search2);

        WorkPlanAnalysisModel reModel = new WorkPlanAnalysisModel();
        if (CollectionUtils.isEmpty(workPlans)) {
            return reModel;
        }

        Map<RiskLevelEnum, WorkPlanRiskLevelCountModel> riskLevelCountMap = new HashMap<>();

        // 作业计划状态统计
        List<WorkPlanStatusModel> workPlanStatusModels = searchWorkPlanStatusStatistics(workPlans);
        WorkPlanStatusCountModel workPlanStatusCountModel = countByWorkStatus(workPlanStatusModels);
        reModel.setWorkPlanStatusCountModel(workPlanStatusCountModel);

        PlanAnalysisTool planAnalysisTool = new PlanAnalysisTool();
//        PlanAnalysisTool.EveryDayCountTool everyDayCountTool = new PlanAnalysisTool.EveryDayCountTool();

        // 重要厂站列表
        List<TransformerSubstation> substations = transformerSubstationMapper.selectList(Wrappers.<TransformerSubstation>lambdaQuery().eq(TransformerSubstation::getIsThirteenAddSixteen, true));
        PlanAnalysisTool.SubstationCountTool substationCountTool = new PlanAnalysisTool.SubstationCountTool(substations);
        for (WorkPlanDataAutoModel workPlan : workPlans) {

//            // 每日计划数量统计
//            if (!everyDayCountTool.count(workPlan)) {
//                log.warn("{},作业计划时间待补充", workPlan.getWorkPlanCode());
//            }

            // 风险等级分类统计
            PlanAnalysisTool.countByRiskLevel(workPlan, PlanAnalysisTool.getRiskLevel(workPlan), m -> riskLevelCountMap);

            // 作业类型分类统计
            if (!planAnalysisTool.workPlanTypeCount(workPlan)) {
                log.warn("{},作业计划类型待补充", workPlan.getWorkPlanCode());
            }

            // 重要厂站分类统计
            if (!substationCountTool.count(workPlan)) {
                log.warn("{},作业计划工作地点待补充", workPlan.getWorkLocation());
            }
        }

//        reModel.setWorkPlanNumModel(everyDayCountTool.getCountResult());
        reModel.setWorkPlanRiskLevelCountModel(new ArrayList<>(riskLevelCountMap.values()));
        reModel.setWorkPlanTypeCountModel(planAnalysisTool.getWorkPlanTypeCountFlatten());
        reModel.setWorkPlanStationRiskLevelCountModel(substationCountTool.getCountResult());
        reModel.setWorkPlanSubstationData(substationCountTool.getPlanCount());

        WorkPlanRiskLevelCountStatistics workPlanRiskLevelCountStatistics = new WorkPlanRiskLevelCountStatistics();
        workPlanRiskLevelCountStatistics.setRiskList(reModel.getWorkPlanRiskLevelCountModel());
        workPlanRiskLevelCountStatistics.setTypeList(reModel.getWorkPlanTypeCountModel());
        reModel.setWorkPlanRiskLevelCountStatistics(workPlanRiskLevelCountStatistics);

        WorkPlanStationRiskLevelStatistics workPlanStationRiskLevelStatistics = new WorkPlanStationRiskLevelStatistics();
        workPlanStationRiskLevelStatistics.setStationList(reModel.getWorkPlanStationRiskLevelCountModel());
        workPlanStationRiskLevelStatistics.setImportantStationList(substations);
        reModel.setWorkPlanStationRiskLevelStatistics(workPlanStationRiskLevelStatistics);

        return reModel;
    }

    /**
     * [{@code beginTime}, {@code endTime})内每日作业计划数量统计
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @return 每日作业计划数量统计
     */
    public WorkPlanNumModel workPlanNumAnalysis(LocalDateTime beginTime, LocalDateTime endTime) {
        if (!ObjectUtils.allNotNull(beginTime, endTime)) {
            return null;
        }

        WorkPlanNumModel workPlanNumModel = new WorkPlanNumModel();
        workPlanNumModel.setBeginDate(beginTime.toLocalDate());
        workPlanNumModel.setEndDate(endTime.toLocalDate());
        workPlanNumModel.setEverydayCount(new ArrayList<>());
        workPlanNumModel.setEverydayCountMap(new HashMap<>());

        WorkPlanDataAutoModel query = new WorkPlanDataAutoModel();
        query.setPlanStartTime(beginTime);
        query.setPlanEndTime(endTime);
        List<WorkPlanDataAutoModel> workPlans = searchWorkPlansByDb(query);
        if (CollectionUtils.isEmpty(workPlans)) {
            return workPlanNumModel;
        }

        PlanAnalysisTool.EveryDayCountTool everyDayCountTool = new PlanAnalysisTool.EveryDayCountTool();
        for (WorkPlanDataAutoModel workPlan : workPlans) {
            if (!everyDayCountTool.countWithModel(workPlan, workPlanNumModel)) {
                log.warn("{},作业计划时间待补充", workPlan.getWorkPlanCode());
            }
        }

        return everyDayCountTool.getCountResult();
    }

    /**
     * 通过历史记录{@link SecondPlanRecord}，统计[{@code beginTime}, {@code endTime})内每日作业计划数量
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @return 每日作业计划数量统计
     */
    public WorkPlanNumModel workPlanNumWithRecord(LocalDateTime beginTime, LocalDateTime endTime) {
        if (!ObjectUtils.allNotNull(beginTime, endTime)) {
            return null;
        }

        List<SecondPlanRecord> secondPlanRecords = secondPlanRecordMapper.selectList(Wrappers.<SecondPlanRecord>lambdaQuery()
                .ge(SecondPlanRecord::getTime, beginTime.minusDays(30))
                .lt(SecondPlanRecord::getTime, endTime)
        );

        WorkPlanNumModel workPlanNumModel = new WorkPlanNumModel();
        workPlanNumModel.setBeginDate(beginTime.toLocalDate());
        workPlanNumModel.setEndDate(endTime.toLocalDate());

        if (secondPlanRecords.isEmpty()) {
            workPlanNumModel.setEverydayCount(new ArrayList<>());
            return workPlanNumModel;
        }

        List<WorkPlanEverydayCountModel> reList = secondPlanRecords.stream().filter(e -> !e.getTime().isBefore(beginTime)).map(e -> {
            WorkPlanEverydayCountModel everydayCountModel = new WorkPlanEverydayCountModel();
            everydayCountModel.setDate(e.getTime().toLocalDate());
            everydayCountModel.setTotal(e.getNum() == null ? 0 : (int) e.getNum());
            workPlanNumModel.setTotal(Optional.ofNullable(workPlanNumModel.getTotal()).orElseGet(() -> 0) + everydayCountModel.getTotal());
            return everydayCountModel;
        }).collect(Collectors.toList());

        // 计算选择时间范围内的滑动平均值
        Map<LocalDate, WorkPlanEverydayCountModel> previousThreeMonthAverageMap = new HashedMap<>();
        for (WorkPlanEverydayCountModel countModel : reList) {
            LocalDate date = countModel.getDate();
            if (date == null) {
                continue;
            }
            Integer average = calcAverage(date, secondPlanRecords, 30);
            countModel.setAverage(average);
            countModel.setPreviousWeekAverage(calcAverage(date, secondPlanRecords, 7));
        }

        workPlanNumModel.setEverydayCount(reList.stream().sorted(Comparator.comparing(WorkPlanEverydayCountModel::getDate)).collect(Collectors.toList()));
        return workPlanNumModel;
    }

    /**
     * 计算平均值
     * @param date
     * @param records
     */
    public Integer calcAverage(LocalDate date, List<SecondPlanRecord> records, long dayNum) {
        // 前 dayNum 天工作日的计划作业数量记录
        OptionalDouble average = records.stream().filter(e -> {
            LocalDateTime time = e.getTime();
            if (time == null) {
                return false;
            }

            LocalDate recordDate = time.toLocalDate();
            return !recordDate.isAfter(date)
                    && !recordDate.isBefore(date.minusDays(dayNum))
                    && recordDate.getDayOfWeek().getValue() < 6;
        }).mapToLong(SecondPlanRecord::getNum).average();

        if (average.isPresent()) {
            return (int) average.getAsDouble();
        }

        return null;
    }

    /**
     * 获取中高风险作业计划数据
     *
     * @param date 日期
     * @return 中高风险作业计划数据
     */
    public List<WorkPlanDataAutoModel> getRiskBetweenMiddleAndHighWorkPlansByDate(LocalDate date) {
        date = date == null ? LocalDate.now() : date;
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1L);

        List<StatusEnum> statuses = Arrays.asList(StatusEnum.NOT_START, StatusEnum.IN_PROGRESS, StatusEnum.INTERRUPTED, StatusEnum.RESCHEDULED);
        // 调度 中高风险
        List<WorkPlanDataAutoModel> search = search(startTime, endTime,
                Collections.singletonList(MajorWideEnum.du),
                Arrays.asList(RiskLevelEnum.MEDIUM, RiskLevelEnum.HIGH),
                Collections.emptyList(),
                statuses
        );
        ArrayList<WorkPlanDataAutoModel> workPlansWhenDate = new ArrayList<>(search);
        // 基建 自动化、继电保护、通信工程
        List<WorkPlanDataAutoModel> search2 = search(startTime, endTime,
                Arrays.asList(MajorWideEnum.jj, MajorWideEnum.yx, MajorWideEnum.yx),
                Arrays.asList(RiskLevelEnum.MEDIUM, RiskLevelEnum.HIGH),
                Arrays.asList(TypeEnum.AUTOMATION, TypeEnum.RELAY_PROTECTION, TypeEnum.COMMUNICATION),
                statuses
        );
        workPlansWhenDate.addAll(search2);

        if (CollectionUtils.isEmpty(workPlansWhenDate)) {
            return Collections.emptyList();
        }

        // 筛选出中高风险作业计划
        List<WorkPlanDataAutoModel> workPlans = workPlansWhenDate.stream().filter(workPlan -> {
            RiskLevelEnum riskLevel = PlanAnalysisTool.getRiskLevel(workPlan);
            if (riskLevel == null) {
                return false;
            }
            return riskLevel.getCode() <= RiskLevelEnum.MEDIUM.getCode();
        }).collect(Collectors.toList());

        if (workPlans.isEmpty()) {
            return Collections.emptyList();
        }

        Map<WorkPlanDataModel, WorkPlanService2.Message> workPlanMessageMap = Collections.emptyMap();
        try {
            workPlanMessageMap = workPlanService2.analysisWorkPlan2(date);
        } catch (RuntimeException e) {
            log.warn("", e);
        }

        Map<String, WorkPlanService2.Message> messageMap = new HashMap<>();
        for (Map.Entry<WorkPlanDataModel, WorkPlanService2.Message> entry : workPlanMessageMap.entrySet()) {
            messageMap.put(entry.getKey().getWorkPlanCode(), entry.getValue());
        }

        List<WorkPlanDataAutoModel> reList = new ArrayList<>();
        for (WorkPlanDataAutoModel workPlan : workPlans) {
            reList.add(workPlan);
            WorkPlanService2.Message message = messageMap.get(workPlan.getWorkPlanCode());
            if (message == null) {
                continue;
            }

            workPlan.setArrangement(message.getFocus());
            workPlan.setControlMeasure(message.getPlan());
        }

        return parseWorkPlanData(reList);

    }

    /**
     * 获取作业计划数据
     * @param startTime 计划开始时间
     * @param endTime 计划结束时间
     * @param majorWides 专业大类
     * @param riskLevels 风险等级
     * @param workTypes 作业类型
     * @return 作业计划
     */
    public List<WorkPlanDataAutoModel> search(LocalDateTime startTime, LocalDateTime endTime,
                                              List<MajorWideEnum> majorWides,
                                              List<RiskLevelEnum> riskLevels,
                                              List<TypeEnum> workTypes,
                                              List<StatusEnum> statuses
    ) {

        WorkPlanDataAutoModel query = new WorkPlanDataAutoModel();
        query.setPlanStartTime(startTime);
        query.setPlanEndTime(endTime);
        if (CollectionUtils.isNotEmpty(majorWides)) {
            query.setWorkProfessions(majorWides.stream().mapToInt(MajorWideEnum::getCode).toArray());
        }
        if (CollectionUtils.isNotEmpty(riskLevels)) {
            query.setRiskLevels(riskLevels.stream().mapToInt(RiskLevelEnum::getCode).toArray());
        }
        if (CollectionUtils.isNotEmpty(workTypes)) {
            query.setWorkTypes(workTypes.stream().mapToInt(TypeEnum::getCode).toArray());
        }
        if (CollectionUtils.isNotEmpty(statuses)) {
            query.setWorkStatuses(statuses.stream().mapToInt(StatusEnum::getCode).toArray());
        }
        return new ArrayList<>(searchWorkPlansBatch(query, 300));
    }

    /**
     * 统计班组饱和度
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 班组饱和度
     */
    public List<WorkPlanCrewSaturationCountModel> crewSaturationAnalysis(LocalDate startDate, LocalDate endDate) {
        if (!ObjectUtils.allNotNull(startDate, endDate)) {
            return Collections.emptyList();
        }

        List<CrewWorkLoadRateModel> crewWorkLoadRateDayRecords = crewWorkLoadRateDayRecordMapper.selectByDateRange(startDate, endDate);
        if (crewWorkLoadRateDayRecords.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Map<LocalDate, CrewRiskLevelNumCountDayRecord>> crewIdRiskCountMap = crewRiskLevelNumCountDayRecordMapper.selectList(Wrappers.<CrewRiskLevelNumCountDayRecord>lambdaQuery()
                .ge(CrewRiskLevelNumCountDayRecord::getDate, startDate)
                .lt(CrewRiskLevelNumCountDayRecord::getDate, endDate)
        ).stream().collect(Collectors.groupingBy(CrewRiskLevelNumCountDayRecord::getCrewId, Collectors.toMap(CrewRiskLevelNumCountDayRecord::getDate, e -> e, (e1, e2) -> e2)));

        Map<Long, Map<LocalDate, List<CrewWorkLoadRateModel>>> crewDaySaturationMap = crewWorkLoadRateDayRecords.stream().collect(
                Collectors.groupingBy(CrewWorkLoadRateModel::getCrewId,
                        Collectors.groupingBy(e -> e.getDate() == null ? endDate : e.getDate())
                )
        );

        List<WorkPlanCrewSaturationCountModel> reList = new ArrayList<>();
        for (Map.Entry<Long, Map<LocalDate, List<CrewWorkLoadRateModel>>> entry : crewDaySaturationMap.entrySet()) {
            Long crewId = entry.getKey();

            Map<LocalDate, List<CrewWorkLoadRateModel>> saturationMap = entry.getValue();
            if (MapUtils.isEmpty(saturationMap)) {
                continue;
            }

            Map<LocalDate, CrewRiskLevelNumCountDayRecord> riskMap = crewIdRiskCountMap.get(crewId);

            for (Map.Entry<LocalDate, List<CrewWorkLoadRateModel>> sa : saturationMap.entrySet()) {
                List<CrewWorkLoadRateModel> saturations = sa.getValue();
                if (CollectionUtils.isEmpty(saturations)) {
                    continue;
                }

                BigDecimal responsiblePersonSaturation = BigDecimal.ZERO, memberSaturation = BigDecimal.ZERO;
                for (CrewWorkLoadRateModel saturation : saturations) {
                    // 工作负责人
                    if (CrewWorkLoadRateCalcService.MANAGER_GROUP_NAME.equals(saturation.getMemberName())) {
                        responsiblePersonSaturation = Optional.ofNullable(saturation.getRate()).orElseGet(() -> BigDecimal.ZERO)
                                .multiply(BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP);
                    }
                    // 作业人员
                    if (CrewWorkLoadRateCalcService.WORKER_GROUP_NAME.equals(saturation.getMemberName())) {
                        memberSaturation = Optional.ofNullable(saturation.getRate()).orElseGet(() -> BigDecimal.ZERO)
                                .multiply(BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP);
                    }
                }
                WorkPlanCrewSaturationCountModel record = new WorkPlanCrewSaturationCountModel();
                record.setCrewName(saturations.get(0).getUnitCrewShortName());
                record.setDate(sa.getKey());
                record.setResponsiblePersonSaturation(responsiblePersonSaturation);
                record.setMemberSaturation(memberSaturation);

                if (riskMap != null) {
                    CrewRiskLevelNumCountDayRecord riskCount = riskMap.get(sa.getKey());
                    if (riskCount != null) {
                        record.setVeryHighCount(Optional.ofNullable(riskCount.getVeryHighCount()).orElse(0));
                        record.setHighCount(Optional.ofNullable(riskCount.getHighCount()).orElse(0));
                        record.setMediumCount(Optional.ofNullable(riskCount.getMediumCount()).orElse(0));
                        record.setLowCount(Optional.ofNullable(riskCount.getLowCount()).orElse(0));
                        record.setAcceptableCount(Optional.ofNullable(riskCount.getAcceptableCount()).orElse(0));
                        record.setTotalCount(record.getVeryHighCount() + record.getHighCount() + record.getMediumCount() + record.getLowCount() + record.getAcceptableCount());
                    }
                }

                reList.add(record);
            }
        }

        // 按日期排序
        return reList.stream().sorted(Comparator.comparing(WorkPlanCrewSaturationCountModel::getDate)).collect(Collectors.toList());
    }

    public WorkPlanCrewSaturationCountStatistics crewSaturationAnalysisStatistics(LocalDate startDate, LocalDate endDate) {
        List<WorkPlanCrewSaturationCountModel> models = crewSaturationAnalysis(startDate, endDate);
        WorkPlanCrewSaturationCountStatistics statistics = new WorkPlanCrewSaturationCountStatistics();
        statistics.setLoadRateModels(models);
        return statistics;
    }

    /**
     * 获取{@code startDate}-{@code endDate}对应的作业计划数据，发送都对应的elink
     * @param startDate 开始日期
     * @param endDate 结束日期 包含
     */
    public void grabDataWhenAndSaveAsFile(LocalDate startDate, LocalDate endDate, String elinkUserIds) {
        List<WorkPlanDataModel> workPlansByDate = getWorkPlansByDate(startDate, endDate);
        String fileName = startDate.format(DateTimeFormatter.ISO_DATE) + "-" + endDate.format(DateTimeFormatter.ISO_DATE) + "作业计划导出结果.xlsx";
        // 写入到指定目录
        // elink文件夹
        File elinkDir = Paths.get(sendFileByElinkSaveFilePath).toFile();
        if (!elinkDir.exists()) {
            log.error("elink文件夹: {}不存在", sendFileByElinkSaveFilePath);
            return;
        }
        File temporaryFile = Paths.get(sendFileByElinkSaveFilePath, fileName).toFile();

        // 写入模板
        ClassPathResource templateFile = new ClassPathResource("workplan/auto_check_template.xlsx");
        try {
            if (temporaryFile.exists() && !temporaryFile.delete()) {
                log.warn("临时文件删除失败: {}", temporaryFile.getAbsolutePath());
                return;
            }
            if (!temporaryFile.createNewFile()) {
                log.error("临时文件创建失败：{}", temporaryFile.getAbsolutePath());
                return;
            }
            EasyExcel.write(temporaryFile)
                    .withTemplate(templateFile.getInputStream())
                    .head(WorkPlanDataModel.class)
                    .needHead(false)
                    .sheet(0)
                    .doFill(workPlansByDate);
        } catch (RuntimeException e) {
            log.warn("临时文件创建失败，文件名：{}", fileName);
            return;
        } catch (IOException e) {
            log.warn("自动获取日作业计划保存模板文件获取失败");
            return;
        }

        if (StringUtils.isBlank(elinkUserIds)) {
            CommonSettings commonSettings = commonSettingsMapper.selectOne(Wrappers.<CommonSettings>lambdaQuery().eq(CommonSettings::getName, "WorkPlanAutoDataService.ELINK_USER_IDS"));
            elinkUserIds = commonSettings.getValue();
        }
        KafkaClient kafkaClient = new KafkaClient(appNameWithinElink);
        // 发送
        kafkaClient.sendElinkFile(fileName, elinkUserIds, null);
    }

}