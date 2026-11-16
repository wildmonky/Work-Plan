package com.zhuofan.oms.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AutoDifyApi {

    public static String txt2wavRunWorkflow(String url, String authorization,
                                                String content,String user,
                                                int connectionTimeout, int readTimeout) throws IOException {
        // 构建JSON请求体
        String jsonRequestBody = buildTxt2wavRequestBody(content,user);

        return commonRunWorkflow(url,authorization,jsonRequestBody,connectionTimeout,readTimeout,true);
    }

    private static String buildTxt2wavRequestBody(String content, String user) {
        JSONObject jsonInputs=new JSONObject();
        jsonInputs.put("query",content);

        return buildCommonRequestBody(jsonInputs,new JSONArray(),user);
    }

    /**
     * @Author fuxiaodong
     * @Description //
     * 语音通知功能，当方式单状态跳转至开工中且”控制原则与要求”字段带有”通知巡维中心”或”通知:
     * 输电所”的字眼就触发电话通知功能，语音的内容用户希望能用大模型来做分析”控制原则与要求”字段，
     * 通过分析出来的站去匹配巡维中心。（要把巡维中心的电话全部打一遍一直至有人接听，如果拨打5次都没人接听则不再拨打）
     * （字段名对应：ZWOCS.MWT_DD_YXFSBG.SGYX和ZWOCS.MWT_UD_YXFSBG.ZZHGLCS）
     * @Version: v1.8
     * @Date 10:33 2026/5/18
     * @Param [url, authorization, content, user, connectionTimeout, readTimeout]
     * @return java.lang.String
     * 2026-05-25：测试通过
     */
    public static String getsitenameRunWorkflow(String url, String authorization,
                                           String content,String user,
                                          int connectionTimeout, int readTimeout) throws IOException {
        // 构建JSON请求体
        String jsonRequestBody = buildGetSitenameRequestBody(content,user);

        return commonRunWorkflow(url,authorization,jsonRequestBody,connectionTimeout,readTimeout,true);
    }

    private static String buildGetSitenameRequestBody(String content, String user) {
        JSONObject jsonInputs=new JSONObject();
        jsonInputs.put("content",content);

        return buildCommonRequestBody(jsonInputs,new JSONArray(),user);
    }

    /**
     * @Author fuxiaodong
     * @Description //调用操作票审核馈线控制管理智能体接口，并返回执行结果
     * @Version: v1.8
     * @Date 23:41 2026/1/11
     * @Param [url, authorization, pdfOldUploadFileId, pdfNewUploadFileId, user, connectionTimeout, readTimeout]
     * @return java.lang.String
     */
    public static String kxkzglRunWorkflow(String url, String authorization,
                                          String bh,String user,
                                          int connectionTimeout, int readTimeout) throws IOException {
        // 构建JSON请求体
        String jsonRequestBody = buildKxkzglRequestBody(bh,user);

        return commonRunWorkflow(url,authorization,jsonRequestBody,connectionTimeout,readTimeout,true);
    }

    /**
     * @Author fuxiaodong
     * @Description //调用操作票审核智能体接口，并返回执行结果
     * @Version: v1.8
     * @Date 23:41 2026/1/11
     * @Param [url, authorization, pdfOldUploadFileId, pdfNewUploadFileId, user, connectionTimeout, readTimeout]
     * @return java.lang.String
     */
    public static String czpskRunWorkflow(String url, String authorization,
                                          String czpContent,String user,
                                          int connectionTimeout, int readTimeout) throws IOException {
        // 构建JSON请求体
        String jsonRequestBody = buildCzpskRequestBody(czpContent,user);

        return commonRunWorkflow(url,authorization,jsonRequestBody,connectionTimeout,readTimeout,false);
    }

    /**
     * @Author fuxiaodong
     * @Description //上传文件到智能体接口，并返回文件ID
     * @Version: v1.8
     * @Date 23:24 2026/1/11
     * @Param [url,token, user, filePath]
     * @return java.lang.String
     * curl -X POST 'http://192.3.1.117/v1/files/upload'
     * --header 'Authorization: Bearer app-ZukUWNzAM8wJZcKOLrd24W0O'
     * --form 'file=@/data/zhuofan/东园站-21.png;type=image/png'  --form 'user=abc-123'
     */
    public static String ddbhtUploadFile(String url, String token, String filePath, String user) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            // 设置Authorization头部
            httpPost.setHeader("Authorization", "Bearer " + token);

            // 构建multipart/form-data请求体
            File file = new File(filePath);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 添加文件部分
            builder.addBinaryBody(
                    "file",
                    file,
                    ContentType.create("image/png"),
                    file.getName()
            );

            // 添加user参数
            builder.addTextBody("user", user);
            builder.addTextBody("response_mode", "blocking");

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            CommonLogger commonLogger=CommonLogger.getInstance();

            // 执行请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                EntityUtils.consume(responseEntity);

                if (response.getStatusLine().getStatusCode() != 200 &&
                        response.getStatusLine().getStatusCode() != 201) {
                    commonLogger.info("AutoDifyApi.ddbhtUploadFile,exit,response status code:"+
                            response.getStatusLine().getStatusCode());
                    return "";
                    //throw new IOException("HTTP Error: " + response.getStatusLine().getStatusCode());
                }

                commonLogger.info("AutoDifyApi.ddbhtUploadFile,suss,response status code:"+
                        response.getStatusLine().getStatusCode());

                commonLogger.info("AutoDifyApi.ddbhtUploadFile,responseString:"+responseString);
                if (!CommonFunc.isValidJson(responseString)){
                    return "";
                }

                JSONObject jsonObject= JSONObject.parseObject(responseString);
                if (!jsonObject.containsKey("id")){
                    return "";
                }

                return jsonObject.getString("id");
            }
        }
    }

    /**
     * @Author fuxiaodong
     * @Description //调用智能体接口，并返回文件执行结果
     * @Version: v1.8
     * @Date 23:41 2026/1/11
     * @Param [url, authorization, pdfOldUploadFileId, pdfNewUploadFileId, user, connectionTimeout, readTimeout]
     * @return java.lang.String
     */
    public static String ddbhtRunWorkflow(String url, String authorization,
                                     String pdfOldUploadFileURL, String pdfNewUploadFileURL,
                                     String user, int connectionTimeout, int readTimeout) throws IOException {
        // 构建JSON请求体
        String jsonRequestBody = buildDdbhtRequestBody(pdfOldUploadFileURL, pdfNewUploadFileURL, user);

        return commonRunWorkflow(url,authorization,jsonRequestBody,connectionTimeout,readTimeout,true);
    }

    /**
     * @Author fuxiaodong
     * @Description //馈线控制管理智能体的请求参数生成
     * @Version: v1.8
     * @Date 14:20 2026/3/27
     * @Param
     * @return
     */
    private static String buildCommonRequestBody(JSONObject jsonInputs,JSONArray jsonFiles,String user) {
        JSONObject json=new JSONObject();

        json.put("inputs",jsonInputs);
        json.put("files",jsonFiles);
        json.put("response_mode","blocking");
        json.put("user",user);

        return json.toJSONString();
    }

    /**
     * @Author fuxiaodong
     * @Description //馈线控制管理智能体的请求参数生成
     * @Version: v1.8
     * @Date 14:20 2026/3/27
     * @Param
     * @return
     */
    private static String buildKxkzglRequestBody(String bh,String user) {
        JSONObject jsonInputs=new JSONObject();
        jsonInputs.put("bh",bh);

        return buildCommonRequestBody(jsonInputs,new JSONArray(),user);
    }

    /**
     * @Author fuxiaodong
     * @Description //调用调度编号图智能体的请求参数生成
     * @Version: v1.8
     * @Date 14:20 2026/3/27
     * @Param
     * @return
    */
    private static String buildDdbhtRequestBody(String pdfOldUploadFileId,
                                           String pdfNewUploadFileId,
                                           String user) {
        return String.format(
                "{\"inputs\":{\"pdf_old\":{\"type\":\"image\",\"transfer_method\":\"local_file\",\"url\":\"%s\"," +
                        "\"upload_file_id\":\"\"},\"pdf_new\":{\"type\":\"image\",\"transfer_method\":\"local_file\"," +
                        "\"url\":\"%s\",\"upload_file_id\":\"\"}},\"response_mode\":\"blocking\",\"user\":\"%s\"}",
                pdfOldUploadFileId, pdfNewUploadFileId, user
        );
    }

    /**
     * @Author fuxiaodong
     * @Description //生成操作票审核的请求代码
     * @Version: v1.8
     * @Date 10:20 2026/3/30
     * @Param [pdfOldUploadFileId, pdfNewUploadFileId, user]
     * @return java.lang.String
     */
    private static String buildCzpskRequestBody(String content,
                                                String user) {
        JSONObject jsonObject=JSONObject.parseObject(
                "{\"inputs\":{},\"response_mode\":\"streaming\",\"conversation_id\":\"\",\"files\":[]}");

        jsonObject.put("user",user);
        jsonObject.put("query",content);

        return jsonObject.toJSONString();
    }

    /**
     * @Author fuxiaodong
     * @Description //公用的调用智能体接口代码
     * @Version: v1.8
     * @Date 23:41 2026/1/11
     * @Param [url, authorization, pdfOldUploadFileId, pdfNewUploadFileId, user, connectionTimeout, readTimeout]
     * @return java.lang.String
     */
    private static String commonRunWorkflow(String url, String authorization,
            String jsonRequestBody, int connectionTimeout, int readTimeout,
                                            boolean flagResultIsJson) throws IOException {
        // 创建连接
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        // 设置超时时间
        connection.setConnectTimeout(connectionTimeout); // 连接超时
        connection.setReadTimeout(readTimeout);          // 读取超时

        // 设置请求头和方法
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + authorization);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 禁用缓存
        connection.setUseCaches(false);

        // 设置连接保持活跃
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Keep-Alive", "timeout=300"); // 300秒

        CommonLogger commonLogger=CommonLogger.getInstance();

        commonLogger.info("AutoDifyApi.commonRunWorkflow,jsonRequestBody:"+jsonRequestBody);
        commonLogger.info("AutoDifyApi.commonRunWorkflow,begin send data ");

        // 发送请求数据
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
        }catch(Exception e){
            commonLogger.info("AutoDifyApi.commonRunWorkflow,OutputStream getBytes error: "+e.getMessage());
            e.printStackTrace();
        }

        commonLogger.info("AutoDifyApi.commonRunWorkflow,begin get response code ");

        // 获取响应
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();

        // 读取响应
        try (InputStream inputStream = responseCode >= 200 && responseCode < 300 ?
                connection.getInputStream() : connection.getErrorStream();
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = br.read(buffer)) != -1) {
                response.append(buffer, 0, charsRead);
            }
        } finally {
            connection.disconnect();
        }

        commonLogger.info("AutoDifyApi.commonRunWorkflow,responseCode: " + responseCode + ", Response: " + response.toString());
        String responseString=response.toString();

        if (responseCode >= 200 && responseCode < 300) {
            //如果不需要返回JSON，则直接返回
            if (!flagResultIsJson){
                return responseString;
            }

            //需要返回JSON
            if (!CommonFunc.isValidJson(responseString)){
                commonLogger.info("AutoDifyApi.commonRunWorkflow,responseString is not json ");
                return "";
            }

            JSONObject jsonObject=JSONObject.parseObject(responseString);
            if (!jsonObject.containsKey("data")){
                return "";
            }

            JSONObject jsonOutput=jsonObject.getJSONObject("data").getJSONObject("outputs");
            if (jsonOutput.containsKey("text")){
                commonLogger.info("AutoDifyApi.commonRunWorkflow,text: "+jsonOutput.getString("text"));
                return jsonOutput.getString("text");
            }

            if (jsonOutput.containsKey("output")){
                commonLogger.info("AutoDifyApi.commonRunWorkflow,output: "+jsonOutput.getString("output"));
                return jsonOutput.getString("output").replace("'","''");
            }

            if (jsonOutput.containsKey("result_list")){
                JSONArray jsonArrList=jsonOutput.getJSONArray("result_list");
                String result="";
                for (int i=0;i<jsonArrList.size();i++){
                    result=jsonArrList.getJSONObject(i).getString("feeder")+":"+
                            jsonArrList.getJSONObject(i).getString("ratedAmpacity")+"\n";

                }
                return result;
            }

            //files：语音转文件专用
            if (jsonOutput.containsKey("files")){
                JSONArray jsonFiles=jsonOutput.getJSONArray("files");
                JSONArray jsonResult=new JSONArray();

                String prefix=CommonFunc.getConfigByKey("api.txt2war.downurl.prefix");
                for (int i=0;i<jsonFiles.size();i++){
                    JSONObject jsonFile=jsonFiles.getJSONObject(i);
                    jsonFile.put("filename",jsonFile.getString("filename"));
                    jsonFile.put("url",jsonFile.getString("url"));
                    jsonResult.add(jsonFile);
                }
                return jsonResult.toJSONString();
            }
        }

        return "";
    }

    public static void main(String[] args) {
        //以下代码测试通过
        /*
        String url = "http://localhost:8443/kemp/ex_web/oms/app/data/api-ddbht-upload.json";
        String token = "app-ZukUWNzAM8wJZcKOLrd24W0O";
        String filePath = "C:\\Users\\fxd\\Pictures\\Screenshots\\1.png";
        String userId = "abc-123";

        try {
            String response = uploadFile(url, token, filePath, userId);
            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        //以下代码测试通过
        String url = "http://localhost:8443/kemp/ex_web/oms/app/data/api-txt2wav-runflow.json";
        String authorization = "app-iECTEdgYeAUVcQ5WLEj9LeMs";
        String pdfOldUploadFileId = "eb4b8386-d90d-44af-a238-2309cfab73e6";
        String pdfNewUploadFileId = "497745ee-d116-4c8b-860e-d55036f61be9";
        String user = "admin@163.com";
        String bh="1.110kV裕鹿线、古樟乙线限流按710A控制，预测重载（85%）。";

        // 设置超时时间（5分钟 = 300000毫秒）
        int connectionTimeout = 30000; // 30秒连接超时
        int readTimeout = 300000;      // 5分钟读取超时

        try {
            String response = txt2wavRunWorkflow(url, authorization, bh, user, connectionTimeout, readTimeout);
            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
