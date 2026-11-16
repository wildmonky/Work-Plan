package com.zhuofan.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuofan.oms.orm.model.CrewMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrewMemberMapper extends BaseMapper<CrewMember> {

    List<CrewMember> selectDetails(List<Long> ids);

    List<CrewMember> selectMoreDetails(List<Long> ids);

}
