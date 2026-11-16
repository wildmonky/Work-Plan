package com.zhuofan.oms.model.workplan;

import com.zhuofan.oms.enums.workplan.RiskLevelEnum;
import com.zhuofan.oms.enums.workplan.TypeEnum;
import com.zhuofan.oms.orm.model.TransformerSubstation;
import com.zhuofan.oms.service.workplan2.WorkPlanAutoDataService;
import com.zhuofan.oms.service.workplan2.WorkPlanDataAutoModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class PlanAnalysisTool {


    /**
     * 每日计划数量统计工具
     */
    public static class EveryDayCountTool {

        private WorkPlanNumModel countModel = new WorkPlanNumModel();

        public EveryDayCountTool() {}

        /**
         * 统计每日作业计划数量
         * @param model 计划作业
         * @return true-成功，false-失败
         */
        public boolean count(WorkPlanDataAutoModel model) {
            return countWithModel(model, countModel);
        }

//        public boolean countWithMap(WorkPlanDataAutoModel model, Map<LocalDate, WorkPlanNumDateModel> planDateNumCountMap) {
//            LocalDateTime planStartTime = model.getPlanStartTime();
//            LocalDateTime planEndTime = model.getPlanEndTime();
//
//            if (!ObjectUtils.allNotNull(planStartTime, planEndTime)) {
//                return false;
//            }
//
//            LocalDate beginDate = planStartTime.toLocalDate();
//            LocalDate endDate = planEndTime.toLocalDate().plusDays(1L);
//
//            for (LocalDate date = beginDate; !date.isAfter(endDate); date = date.plusDays(1L)) {
//                WorkPlanNumDateModel workPlanNumDateModel = planDateNumCountMap.get(date);
//                if (workPlanNumDateModel == null) {
//                    WorkPlanNumDateModel newModel = new WorkPlanNumDateModel();
//                    newModel.setPlanNum(1L);
//                    newModel.setDate(date);
//                    planDateNumCountMap.put(date, newModel);
//                    continue;
//                }
//                workPlanNumDateModel.setPlanNum(workPlanNumDateModel.getPlanNum() + 1L);
//                planDateNumCountMap.put(date, workPlanNumDateModel);
//            }
//
//            return true;
//        }
        public boolean countWithModel(WorkPlanDataAutoModel model, WorkPlanNumModel countModel) {
            this.countModel = countModel;
            LocalDateTime planStartTime = model.getPlanStartTime();
            LocalDateTime planEndTime = model.getPlanEndTime();

            if (!ObjectUtils.allNotNull(planStartTime, planEndTime)) {
                return false;
            }

            LocalDate planStartDate = planStartTime.toLocalDate();
            LocalDate planEndDate = planEndTime.toLocalDate();

            if (planEndDate.isBefore(this.countModel.getBeginDate()) || planEndDate.isAfter(this.countModel.getEndDate())) {
                // 跳过，不在期望范围内
                return true;
            }

            Map<LocalDate, WorkPlanEverydayCountModel> everydayountMap = this.countModel.getEverydayCountMap();
            if (everydayountMap == null) {
                everydayountMap = new HashMap<>();
                this.countModel.setEverydayCountMap(everydayountMap);
            }


            for (LocalDate date = this.countModel.getBeginDate(); !date.isAfter(this.countModel.getEndDate()); date = date.plusDays(1L)) {
                if (date.isBefore(planStartDate) || date.isAfter(planEndDate)) {
                    continue;
                }

                WorkPlanEverydayCountModel everydayCountModel = everydayountMap.computeIfAbsent(date, k -> {
                    WorkPlanEverydayCountModel newModel = new WorkPlanEverydayCountModel();
                    newModel.setTotal(0);
                    newModel.setDate(k);
                    return newModel;
                });
                everydayCountModel.setTotal(everydayCountModel.getTotal() + 1);
            }

            return true;
        }

        /**
         * @return 每日作业计划数量列表
         */
        public WorkPlanNumModel getCountResult() {
            if (this.countModel != null && MapUtils.isNotEmpty(this.countModel.getEverydayCountMap())) {
                Collection<WorkPlanEverydayCountModel> values = this.countModel.getEverydayCountMap().values();
                long total = values.stream().mapToLong(WorkPlanEverydayCountModel::getTotal).sum();
                int dayNum = values.size();
                this.countModel.setEverydayCount(new ArrayList<>(this.countModel.getEverydayCountMap().values()));
                this.countModel.setTotal((int) total);
                this.countModel.setAverage((int) total / dayNum);
            }

            return this.countModel;
        }


    }


    private final Map<String, WorkPlanTypeCountModel> workPlanTypeCountMap = new HashMap<>();

    /**
     * 统计作业计划类型
     * @param model 作业计划
     * @return true-成功，false-失败
     */
    public boolean workPlanTypeCount(WorkPlanDataAutoModel model) {
        String workType = model.getWorkType();
        if (StringUtils.isBlank(workType)) {
            return false;
        }

        TypeEnum typeEnum = getWorkType(model);
        RiskLevelEnum riskLevelEnum = getRiskLevel(model);

        // 正常分类
        workPlanTypeCount(typeEnum.getCode(), typeEnum.getDesc(), riskLevelEnum, model);
        // 总计
        workPlanTypeCount(-1, "总计", riskLevelEnum, model);
        return true;
    }

    private void workPlanTypeCount(Integer workTypeCode, String workType, RiskLevelEnum riskLevelEnum, WorkPlanDataAutoModel model) {
        WorkPlanTypeCountModel workPlanTypeCountModel = workPlanTypeCountMap.computeIfAbsent(workType, k -> {
            WorkPlanTypeCountModel newTypeCountModel = new WorkPlanTypeCountModel();
            newTypeCountModel.setWorkType(workType);
            newTypeCountModel.setWorkTypeCode(workTypeCode);
            newTypeCountModel.setCount(0L);
            return newTypeCountModel;
        });
        workPlanTypeCountModel.setCount(workPlanTypeCountModel.getCount() + 1);

        List<WorkPlanRiskLevelCountModel> riskLevelCounts = Optional.ofNullable(workPlanTypeCountModel.getRiskLevelCount()).orElseGet(() -> {
            workPlanTypeCountModel.setRiskLevelCount(new ArrayList<>());
            return workPlanTypeCountModel.getRiskLevelCount();
        });

        WorkPlanRiskLevelCountModel riskLevelCount = riskLevelCounts.stream().filter(e -> Objects.equals(e.getRiskLevel(), riskLevelEnum.getCode())).findFirst().orElseGet(() -> {
            WorkPlanRiskLevelCountModel riskLevelCountModel = new WorkPlanRiskLevelCountModel();
            riskLevelCountModel.setCount(0);
            riskLevelCountModel.setRiskLevel(riskLevelEnum.getCode());
            riskLevelCountModel.setRisk(riskLevelEnum.getDesc());
            riskLevelCounts.add(riskLevelCountModel);
            return riskLevelCountModel;
        });

        riskLevelCount.setCount(riskLevelCount.getCount() + 1);
    }

    /**
     * @return 作业计划类型列表
     */
    public List<WorkPlanTypeCountModel> getWorkPlanTypeCount() {
        if (MapUtils.isEmpty(workPlanTypeCountMap)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(workPlanTypeCountMap.values());
    }

    /**
     * @return 作业计划类型列表-扁平化处理
     */
    public List<WorkPlanTypeCountModel> getWorkPlanTypeCountFlatten() {
        if (MapUtils.isEmpty(workPlanTypeCountMap)) {
            return Collections.emptyList();
        }

        return workPlanTypeCountMap.values().stream().map(e -> {
            List<WorkPlanRiskLevelCountModel> countList = e.getRiskLevelCount();
            if (CollectionUtils.isEmpty(countList)) {
                return e;
            }

            Map<Integer, WorkPlanRiskLevelCountModel> map = countList.stream().collect(Collectors.toMap(WorkPlanRiskLevelCountModel::getRiskLevel, m -> m));
            WorkPlanRiskLevelCountModel veryHigh = map.get(RiskLevelEnum.VERY_HIGH.getCode());
            e.setVeryHighRiskCount(veryHigh == null ? 0 : veryHigh.getCount());

            WorkPlanRiskLevelCountModel high = map.get(RiskLevelEnum.HIGH.getCode());
            e.setHighRiskCount(high == null ? 0 : high.getCount());

            WorkPlanRiskLevelCountModel medium = map.get(RiskLevelEnum.MEDIUM.getCode());
            e.setMediumRiskCount(medium == null ? 0 : medium.getCount());

            WorkPlanRiskLevelCountModel low = map.get(RiskLevelEnum.LOW.getCode());
            e.setLowRiskCount(low == null ? 0 : low.getCount());

            WorkPlanRiskLevelCountModel acceptable = map.get(RiskLevelEnum.ACCEPTABLE.getCode());
            e.setAcceptableRiskCount(acceptable == null ? 0 : acceptable.getCount());

            return e;
        }).collect(Collectors.toList());
    }

    /**
     * 站点统计
     */
    public static class SubstationCountTool {

        private final Map<String, Map<RiskLevelEnum, WorkPlanRiskLevelCountModel>> riskLevelMap = new HashMap<>();

        private final Map<String, List<WorkPlanDataAutoModel>> workPlanMap = new HashMap<>();

        // 重要厂站
        private final List<TransformerSubstation> substations;

        public SubstationCountTool(List<TransformerSubstation> substations) {
            if (substations == null) {
                substations = Collections.emptyList();
            }
            this.substations = substations;
        }

        /**
         * 统计作业计划 作业站点
         * @param model 作业计划
         * @return true-成功，false-失败
         */
        public boolean count(WorkPlanDataAutoModel model) {

            if (CollectionUtils.isEmpty(substations)) {
                throw new RuntimeException("重要厂站未配置");
            }

            String workLocation = model.getWorkLocation();
            if (StringUtils.isBlank(workLocation)) {
                return false;
            }

            RiskLevelEnum riskLevelEnum = getRiskLevel(model);

            for (TransformerSubstation substation : substations) {
                if (!workLocation.contains(substation.getName() + "站")) {
                    continue;
                }

                String stationName = Optional.ofNullable(substation.getVoltage()).orElse("") + substation.getName() + "站";
                countByRiskLevel(model, riskLevelEnum, m -> riskLevelMap.computeIfAbsent(stationName, k -> new HashMap<>()));
                List<WorkPlanDataAutoModel> workPlans = workPlanMap.computeIfAbsent(stationName, k -> new ArrayList<>());
                workPlans.add(model);
            }

            return true;
        }

        /**
         * @return 作业计划作业站点列表
         */
        public List<WorkPlanStationRiskLevelCountModel> getCountResult() {
            if (MapUtils.isEmpty(riskLevelMap)) {
                return Collections.emptyList();
            }

            List<WorkPlanStationRiskLevelCountModel> reList = new ArrayList<>();
            for (Map.Entry<String, Map<RiskLevelEnum, WorkPlanRiskLevelCountModel>> entry : riskLevelMap.entrySet()) {
                String substation = entry.getKey();
                Map<RiskLevelEnum, WorkPlanRiskLevelCountModel> riskLevelCountMap = entry.getValue();

                if (MapUtils.isEmpty(riskLevelCountMap)) {
                    for (RiskLevelEnum risk : RiskLevelEnum.values()) {
                        reList.add(new WorkPlanStationRiskLevelCountModel(substation, risk, 0));
                    }
                    continue;
                }

                for (Map.Entry<RiskLevelEnum, WorkPlanRiskLevelCountModel> riskLevelCountEntry : riskLevelCountMap.entrySet()) {
                    RiskLevelEnum riskLevel = riskLevelCountEntry.getKey();
                    WorkPlanRiskLevelCountModel riskLevelCount = riskLevelCountEntry.getValue();
                    WorkPlanStationRiskLevelCountModel re = new WorkPlanStationRiskLevelCountModel(substation, riskLevel, riskLevelCount.getCount());
                    reList.add(re);
                }
            }

            return reList;
        }

        public Map<String, List<WorkPlanDataAutoModel>> getPlanCount() {
            for (Map.Entry<String, List<WorkPlanDataAutoModel>> entry : workPlanMap.entrySet()) {
                entry.setValue(WorkPlanAutoDataService.parseWorkPlanData(entry.getValue()));
            }
            return workPlanMap;
        }
    }


    /**
     * 班组饱和度 风险等级 统计工具
     */
    public static class CrewSaturationCountTool {

        private final Map<String, Map<LocalDate, WorkPlanCrewSaturationCountModel>> crewCountMap = new HashMap<>();
        private final Map<WorkPlanCrewSaturationCountModel, Map<RiskLevelEnum, WorkPlanRiskLevelCountModel>> crewSaturationRiskLevelCountMap = new HashMap<>();

        public CrewSaturationCountTool() {}

        public boolean count(WorkPlanDataAutoModel model) {
            // 运行班组
            String runningCrewName = model.getRunningCrewName();
            if (StringUtils.isBlank(runningCrewName)) {
                return false;
            }

            RiskLevelEnum riskLevel = getRiskLevel(model);
            Map<LocalDate, WorkPlanCrewSaturationCountModel> crewCounts = crewCountMap.computeIfAbsent(runningCrewName, k -> {
                Map<LocalDate, WorkPlanCrewSaturationCountModel> map = new HashMap<>();
                LocalDate beginDate = model.getPlanStartTime().toLocalDate();
                LocalDate endDate = model.getPlanEndTime().toLocalDate();
                while (!beginDate.isAfter(endDate)) {
                    WorkPlanCrewSaturationCountModel count = new WorkPlanCrewSaturationCountModel();
                    count.setDate(beginDate);
//                    count.setCrewId(model.getCrewId());
                    count.setCrewName(runningCrewName);
                    map.put(beginDate, count);
                    beginDate = beginDate.plusDays(1);
                }
                return map;
            });
            for (Map.Entry<LocalDate, WorkPlanCrewSaturationCountModel> entry : crewCounts.entrySet()) {
                LocalDate key = entry.getKey();
                if (key.isBefore(model.getPlanStartTime().toLocalDate()) || key.isAfter(model.getPlanEndTime().toLocalDate())) {
                    continue;
                }

                WorkPlanCrewSaturationCountModel saturationCount = entry.getValue();
                countByRiskLevel(model, riskLevel, m -> crewSaturationRiskLevelCountMap.computeIfAbsent(saturationCount, k -> new HashMap<>()));
            }

            return true;
        }


        /**
         * @return 班组饱和度
         */
        public List<WorkPlanCrewSaturationCountModel> getCrewSaturationCount() {
            if (MapUtils.isEmpty(crewSaturationRiskLevelCountMap)) {
                return Collections.emptyList();
            }

            for (Map.Entry<WorkPlanCrewSaturationCountModel, Map<RiskLevelEnum, WorkPlanRiskLevelCountModel>> entry : crewSaturationRiskLevelCountMap.entrySet()) {
                WorkPlanCrewSaturationCountModel model = entry.getKey();
                List<WorkPlanRiskLevelCountModel> riskAndNumCount = model.getRiskAndNumCount();
                model.setRiskAndNumCount(riskAndNumCount);
            }

            return crewSaturationRiskLevelCountMap.entrySet().stream().map(e -> {
                WorkPlanCrewSaturationCountModel model = e.getKey();
                model.setRiskAndNumCount(new ArrayList<>(e.getValue().values()));
                return model;
            }).collect(Collectors.toList());
        }

    }


    /**
     * 风险统计等级
     *
     * @param model 作业计划
     * @param riskLevelCountMapFunc 获取对应的风险等级Map
     *                              当需要多级分类时,可自行控制如何获取Map,方便分类
     * @return true-成功，false-失败
     */
    public static void countByRiskLevel(WorkPlanDataAutoModel model, RiskLevelEnum riskLevel,
                                      Function<WorkPlanDataAutoModel, Map<RiskLevelEnum, WorkPlanRiskLevelCountModel>> riskLevelCountMapFunc
    ) {
        Map<RiskLevelEnum, WorkPlanRiskLevelCountModel> riskLevelCountMap = riskLevelCountMapFunc.apply(model);
        WorkPlanRiskLevelCountModel riskLevelCountModel = riskLevelCountMap.computeIfAbsent(riskLevel, k -> {
            WorkPlanRiskLevelCountModel countModel = new WorkPlanRiskLevelCountModel();
            countModel.setCount(0);
            countModel.setRiskLevel(riskLevel.getCode());
            countModel.setRisk(riskLevel.getDesc());
            return countModel;
        });
        riskLevelCountModel.setCount(riskLevelCountModel.getCount() + 1);
    }

    public static RiskLevelEnum getRiskLevel(WorkPlanDataAutoModel model) {
        if (model == null) {
            return RiskLevelEnum.UNKNOWN;
        }

        return RiskLevelEnum.getRiskLevel(model.getRiskLevel());
    }

    public static TypeEnum getWorkType(WorkPlanDataAutoModel model) {
        if (model == null) {
            return TypeEnum.UNKNOW;
        }

        return TypeEnum.getWorkType(model.getWorkType());
    }



}
