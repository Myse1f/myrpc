/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.server.acceptor.handler;

import com.myse1f.myrpc.common.bean.RpcRequest;
import com.myse1f.myrpc.common.bean.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@ChannelHandler.Sharable
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Autowired
    private ApplicationContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        log.info("receive request {}", rpcRequest);
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());

        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Exception e) {
            log.error("rpc error", e);
            response.setException(e);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Exception {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Class<?>[] parameterTypes = request.getParameterTypes();

        Object targetClass = context.getBean(Class.forName(className));
        Method targetMethod = targetClass.getClass().getMethod(methodName, parameterTypes);
        Object result = targetMethod.invoke(targetClass, parameters);

        return result;
    }
}
