/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.client.connector;

import com.myse1f.myrpc.common.bean.RpcRequest;
import com.myse1f.myrpc.common.bean.RpcResponse;
import com.myse1f.myrpc.common.codec.RpcDecoder;
import com.myse1f.myrpc.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClientConnector extends SimpleChannelInboundHandler<RpcResponse> {

    private String host;
    private int port;
    private RpcResponse response;

    public RpcClientConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcResponse sendRequest(RpcRequest request) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RpcEncoder(RpcRequest.class, 0));
                            pipeline.addLast(new RpcDecoder(RpcResponse.class));
                            pipeline.addLast(RpcClientConnector.this);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();

            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
    }
}
