package com.zhuofan.oms.service;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.datasource.JdbcDao;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class GrantService {

    /**
     * @Author fuxiaodong
     * @Description //获取本部门的领导，如果本部门没获取到，则获取上级部门的领导
     * @Version: v1.8
     * @Date 10:18 2026/8/20
     * @Param [deptId]
     * @return java.lang.String
     */
    public static String getDeptLeader(String deptId,String fieldName){
        String result="";

        try(JdbcDao jdbcDao=new JdbcDao()) {
            String sqlDeptLeader = String.format("select WM_CONCAT(%s) %s from sys_users where " +
                    "DEPARTMENTID='%s' and flag_leader='1'", fieldName,fieldName,deptId);
            result=jdbcDao.getFieldValue(sqlDeptLeader,fieldName.toUpperCase(Locale.ROOT));

            if (StringUtils.isEmpty(result)){
                sqlDeptLeader = String.format("select WM_CONCAT(%s) %s from sys_users where DEPARTMENTID IN " +
                        "(select PARENTID from SYS_DEPARTMENTS where DEPARTMENTID='%s') and flag_leader='1'",
                        fieldName,fieldName,deptId);
                result=jdbcDao.getFieldValue(sqlDeptLeader,fieldName.toUpperCase(Locale.ROOT));
            }

            jdbcDao.close();
        }

        return result;

    }

    /**
     * @Author fuxiaodong
     * @Description //TODO ：根据部门ID，返回部门的完整路径
     * @Version: v1.8
     * @Date 9:12 2025/6/10
     * @Param [deptId]
     * @return java.lang.String
     */
    public static String getDeptFullName(String deptId){
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String sql = "select dept_name from ( " +
                    "  SELECT *,SYS_CONNECT_BY_PATH(DEPARTMENTNAME, '/') AS DEPT_NAME " +
                    "  FROM SYS_DEPARTMENTS " +
                    "  START WITH PARENTID = '0' CONNECT BY PRIOR(DEPARTMENTID) = PARENTID " +
                    "  ORDER SIBLINGS BY DEPARTMENTNAME ASC) " +
                    " where DEPARTMENTID='" + deptId + "'";
            String deptName = jdbcDao.getFieldValue(sql, "DEPT_NAME");
            jdbcDao.close();

            return deptName;
        }
    }

    /**
     * @Author fuxiaodong
     * @Description //TODO 判断某个ID的用户是否有某个角色的权限
     * @Version: v1.8
     * @Date 15:32 2025/6/9
     * @Param
     * @return
     */
    public static boolean checkUserGrantByRoleId(String userId,String roleId){
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String sql = String.format("select count(*) mycount from sys_user_role " +
                    " where userid='%s' and roleid='%s'", userId, roleId);
            int count = CommonFunc.strToInt(jdbcDao.getFieldValue(sql, "MYCOUNT"));

            jdbcDao.close();

            return count > 0;
        }
    }

}
