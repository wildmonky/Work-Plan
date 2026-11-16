package com.zhuofan.oms.controller.sys;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.dao.SysModulesDAO;
import com.zhuofan.core.orm.model.SysModules;
import com.zhuofan.core.util.SessionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/*
 */
@RestController
@CrossOrigin
public class HomepageController  {
    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private HttpServletResponse response;

    @Autowired
    private Environment env;

    @Autowired
    private SysConfig sysConfig;

    /*
     * 主界面数据请求
     * 参数:无
     * 返回数据:
     *{
			result:0/1,
			data:list
		};
     */
    @RequestMapping(value = "/Homepage/MyHome",method = RequestMethod.GET)
    public Map<String, Object> MyHome(@RequestParam Map<String,String> params) throws  Exception {
        String sysTitle=sysConfig.getTitle();

        Map<String, Object> mapData = new HashMap<String, Object>();

        //1.检查是否登录
//        if (!CommonFunc.checkIsLogined(request)){
//            return CommonFunc.makeErrResult("300099");
//        }

        //2.获取用户信息
        try {
            String userId = Eval.getLoginUserId();
            SysModulesDAO sysmodulesDAO = new SysModulesDAO();
            ArrayList<SysModules> sysModulesList = sysmodulesDAO.getMenuByUserId(userId,"-1");
            if (sysModulesList==null){
                //return CommonFunc.makeErrResult("300098");
            }
            if (sysModulesList!=null) {
                for (int x = 0; x < sysModulesList.size(); x++) {
                    ArrayList<SysModules> subModulesList = sysmodulesDAO.getMenuByUserId(userId, sysModulesList.get(x).getModuleid());
                    sysModulesList.get(x).setExtraInfo(subModulesList);
                }
            }
            //取二级菜单
            mapData.put("sys_title",sysTitle);
            mapData.put("login_username",request.getSession().getAttribute(SessionParams.CurrentUserName).toString());
            mapData.put("modulelist",sysModulesList);
            return CommonFunc.makeSussResult(mapData);
        }catch (Exception e){
            e.printStackTrace();
            return CommonFunc.makeErrResult("999999");
        }
    }

}
