package com.qqz;

import com.qqz.handler.RegistryClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author qqz @Date:2022/8/26
 */
public class RegistryClient {

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup group = new NioEventLoopGroup();

    public static void main(String[] args) {
        new RegistryClient().start(new InetSocketAddress("localhost",7788));
    }

    public void start(InetSocketAddress address){
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new RegistryClientInitializer());
        try {
            ChannelFuture future = bootstrap.connect(address).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            group.shutdownGracefully();
        }
    }
}
