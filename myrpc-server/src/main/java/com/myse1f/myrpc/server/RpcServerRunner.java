/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.server;

import com.myse1f.myrpc.common.annotation.ServiceProvider;
import com.myse1f.myrpc.common.util.IpUtils;
import com.myse1f.myrpc.registry.ServiceRegistry;
import com.myse1f.myrpc.server.acceptor.RpcServerAcceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class RpcServerRunner {

    @Value("${rpc.server.networkPort}")
    private int port;

    @Autowired
    private ServiceRegistry registry;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcServerAcceptor rpcServerAcceptor;

    private static ExecutorService excutor;

    public void run() {
        excutor = Executors.newSingleThreadExecutor();
        excutor.execute(rpcServerAcceptor);

        // register
        registerService();
    }

    private void registerService() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(ServiceProvider.class);
        if (MapUtils.isEmpty(providers)) {
            return;
        }
        for (Object provider : providers.values()) {
            ServiceProvider service = provider.getClass().getAnnotation(ServiceProvider.class);
            String serviceName = service.klass().getName();
            String address = IpUtils.getRealIp() + ":" + port;
            registry.register(serviceName, address);
            log.info("register {}, address: {}", serviceName, address);
        }
    }

    @PreDestroy
    public void destroy() {
        if (excutor != null) {
            excutor.shutdown();
        }
    }
}
