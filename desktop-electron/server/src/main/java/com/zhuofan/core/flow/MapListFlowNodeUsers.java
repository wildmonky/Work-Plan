package com.zhuofan.core.flow;

import java.util.ArrayList;

public class MapListFlowNodeUsers {
    private String key;

    private String switchName;

    private String switchVal;

    private ArrayList<FlowNodeUsers> listFlowNodeUsers;

    private String flagHideUser;

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getSwitchVal() {
        return switchVal;
    }

    public void setSwitchVal(String switchVal) {
        this.switchVal = switchVal;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<FlowNodeUsers> getListFlowNodeUsers() {
        return listFlowNodeUsers;
    }

    public void setListFlowNodeUsers(ArrayList<FlowNodeUsers> listFlowNodeUsers) {
        this.listFlowNodeUsers = listFlowNodeUsers;
    }

    public String getFlagHideUser() {
        return flagHideUser;
    }

    public void setFlagHideUser(String flagHideUser) {
        this.flagHideUser = flagHideUser;
    }
}
