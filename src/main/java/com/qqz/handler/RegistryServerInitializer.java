package com.qqz.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author qqz @Date:2022/8/26
 */
public class RegistryServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast("encoder",new StringEncoder())
                .addLast("decoder",new StringDecoder())
                .addLast(new RegistryHeartBeatServerHandler());
    }
}
