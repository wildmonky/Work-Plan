package com.zhuofan.core.dgomsapi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.datasource.JdbcDao;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DgomsApi {

    /*
    获取单个数据表接口
     */
    public static String getDgomsData(String username,String password,String location,String tableid,
                                      String tableName,String pkFieldName,String filterSTime,String filterETime)
            throws IOException, KeyManagementException, NoSuchAlgorithmException {
        CommonLogger commonLogger=CommonLogger.getInstance();

        ObjectMapper objectMapper = new ObjectMapper();

        // 访问OAuth2服务，获取access_token
        String tokenEndpoint = Constant.OAUTH2_SERVER_ENDPOINT + "?grant_type=password&username=" + username
                + "&password=" + password + "&scope=all";
        Map<String, String> tokenHeaderMap = new HashMap<>();

        String authoz = Base64Util.basicAuthorization();

        tokenHeaderMap.put("Authorization", authoz);
        tokenHeaderMap.put("Content-Type", "application/x-www-form-urlencoded");

        System.out.println("username:" + username);
        System.out.println("password:" + password);
        System.out.println("tableid:" + tableid);
        System.out.println("tokenEndpoint:" + tokenEndpoint);
        System.out.println("authoz:" + authoz);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":username:"+username);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":password:"+password);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":tableid:"+tableid);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":tokenEndpoint:"+tokenEndpoint);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":authoz:"+authoz);

        String tokenResponseContent = HttpUtil.doPost(tokenEndpoint, null, tokenHeaderMap);

        System.out.println("token response:" + tokenResponseContent);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":token response:"+authoz);

        Map<String, String> tokenResponseMap =null;

;       try {
            tokenResponseMap = objectMapper.readValue(tokenResponseContent,
                    new TypeReference<Map<String, String>>() {
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

        // 拿到access_token访问metabase服务，获取数据库的数据
        // OMS_PI6000.MWT_UD_JXSQD 1185120817087053826
        // DICP.OPERATIONTICKET 1185114776228134913
        // DICP.OPERATIONORDER 1185114776228134913

        String datasetEndpoint = "";

        if (filterSTime.length()==10 && filterETime.length()==10){
            //有时间
            datasetEndpoint=Constant.METABASE_ENDPOINT + "api/dataset/" + tableid + "/"
                    + location + "/history?filterSTime="+filterSTime+"%2000:00:00&filterETime="+filterETime+"%2000:00:00";
        }else{
            datasetEndpoint=Constant.METABASE_ENDPOINT + "api/dataset/" + tableid + "/" + location + '/';
        }
        System.out.println("datasetEndpoint:" + datasetEndpoint);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":datasetEndpoint:"+datasetEndpoint);

        Map<String, String> datasetHeaderMap = new HashMap<>();

        String auth ="";
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":tokenResponseMap:"+tokenResponseMap.toString());
        if (tokenResponseMap!=null){
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":tokenResponseMap.access_token:"+tokenResponseMap.get("access_token"));
            auth=Base64Util.bearerAuthorization(tokenResponseMap.get("access_token"));
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":auth:"+auth);
        }

        datasetHeaderMap.put("Authorization", auth);
        System.out.println("data Authorization:" + auth);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":data Authorization:"+auth);

        String datasetContent = HttpUtil.doGet(datasetEndpoint, datasetHeaderMap);
        System.out.println(datasetContent);
        commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+":"+tableid+":datasetContent:"+datasetContent);

        //保存到数据库"
        try(JdbcDao jdbcDao=new JdbcDao()) {

            //解析JSON
            JSONArray jsonData = null;
            try {
                jsonData = JSONObject.parseArray(datasetContent);
            } catch (Exception e) {
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":datasetContent parse to jsonData error");
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":" + e.getMessage().toString());
                e.printStackTrace();
            }

            if (jsonData == null) {
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":datasetContent is not jsons array");
                return datasetContent;
            }

            //解析返回的数据
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":datasetContent is  jsons array");
            for (int i = 0; i < jsonData.size(); i++) {
                JSONObject jsonItem = JSONObject.parseObject(jsonData.get(i).toString().trim());
                if (jsonItem == null) {
                    commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":第" + String.valueOf(i) + "行 is not jsons string");
                    continue;
                }

                String pkValue = "";
                String fieldNames = "";
                String updates = "", inserts = "";
                int updateIndex = 0, index = 0;

                ArrayList<String> listParam = new ArrayList<String>();
                ArrayList<String> listParamUpdate = new ArrayList<String>();
                for (String fieldName : jsonItem.keySet()) {
                    String fieldValue = jsonItem.get(fieldName).toString().trim();
                    fieldNames = (index == 0) ? fieldName : fieldNames + "," + fieldName;

                    if (fieldName.equals(pkFieldName)) {
                        pkValue = fieldValue;
                    } else {
                        updates = (updateIndex == 0) ? fieldName + "=?" : updates + "," + fieldName + "=?";
                        updateIndex++;
                        listParamUpdate.add(fieldValue);
                    }

                    inserts = (index == 0) ? "?" : inserts + "," + "?";
                    index++;
                    listParam.add(fieldValue);

                    if (fieldName.equals(pkFieldName)) {
                        pkValue = fieldValue;
                    }
                }

                listParamUpdate.addAll(listParam);
                if (StringUtils.isEmpty(pkValue)) {
                    commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":第" + String.valueOf(i) + "行 pkvalue is empty");
                    continue;
                }

                String sql = String.format("merge into DGZW_OMS.%s as t1 USING (select '%s' %s from dual ) as t2 on t1.%s=t2.%s WHEN MATCHED THEN  " +
                                "update  set %s WHEN NOT MATCHED THEN " +
                                "INSERT (%s) VALUES (%s)",
                        tableName, pkValue, pkFieldName, pkFieldName, pkFieldName, updates, fieldNames, inserts);

                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":getdgomsapi,execute sql:" + sql);

                jdbcDao.execute(sql, listParamUpdate);
            }
            jdbcDao.close();
        }

        /*
        try {
            // TODO access_token会有过期时间，
            //* 需要使用tokenResponseMap中的refresh_token换取新的access_token //
            //* 建议使用expires_in来判断是否过期，expires_in表示access_token的有效期，单位是秒
            String refreshTokenEndpoint = Constant.OAUTH2_SERVER_ENDPOINT +
                    "oauth/token?grant_type=refresh_token&refresh_token=" +
                    tokenResponseMap.get("refresh_token") + "&scope=all";

            System.out.println("refreshTokenEndpoint:" + refreshTokenEndpoint);
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":refreshTokenEndpoint:" + refreshTokenEndpoint);

            tokenResponseContent = HttpUtil.doPost(refreshTokenEndpoint, null,
                    tokenHeaderMap);
            tokenResponseMap = objectMapper.readValue(tokenResponseContent, new
                    TypeReference<Map<String, String>>() {
                    });

            System.out.println("update Token：" + tokenHeaderMap.get("access_token"));
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + ":update Token:" + tokenHeaderMap.get("access_token"));

        }catch (Exception e){
            e.printStackTrace();
        }
        */

        return datasetContent;
    }

    public static void main(String[] args)  throws Exception{
        DgomsApi.getDgomsData("林泽宏","lzh@123!","dg","1328959792034652162",
                "T_RUNLOG_SHIFT_JJBXJ_DD","S_ID","","");
    }
}
