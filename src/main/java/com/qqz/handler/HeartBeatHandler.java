package com.qqz.handler;

import com.qqz.pojo.BasicService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qqz @Date:2022/8/25
 */

@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    public final static Map<String, List<BasicService>> machineMapping = new ConcurrentHashMap<>();
    private static final ByteBuf HEARTBEAT_SEQUENCE =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", StandardCharsets.ISO_8859_1));

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        BasicService basicService = new BasicService(true, ctx.channel().remoteAddress().toString(), 5);
        log.info("server active");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String){
            System.out.println((String)msg);
        }else {
            ByteBuf buf = (ByteBuf) msg;
            byte[] message = new byte[buf.readableBytes()];
            buf.readBytes(message);
            System.out.println(new String(message,StandardCharsets.UTF_8));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            log.info("Sending heartbeat to "+ctx.channel().remoteAddress());
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
