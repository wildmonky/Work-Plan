package com.zhuofan.oms.controller.sys;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.ServerKeyIv;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.orm.dao.*;
import com.zhuofan.core.orm.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/*
 */
@RestController
@CrossOrigin
public class UsergrantController extends BaseController{

    /*
     * 角色权限分配获取信息
     * 参数:roleId
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Usergrant/GetRolegrant",method = RequestMethod.GET)
    public Map<String, Object> GetRolegrant(@RequestParam Map<String,String> params) throws Exception{
        String roleId= CommonFunc.nullToSpace(params.get("roleid"));

        SysRoles sysRoles=(SysRoles) new SysRolesDAO().queryById(new SysRoles(),roleId);

        if (sysRoles==null){
            return CommonFunc.makeErrResult("300032");
        }

        //获取所有的角色权限
        SysModulesDAO sysModulesDAO=new SysModulesDAO();
        ArrayList<SysModules> listRoleModules=sysModulesDAO.getByRoleId(roleId);
        ArrayList<String> listRoleModule=new ArrayList<String>();

        for (int i=0;i<listRoleModules.size();i++){
            listRoleModule.add(listRoleModules.get(i).getModuleid());
        }

        //获取所有的模块
        ArrayList<SysModules> listModules=sysModulesDAO.getMenuAll();
        ArrayList<SysModules> listParentModules=new ArrayList<SysModules>();
        ArrayList<ArrayList<SysModules>> listChildModules=new ArrayList<ArrayList<SysModules>>();
        HashMap<String,Integer> mapIndexs=new HashMap<String,Integer>();


        for (int i=0;i<listModules.size();i++){
            String parentId=listModules.get(i).getParentid().toString();
            if ("-1".equals(parentId)){
                listParentModules.add(listModules.get(i));
                listChildModules.add(new ArrayList<SysModules>());
                mapIndexs.put(listModules.get(i).getModuleid().toString(),listParentModules.size()-1);
            }else{
                if (mapIndexs.containsKey(parentId)){
                    int index=mapIndexs.get(parentId);
                    listChildModules.get(index).add(listModules.get(i));
                }
            }
        }
        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("sys_title",sysConfig.getTitle());
        mapData.put("page_title","给用户分配角色");
        mapData.put("roleid", roleId);
        mapData.put("listRoleModule", listRoleModule);
        mapData.put("listParentModules", listParentModules);
        mapData.put("listChildModules", listChildModules);

        return CommonFunc.makeSussResult(mapData,new ServerKeyIv(sysConfig.getTempfilePath()));
    }

    /*
     * 用户权限分配获取信息
     * 参数:无
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Usergrant/GetUsergrant",method = RequestMethod.GET)
    public Map<String, Object> GetUsergrant(@RequestParam Map<String,String> params) throws Exception{
        String userid= CommonFunc.nullToSpace(params.get("userid"));

        SysUsers sysUsers=(SysUsers) new SysUsersDAO().queryById(new SysUsers(),userid);

        if (sysUsers==null){
            return CommonFunc.makeErrResult("300002");
        }

        //获取用户的所有角色
        String sql="select * from sys_user_role where userid='"+userid+"'";
        List<SysUserRole> listUserRole=(List<SysUserRole>) new SysUserRoleDAO().query(new SysUserRole(),sql);

        String roleids="";
        if (listUserRole!=null){
            for(int i=0;i<listUserRole.size();i++){
                roleids=(StringUtils.isEmpty(roleids))?listUserRole.get(i).getRoleId():roleids+","+listUserRole.get(i).getRoleId();
            }
        }

        try(JdbcDao jdbcDao=new JdbcDao()) {
            sql = "select roleid FLDN_K,rolename FLDN_V from sys_roles ";
            ArrayList<HashMap<String, String>> roleInfo = jdbcDao.getResult(sql);

            Map<String, Object> mapData = new HashMap<String, Object>();
            mapData.put("sys_title", sysConfig.getTitle());
            mapData.put("page_title", "给用户分配角色");
            mapData.put("userid", userid);
            mapData.put("roleids", roleids);
            mapData.put("roleInfo", roleInfo);

            jdbcDao.close();

            return CommonFunc.makeSussResult(mapData, new ServerKeyIv(sysConfig.getTempfilePath()));
        }
    }

    /*
     * 用户权限分配信息保存
     * 参数:无
     * 返回数据:
     *{
            result:0/1,
            data:list
        };
     */
    @RequestMapping(value = "/Usergrant/SetUsergrant",method = RequestMethod.POST)
    public Map<String, Object> SetUsergrant(@RequestParam Map<String,Object> params) throws Exception {
        Map<String, Object> mapPost = CommonFunc.checkServerPost(params, sysConfig);

        if (mapPost.get("result").equals("0")) {
            //失败
            return mapPost;
        }

        Map<String,String> mapVar= (Map<String, String>) mapPost.get("vars");
        String userid=CommonFunc.getNotNullString(mapVar.get("userid"));
        String roleids=CommonFunc.getNotNullString(mapVar.get("roleids"));

        if (StringUtils.isEmpty(roleids)){
            return CommonFunc.makeErrResult("300021");
        }

        SysUsers sysUsers=(SysUsers) new SysUsersDAO().queryById(new SysUsers(),userid);

        if (sysUsers==null){
            return CommonFunc.makeErrResult("300002");
        }

        String[] listRoleid=roleids.split(",");
        try(JdbcDao jdbcDao=new JdbcDao()) {
            SysUserRoleDAO sysUserRoleDAO = new SysUserRoleDAO();
            jdbcDao.execute("delete from sys_userrole where userid='" + userid + "'");

            for (int i = 0; i < listRoleid.length; i++) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userid);
                sysUserRole.setRoleId(listRoleid[i]);
                sysUserRole.setUserRoleId(jdbcDao.getSeqNextVal());
                sysUserRoleDAO.saveNew(sysUserRole);
            }

            Map<String, Object> mapData = new HashMap<String, Object>();
            mapData.put("redirectFlag", "1");
            mapData.put("redirectUrl", "module/sys/userroleset.html?userid=" + userid);

            jdbcDao.close();

            return CommonFunc.makeSussResult(mapData);
        }
    }

    /*
    * 角色权限分配信息保存
    * 参数:无
    * 返回数据:
    *{
           result:0/1,
           data:list
       };
    */
    @RequestMapping(value = "/Usergrant/SetRolegrant",method = RequestMethod.POST)
    public Map<String, Object> SetRolegrant(@RequestParam Map<String,Object> params) throws Exception {
        Map<String, Object> mapPost = CommonFunc.checkServerPost(params, sysConfig);

        if (mapPost.get("result").equals("0")) {
            //失败
            return mapPost;
        }

        Map<String,String> mapVar= (Map<String, String>) mapPost.get("vars");
        String roleid=CommonFunc.getNotNullString(mapVar.get("roleid"));
        String moduleids=CommonFunc.getNotNullString(mapVar.get("moduleid"));

        if (StringUtils.isEmpty(roleid)){
            return CommonFunc.makeErrResult("300021");
        }

        SysRoles sysRoles=(SysRoles) new SysRolesDAO().queryById(new SysRoles(),roleid);

        if (sysRoles==null){
            return CommonFunc.makeErrResult("300002");
        }

        String[] listModuleid=moduleids.split(",");

        try(JdbcDao jdbcDao=new JdbcDao()) {
            SysRolePrivilegesDAO sysRolePrivilegesDAO = new SysRolePrivilegesDAO();
            jdbcDao.execute("delete from SYS_ROLE_PRIVILEGES where ROLEID='" + roleid + "'");

            for (int i = 0; i < listModuleid.length; i++) {
                SysRolePrivileges sysRolePrivileges = new SysRolePrivileges();
                sysRolePrivileges.setRoleId(roleid);
                sysRolePrivileges.setModuleId(listModuleid[i]);
                sysRolePrivileges.setPrivilegeId(jdbcDao.getSeqNextVal());
                sysRolePrivilegesDAO.saveNew(sysRolePrivileges);
            }

            Map<String, Object> mapData = new HashMap<String, Object>();
            mapData.put("redirectFlag", "1");
            mapData.put("redirectUrl", "module/sysrole/grantset.html?roleid=" + roleid);

            jdbcDao.close();

            return CommonFunc.makeSussResult(mapData);
        }
    }
}
