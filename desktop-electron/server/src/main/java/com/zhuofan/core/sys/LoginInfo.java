/**
 * com.zhuofan.core.sys
 *
 *
 *   ver     date           author 
 * ====================================================== 
 *           2015-06-15      fuxiaodong 
 *
 * */

package com.zhuofan.core.sys;

import com.zhuofan.core.orm.model.SysUsers;
import com.zhuofan.core.orm.model.SysDepartments;

/**
 * @ClassName: LoginInfo
 * @Description: 系统登录信息
 * @author 广州卓凡扶小冬
 * @date Jun 15, 2015 8:19:02 PM
 */
public class LoginInfo {
    private String username;

    private String password;

    private SysUsers loginSysUser;

    private SysDepartments loginSysDept;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SysUsers getLoginSysUser() {
        return loginSysUser;
    }

    public void setLoginSysUser(SysUsers loginSysUser) {
        this.loginSysUser = loginSysUser;
    }

    public SysDepartments getLoginSysDept() {
        return loginSysDept;
    }

    public void setLoginSysDept(SysDepartments loginSysDept) {
        this.loginSysDept = loginSysDept;
    }

    /**
     * @Title: getLoginUserId
     * @Description:获取登录用户的ID
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public String getLoginUserId() {
        if (this.loginSysUser == null) {
            return "";
        }

        return this.loginSysUser.getUserid();
    }

    /**
     * @Title: getLoginDeptId
     * @Description: 获取登录部门的ID
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public String getLoginDeptId() {
        if (this.loginSysDept == null) {
            return "";
        }

        return this.loginSysDept.getDepartmentid();
    }

    /**
     * @Title: getLoginDeptName
     * @Description: 获取登录部门的名称
     * @return
     * @author 广州卓凡扶小冬
     * @throws
     */
    public String getLoginDeptName() {
        if (this.loginSysDept == null) {
            return "";
        }

        return this.loginSysDept.getDepartmentname();
    }
}