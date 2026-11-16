package com.zhuofan.core.dgomsapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class HttpUtil {

    public static String doPost(String requestUrl, String requestBody, Map<String, String> requestHeaders) {
        try {
            //获取DefaultHttpClient请求
            HttpClient client =null;
            client=HttpsClient.getInstance();// HttpClientBuilder.create().build();

            final HttpPost httpPost = new HttpPost(requestUrl);
            //设置请求头

            requestHeaders.forEach(httpPost::addHeader);
            if (requestBody != null && !"".equalsIgnoreCase(requestBody.trim())) {
                httpPost.setEntity(new StringEntity(requestBody, "UTF-8"));
            }
            HttpResponse response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
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

}