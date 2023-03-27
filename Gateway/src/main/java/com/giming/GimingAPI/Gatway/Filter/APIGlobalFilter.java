package com.giming.GimingAPI.Gatway.Filter;

import cn.hutool.crypto.digest.MD5;
import com.giming.GimingAPI.Gatway.common.ErrorCode;
import com.giming.GimingAPI.Gatway.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 全局处理
 *
 * @author GimingRao
 * @data 2023/3/25 11:26
 */
@Component
@Slf4j
public class APIGlobalFilter implements GlobalFilter , Ordered {
    /**
     * 过滤器
     *
     * 1. 请求日志
     * 2. 鉴权
     * 3. 黑白名单
     * 4. 接口判断
     * 5. 计数
     * 6. 频繁访问限制
     * 7. 响应日志
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /*
        请求日志包含：
        1. 请求ip
        2. 请求url
        3. 请求方法
        4. 请求体
        5. 请求参数
        6. 请求头
         */
        log.info("请求ip：{}",exchange.getRequest().getRemoteAddress());
        log.info("请求url：{}",exchange.getRequest().getURI());
        log.info("请求方法：{}",exchange.getRequest().getMethod());
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        if (queryParams.size()!=0)
            log.info("请求参数：{}",exchange.getRequest().getQueryParams());
        log.info("accessKey：{}",exchange.getRequest().getHeaders().get("accessKey"));
        /*
        鉴权
         */
        //设置hash存放时间戳，设置定时器当时间戳过期则删除
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Set<Long> containedTime=new HashSet<>();
        long currentTime = Long.parseLong(headers.getFirst("currentTime"));
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Set<Long> expiredNonces = new HashSet<>();
                for (long time:containedTime){
                    if (!(System.currentTimeMillis()-currentTime<=60*1000))
                        expiredNonces.add(time);
                }
                containedTime.removeAll(expiredNonces);
            }
        }, 60 * 5);
        //得到加密前的accessKey与secretKey
        String signedsecretKey = headers.getFirst("signedsecretKey");
        String accessKey = headers.getFirst("accessKey");

        //判断accessKey
        if (accessKey==null||!accessKey.equals("Giming"))
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        //判断时间戳
        if (!(System.currentTimeMillis()-currentTime<=1000*60)||containedTime.contains(currentTime))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        //对secretKey解密
        String signedTruthSecretKey = MD5.create().digestHex("123456"+currentTime+accessKey);
        if (signedsecretKey==null||!signedsecretKey.equals(signedTruthSecretKey))
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        containedTime.add(currentTime);


        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    log.info("状态码: {}", exchange.getResponse().getStatusCode());
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
