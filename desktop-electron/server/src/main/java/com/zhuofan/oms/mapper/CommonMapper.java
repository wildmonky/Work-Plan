package com.zhuofan.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CommonMapper<T> extends BaseMapper<T> {
    /**
     * 真正的批量插入
     * @param entityList
     * @return
     */
    int insertBatchSomeColumn(List<T> entityList);

    @Select(" SELECT SEQ.NEXTVAL NEXTVAL FROM DUAL ")
    long nextSeqId();

    @Select("SELECT count(*) FROM SYS.ALL_TABLES WHERE TABLE_NAME = #{tableName} AND OWNER = #{schema}")
    boolean tableExists(String schema, @NonNull String tableName);

    @Select("SELECT * FROM ${tableName} WHERE ${primaryKeyColumn} = #{id}")
    T selectOneByPrimaryKey(String tableName, String primaryKeyColumn, String id);
}
