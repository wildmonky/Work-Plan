package com.zhuofan.core.flow;

import com.zhuofan.core.sys.UserInfoShort;

import java.util.ArrayList;

/*
FlowNodeUsers：节点有权限的用户列表
 */
public class FlowNodeUsers {

    private String nodeName;

    private String flowNodeId;

    private String nodeType;

    private ArrayList<UserInfoShort> listUser;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }



    public ArrayList<UserInfoShort> getListUser() {
        return listUser;
    }

    public void setListUser(ArrayList<UserInfoShort> listUser) {
        this.listUser = listUser;
    }


    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getFlowNodeId() {
        return flowNodeId;
    }

    public void setFlowNodeId(String flowNodeId) {
        this.flowNodeId = flowNodeId;
    }
}
