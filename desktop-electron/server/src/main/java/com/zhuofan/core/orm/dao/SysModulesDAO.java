/** 
 * com.zhoufan.core.orm.model
 * 
 * 
 *   ver     date           author 
 * 用户角色表：SYS_USER_ROLE 
 *           2015-05-20      fuxiaodong 
 * 
 * Copyright (c) 2015, TNT All Rights Reserved. 
 * */

package com.zhuofan.core.orm.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.orm.BaseOrmSupport;
import com.zhuofan.core.orm.model.SysModules;
import com.zhuofan.core.util.BeanUtils;

/**
 * SysModulesDAO 模块表DAO
 * 
 * @author fuxiaodong
 * @Date 2015-05-20
 * 
 */
public class SysModulesDAO extends BaseOrmSupport<SysModules> {
    public ArrayList<SysModules> getByRoleId(String roleId) throws Exception{
        ArrayList<SysModules> SysRoleModPrivList = null;

        StringBuffer sb = new StringBuffer();
        sb.append("select * from sys_modules where moduleid in (select moduleid from sys_role_privileges where roleid='"+roleId+"')");

        SysRoleModPrivList = (ArrayList<SysModules>)this.query(new SysModules(), sb.toString());

        return SysRoleModPrivList;
    }
    
    public ArrayList<SysModules> getMenuByUserId(String userId,String parentId) throws Exception{
        String whereParentId=" AND SMOU.parentid='"+parentId+"' ";

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT SMOU.* FROM SYS_MODULES SMOU where moduleid in (");
        sb.append("select moduleid from sys_role_privileges where roleid in (");
        sb.append("select roleid from sys_user_role where userid='"+userId+"'))");
        sb.append("and parentid='"+parentId+"' ORDER BY SORTINDEX ");

        ArrayList<SysModules> SysRoleModPrivList = null;
        SysRoleModPrivList = (ArrayList<SysModules>)this.query(new SysModules(), sb.toString());
        return SysRoleModPrivList;
    }
    
    public ArrayList<SysModules> getMenuAll() throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT SMOU.* ");
        sb.append("FROM SYS_MODULES SMOU ");
        sb.append("WHERE SMOU.ISSHOW='0' ");
        sb.append("ORDER BY parentid ASC,TO_NUMBER(SMOU.SORTINDEX) ASC");
        
        ArrayList<SysModules> sysModList = null;
        sysModList = (ArrayList<SysModules>)this.query(new SysModules(), sb.toString());
        return sysModList;
    }

    public LinkedHashMap<String,Object> getMenuByTree() throws Exception{
        ArrayList<SysModules> sysModList = this.getMenuAll();
        LinkedHashMap<String,Object> mapResult=new LinkedHashMap<String,Object>();
        for(int i=0;i<sysModList.size();i++){
            String parentId=sysModList.get(i).getParentid();
            if (parentId.equals("-1")){
                parentId="root";
            }

            String idKey="P_"+parentId;
            ArrayList<SysModules> listTmp;

            if (mapResult.containsKey(idKey)){
                listTmp=(ArrayList<SysModules>)mapResult.get(idKey);
                listTmp.add(sysModList.get(i));
                mapResult.replace(idKey,listTmp);
            }else{
                //不存在,新建一个
                listTmp=new ArrayList<SysModules>();
                listTmp.add(sysModList.get(i));
                mapResult.put(idKey,listTmp);
            }
        }

        return mapResult;
    }

    public void getMoudleChild(HashMap<String, String> map) {
        StringBuffer sbResult = new StringBuffer();

        try(JdbcDao jdbcDao = new JdbcDao()) {
            List<HashMap<String, String>> list = jdbcDao.getResult("");
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String[] dataTit = null;
                    if (!BeanUtils.isEmpty(list.get(i).get("DATATIT"))) {
                        dataTit = list.get(i).get("DATATIT").split(",");
                    }

                    StringBuffer sb1 = new StringBuffer();
                    if (list.get(i).get("LEVELS").equals("1")) {
                        sb1.append("<li");
                        if (i == 0) {
                            sb1.append(" class='active'");
                        }
                        sb1.append("><a href='javascript:;' data-toggle='slidebar'>");
                        sb1.append("<i class='" + list.get(i).get("ICOCLASS") + "'></i>&nbsp;");
                        sb1.append(list.get(i).get("MODULENAME") + "</a>");
                        sb1.append("<div class='items hide' data-noinit='true'>");

                        StringBuffer sb2 = new StringBuffer();
                        sb2.append("<ul id='bjui-hnav-tree" + list.get(i).get("MODULEID") + "' ");
                        sb2.append("class='ztree ztree_main' data-toggle='ztree' ");
                        sb2.append("data-on-click='MainMenuClick' data-expand-all='true' ");

                        if (!BeanUtils.isEmpty(dataTit)) {
                            for (String ulTile : dataTit) {
                                sb2.append("data-tit='" + ulTile + "'>");
                                while (true) {
                                    i++;
                                    String child = getMoudleChildPriv(list.get(i));
                                    if (BeanUtils.isEmpty(child)) {
                                        break;
                                    } else {
                                        sb2.append(child);
                                    }
                                }
                                sb2.append("</ul>");
                            }
                        } else {
                            sb2.append("data-tit=''>");
                            while (true) {
                                i++;
                                String child = getMoudleChildPriv(list.get(i));
                                if (BeanUtils.isEmpty(child)) {
                                    break;
                                } else {
                                    sb2.append(child);
                                }
                            }
                            sb2.append("</ul>");
                        }
                        sb1.append(sb2.toString() + "</div>");
                        sb1.append("</li>");
                    }
                    sbResult.append(sb1.toString());
                }
            }
            jdbcDao.close();
        }
    }
    
    public String getMoudleChildPriv(HashMap<String, String> map) {
        
        StringBuffer sb = new StringBuffer();
        if(map.get("LEVELS").equals("2")){
            sb.append("<li data-id='" + map.get("MODULEID") + "' ");
            sb.append("data-pid='" + map.get("PARENTID") + "' ");
            
            if(map.get("URL").isEmpty()){
                sb.append("data-faicon='folder-open-o'>");
            } else {
                sb.append("data-url='" + map.get("URL") + "' ");
                sb.append("data-tabid='table" + map.get("MODULEID") + "'>");
            }
            sb.append(map.get("MODULENAME") + "</li>");
            return sb.toString();
        } else {
            return "";
        }
    }
    
}
