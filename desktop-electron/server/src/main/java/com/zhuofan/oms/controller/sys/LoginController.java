package com.zhuofan.oms.controller.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.common.MD5;
import com.zhuofan.core.common.ServerKeyIv;
import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.orm.dao.SysRolesDAO;
import com.zhuofan.core.orm.dao.SysSessionsDAO;
import com.zhuofan.core.orm.dao.SysUserRoleDAO;
import com.zhuofan.core.orm.dao.SysUsersDAO;
import com.zhuofan.core.orm.model.SysRoles;
import com.zhuofan.core.orm.model.SysSessions;
import com.zhuofan.core.orm.model.SysUserRole;
import com.zhuofan.core.orm.model.SysUsers;
import com.zhuofan.core.util.RequestUtils;
import com.zhuofan.core.util.SessionParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/*
 */
@RestController
@CrossOrigin()
public class LoginController {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private HttpServletResponse response;

    @Autowired
    private DatasourceConfig datasourceConfig;

    @Autowired
    private Environment env;

    @Autowired
    private SysConfig sysConfig;

    private CommonLogger logger= CommonLogger.getInstance();

    /*
     * 重新登录请求
     * 参数:无
     * 返回数据:成功
     */
    @RequestMapping(value = "/ReLogin",method = RequestMethod.GET)
    public Map<String, Object> ReLogin(@RequestParam Map<String,String> params){
        request.getSession().invalidate();

        return CommonFunc.makeSussResult(null);
    }

    /*
     * 登录界面请求
     * 参数:
     * err_code:错误代码
     * 返回数据:项目常规信息,错误提示(如有)
     *{
			sys_title:"东莞局调度电子档案管理系统",
			sys_version:"V1.0",

			page_title:"系统登录",
			login_notice:"xxxxx",
			err_msg:"fffff",
			message: 'Hello Vue!'
		};
     */
    @RequestMapping(value = "/Login",method = RequestMethod.GET)
    public Map<String, Object> Login(@RequestParam Map<String,String> params) throws  Exception{
        String err_code=CommonFunc.getNotNullString(params.get("err_code"));

        String sysTitle=sysConfig.getTitle();
        String sysVersion=sysConfig.getVersion();
        String pageTitle=sysConfig.getPagetitleLogin();
        String errMsg=CommonFunc.getMsgByKey("err."+err_code);
        String loginNotice="";

        Map<String, Object> map = new HashMap<>();

        map.put("sys_title",sysTitle);
        map.put("sys_version",sysVersion);
        map.put("page_title",pageTitle);
        map.put("err_msg",errMsg);
        map.put("login_notice",loginNotice);

        return CommonFunc.makeSussResult(map,new ServerKeyIv(sysConfig.getTempfilePath()));
    }


    /*
     * 登录认证
     * 参数:_server_iv_,_client_sign_,_client_sign_
     * err_code:错误代码
     * 返回数据:错误代码
     */
    @RequestMapping(value="/Login/Check")
    public Map<String, Object> LoginCheck(@RequestParam Map<String,Object> params) throws Exception{
        Map<String,Object> mapPost=CommonFunc.checkServerPost(params,sysConfig);

            if (mapPost.get("result").equals("0")){
            //失败
            return mapPost;
        }

        //解析成功,开始处理登录验证
        Map<String,Object> mapResultData=new HashMap<String,Object>();
        Map<String,String> mapVar= (Map<String, String>) mapPost.get("vars");
        String username=mapVar.get("username");
        String password=mapVar.get("password");

        logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheck:username:"+username);
        logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheck:password:"+password);

        SysUsersDAO userDao = new SysUsersDAO();
        try {
            if (username.isEmpty() || password.isEmpty()) {
                //用户名和密码输入不完整
                return CommonFunc.makeErrResult("300001");
            }

            String md5Password = MD5.GetMD5Code(password);
            SysUsers sysUser = userDao.getByUserNamePass(username, md5Password);
            logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheck:sysUser:"+sysUser);

            if (sysUser == null) {
                //用户名或密码错误
                return CommonFunc.makeErrResult("300002");
            }

            if(sysUser.getActived().equals("0")){
                //该用户已被禁用，请联系管理员
                return CommonFunc.makeErrResult("300003");
            }

            logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheck:sysUser string:"+sysUser.toString());

            /****
             * 此处需增加密码修改时间判断
             *
             *
             *
             */

            // 设置session信息
            request.getSession().setAttribute(SessionParams.CurrentUserName, username);
            request.getSession().setAttribute(SessionParams.CurrentUserPas, md5Password);


//			SysDepartMentsDAO deptDao = new SysDepartMentsDAO();
//			SysDepartMents sysDept;

//			sysDept = (SysDepartMents) deptDao.queryById(new SysDepartMents(), sysUser.getDepartmentID());

            SysUserRoleDAO sysRolesDao = new SysUserRoleDAO();
            SysUserRole sysUserRole = sysRolesDao.getByUserId(sysUser.getUserid());

            SysRolesDAO sysRolesDAO = new SysRolesDAO();
            SysRoles sysRoles = new SysRoles();

            request.getSession().setAttribute(SessionParams.CurrentUserID, sysUser.getUserid());// 设置用户Id

            request.getSession().setAttribute("loginSysUser", sysUser);
//			request.getSession().setAttribute("loginSysDept", sysDept);

            // 登录成功，将登录信息记录到sessions中，并保存到sys_sessions表中
            String sessionId = request.getSession().getId();

            SysSessions sysSession = new SysSessions();
            SysSessionsDAO sysSessionDao = new SysSessionsDAO();
            sysSessionDao.deleteById(sysSession, sessionId);

            sysSession.setSessionId(sessionId);
            sysSession.setUserId(sysUser.getUserid());
            sysSession.setLoginTime(CommonFunc.unixTime());
            sysSession.setLoginIp(CommonFunc.getRemortIP(request));
            sysSession.setLastTime(CommonFunc.unixTime());
            sysSession.setLastUrl(request.getRequestURI().toString());
            sysSession.setUserName(sysUser.getUsername());
            sysSessionDao.saveNew(sysSession);

            return CommonFunc.makeSussResult(mapResultData);
        } catch (Exception e) {
            e.printStackTrace();

            //登陆错误，请联系管理员
            return CommonFunc.makeErrResult("300009");
        }

    }

    /*
     * 登录认证:东方电子集成环境登录
     * 参数:_server_iv_,_client_sign_,_client_sign_
     * err_code:错误代码
     * 返回数据:错误代码
     */
    @PostMapping(value="/Login/CheckDfdz")
    public Map<String, Object> LoginCheckDfdz(@RequestParam Map<String,Object> params) throws Exception {
        Map<String, Object> mapPost = CommonFunc.checkServerPost(params, sysConfig);
        String redirectUrl=CommonFunc.getNotNullString(params.get("redirect_url"));
        logger.info("市局登录，跳转url: "+redirectUrl);

        if (mapPost.get("result").equals("0")) {
            //失败
            return mapPost;
        }

        Map<String,String> mapVar= (Map<String, String>) mapPost.get("vars");
        String tokenDfdz=CommonFunc.getNotNullString(mapVar.get("token_dfdz"));
        String[] arrToken=tokenDfdz.split("\\.",3);

        logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheckDfdz:tokenDfdz:"+tokenDfdz);

        if (arrToken.length<3){
            logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheckDfdz:300011");
            return CommonFunc.makeErrResult("300011");
        }

        String tokenHeader=arrToken[0];
        String tokenContent=arrToken[1];
        String tokenSign=arrToken[2];

        JSONObject jsonHeader= (JSONObject) JSON.parse(Base64.decodeBase64(tokenHeader));
        JSONObject jsonContent= (JSONObject) JSON.parse(Base64.decodeBase64(tokenContent));

        logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheckDfdz:jsonHeader:"+jsonHeader);

        logger.info(CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()))+":LoginCheckDfdz:jsonContent:"+jsonContent);

        if (jsonContent==null){
            return CommonFunc.makeErrResult("300012");
        }

        //此处需判断sign进行校验,略

        String usernameDfdz=jsonContent.getString("user");
        String mobileDfdz=jsonContent.getString("phone");
        logger.info("userName："+usernameDfdz);
        logger.info("dfdz mobileDfdz ："+mobileDfdz);

        if (StringUtils.isEmpty(usernameDfdz)){
            return CommonFunc.makeErrResult("300013");
        }

        SysUsersDAO userDao = new SysUsersDAO();
        SysUsers sysUser = userDao.getByUsernameDfdz(usernameDfdz,mobileDfdz);

        Map<String,Object> result= LoginUserCheck(sysUser);

        if (result.get("result").equals("0")){
            //登录失败
            //自动增加用户表
            try(JdbcDao jdbcDao=new JdbcDao()) {
                String newPassword = MD5.GetMD5Code("abc@123!");
                String sqlUser = String.format("insert into sys_users (userid,username,userpassword,DEPARTMENTID,actived,username_dfdz) " +
                                " values ('%s','%s','%s','%s','1','%s')",
                        jdbcDao.getSeqNextVal(), usernameDfdz, newPassword, "34645746", usernameDfdz);
                //jdbcDao.execute(sqlUser);
                jdbcDao.close();

                sysUser = userDao.getByUsernameDfdz(usernameDfdz,mobileDfdz);
                result = LoginUserCheck(sysUser);
                String baseUrl = RequestUtils.getBaseUrl(request);
                if (result.get("result").equals("0")) {
                    response.sendRedirect(baseUrl + sysConfig.getClientUrl() + "/login.html");
                    return null;
                }

                jdbcDao.close();
            }

        }

        //登录成功
        //记录东方电子TOKEN
        request.getSession().setAttribute("tokenDfdz", tokenDfdz);

        String baseUrl = RequestUtils.getBaseUrl(request);
        String to;
        if (StringUtils.isEmpty(redirectUrl)) {
            to = baseUrl + sysConfig.getClientUrl() + "/index.html";
        }else{
            to = baseUrl+sysConfig.getClientUrl() + "/"+redirectUrl;
        }
        logger.info("跳转至路径：{}"+to);
        response.sendRedirect(to);

        return null;
    }

    /*
     * 登录认证:东方电子集成环境登录
     * 参数:_server_iv_,_client_sign_,_client_sign_
     * err_code:错误代码
     * 返回数据:错误代码
     */
    private Map<String, Object> LoginUserCheck(SysUsers sysUser) {

        Map<String,Object> mapResultData=new HashMap<String,Object>();

        try{
            if (sysUser == null) {
                //用户名或密码错误
                return CommonFunc.makeErrResult("300002");
            }

            if(sysUser.getActived().equals("0")){
                //该用户已被禁用，请联系管理员
                return CommonFunc.makeErrResult("300003");
            }


            /****
             * 此处需增加密码修改时间判断
             *
             *
             *
             */

            // 设置session信息
            request.getSession().setAttribute(SessionParams.CurrentUserName, sysUser.getUsername());
            request.getSession().setAttribute(SessionParams.CurrentUserPas, sysUser.getUserpassword());


//			SysDepartMentsDAO deptDao = new SysDepartMentsDAO();
//			SysDepartMents sysDept;

//			sysDept = (SysDepartMents) deptDao.queryById(new SysDepartMents(), sysUser.getDepartmentID());

            SysUserRoleDAO sysRolesDao = new SysUserRoleDAO();
            SysUserRole sysUserRole = sysRolesDao.getByUserId(sysUser.getUserid());

            SysRolesDAO sysRolesDAO = new SysRolesDAO();
            SysRoles sysRoles = new SysRoles();
            sysRoles = sysRolesDAO.getByRoleId(sysUser.getRoleid());

            request.getSession().setAttribute(SessionParams.CurrentUserID, sysUser.getUserid());// 设置用户Id
            request.getSession().setAttribute("userRoleId", sysUser.getRoleid());// 设置用户角色Id

            request.getSession().setAttribute("loginSysUser", sysUser);
//			request.getSession().setAttribute("loginSysDept", sysDept);

            // 登录成功，将登录信息记录到sessions中，并保存到sys_sessions表中
            String sessionId = request.getSession().getId();

            SysSessions sysSession = new SysSessions();
            SysSessionsDAO sysSessionDao = new SysSessionsDAO();
            sysSessionDao.deleteById(sysSession, sessionId);

            sysSession.setSessionId(sessionId);
            sysSession.setUserId(sysUser.getUserid());
            sysSession.setLoginTime(CommonFunc.unixTime());
            sysSession.setLoginIp(CommonFunc.getRemortIP(request));
            sysSession.setLastTime(CommonFunc.unixTime());
            sysSession.setLastUrl(request.getRequestURI().toString());
            sysSession.setUserName(sysUser.getUsername());

            sysSessionDao.saveNew(sysSession);

            return CommonFunc.makeSussResult(mapResultData);
        } catch (Exception e) {
            e.printStackTrace();

            //登陆错误，请联系管理员
            return CommonFunc.makeErrResult("300009");
        }
    }

    @GetMapping("/getClientInfo")
    public String getClientInfo(HttpServletRequest request) {
        // 获取基础URL
        String baseUrl = RequestUtils.getBaseUrl(request);

        // 示例结果可能是：
        //   http://localhost:8181/
        //   或 https://88.888.88.88:8888/

        return "Base URL: " + baseUrl;
    }
}
