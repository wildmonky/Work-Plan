package com.zhuofan.core.common;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Timer;

import com.zhuofan.core.datasource.DataSource;
import com.zhuofan.core.datasource.DataSource.checkConnService;
import com.zhuofan.core.config.DatasourceConfig;
import com.zhuofan.core.config.SysConfig;

import javax.xml.crypto.Data;

/**
 * 
 * @类名 系统公用变量对象
 * @作者 fxd
 * @日期 2015年12月16日
 * @描述
 *
 */

public class CommonObject {
	// 数据库配置对象
	private DatasourceConfig dbConf;
	// 系统配置对象
	private SysConfig sysConf;

	// 实例
	private static CommonObject instance = null;

	// 初始化实例
	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new CommonObject();
		}
	}

	// 获取实例
	public static CommonObject getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	// 私有化构造函数
	private CommonObject() {
		dbConf = new DatasourceConfig();
		sysConf = new SysConfig();

		// 获取项目相关信息

		try {
			InputStream in = DataSource.class.getClassLoader().getResourceAsStream("application-" + "@ACTIVE_PROFILE@" + ".properties");
			Properties prop = new Properties();
			prop.load(in);

			/**
			 * 获取系统配置相关部分
			 */
			sysConf.setTitle(new String(prop.getProperty("sys.title").getBytes("ISO-8859-1"), "UTF-8"));
			sysConf.setVersion(new String(prop.getProperty("sys.version").getBytes("ISO-8859-1"), "UTF-8"));
			sysConf.setTempfilePath(new String(prop.getProperty("sys.tempfile_path").getBytes("ISO-8859-1"), "UTF-8"));

			/**
			 * 获取数据库配置相关部分
			 */
			dbConf.setUrl(prop.getProperty("spring.datasource.dynamic.datasource.master.url"));
			dbConf.setUsername(prop.getProperty("spring.datasource.dynamic.datasource.master.username"));
			dbConf.setpassword(prop.getProperty("spring.datasource.dynamic.datasource.master.password"));
			dbConf.setAutoCommit(prop.getProperty("spring.datasource.auto-commit"));
			dbConf.setDriverClassName(prop.getProperty("spring.datasource.driver-class-name"));

			String sTmp;

			if(prop.containsKey("spring.datasource.min-conn")) {
				sTmp = prop.getProperty("spring.datasource.min-conn");
				if(CommonFunc.isNumeric(sTmp)) {
					dbConf.setMinConn(Integer.valueOf(sTmp));
				}
			}

			if(prop.containsKey("spring.datasource.max-conn")) {
				sTmp = prop.getProperty("spring.datasource.max-conn");
				if(CommonFunc.isNumeric(sTmp)) {
					dbConf.setMaxConn(Integer.valueOf(sTmp));
				}
			}

			if(prop.containsKey("spring.datasource.init-conn")) {
				sTmp = prop.getProperty("spring.datasource.init-conn");
				if(CommonFunc.isNumeric(sTmp)) {
					dbConf.setInitConn(Integer.valueOf(sTmp));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DatasourceConfig getDbConf() {
		return dbConf;
	}

	public SysConfig getSysConf() {
		return sysConf;
	}

    public static final String AT_ADD = "add";
    public static final String AT_EDIT = "edit";
	public static final String AT_DELETE = "delete";
    public static final String AT_VIEW = "view";
    public static final String AT_EXCEL = "excel";

	/**
	 * 配网馈线重过载等通知方式的可选值为四种：
	 * “文字不通知、电话不通知”
	 * “文字不通知、电话通知”
	 * “文字通知、电话不通知”
	 * “文字通知、电话通知
	*/
	public static final LinkedHashMap<String, String> MAP_PWGJ_NOTIFY_TYPE = new LinkedHashMap<String, String>() {
		{
			put("3", "文字通知、电话通知");
			put("0", "文字不通知、电话不通知");
			put("1", "文字不通知、电话通知");
			put("2", "文字通知、电话不通知");
		}
	};

	// sysTable模型中，inputType字段
	public static final LinkedHashMap<String, String> sysTable_InputType = new LinkedHashMap<String, String>() {
		{
			put("1", "单列模板");
			put("2", "两列模板");
			put("3", "三列模板");
			put("0", "多行模板");
		}
	};

	// sysTable模型中，inputType字段
	public static final LinkedHashMap<String, String> sysTable_IsSubTable = new LinkedHashMap<String, String>() {
		{
			put("0", "主表");
			put("1", "子表");
		}
	};

	// systableColumn模型中,inputType字段
	public static final LinkedHashMap<String, String> sysTableColumn_inputType = new LinkedHashMap<String, String>() {
		{
			put("1", "单行文本框");
			put("2", "多行文本框");
			put("3", "日期录入");
			put("4", "数组");
			put("5", "数组(关键字)");
			put("6", "数据查询");
			put("7","用户ID");
			put("8","用户名称");
			put("9","数字");
			put("10", "文件");
			put("11", "多选");
			put("12", "数据查询多选");
		}
	};
	
	// systableColumn模型中,columntype字段
	public static final LinkedHashMap<String, String> sysTableColumn_columnType = new LinkedHashMap<String, String>() {
		{
			put("VARCHAR2", "字符串");
			put("NUMBER", "数字");
			put("DATE", "日期");			
		}
	};
	
	//是,否
	public static final LinkedHashMap<String,String> mapYesNo=new LinkedHashMap<String,String>(){
		{
			put("1","是");
			put("0","否");
		}
	};

	//男,女
	public static final LinkedHashMap<String,String> mapSex=new LinkedHashMap<String,String>(){
		{
			put("男","男");
			put("女","女");
		}
	};

	//是,否
	public static final LinkedHashMap<String,String> mapAuditResult=new LinkedHashMap<String,String>(){
		{
			put("1","通过");
			put("0","不通过");
		}
	};

	//节点类型
	public static final LinkedHashMap<String,String> mapFlowNodeType=new LinkedHashMap<String,String>(){
		{
			put("10","顺序节点");
			put("20","分支节点");
			put("30","会签节点");
		}
	};

	//基础表类型
	public static final LinkedHashMap<String,String> mapJCBType=new LinkedHashMap<String,String>(){
		{
			put("10","断面负荷");
			put("20","主变变低");
			put("40","110kV线路");
			put("30","220kV线路");
			put("50","500kV线路");
		}
	};

	//电压等级
	public static final LinkedHashMap<String,String> mapJCBVolLevel=new LinkedHashMap<String,String>(){
		{
			put("110kV","110kV");
			put("220kV","220kV");
			put("500kV","500kV");
			put("35kV","35kV");
			put("10kV","10kV");
		}
	};

	//调度编号图所属类型
	public static final LinkedHashMap<String,String> mapDdbhSslx=new LinkedHashMap<String,String>(){
		{
			put("0","厂站图");
			put("1","T接图");
			put("2","D类用户站");
			put("3","F类发电厂");
		}
	};

	//调度编号图图档状态
	public static final LinkedHashMap<String,String> mapDdbhTdzt=new LinkedHashMap<String,String>(){
		{
			put("1","替换旧图");
			put("2","新增图档");
		}
	};


	//项目信息管理是否结算
	public static final LinkedHashMap<String,String> mapSfjs=new LinkedHashMap<String,String>(){
		{
			put("否","否");
			put("是","是");
		}
	};

	//项目信息管理是否主项目
	public static final LinkedHashMap<String,String> mapSfzxm=new LinkedHashMap<String,String>(){
		{
			put("1","否");
			put("2","是");
		}
	};

	//项目信息管理项目性质
	public static final LinkedHashMap<String,String> mapXmxz=new LinkedHashMap<String,String>(){
		{
			put("电网建设","电网建设");
			put("主网建设","主网建设");
			put("配网建设","配网建设");
			put("小型基建","小型基建");
			put("技改","技改");
			put("修理","修理");
			put("科技","科技");
			put("信息化","信息化");
			put("其他","其他");
		}
	};


	//计划支付资金年份
	public static final LinkedHashMap<String,String> mapZfzjNf=new LinkedHashMap<String,String>(){
		{
			put("2017","2017");
			put("2018","2018");
			put("2019","2019");
			put("2020","2020");
			put("2021","2021");
			put("2022","2022");
			put("2023","2023");
			put("2024","2024");
			put("2025","2025");
			put("2026","2026");
			put("2027","2027");
			put("2028","2028");
			put("2029","2029");
			put("2030","2030");
		}
	};


	//运方基础表类型
	public static final LinkedHashMap<String,String> mapYfJCBType=new LinkedHashMap<String,String>(){
		{
			put("10","500kV变电站");
			put("20","220kV变电站");
			put("40","220kV变低");
			put("30","110kV变电站");
			put("50","10kV大电流柜");
			put("60","220kV线路");
			put("70","110kV线路");
			put("99","辅助计算点");

		}
	};

	//值班日志状态类型
	public static final LinkedHashMap<String,String> mapJjbStartType=new LinkedHashMap<String,String>(){
		{
			put("2","执行中");
			put("1","交班中");
			put("99","历史");
			put("0","已回退");
		}
	};

	public static final LinkedHashMap<String,String> mapXmlx=new LinkedHashMap<String,String>(){
		{
			put("技改","技改");
			put("修理","修理");
		}
	};

	public static final LinkedHashMap<String,String> mapAzdzlx=new LinkedHashMap<String,String>(){
		{
			put("1","运行定值单");
			put("2","投产前");
			put("3","临时定值单");
			put("4","调试定值单");
		}
	};

	public static final LinkedHashMap<String,String> mapdtOrZt=new LinkedHashMap<String,String>(){
		{
			put("0","县调");
			put("1","地调");
			put("2","中调");
			put("3","总调");
		}

	};

	public static final LinkedHashMap<String,String> mapChzZnfx=new LinkedHashMap<String,String>(){
		{
			put("当前位置与挂牌不符（异常）","当前位置与挂牌不符（异常）");
			put("带电作业已退出重合闸（正常）","带电作业已退出重合闸（正常）");
			put("当前位置与定值不符（异常）","当前位置与定值不符（异常）");
			put("带电作业无需退出重合闸（正常）","带电作业无需退出重合闸（正常）");
			put("未录入定值配置情况（异常）","未录入定值配置情况（异常）");
			put("正常","正常");
		}
	};

	public static final LinkedHashMap<String,String> mapXwzx=new LinkedHashMap<String,String>(){
		{
			put("板桥巡维","板桥巡维");
			put("大朗巡维","大朗巡维");
			put("万江巡维","万江巡维");
			put("长安巡维","长安巡维");
			put("莆心巡维","莆心巡维");
			put("元江巡维","元江巡维");
			put("跃立巡维","跃立巡维");
			put("景湖巡维","景湖巡维");
			put("葵湖巡维","葵湖巡维");
			put("步田巡维","步田巡维");
			put("盛丰巡维","盛丰巡维");
			put("立新巡维","立新巡维");
			put("北栅巡维","北栅巡维");
			put("水乡巡维","水乡巡维");
			put("松山湖巡维","松山湖巡维");
			put("莞城巡维","莞城巡维");
			put("东莞巡维","东莞巡维");
			put("横沥巡维","横沥巡维");
			put("裕元巡维","裕元巡维");
			put("掌洲巡维","掌洲巡维");
			put("南粤巡维","南粤巡维");
		}
	};
}
