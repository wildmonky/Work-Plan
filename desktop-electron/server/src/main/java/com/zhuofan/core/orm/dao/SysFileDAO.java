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
import com.zhuofan.core.orm.model.SysFile;
import com.zhuofan.core.orm.model.SysModules;
import com.zhuofan.core.orm.model.SysUsers;

/**
 * BaseOrmSupport 用户角色表DAO
 * 
 * @author sunny
 * @Date 2017-07-20
 * 
 */
public class SysFileDAO extends BaseOrmSupport<SysFile> {

	public ArrayList<SysFile> getListByGxId(String tableName, String gxId) {

		String sql = "select * from SYS_FILE where gx_id='" + tableName + "_"
				+ gxId + "'";

		ArrayList<SysFile> sysFileList = null;
		try {
			sysFileList = (ArrayList<SysFile>) this.query(new SysFile(), sql);
		} catch (Exception e) {
			sysFileList = null;
		}
		return sysFileList;
	}
}
