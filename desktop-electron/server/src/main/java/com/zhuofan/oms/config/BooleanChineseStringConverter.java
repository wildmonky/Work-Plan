package com.zhuofan.oms.config;

import com.alibaba.excel.converters.booleanconverter.BooleanStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.BooleanUtils;

public class BooleanChineseStringConverter extends BooleanStringConverter {

    @Override
    public Boolean convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) {
        return "是".equals(cellData.getStringValue()) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public WriteCellData<?> convertToExcelData(Boolean value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(BooleanUtils.isTrue(value) ? "是" : "否");
    }

}
