package com.zhuofan.oms.service.workplan2;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhuofan.oms.config.mybatis.DbTransactionManager;
import com.zhuofan.oms.entity.workplan.CrewRiskLevelNumCountDayRecord;
import com.zhuofan.oms.entity.workplan.CrewWorkLoadRateDayRecord;
import com.zhuofan.oms.enums.workplan.RiskLevelEnum;
import com.zhuofan.oms.mapper.workplan.CrewRiskLevelNumCountDayRecordMapper;
import com.zhuofan.oms.orm.model.WorkPlanLoadRatio;
import com.zhuofan.oms.mapper.CrewMapper;
import com.zhuofan.oms.mapper.CrewMemberMapper;
import com.zhuofan.oms.mapper.workplan.CrewWorkLoadRateDayRecordMapper;
import com.zhuofan.oms.mapper.workplan.WorkPlanLoadRatioMapper;
import com.zhuofan.oms.model.workplan.CrewWorkLoadRateModel;
import com.zhuofan.oms.orm.model.Crew;
import com.zhuofan.oms.orm.model.CrewMember;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <h1>作业计划班组饱和度计算服务</h1>
 *
 * <p>
 * 计划作业属性：<br/>
 *  {@code crew}=工作班组<br/>
 *  {@code beginTime}=计划开始时间<br/>
 *  {@code endTime}=计划结束时间<br/>
 *  {@code workType}=工作类别<br/>
 * </p>
 *
 * <p>根据每项作业计划的“工作类别”列获取该项作业的饱和度系数a：<br/>
 *  {@code a}（饱和度系数-工作负责人）<br/>
 *  {@code b}（饱和度系数-作业人员）=a*作业人数<br/>
 * </p>
 * <p>
 *  当日{@code date}班组{@code c}的饱和度：<br/>
 *      {@code workplan}({@code c}班组当日作业计划): {@code beginTime} <= {@code date} <= {@code endTime}<br/>
 *      饱和度（工作负责人）= {@code sum(workplan的a)/c班组工作负责人数}<br/>
 *      饱和度（作业人员）= {@code sum(workplan的b)/c班组作业人员人数}
 * </p>
 *
 * @date 2026-06-30
 * @author lz
 */
@Slf4j
@Service
public class CrewWorkLoadRateCalcService {

    @Resource
    private CrewMapper crewMapper;

    @Resource
    private CrewMemberMapper crewMemberMapper;

    @Resource
    private WorkPlanLoadRatioMapper workPlanLoadRatioMapper;

    @Resource
    private CrewWorkLoadRateDayRecordMapper crewWorkLoadRateDayRecordMapper;

    @Resource
    private CrewRiskLevelNumCountDayRecordMapper crewRiskLevelNumCountDayRecordMapper;

    @Resource
    private DbTransactionManager dbTransactionManager;

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final static DateTimeFormatter DATE_TIME_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public final static String MANAGER_GROUP_NAME = "工作负责人";

    public final static String WORKER_GROUP_NAME = "作业人员";

    /**
     * 计算{@code date}当日的班组饱和度
     *
     * @param date 日期
     * @param workPlanDataModels {@code date} 当日作业计划数据
     * @param <T> 填充饱和度后的作业计划数据
     */
    public <T extends WorkPlanDataModel> Map<Crew, List<CrewWorkLoadRateModel>> calc(LocalDate date, List<T> workPlanDataModels) {
        Map<Crew, List<CrewWorkLoadRateModel>> crewRateMap = calc(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), workPlanDataModels, crewWorkLoadRateModels -> {
            crewWorkLoadRateModels.forEach(rate -> rate.setDate(date));
        }, BigDecimal.ONE);

        if (MapUtils.isEmpty(crewRateMap)) {
            return crewRateMap;
        }

        List<CrewWorkLoadRateDayRecord> rateData = crewRateMap.values().stream()
                .flatMap(e -> e.stream().map(e1 -> (CrewWorkLoadRateDayRecord) e1))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rateData)) {
            return crewRateMap;
        }

        // 保存
        dbTransactionManager.transaction(() -> {
            crewWorkLoadRateDayRecordMapper.delete(Wrappers.<CrewWorkLoadRateDayRecord>lambdaQuery().eq(CrewWorkLoadRateDayRecord::getDate, date));
            crewWorkLoadRateDayRecordMapper.batchInsert(rateData);
            return null;
        });

        return crewRateMap;
    }

    /**
     * 计算{@code beginTime}至{@code endTime}的班组饱和度
     *
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param workPlanDataModels 涉及到开始时间哥结束时间的作业计划数据
     * @param defaultRatio 默认饱和度系数 1
     * @param <T> 填充饱和度后的作业计划数据
     * @return    填充饱和度后的作业计划数据列表
     */
    public <T extends WorkPlanDataModel> Map<Crew, List<CrewWorkLoadRateModel>> calc(LocalDateTime beginTime, LocalDateTime endTime,
                                                                                     List<T> workPlanDataModels,
                                                                                     Consumer<List<CrewWorkLoadRateModel>> crewWorkLoadRateConsumer,
                                                                                     BigDecimal defaultRatio
    ) {
        List<Crew> crews = crewMapper.selectList(Wrappers.emptyWrapper());
        if (CollectionUtils.isEmpty(crews)) {
            return Collections.emptyMap();
        }
        // crewId-crewMembers map
        Map<Long, List<CrewMember>> crewMemberMap = crewMemberMapper.selectList(Wrappers.emptyWrapper()).stream().collect(Collectors.groupingBy(CrewMember::getCrewId));

        Map<Crew, List<CrewWorkLoadRateModel>> reCrewWorkPlanRateDataMap = new HashMap<>();
        for (Crew crew : crews) {
            Long crewId = crew.getId();
            List<CrewMember> crewMembers = crewMemberMap.getOrDefault(crewId, Collections.emptyList());

            List<CrewWorkLoadRateModel> rateList = new ArrayList<>();
            // 工作负责人数量
            long managerCount = crewMembers.stream().filter(e -> BooleanUtils.isTrue(e.getIsManager())).count();
            CrewWorkLoadRateModel managerRateModel = CrewWorkLoadRateModel.build(crew, null, MANAGER_GROUP_NAME, (int) managerCount);
            rateList.add(managerRateModel);
            // 作业人员饱和率
            CrewWorkLoadRateModel workerRateModel = CrewWorkLoadRateModel.build(crew, null, WORKER_GROUP_NAME, crewMembers.size());
            rateList.add(workerRateModel);
            // 班组人员饱和率
            for (CrewMember crewMember : crewMembers) {
                rateList.add(CrewWorkLoadRateModel.build(crew, null, crewMember.getName(), 1));
            }
            reCrewWorkPlanRateDataMap.put(crew, rateList);
        }

        List<WorkPlanDataModel> workPlans = workPlanDataModels.stream().filter(e -> {
            String planStartTimeStr = e.getPlanStartTime();
            String planEndTimeStr = e.getPlanEndTime();

            if (StringUtils.isAnyBlank(planStartTimeStr, planEndTimeStr)) {
                return false;
            }

            LocalDateTime planBeginTime = null, planEndTime = null;
            try {
                planBeginTime = LocalDate.parse(planStartTimeStr, DATE_TIME_FORMATTER).atStartOfDay();
                planEndTime = LocalDate.parse(planEndTimeStr, DATE_TIME_FORMATTER).atStartOfDay();
            } catch (DateTimeParseException ex) {
                planBeginTime = LocalDate.parse(planStartTimeStr, DATE_TIME_FORMATTER1).atStartOfDay();
                planEndTime = LocalDate.parse(planEndTimeStr, DATE_TIME_FORMATTER1).atStartOfDay();
            }

            return !(beginTime.isAfter(planEndTime) || endTime.isBefore(planBeginTime));
        }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(workPlans)) {
//            // 计算运行班组
//            workPlanService2.analysisDepartmentAndRunningCrew(workPlans);
            List<WorkPlanLoadRatio> workLoadRatios = workPlanLoadRatioMapper.selectList(Wrappers.emptyWrapper());
            Map<Long, List<WorkPlanDataModel>> crewWorkPlanMap = workPlans.stream().filter(e -> e.getCalcRunningCrewId() != null).collect(Collectors.groupingBy(WorkPlanDataModel::getCalcRunningCrewId));
            crews.forEach(crew -> {
                List<WorkPlanDataModel> crewWorkPlans = crewWorkPlanMap.get(crew.getId());
                if (CollectionUtils.isEmpty(crewWorkPlans)) {
                    log.info("{}~{} 班组{}-{}，没有作业计划", beginTime, endTime, crew.getName(), crew.getType());
                    return;
                }
                List<CrewWorkLoadRateModel> crewWorkLoadRateDataModels = reCrewWorkPlanRateDataMap.get(crew);
                calcAndFillRate(crewWorkPlans, workLoadRatios, crewWorkLoadRateDataModels, defaultRatio == null ? BigDecimal.ONE : defaultRatio);
            });
        }

        reCrewWorkPlanRateDataMap.forEach((key, value) -> {
            if (crewWorkLoadRateConsumer == null) {
                return;
            }
            crewWorkLoadRateConsumer.accept(value);
        });

        return reCrewWorkPlanRateDataMap;
    }

    /**
     * 计算{@code crew}班组的饱和度
     *
     * @param workPlanDataModels 作业计划
     * @param workLoadRates 饱和度系数列表
     * @param crewWorkLoadRateModels 待填充饱和度的作业计划数据
     * @param defaultRatio 默认饱和度系数 1
     * @param <T> 填充饱和度后的作业计划
     */
    public <T extends WorkPlanDataModel> void calcAndFillRate(List<T> workPlanDataModels,
                                                              List<WorkPlanLoadRatio> workLoadRates,
                                                              List<CrewWorkLoadRateModel> crewWorkLoadRateModels,
                                                              BigDecimal defaultRatio
    ) {
        if (CollectionUtils.isEmpty(workPlanDataModels) || CollectionUtils.isEmpty(workLoadRates) || CollectionUtils.isEmpty(crewWorkLoadRateModels)) {
            return;
        }

        Map<String, WorkPlanLoadRatio> workTypeRateMap = workLoadRates.stream().collect(Collectors.toMap(WorkPlanLoadRatio::getWorkType, e -> e, (e1, e2) -> e2));
        Map<String, CrewWorkLoadRateModel> rateDataMap = crewWorkLoadRateModels.stream().collect(Collectors.toMap(CrewWorkLoadRateDayRecord::getMemberName, e -> e, (e1, e2) -> e2));

        int veryHighRiskCount = 0, highRiskCount = 0, mediumRiskCount = 0, lowRiskCount = 0, acceptableRiskCount = 0;
        for (T t : workPlanDataModels) {
            if ("外单位".equals(t.getWorkMajor())) {
                continue;
            }

            RiskLevelEnum riskLevel = RiskLevelEnum.getRiskLevel(t.getRiskLevel());
            switch (riskLevel){
                case VERY_HIGH:
                    veryHighRiskCount++;
                    break;
                case HIGH:
                    highRiskCount++;
                    break;
                case MEDIUM:
                    mediumRiskCount++;
                    break;
                case LOW:
                    lowRiskCount++;
                    break;
                case ACCEPTABLE:
                    acceptableRiskCount++;
                    break;
                default:
                    break;
            }

            String workType = t.getWorkType();
            String workMembers = t.getWorkMembers();
            List<String> workMemberNameList = StringUtils.isBlank(workMembers) ? Collections.emptyList() : Arrays.asList(workMembers.split("[；;,，]"));
            WorkPlanLoadRatio workPlanLoadRatio = workTypeRateMap.get(workType);
            BigDecimal ratio = workPlanLoadRatio == null ? defaultRatio : workPlanLoadRatio.getRatio();

            BigDecimal managerRatio = ratio;
            BigDecimal workerRatio = ratio.multiply(BigDecimal.valueOf(workMemberNameList.size()));

            CrewWorkLoadRateModel managerRate = rateDataMap.get(MANAGER_GROUP_NAME);
            if (managerRate != null) {
                managerRate.setSumRatio(Optional.ofNullable(managerRate.getSumRatio()).orElseGet(() -> BigDecimal.ZERO).add(managerRatio));
            }

            CrewWorkLoadRateModel workerRate = rateDataMap.get(WORKER_GROUP_NAME);
            if (workerRate != null) {
                workerRate.setSumRatio(Optional.ofNullable(workerRate.getSumRatio()).orElseGet(() -> BigDecimal.ZERO).add(workerRatio));
            }

            CrewWorkLoadRateModel managerCrewWorkLoadRateModel = rateDataMap.get(t.getWorkResponsiblePerson());
            if (managerCrewWorkLoadRateModel != null) {
                managerCrewWorkLoadRateModel.setSumRatio(Optional.ofNullable(managerCrewWorkLoadRateModel.getSumRatio()).orElseGet(() -> BigDecimal.ZERO).add(managerRatio));
            }

            for (String workMemberName : workMemberNameList) {
                CrewWorkLoadRateModel crewWorkLoadRateModel = rateDataMap.get(workMemberName);
                if (crewWorkLoadRateModel == null) {
                    continue;
                }
                crewWorkLoadRateModel.setSumRatio(Optional.ofNullable(crewWorkLoadRateModel.getSumRatio()).orElseGet(() -> BigDecimal.ZERO).add(ratio));
            }
        }
        // 保存班组各风险作业数
    }

    /**
     * 班组每日作业数按 风险等级分组统计
     * @param date 日期
     * @param workPlanDataModels 计划作业
     * @return 统计结果：班组-风险等级作业数
     */
    public Map<Crew, CrewRiskLevelNumCountDayRecord> calcRiskLevelNumCount(LocalDate date, List<WorkPlanDataModel> workPlanDataModels) {
        List<Crew> crews = this.crewMapper.selectList(Wrappers.emptyWrapper());
        if (crews.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Crew> crewIdMap = crews.stream().collect(Collectors.toMap(Crew::getId, e -> e));
        Map<Crew, CrewRiskLevelNumCountDayRecord> reCrewRisLevelCountMap = crews.stream().collect(Collectors.toMap(e -> e, e -> {
            CrewRiskLevelNumCountDayRecord record = new CrewRiskLevelNumCountDayRecord();
            record.setDate(date);
            record.setCrewId(e.getId());
            record.setVeryHighCount(0);
            record.setHighCount(0);
            record.setMediumCount(0);
            record.setLowCount(0);
            record.setAcceptableCount(0);
            record.setTotalCount(0);
            return record;
        }));

        if (CollectionUtils.isEmpty(workPlanDataModels)) {
            return reCrewRisLevelCountMap;
        }

        Map<Long, Map<RiskLevelEnum, Long>> planCrewRiskCountMap = workPlanDataModels.stream().filter(e -> e.getCalcRunningCrewId() != null).collect(Collectors.groupingBy(WorkPlanDataModel::getCalcRunningCrewId, Collectors.groupingBy(e -> RiskLevelEnum.getRiskLevel(e.getRiskLevel()), Collectors.counting())));

        for (Map.Entry<Long, Map<RiskLevelEnum, Long>> entry : planCrewRiskCountMap.entrySet()) {
            Long crewId = entry.getKey();
            Crew crew = crewIdMap.get(crewId);
            if (crew == null) {
                continue;
            }
            CrewRiskLevelNumCountDayRecord record = reCrewRisLevelCountMap.computeIfAbsent(crew, k -> {
                CrewRiskLevelNumCountDayRecord newRecord = new CrewRiskLevelNumCountDayRecord();
                newRecord.setDate(date);
                newRecord.setCrewId(k.getId());
                newRecord.setVeryHighCount(0);
                newRecord.setHighCount(0);
                newRecord.setMediumCount(0);
                newRecord.setLowCount(0);
                newRecord.setAcceptableCount(0);
                return newRecord;
            });

            Map<RiskLevelEnum, Long> riskCountMap = entry.getValue();
            if (MapUtils.isEmpty(riskCountMap)) {
                continue;
            }

            int veryHighCount = Optional.ofNullable(riskCountMap.get(RiskLevelEnum.VERY_HIGH)).orElseGet(() -> 0L).intValue();
            record.setVeryHighCount(veryHighCount);
            int highCount = Optional.ofNullable(riskCountMap.get(RiskLevelEnum.HIGH)).orElseGet(() -> 0L).intValue();
            record.setHighCount(highCount);
            int mediumCount = Optional.ofNullable(riskCountMap.get(RiskLevelEnum.MEDIUM)).orElseGet(() -> 0L).intValue();
            record.setMediumCount(mediumCount);
            int lowCount = Optional.ofNullable(riskCountMap.get(RiskLevelEnum.LOW)).orElseGet(() -> 0L).intValue();
            record.setLowCount(lowCount);
            int acceptableCount = Optional.ofNullable(riskCountMap.get(RiskLevelEnum.ACCEPTABLE)).orElseGet(() -> 0L).intValue();
            record.setAcceptableCount(acceptableCount);

            record.setTotalCount(highCount + veryHighCount + mediumCount + lowCount + acceptableCount);
        }

        // 保存到数据库
        dbTransactionManager.transaction(() -> {
            crewRiskLevelNumCountDayRecordMapper.delete(Wrappers.<CrewRiskLevelNumCountDayRecord>lambdaQuery()
                    .eq(CrewRiskLevelNumCountDayRecord::getDate, date)
            );
            crewRiskLevelNumCountDayRecordMapper.insertBatchSomeColumn(new ArrayList<>(reCrewRisLevelCountMap.values()));
            return null;
        });

        return reCrewRisLevelCountMap;
    }

}
