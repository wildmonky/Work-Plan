package com.zhuofan.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuofan.oms.model.CrewModel;
import com.zhuofan.oms.orm.model.Crew;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrewMapper extends BaseMapper<Crew> {

    List<CrewModel> searchAll();

}
