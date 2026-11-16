package com.zhuofan.oms.config;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置 WebClient 的各类设置和日志记录
 */
@Slf4j
//@Configuration
public class WebClientConfigurer {

//    /**
//     * 全局 WebClient.Builder 配置
//     *
//     * @return 配置好的 WebClient.Builder
//     */
//    @Bean
//    public WebClient.Builder webClientBuilder() {
//        // 将 TCP 客户端配置应用到 HTTP 客户端
//        HttpClient httpClient = HttpClient.create()
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 连接超时
//                .doOnConnected(conn -> conn
//                        .addHandlerLast(new ReadTimeoutHandler(5)) // 读超时
//                        .addHandlerLast(new WriteTimeoutHandler(5)) // 写超时
//                );
//
//        // 配置 WebClient 构建器，包括 HTTP 连接器、交换策略、请求和响应日志
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .exchangeStrategies(ExchangeStrategies.builder()
//                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 内存限制
//                        .build())
////                .filter(logRequest())  // 请求日志
////                .filter(logResponse()); // 响应日志
//                .filter(logRequestAndResponse());
//    }
//
//    /**
//     * 针对 audio 的 WebClient 配置
//     * {@link com.zhuofan.oms.api.AudioApi}
//     *
//     * @param builder 全局配置的 WebClient.Builder
//     * @return 配置好的 WebClient 实例
//     */
//    @Bean(name = "audioWebClient")
//    public WebClient audioWebClient(WebClient.Builder builder) {
//        // 为 service1 配置特定的 base URL 和默认头部
//        return builder
//                .baseUrl("http://localhost:8181")
//                .defaultHeader("Content-Type", "application/json")
//                .build();
//    }
//
//    /**
//     * 请求日志拦截器
//     *
//     * @return 记录请求日志的 ExchangeFilterFunction
//     */
//    private ExchangeFilterFunction logRequest() {
//        // 拦截请求并打印请求方法和URL
//        return ExchangeFilterFunction.ofRequestProcessor(request -> {
//            log.info("远程调用接口: {}, {}", request.method(), request.url());
//            return Mono.just(request);
//        });
//    }
//
//    /**
//     * 响应日志拦截器
//     *
//     * @return 记录响应日志的 ExchangeFilterFunction
//     */
//    private ExchangeFilterFunction logResponse() {
//        // 拦截响应并打印响应状态码
//        return ExchangeFilterFunction.ofResponseProcessor(response -> {
//            HttpStatus httpStatus = response.statusCode();
//            if (httpStatus.is2xxSuccessful()) {
//                log.info("远程接口调用结果: {}, {}", "", httpStatus);
//            } else {
//                log.error("远程接口调用结果: {}", httpStatus);
//            }
//            return Mono.just(response);
//        });
//    }
//
//    private ExchangeFilterFunction logRequestAndResponse() {
//        // 请求日志
//        return (request, next) -> {
//            LocalDateTime startTime = LocalDateTime.now();
//            return next.exchange(request).doOnNext(response -> {
//                HttpStatus httpStatus = response.statusCode();
//                if (httpStatus.is2xxSuccessful()) {
//                    log.info("远程接口调用: {}, {}, 耗时: {} ms, 结果: {}", request.method(), request.url(), Duration.between(startTime, LocalDateTime.now()).toMillis(), httpStatus);
//                } else {
//                    log.error("远程接口调用: {}, {}, 耗时: {} ms, 结果: {}", request.method(), request.url(), Duration.between(startTime, LocalDateTime.now()).toMillis(), httpStatus);
//                }
//            });
//        };
//    }
}
