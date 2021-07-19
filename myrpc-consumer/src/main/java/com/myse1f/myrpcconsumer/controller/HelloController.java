/**
 * Created By Yufan Wu
 * 2021/7/19
 */
package com.myse1f.myrpcconsumer.controller;

import com.myse1f.myrpc.common.api.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        log.info("/hello?{}", name);
        return helloService.sayHello(name);
    }
}
