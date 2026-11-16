package com.zhuofan.core.util;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.util.HttpsClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class HttpUtil {

    /**
     * 发送POST请求并获取响应
     * @param urlString 目标URL
     * @param jsonInputString JSON字符串
     * @return 响应内容
     * @throws IOException 如果请求失败
     */
    public static String sendHttpJsonPost(String urlString, String jsonInputString) throws IOException {
        // 创建URL对象
        CommonLogger commonLogger=CommonLogger.getInstance();

        commonLogger.info("sendHttpJsonPost :urlString:"+urlString);

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置请求方法为POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // 设置请求头
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json,*/*");

        // 设置连接超时和读取超时
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        // 发送JSON数据
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }catch (Exception e){
            e.printStackTrace();
        }

        // 读取响应
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            connection.disconnect();
        }

        return "";
    }

    /**
     * 获取响应状态码
     * @param urlString 目标URL
     * @param jsonInputString JSON字符串
     * @return 响应状态码
     * @throws IOException 如果请求失败
     */
    public static int getResponseCode(String urlString, String jsonInputString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return connection.getResponseCode();
        } finally {
            connection.disconnect();
        }
    }

    public static String doPost(String requestUrl, String requestBody, Map<String, String> requestHeaders) {
        CommonLogger commonLogger=CommonLogger.getInstance();
        try {
            //获取DefaultHttpClient请求
            HttpClient client =null;
            client=HttpsClient.getInstance();// HttpClientBuilder.create().build();


            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+" begin post requestUrl:"+requestUrl);
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+" begin post requestHeaders:"+requestHeaders.toString());
            final HttpPost httpPost = new HttpPost(requestUrl);
            //设置请求头

            requestHeaders.forEach(httpPost::addHeader);
            if (requestBody != null && !"".equalsIgnoreCase(requestBody.trim())) {
                httpPost.setEntity(new StringEntity(requestBody, "UTF-8"));
            }
            HttpResponse response = client.execute(httpPost);

            String result=EntityUtils.toString(response.getEntity(), "UTF-8");
            if (result.length()<1000) {
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + " end post ,response :" + result);
            }else{
                commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr()) + " end post ,response size:" + String.valueOf(result.length()));
            }

            return result;

        } catch (Exception e) {
            commonLogger.info(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+" post error:"+e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get请求
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static String doGet(String requestUrl, Map<String, String> requestHeaders) throws KeyManagementException, NoSuchAlgorithmException {
        try {
            //获取DefaultHttpClient请求
            HttpClient client = null;

            client=HttpsClient.getInstance();//HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(requestUrl);

            //设置请求头
            httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");
            requestHeaders.forEach(httpGet::addHeader);

            HttpResponse httpResponse = client.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, EntityUtils.getContentCharSet(entity));//"UTF-8"
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception{
        String tmp=sendHttpJsonPost("http://localhost:8443/kemp/ex_web/oms/omsserver/Api/Pwgj/StartBc",
                "{\"act\":\"push\",\"api_noun\":\"DG_YHTX_STARTBC\",\"data_type\":\"json\",\"data\":\"ewogICJyZXFpZCI6ICIyMDI1MDkwNTAwOCIsCiAgImNvbnRlbnRzIjogIui/meaYr+S4gOS4qua1i+ivleivremfs+aWh+acrCIsCiAgImNsZ251bWJlciI6ICI5MjUyIiwKICAibWVtYmVyaW5mbyI6ICIwMDE4OTI4NzA2ODA1IiwKICAibmVlZHJlcyI6IjEiLAogICJyZXNkYXkiOjcKfQo=\",\"msg_id\":\"1\",\"receiver\":7,\"sender\":7}");
        System.out.println(tmp);
    }
}
