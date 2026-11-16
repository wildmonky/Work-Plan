/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_USERS 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */

package com.zhuofan.core.orm.dao;

import java.util.ArrayList;

import com.zhuofan.core.common.PageModel;
import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.model.SysUsers;
import org.apache.commons.lang3.StringUtils;

/**
 * BaseOrmSupport 用户角色表DAO
 * 
 * @author fuxiaodong
 * @Date 2015-05-20
 * 
 */
public class SysUsersDAO extends BaseOrmSupport<SysUsers> {

    /**
     * 根据用户名获取单条记录(边缘集成平台)
     * @param usernameDfdz
     * @return
     */
    public SysUsers getByUsernameDfdz(String usernameDfdz,String mobile) {
        try {
            this.setWhere(String.format("USERNAME_DFDZ='%s'",usernameDfdz));
            long count=this.getRowCount(new SysUsers());

            if (count<=1){
                return (SysUsers) this.queryFirstRow(new SysUsers());
            }

            if (StringUtils.isNotEmpty(mobile)){
                this.setWhere(String.format("USERNAME_DFDZ='%s' and MOBILE='%s'",usernameDfdz,mobile));
            }

            return (SysUsers) this.queryFirstRow(new SysUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return new SysUsers();
        }
    }

    /**
     * 根据用户名获取单条记录
     * @param userName
     * @return
     */
    public SysUsers getByUserName(String userName) {
        this.setWhere("USERNAME='" + userName + "'");
        try {
            return (SysUsers) this.queryFirstRow(new SysUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return new SysUsers();
        }
    }

    /**
     * 根据用户ID获取单条记录
     * @param userId
     * @return
     */
    public SysUsers getByUserId(String userId) {
        this.setWhere("USERID='" + userId + "'");
        try {
            return (SysUsers) this.queryFirstRow(new SysUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return new SysUsers();
        }
    }

    public String getAllUserName() {
        this.setWhere("ACTIVED='1' AND ISTEST='0'");//测试账户为1，不显示在统计表
        this.setOrder("USERNAME ASC");
        try {
            ArrayList<SysUsers> list = this.queryPage(new SysUsers());
            StringBuffer sb = new StringBuffer();
            for(SysUsers user :list){//user.getUserId() + ":" + 
                sb.append(user.getUsername() + ";");
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据用户名和密码获取单条记录
     * @param userName
     * @param password
     * @return
     */
    public SysUsers getByUserNamePass(String userName, String password) {
        this.setWhere("USERNAME='" + userName + "' AND USERPASSWORD='"
                + password + "'");
        try {
            return (SysUsers) this.queryFirstRow(new SysUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return new SysUsers();
        }
    }
}
