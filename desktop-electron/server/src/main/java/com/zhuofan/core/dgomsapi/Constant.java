package com.zhuofan.core.dgomsapi;

import java.util.LinkedHashMap;

/**
 * 常用变量
 *
 * @author Zongwei
 * @date 2019/10/17 14:44
 */
public class Constant {

    //public static final String OMS_USERNAME = "林泽宏";
    //public static final String OMS_PASSWORD = "lzh@123!";
    public static final String OMS_USERNAME = "赖伟坚";
    public static final String OMS_PASSWORD = "Lwj@123!";
    public static final String OMS_LOCATION = "dg";

    /**
     * TODO 此处替换成自己的client_id
     */
    //public static final String CLIENT_ID = "st_client";
    public static final String CLIENT_ID = "dg_client";

    /**
     * TODO 此处替换成自己的client_secret
     */
    //public static final String CLIENT_SECRET = "st_client_9527";
    public static final String CLIENT_SECRET = "dg_client_9527";

    public static final String OAUTH2_SERVER_ENDPOINT = "https://10.150.106.50:8443/nari-oauth2-server/oauth/token";
    //public static final String OAUTH2_SERVER_ENDPOINT = "https://localhost:8443/oms/app/data/dgoms.json?";

    public static final String METABASE_ENDPOINT = "https://10.150.106.50:8443/nari-metabase-server/";
    //public static final String METABASE_ENDPOINT = "https://localhost:8443/oms/app/data/dgoms.json?";

    //所有表ID
    public final static LinkedHashMap<String, String> MAP_OMS_TABLE = new LinkedHashMap<String, String>(){{
        put("1328959792034652162", "T_RUNLOG_SHIFT_JJBXJ_DD:S_ID");
        put("1328962353307361281", "T_RUNLOG_LOG_JLXLTZ_SJDW_DD:ID");
        put("1328965215894675457", "T_RUNLOG_LOG_ZHTD_DD:ID");
        put("1328965447885824001", "T_RUNLOG_LOG_JLXLTZ_DD:ID");
        put("1328967100894584833", "T_RUNLOG_LOG_ZLTZ_DD:ID");
        put("1328967221933809665", "T_RUNLOG_LOG_JZTZ_DD:ID");
        put("1328967325335986177", "T_RUNLOG_LOG_BDSBTZ_DD:ID");
        put("1328967445720899585", "T_RUNLOG_LOG_PYDTZ_DD:ID");
        put("1328967578495787009", "T_RUNLOG_LOG_XTSGYC_DD:ID");
        put("1328967848772542466", "T_RUNLOG_LOG_SBQX_DD:ID");
        put("1328967957824446465", "T_RUNLOG_LOG_QTYC_DD:ID");
        put("1328968060501008386", "T_RUNLOG_LOG_YXFX_DD:ID");
        put("1328968178373533697", "T_RUNLOG_LOG_DZZX_DD:ID");
        put("1328968287375106049", "T_RUNLOG_LOG_YXZYSX_DD:ID");
        put("1328969197950115841", "T_RUNLOG_LOG_SBQD_DD:ID");
        put("1328969305240412162", "T_RUNLOG_LOG_XLYZHP_DD:ID");
        put("1328969418604060673", "T_RUNLOG_LOG_XLYZHP_JDQK_DD:ID");
        put("1328969532001263618", "T_RUNLOG_LOG_XLEZHP_DD:ID");
        put("1328969729758502914", "T_RUNLOG_LOG_XLDDZY_DD:ID");
        put("1328969916744769537", "T_RUNLOG_LOG_DDZYP_DD:ID");
        put("1328970020432158721", "T_RUNLOG_LOG_YKCZJL_DD:ID");
        put("1328970121321947137", "T_RUNLOG_LOG_CZSYTJGP_DD:ID");
        put("1328970238275919873", "T_RUNLOG_LOG_XKWTCZ_DD:ID");
        put("1328970371554123777", "T_RUNLOG_LOG_XL_DD:ID");
        put("1328970480736051202", "T_RUNLOG_LOG_STATION_DD:ID");
        put("1328970595659980802", "T_RUNLOG_LOG_JZ_DD:ID");
        put("1328970720864149506", "T_RUNLOG_LOG_SBTCTYXX_DD:ID");
        put("1328971085634379777", "T_RUNLOG_LOG_ECSBZT_DD:ID");
        put("1328971182371807233", "T_RUNLOG_LOG_CFXD_DD:ID");
        put("1328971273128157186", "T_RUNLOG_LOG_DCFDQXXF_DD:ID");
        put("1328971362118705153", "T_RUNLOG_LOG_ZLXTGZ_DD:ID");
        put("1328971465919340545", "T_RUNLOG_LOG_ZDHTXGZ_DD:ID");
        put("1328971569719975938", "T_RUNLOG_LOG_ZDHTXQXGLJL_DD:ID");
        put("1328971676322406402", "T_RUNLOG_LOG_YRZB_DD:ID");
        put("1328971779586170882", "T_RUNLOG_LOG_AVCYXKZ_DD:ID");
        put("1328971907822821377", "T_RUNLOG_LOG_YCXHCL_DD:ID");
        put("1328972006930030593", "T_RUNLOG_LOG_DDGZPJ_DD:ID");
        put("1328972101587083265", "T_RUNLOG_LOG_QTJS_DD:ID");
        put("1328972196722286594", "T_RUNLOG_LOG_SGYX_DD:ID");
        put("1328972293111586817", "T_RUNLOG_LOG_BGD_DD:ID");
        put("1328972391019225090", "T_RUNLOG_LOG_DMJGZ_DD:ID");
        put("1185124066830659586", "MWT_UD_JXSQD:ID");
        put("1187317480745050114", "MWT_UD_JXSQD_SB:ID");
        put("1292646594553929730", "MWT_UD_JXSQD_SQXK:ID");
        put("1292646508579086338", "MWT_UD_JXSQD_SQXK_YJ:ID");
        put("1292646246460252161", "MWT_UD_JXSQD_SQXK_EJ:ID");
        put("1191996167432097794", "MWT_UD_JXSQD_YCSBZT:ID");
        put("1191996223547691010", "MWT_UD_JXSQD_ECSBZT:ID");
        put("1194894877052854273", "MWT_UD_JXSQD_QFYJ:ID");
        put("1185115406732689410", "OPERATIONTICKET:INST_ID");
        put("1185116398941437953", "OPERATIONORDER:INST_ID");
        put("1194894228017864706", "OPERATIONTICKET_YFL:INST_ID");

        put("1415615661014638594", "INTERACTIVEMESSAGE:ID");
        //"1189849278293168129","CZP_JXD_RELATION:ID");
        put("1187317480745050114", "MWT_UD_JXSBLB:ID");
        put("1191996167432097794", "MWT_UD_YCSBZTYQ:ID");
        put("1194894437254914050", "PERMISSIONGD:INST_ID");
        put("1184405255467671553", "MWT_UD_YXFSBG:ID");
        put("1392764828447248385", "T_RUNLOG_LOG_JLXLTZ_CZHBXX_DD:ID");
        put("1194895034678992898", "MWT_DD_YXFSBG:ID");
        //put("1820656094486335490", "DICP_ALARMINFO:XHID");
    }};

    public final static LinkedHashMap<String, String> MAP_OMS_TABLE_FIVE_MINUTE = new LinkedHashMap<String, String>(){{
        put("1820656094486335490", "DICP_ALARMINFO:ID");
    }};
}
