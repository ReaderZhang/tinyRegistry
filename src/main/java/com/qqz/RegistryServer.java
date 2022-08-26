package com.qqz;

import com.qqz.handler.RegistryServerInitializer;
import com.qqz.holder.ServerHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qqz @Date:2022/8/26
 */

@Slf4j
public class RegistryServer {



    private final EventLoopGroup boss = new NioEventLoopGroup(1);

    private final EventLoopGroup worker = new NioEventLoopGroup();

    private final ServerBootstrap bootstrap = new ServerBootstrap();

    public void start(InetSocketAddress address){
        bootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new RegistryServerInitializer());
        try {
            ChannelFuture future = bootstrap.bind(address).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        //轮询服务健康情况
        new Thread(()->{
            System.out.println("Thread start");
            log.info("mapping size is {}",ServerHolder.machineMapping.size());
            while (true){
                ServerHolder.machineMapping.forEach((k, v)->{

                    AtomicInteger mark = new AtomicInteger(0);
                    v.forEach((key,value)->{
                        //不健康排除
                        if (value.isActive()){
                            //超过10秒标记为不健康
                            if (System.currentTimeMillis()-value.getSecond()>=3_000L){
                                value.setActive(false);
                                mark.set(mark.get()+1);
                            }
                        }
                    });
                    if (mark.get()!=0){
                        log.info("service {} DOWN {}", k, mark.get());
                        mark.set(0);
                    }
                });
            }
        }).start();
        new RegistryServer().start(new InetSocketAddress("localhost",7788));
    }
}
