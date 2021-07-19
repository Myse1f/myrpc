package com.myse1f.myrpcprovider;

import com.myse1f.myrpc.server.RpcServerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyrpcProviderApplication implements ApplicationRunner {

    @Autowired
    private RpcServerRunner rpcServerRunner;

    public static void main(String[] args) {
        SpringApplication.run(MyrpcProviderApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        rpcServerRunner.run();
    }
}
