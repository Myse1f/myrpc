/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.server.acceptor;

import com.myse1f.myrpc.common.bean.RpcRequest;
import com.myse1f.myrpc.common.bean.RpcResponse;
import com.myse1f.myrpc.common.codec.RpcDecoder;
import com.myse1f.myrpc.common.codec.RpcEncoder;
import com.myse1f.myrpc.server.acceptor.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RpcServerAcceptor implements Runnable{

    @Autowired
    private RpcServerHandler rpcServerHandler;

    private int port;

    public RpcServerAcceptor(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boos = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        bootstrap.group(boos, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new RpcDecoder(RpcRequest.class));
                        pipeline.addLast(new RpcEncoder(RpcResponse.class,  0));
                        pipeline.addLast(rpcServerHandler);
                    }
                });

        try {
            log.info("server start at port {}", port);
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
