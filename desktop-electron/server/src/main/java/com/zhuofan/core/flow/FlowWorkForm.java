package com.zhuofan.core.flow;

import com.zhuofan.core.orm.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class FlowWorkForm {

    //流程信息
    private SysFlow sysFlow;

    //节点信息
    private SysFlowNode sysFlowNode;

    //流程实例信息
    private SysFlowInstance sysFlowInstance;

    //业务表数据
    private SysTable sysTable;

    //当前流程执行信息
    private SysFlowExec sysFlowExec;

    private ArrayList<Object> listFlowNode;

    private HashMap<String,String> mapModifyGrant;

    private ArrayList<MapListFlowNodeUsers> listMapFlowNodeUsers ;

    private String defUserid;
    private String defDeptname;
    private String defUsername;

    public SysFlow getSysFlow() {
        return sysFlow;
    }

    public void setSysFlow(SysFlow sysFlow) {
        this.sysFlow = sysFlow;
    }

    public SysFlowInstance getSysFlowInstance() {
        return sysFlowInstance;
    }

    public void setSysFlowInstance(SysFlowInstance sysFlowInstance) {
        this.sysFlowInstance = sysFlowInstance;
    }

    public void setSysTable(SysTable sysTable) {
        this.sysTable = sysTable;
    }

    public SysTable getSysTable() {
        return sysTable;
    }

    public SysFlowExec getSysFlowExec() {
        return sysFlowExec;
    }

    public void setSysFlowExec(SysFlowExec sysFlowExec) {
        this.sysFlowExec = sysFlowExec;
    }

    public ArrayList<Object> getListFlowNode() {
        return listFlowNode;
    }

    public void setListFlowNode(ArrayList<Object> listFlowNode) {
        this.listFlowNode = listFlowNode;
    }

    public HashMap<String, String> getMapModifyGrant() {
        return mapModifyGrant;
    }

    public void setMapModifyGrant(HashMap<String, String> mapModifyGrant) {
        this.mapModifyGrant = mapModifyGrant;
    }

    public SysFlowNode getSysFlowNode() {
        return sysFlowNode;
    }

    public void setSysFlowNode(SysFlowNode sysFlowNode) {
        this.sysFlowNode = sysFlowNode;
    }

    public ArrayList<MapListFlowNodeUsers> getListMapFlowNodeUsers() {
        return listMapFlowNodeUsers;
    }

    public void setListMapFlowNodeUsers(ArrayList<MapListFlowNodeUsers> listMapFlowNodeUsers) {
        this.listMapFlowNodeUsers = listMapFlowNodeUsers;
    }

    public String getDefUserid() {
        return defUserid;
    }

    public void setDefUserid(String defUserid) {
        this.defUserid = defUserid;
    }

    public String getDefDeptname() {
        return defDeptname;
    }

    public void setDefDeptname(String defDeptname) {
        this.defDeptname = defDeptname;
    }

    public String getDefUsername() {
        return defUsername;
    }

    public void setDefUsername(String defUsername) {
        this.defUsername = defUsername;
    }
}
