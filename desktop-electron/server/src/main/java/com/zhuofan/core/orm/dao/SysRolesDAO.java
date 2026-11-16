/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_ROLES 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */

package com.zhuofan.core.orm.dao;

import java.util.ArrayList;

import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.model.SysRoles;

/**
 * SysRolesDAO 角色表DAO
 * 
 * @author fuxiaodong
 * @Date 2015-05-20
 * 
 */
public class SysRolesDAO extends BaseOrmSupport<SysRoles> {
    
    public ArrayList<SysRoles> getListRoles() {
        ArrayList<SysRoles> arrayList = new ArrayList<SysRoles>();
        try {
            arrayList = this.queryPage(new SysRoles());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    
    
    /**
     * 根据角色ID获取角色详细记录
     * 
     */
    public SysRoles getByRoleId(String roleId) {
        this.setWhere("ROLEID='" + roleId + "'");
        try {
            return (SysRoles) this.queryFirstRow(new SysRoles());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
