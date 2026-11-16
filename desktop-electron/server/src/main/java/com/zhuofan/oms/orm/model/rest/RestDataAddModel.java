package com.zhuofan.oms.orm.model.rest;

import com.zhuofan.core.orm.BaseRestModel;
import com.zhuofan.core.orm.model.SysTable;
import com.zhuofan.core.orm.model.SysTableColumn;
import com.zhuofan.core.orm.model.SysTableColumnGroup;

import java.util.ArrayList;
import java.util.HashMap;

/*
data/add返回的数据模型类
 */
public class RestDataAddModel extends BaseRestModel {
    //操作数据名称
    private String dataName;

    //操作类型
    private String actionType;

    //主键
    private String pkId;

    //数据表信息
    private SysTable sysTable;

    //字段分组信息
    private ArrayList<SysTableColumnGroup> listGroup;

    //字段列信息
    private HashMap<String,Object> listGroupCol;

    //数据对象
    private Object objectData;

    //分组
    private String groupCode;

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPkId() {
        return pkId;
    }

    public void setPkId(String pkId) {
        this.pkId = pkId;
    }

    public SysTable getSysTable() {
        return sysTable;
    }

    public void setSysTable(SysTable sysTable) {
        this.sysTable = sysTable;
    }

    public ArrayList<SysTableColumnGroup> getListGroup() {
        return listGroup;
    }

    public void setListGroup(ArrayList<SysTableColumnGroup> listGroup) {
        this.listGroup = listGroup;
    }

    public HashMap<String,Object> getListGroupCol() {
        return listGroupCol;
    }

    public void setListGroupCol(HashMap<String,Object> listGroupCol) {
        this.listGroupCol = listGroupCol;
    }

    public Object getObjectData() {
        return objectData;
    }

    public void setObjectData(Object objectData) {
        this.objectData = objectData;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
