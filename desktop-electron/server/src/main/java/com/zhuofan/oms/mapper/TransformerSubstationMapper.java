package com.zhuofan.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuofan.oms.orm.model.TransformerSubstation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TransformerSubstationMapper extends BaseMapper<TransformerSubstation> {

    @Select("select COLUMN_NAME from SYS_TABLE_COLUMN " +
            "WHERE TABLE_ID = (select TABLE_ID from SYS_TABLE WHERE TABLE_CODE = 'TRANSFORMER_SUBSTATION')" +
            " and column_code = 'IS_THIRTEEN_ADD_SIXTEEN'")
    String selectLastCellName();

    @Update("update SYS_TABLE_COLUMN set column_name = #{lastCellName} " +
            "WHERE TABLE_ID = (select TABLE_ID from SYS_TABLE WHERE TABLE_CODE = 'TRANSFORMER_SUBSTATION') " +
            "and column_code = 'IS_THIRTEEN_ADD_SIXTEEN'")
    void updateLastCellName(String lastCellName);

    default void updateLastCellNameSafe(String lastCellName) {
        if (StringUtils.isBlank(lastCellName)) {
            return;
        }
        updateLastCellName(lastCellName);
    }

    List<String> selectExistedSubstationName();

}
