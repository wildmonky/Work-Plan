package com.zhuofan.oms.mapper.workplan;

import com.google.common.collect.Lists;
import com.zhuofan.oms.entity.workplan.CrewWorkLoadRateDayRecord;
import com.zhuofan.oms.mapper.CommonMapper;
import com.zhuofan.oms.model.workplan.CrewWorkLoadRateModel;

import java.time.LocalDate;
import java.util.List;

public interface CrewWorkLoadRateDayRecordMapper extends CommonMapper<CrewWorkLoadRateDayRecord> {

    default void batchInsert(List<CrewWorkLoadRateDayRecord> list) {
        List<List<CrewWorkLoadRateDayRecord>> partition = Lists.partition(list, 500);
        partition.forEach(this::insertBatchSomeColumn);
    }

    List<CrewWorkLoadRateModel> selectByDateRange(LocalDate startDate, LocalDate endDate);

}
