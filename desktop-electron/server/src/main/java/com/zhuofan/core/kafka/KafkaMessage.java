package com.zhuofan.core.kafka;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.util.HttpUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;

public class KafkaMessage {

    public static void main(String[] args) throws Exception {

        KafkaMessage kafkaMessage=new KafkaMessage();
        kafkaMessage.getPowergridAnalysis("2025-11-28",1);
        kafkaMessage.getPowergridAnalysis("2025-11-28",2);
        kafkaMessage.getPowergridAnalysis("2025-11-28",3);
        kafkaMessage.getPowergridAnalysis("2025-11-28",4);
    }

    private String getFzlInfo(JSONArray jsonFzl,String sType,String num,String str){
        String result="";
        for (int i=0;i<jsonFzl.size();i++){
            JSONObject jsonItem=(JSONObject) jsonFzl.get(i);
            String item = "";
            if(num.equals("1")){
                item=String.format("（%s） %skV%s%s"+str+"%s%%;",
                        String.valueOf(i+1),
                        jsonItem.getString("DYDJ_SRC").trim(),
                        jsonItem.getString("NAME").trim(),
                        jsonItem.getString("EQUIPMENT").replace("10_设备运行负载率",""),
                        String.format("%.2f", Double.valueOf(jsonItem.getString("FZL_MAX"+sType))));
            }else{
                item=String.format("（%s） %skV%s"+str+"%s%%;",
                        String.valueOf(i+1),
                        jsonItem.getString("DYDJ_SRC").trim(),
                        //jsonItem.getString("NAME"),
                        jsonItem.getString("EQUIPMENT").replace("10_设备运行负载率",""),
                        String.format("%.2f", Double.valueOf(jsonItem.getString("FZL_MAX"+sType))));
            }


            result=result+item+"\n";
        }
        if(result != null && result.length() >= 2){
            result = result.substring(0, result.length() - 2) .replace("(长期)","")+ "。";
        }
        CommonLogger commonLogger=CommonLogger.getInstance();
        commonLogger.info("Kafkamessage,result：" + result);
        return result;
    }

    //type:1,2,3,4:
    public String getPowergridAnalysis(String ymd,int type) throws Exception{
        DecimalFormat df = new DecimalFormat("#.##");
        CommonLogger commonLogger=CommonLogger.getInstance();

        commonLogger.info(String.format("%s  getPowergridAnalysis",
                CommonFunc.unix2Date(CommonFunc.unixTimeStr())));

        String url="http://localhost:"+CommonFunc.getConfigByKey("server.port")+
                CommonFunc.getConfigByKey("server.servlet.context-path")+"/Zwdd/PowergridAnalysis?ymd="+ymd;

        commonLogger.info(String.format("%s  getPowergridAnalysis url:%s",
                CommonFunc.unix2Date(CommonFunc.unixTimeStr()),url));



        String content=HttpUtil.doGet(url,new HashMap<>());

        commonLogger.info(String.format("%s  getPowergridAnalysis content:%s",
                CommonFunc.unix2Date(CommonFunc.unixTimeStr()),content));

        JSONObject json=JSONObject.parseObject(content);
        if (json==null){
            commonLogger.info("getPowergridAnalysis json is null,return null");
            return null;
        }

        if (!json.containsKey("result")){
            commonLogger.info("getPowergridAnalysis json  containsKey result not exists,return null");
            return null;
        }

        if (!"1".equals(json.getString("result"))){
            commonLogger.info("getPowergridAnalysis json.result!=1,return null");
            return null;
        }

        JSONObject jsonData=json.getJSONObject("data");
        if (jsonData==null){
            commonLogger.info("getPowergridAnalysis json.data is null,return null");
            return null;
        }

        String sType=String.valueOf(type);
        String month=jsonData.getString("month");
        String day=jsonData.getString("day");
        String ymdPrev=jsonData.getString("ymdPrev");
        String ymdPrevFri=jsonData.getString("ymdPrevFri");
        String today=jsonData.getString("today");
        String template="";
        String hourStr=(type==1)?"上午":(type==2?"下午":"晚上");

        //三峰
        if (type==1 || type==2 || type==3){
            String flagElinkCustom = jsonData.getString("flagElinkCustom"+String.valueOf(type));

            if ("1".equals(flagElinkCustom)) {
                //使用修改后的发送
                JSONObject jsonCustom=JSONObject.parseObject(jsonData.getString("jsonElinkCustom"+String.valueOf(type)).
                        replace("<br/>","\n\r"));
                String fieldTmp=type==1?"am":type==2?"pm":"night";
                template=String.format("【%s月%s日东莞电网%s峰期负荷情况】\n" +
                                "%s\n%s\n%s\n%s\n%s\n%s\n   ",
                        month,day,hourStr,
                        jsonCustom.getString("txt_"+fieldTmp+"_1").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_"+fieldTmp+"_2").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_"+fieldTmp+"_3").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_"+fieldTmp+"_4").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_"+fieldTmp+"_5").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_"+fieldTmp+"_6").replace("<br>","\r\n")
                );
            }else{
                JSONObject map092=jsonData.getJSONObject("map092");
                JSONObject mapDgwg=jsonData.getJSONObject("mapDgwg");
                String dgwgDiffStr=jsonData.getString("dgwgDiff"+sType+"Str");
                JSONArray fzl500=jsonData.getJSONObject("mapFzl").getJSONArray("fzl_500_"+sType);
                JSONArray fzl220=jsonData.getJSONObject("mapFzl").getJSONArray("fzl_220_"+sType);
                JSONArray fzl110=jsonData.getJSONObject("mapFzl").getJSONArray("fzl_110_"+sType);
                JSONArray fzl10=jsonData.getJSONObject("mapFzl").getJSONArray("fzl_10_"+sType);
                JSONObject jsonFhdl=jsonData.getJSONObject("mapFhdl");
                String xtsbxl="";
                if (jsonFhdl.containsKey("xtsbxl")){
                    xtsbxl=jsonFhdl.getString("xtsbxl");
                }

                double maxval=CommonFunc.strToDouble(map092.getString("MAXVAL"+sType));
                double maxval0,maxval1,maxval2,maxval3;
                maxval0=CommonFunc.strToDouble(map092.getString("MAXVAL0"));
                maxval1=CommonFunc.strToDouble(map092.getString("MAXVAL1"));
                maxval2=CommonFunc.strToDouble(map092.getString("MAXVAL2"));
                maxval3=CommonFunc.strToDouble(map092.getString("MAXVAL3"));

                String maxvalStr="";
                if (type==1){
                    maxvalStr=jsonData.getString("maxValAm");
                }else if (type==2){
                    maxvalStr=jsonData.getString("maxValPm");
                }else if (type==3){
                    maxvalStr=jsonData.getString("maxValNt");
                }

                template=String.format("【%s月%s日东莞电网%s峰期负荷情况】\n"+
                        "一、今天%s峰期东莞电网系统最高负荷%s万千瓦"+
                        "（较%s%s峰期%s约%s万千瓦）。\n"+
                        "二、500kV输变电设备断面超95％以上有%s个%s\n%s"+
                        "三、220kV输变电设备断面超95％以上有%s个%s\n%s"+
                        "四、110kV输变电设备负载率超95％以上有%s个%s\n%s"+
                        "五、主变变低进线柜负载率超95％以上有%s个%s\n%s",
                        month,day,hourStr,
                        hourStr,maxvalStr,
                        today,hourStr,dgwgDiffStr,jsonData.getString("dgwgDiff"+sType),
                        fzl500.size(),(fzl500.size()==0)?"。":"：",getFzlInfo(fzl500,sType,"","断面"),
                        fzl220.size(),(fzl220.size()==0)?"。":"：",getFzlInfo(fzl220,sType,"","断面"),
                        fzl110.size(),(fzl110.size()==0)?"。":"：",getFzlInfo(fzl110,sType,"",""),
                        fzl10.size(),(fzl10.size()==0)?"。":"：",getFzlInfo(fzl10,sType,"1","变低")
                );
                    //jsonData.getString("xtsbxlCount"),xtsbxl);
            }
        }else {
            //日生产信息
            String flagElinkCustom = jsonData.getString("flagElinkCustom");
            String monthPrev = jsonData.getString("monthPrev");
            String dayPrev = jsonData.getString("dayPrev");
            String monthLastFri = jsonData.getString("monthLastFri");
            String dayLastFri = jsonData.getString("dayLastFri");

            commonLogger.info("getPowergridAnalysis start get message for type 4");

            if ("1".equals(flagElinkCustom)) {
                //使用修改后的发送
                JSONObject jsonCustom=JSONObject.parseObject(jsonData.getString("jsonElinkCustom").replace("<br/>","\n\r"));
                template=String.format("【%s月%s日东莞电网运行简报】\n" +
                                "%s\n%s\n%s\n%s\n%s\n",
                        monthPrev, dayPrev,
                        jsonCustom.getString("txt_prevday_1").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_prevday_2").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_prevday_3").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_prevday_4").replace("<br>","\r\n"),
                        jsonCustom.getString("txt_prevday_5").replace("<br>","\r\n"));
            } else {

                JSONObject mapPrev092 = jsonData.getJSONObject("mapPrev092");
                JSONObject mapPrev714 = jsonData.getJSONObject("mapPrev714");
                JSONObject mapFhdlPrev = jsonData.getJSONObject("mapFhdlPrev");
                JSONObject mapTotalGzLastFri = jsonData.getJSONObject("mapTotalGzLastFri");
                JSONObject mapTotalGzPrev = jsonData.getJSONObject("mapTotalGzPrev");
                JSONObject mapRisk = jsonData.getJSONObject("mapRisk");
                JSONArray listRisk = jsonData.getJSONArray("listRisk");
                JSONObject mapFhdl = jsonData.getJSONObject("mapFhdl");
                String strRisk = "";

                for (int i = 0; i < listRisk.size(); i++) {
                    JSONObject jsonItem = (JSONObject) listRisk.get(i);
                    strRisk = strRisk + String.format("（%s）%s-%s，（风险任务书编号：%s)。管控要求：\n",
                            String.valueOf(i + 1), jsonItem.getString("TIME_FROM"),
                            jsonItem.getString("TIME_TO"),
                            jsonItem.getString("RISK_NO"),
                            jsonItem.getString("CONTENT"));
                }

                DecimalFormat decimalFormat = new DecimalFormat("0.00");

                template = String.format("【%s月%s日东莞电网运行简报】\n"
                                //+ "1、%s月%s日统调最高负荷达%sMW，与去年同期同比增长%s;系统最高负荷达%sMW，与去年同期同比增长%s。（系统负荷=统调负荷+低压分布式光伏）。\n"
                                + "1、%s月%s日统调最高负荷%sMW，与去年同期同比增长%s。"
                                +"系统最高负荷%sMW，与去年同期同比增长%s。 （系统负荷=统调负荷+低压分布式光伏）。\n"
                                + "2、%s月%s日供电量%s亿度，本月累计%s亿度，"
                                + "同比%s年增长%s，较%s年同期增长%s，本年累计%s亿度，"
                                + "同比%s年增长%s，较%s年同期增长%s，。\n"
                                + "3、主网设备跳闸情况：无。\n\n"

                                + "%s月%s日东莞电网最高负荷%sMW，其中220kV及以上输变电设备负载率达控制值90%%以上有%s个； "
                                + "110kV输变电设备负载率达控制值90%%以上有%s个； 主变10kV侧重过载设备有%s个。 "
                                + "具体内容及工作要求详见《东莞电网主网设备重载预警清单》，该清单已同时在企业信息门户网站公告板中公布。\n\n"

                                + "%s月%s日东莞电网最高负荷%sMW，其中220kV及以上输变电设备负载率达控制值90%%以上有%s个； "
                                + "110kV输变电设备负载率达控制值90%%以上有%s个； 主变10kV侧重过载设备有%s个，"
                                + " 具体重载设备见eLink重载设备管控群《东莞电网主网设备重载预警清单》，请变电一所、变电二所、变电三所、输电所、东北区局、"
                                + "长安局、城区局、虎门局、南区局、东区局、东南区局组织制定重载设备日运维计划，落实做好巡视、测温以及荷控制工作，"
                                + "发现设备异常及时通知当值调度员和做好缺陷管理工作。\n\n"

                                //+ "%s月%s日网供指标：早峰%sMW，午峰%sMW，晚峰%sMW，错峰预警信号为%s。\n"
                                + "%s月%s日，东莞电网检修风险预警：地区一级事件风险%s项，地区二级事件风险%s项，地区三级事件风险%s项，地区四级事件风险%s项。\n"
                                + "%s"
                                + "%s月%s日网供指标：早峰%sMW，午峰%sMW，晚峰%sMW，错峰预警信号为%s。",
                        monthPrev, dayPrev, monthPrev, dayPrev,
                        decimalFormat.format(CommonFunc.strToDouble(mapPrev714.getString("MAXVAL"))),
                        CommonFunc.getNotNullString(jsonData.getString("diffTqdb714")),
                        decimalFormat.format(CommonFunc.strToDouble(mapPrev092.getString("MAXVAL"))),
//                        CommonFunc.getNotNullString(jsonData.getString("diffPrev092")),
//                        CommonFunc.getNotNullString(jsonData.getString("Tbrq")),
                        CommonFunc.getNotNullString(jsonData.getString("diffTqdb092")),
//                        CommonFunc.getNotNullString(jsonData.getString("xtfh")),
//                        CommonFunc.getNotNullString(jsonData.getString("xtdb")),
                        monthPrev, dayPrev,
                        mapFhdlPrev.containsKey("DAY_DL") ? decimalFormat.format(CommonFunc.strToDouble(mapFhdlPrev.getString("DAY_DL")) / 100000) : "",
                        mapFhdlPrev.containsKey("MONTH_OCS_DL") ? decimalFormat.format(CommonFunc.strToDouble(mapFhdlPrev.getString("MONTH_OCS_DL")) / 100000) : "",
                        mapFhdlPrev.containsKey("YEARNO") ? mapFhdlPrev.getInteger("YEARNO") - 1 : "",
                        mapFhdlPrev.containsKey("MONTH_ZZL_PREV") ? mapFhdlPrev.getString("MONTH_ZZL_PREV") : "",
                        mapFhdlPrev.containsKey("YEARNO") ? mapFhdlPrev.getInteger("YEARNO") - 2 : "",
                        mapFhdlPrev.containsKey("MONTH_ZZL_PREV2") ? mapFhdlPrev.getString("MONTH_ZZL_PREV2") : "",
                        mapFhdlPrev.containsKey("YEAR_OCS_DL") ? decimalFormat.format(CommonFunc.strToDouble(mapFhdlPrev.getString("YEAR_OCS_DL")) / 100000) : "",
                        mapFhdlPrev.containsKey("YEARNO") ? mapFhdlPrev.getInteger("YEARNO") - 1 : "",
                        mapFhdlPrev.containsKey("YEAR_ZZL_PREV") ? mapFhdlPrev.getString("YEAR_ZZL_PREV") : "",
                        mapFhdlPrev.containsKey("YEARNO") ? mapFhdlPrev.getInteger("YEARNO") - 2 : "",
                        mapFhdlPrev.containsKey("YEAR_ZZL_PREV2") ? mapFhdlPrev.getString("YEAR_ZZL_PREV2") : "",

                        monthLastFri, dayLastFri,
                        df.format(new BigDecimal(jsonData.getJSONObject("mapLastFri092").getString("MAXVAL"))),
                        CommonFunc.strToInt(mapTotalGzLastFri.getString("total_220kV及以上")),
                        CommonFunc.strToInt(mapTotalGzLastFri.getString("total_110kV")),
                        CommonFunc.strToInt(mapTotalGzLastFri.getString("total_10kV")),

                        monthLastFri, dayLastFri,
                        df.format(new BigDecimal(jsonData.getJSONObject("mapLastFri092").getString("MAXVAL"))),
                        CommonFunc.strToInt(mapTotalGzLastFri.getString("total_220kV及以上")),
                        CommonFunc.strToInt(mapTotalGzLastFri.getString("total_110kV")),
                        CommonFunc.strToInt(mapTotalGzLastFri.getString("total_10kV")),

                        /*
                        monthPrev, dayPrev,
                        mapFhdlPrev.containsKey("WGZB1") ? mapFhdlPrev.getString("WGZB1") : "",
                        mapFhdlPrev.containsKey("WGZB2") ? mapFhdlPrev.getString("WGZB2") : "",
                        mapFhdlPrev.containsKey("WGZB3") ? mapFhdlPrev.getString("WGZB3") : "",
                        mapFhdlPrev.containsKey("CFXX") ? mapFhdlPrev.getString("CFXX") : "",
                        */

                        month, day,
                        mapRisk.getString("riskTotal1"),
                        mapRisk.getString("riskTotal2"),
                        mapRisk.getString("riskTotal3"),
                        mapRisk.getString("riskTotal4"),
                        strRisk,

                        month, day,
                        mapFhdl.getString("WGZB1"),
                        mapFhdl.getString("WGZB2"),
                        mapFhdl.getString("WGZB3"),
                        mapFhdl.getString("CFXX")
                );
            }
        }

        commonLogger.info("template:"+template);
        System.out.println(template);
        return template;
    }
}
