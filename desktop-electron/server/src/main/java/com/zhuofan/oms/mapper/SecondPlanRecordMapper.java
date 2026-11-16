package com.zhuofan.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuofan.oms.orm.model.SecondPlanRecord;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface SecondPlanRecordMapper extends BaseMapper<SecondPlanRecord> {

    int selectAverageNum(LocalDate beginDate, LocalDate endDate);

}
