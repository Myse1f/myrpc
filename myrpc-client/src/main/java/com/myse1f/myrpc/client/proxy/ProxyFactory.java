/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.client.proxy;

import com.myse1f.myrpc.client.connector.RpcClientConnector;
import com.myse1f.myrpc.common.bean.RpcRequest;
import com.myse1f.myrpc.common.bean.RpcResponse;
import com.myse1f.myrpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class ProxyFactory {

    @Autowired
    private ServiceRegistry registry;

    public <T> T newProxyInstance(Class<T> klass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(klass);
        enhancer.setCallback(new ProxyCallback());
        return (T) enhancer.create();
    }

    class ProxyCallback implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return doIntercept(method, objects);
        }

        private Object doIntercept(Method method, Object[] parameters) throws Exception {
            String requestId = UUID.randomUUID().toString();
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            RpcRequest request = RpcRequest.builder()
                    .requestId(requestId)
                    .className(className)
                    .methodName(methodName)
                    .parameterTypes(parameterTypes)
                    .parameters(parameters)
                    .build();

            List<String> services = registry.getService(className);
            String target = services.get(ThreadLocalRandom.current().nextInt(services.size()));
            log.info("select service {} : {}", className, target);
            String[] split = StringUtils.split(target, ":");
            String host = split[0];
            int port = Integer.parseInt(split[1]);

            RpcClientConnector connector = new RpcClientConnector(host, port);
            RpcResponse response = connector.sendRequest(request);
            log.info("get response {}", response);
            if (response.getException() != null) {
                throw response.getException();
            } else {
                return response.getResult();
            }
        }
    }
}
