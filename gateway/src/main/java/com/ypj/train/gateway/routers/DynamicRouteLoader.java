package com.ypj.train.gateway.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter writer;
    private final Set<String> routeIds = new HashSet<>();
    private final String dateName = "gateway-route.json";
    private final String group="DEFAULT_GROUP";

    @PostConstruct
    public void init() throws NacosException {
        String configAndSignListener = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dateName, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        updateRoutes(configInfo);
                    }
                });
        updateRoutes(configAndSignListener);
    }

    private void updateRoutes(String configInfo) {
        System.out.println("configInfo = " + configInfo);
        List<RouteDefinition> list = JSONUtil.toList(configInfo, RouteDefinition.class);
        for(String id:routeIds){
            writer.delete(Mono.just(id)).subscribe();
        }
        routeIds.clear();
        for(RouteDefinition routeDefinition:list){
            writer.save(Mono.just(routeDefinition)).subscribe();
            routeIds.add(routeDefinition.getId());
        }
    }
}
