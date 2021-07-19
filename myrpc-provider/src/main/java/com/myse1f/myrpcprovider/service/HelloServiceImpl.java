/**
 * Created By Yufan Wu
 * 2021/7/19
 */
package com.myse1f.myrpcprovider.service;

import com.myse1f.myrpc.common.annotation.ServiceProvider;
import com.myse1f.myrpc.common.api.HelloService;

@ServiceProvider(klass = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
