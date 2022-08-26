package com.qqz.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author qqz @Date:2022/8/26
 */
public class RegistryClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new IdleStateHandler(0,1,0, TimeUnit.SECONDS))
                .addLast("encoder",new StringEncoder())
                .addLast("decoder",new StringDecoder())
                .addLast(new RegistryHeartBeatClientHandler("test"));
    }
}
