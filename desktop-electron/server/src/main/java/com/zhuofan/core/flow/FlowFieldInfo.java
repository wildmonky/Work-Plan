package com.zhuofan.core.flow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class FlowFieldInfo {
    private String groupId;
    private String tableName;
    private String fieldName;
    private Object value;
    private Object valueShow;

    private String inputType;
    private String showType;

    private String isHide;

    private String fieldCode;

    private String nullable;

    private String columnLength;

    private HashMap<String,String> datamap;

    private String css;

    private String defaultValue;

    private JSONArray jsonData;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String columnLength) {
        this.columnLength = columnLength;
    }

    public HashMap<String, String> getDatamap() {
        return datamap;
    }

    public void setDatamap(HashMap<String, String> datamap) {
        this.datamap = datamap;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getValueShow() {
        return valueShow;
    }

    public void setValueShow(Object valueShow) {
        this.valueShow = valueShow;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public JSONArray getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONArray jsonData) {
        this.jsonData = jsonData;
    }
}
