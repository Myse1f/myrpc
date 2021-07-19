package com.myse1f.myrpcconsumer;

import com.myse1f.myrpc.client.proxy.ProxyFactory;
import com.myse1f.myrpc.common.api.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyrpcConsumerApplication {

    @Autowired
    private ProxyFactory proxyFactory;

    public static void main(String[] args) {
        SpringApplication.run(MyrpcConsumerApplication.class, args);
    }

    @Bean
    public HelloService helloService() {
        return proxyFactory.newProxyInstance(HelloService.class);
    }
}
