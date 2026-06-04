package com.ypj.train.gateway.filters;

import com.ypj.train.gateway.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoginMemberFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if(path.contains("/login")||path.contains("/send-code")||path.contains("admin")){
            log.info("不需要登录校验：{}",path);
            return chain.filter(exchange);
        }
        log.info("需要登录校验");
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if(token==null||token.isEmpty()){
            log.info("token为空，校验不通过");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        boolean validate = JwtUtil.validate(token);
        if(validate){
            log.info("token有效，校验通过");
            return chain.filter(exchange);
        }
        log.info("token失效，校验不通过");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
