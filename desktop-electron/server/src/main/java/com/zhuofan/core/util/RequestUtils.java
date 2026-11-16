package com.zhuofan.core.util;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class RequestUtils {

    /**
     * 获取客户端原始请求的基础URL（协议://主机:端口）
     */
    public static String getBaseUrl(HttpServletRequest request) {
        // 1. 获取协议 (优先从代理头获取)
        String protocol = getFirstHeaderValue(request, "X-Forwarded-Proto");
        if (protocol == null) {
            protocol = request.getScheme(); // 默认使用请求的协议
        }

        // 2. 获取主机和端口 (优先从代理头获取)
        String hostWithPort = getFirstHeaderValue(request, "X-Forwarded-Host");
        if (hostWithPort == null) {
            // 若无代理头，使用请求的Host头（包含端口）
            hostWithPort = request.getHeader("Host");
            if (hostWithPort == null) {
                // 最终回退：服务器名 + 端口
                hostWithPort = request.getServerName() + ":" + request.getServerPort();
            }
        }

        // 3. 标准化主机（处理端口）
        hostWithPort = normalizeHostWithPort(hostWithPort, protocol);

        // 4. 组合基础URL
        String tmp=protocol + "://" + hostWithPort + "/";

        if ("http://192.3.1.92:80/".equals(tmp)){
            tmp="https://192.3.1.92/";
        }

        if ("http://10.150.191.204:80/".equals(tmp)){
            tmp="https://10.150.191.204/";
        }

        return tmp;
    }

    /**
     * 从可能包含多个值的头部中获取第一个值
     */
    private static String getFirstHeaderValue(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        if (headerValue != null && headerValue.contains(",")) {
            // 处理多层代理情况：取第一个值
            return headerValue.split(",")[0].trim();
        }
        return headerValue;
    }

    /**
     * 标准化主机+端口格式
     */
    private static String normalizeHostWithPort(String hostWithPort, String protocol) {
        // 确保包含端口
        if (!hostWithPort.contains(":")) {
            // 根据协议添加默认端口
            int defaultPort = "https".equalsIgnoreCase(protocol) ? 443 : 80;
            hostWithPort = hostWithPort + ":" + defaultPort;
        }


        return hostWithPort;
    }

    /**
     * 安全获取方法（验证代理是否可信）
     */
    public static String getTrustedBaseUrl(HttpServletRequest request, List<String> allowedProxies) {
        String clientIp = request.getRemoteAddr();
        if (!allowedProxies.contains(clientIp)) {
            // 非可信代理直接返回服务器自身信息
            return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
        }
        return getBaseUrl(request);
    }
}