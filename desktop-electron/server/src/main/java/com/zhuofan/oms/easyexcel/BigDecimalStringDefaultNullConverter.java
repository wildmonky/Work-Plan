package com.zhuofan.oms.easyexcel;

import com.alibaba.excel.converters.bigdecimal.BigDecimalStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

public class BigDecimalStringDefaultNullConverter extends BigDecimalStringConverter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public BigDecimal convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                        GlobalConfiguration globalConfiguration) throws ParseException {
        BigDecimal re;
        switch (cellData.getType()) {
            case NUMBER:
                re = cellData.getNumberValue();
                break;
            case STRING:
                String value = cellData.getStringValue();
                if (StringUtils.isBlank(value)) {
                    return null;
                }

                try{
                    re = super.convertToJavaData(cellData, contentProperty, globalConfiguration);
                    break;
                } catch (NumberFormatException e) {
                    log.warn("{}值为: {}，无法转换为BigDecimal", contentProperty.getField().getName(), value);
                    return null;
                }
            default: throw new NumberFormatException(String.format("数据转换异常: %s", cellData));
        }
        return re.setScale(2, RoundingMode.HALF_UP);
    }

}
