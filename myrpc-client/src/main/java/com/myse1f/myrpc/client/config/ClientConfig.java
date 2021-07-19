/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.client.config;

import com.myse1f.myrpc.registry.ServiceRegistry;
import com.myse1f.myrpc.registry.zookeeper.ZKServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "com.myse1f.myrpc.client")
public class ClientConfig {

    @Value("${rpc.server.registry.addr}")
    private String registryAddr;

    @Bean
    @ConditionalOnMissingBean(ServiceRegistry.class)
    public ServiceRegistry serviceRegistry() {
        return new ZKServiceRegistry(registryAddr);
    }
}
