package com.zhuofan.core.sys;

/*
UserInfoShort：最简短的用户信息：用户ID，姓名，部门ID，姓名
 */
public class UserInfoShort {

    private String userId;

    private String userName;

    private String deptId;

    private String deptName;


    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
