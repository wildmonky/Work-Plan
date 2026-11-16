package com.zhuofan.core.dgomsapi;

import java.util.Base64;

import static com.zhuofan.core.dgomsapi.Constant.CLIENT_ID;
import static com.zhuofan.core.dgomsapi.Constant.CLIENT_SECRET;

public class Base64Util {
    public static String basicAuthorization() {
        System.out.println("CLIENT_ID:"+CLIENT_ID);
        System.out.println("CLIENT_SECRET:"+CLIENT_SECRET);
        return "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
    }

    public static String bearerAuthorization(String accessToken) {
        return "Bearer " + accessToken;
    }
}
