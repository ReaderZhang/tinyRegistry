package com.qqz.handler;

import com.qqz.RegistryClient;
import com.qqz.constant.MessagePrefix;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author qqz @Date:2022/8/26
 * @usage 客户端心跳handler
 */

@Slf4j
public class RegistryHeartBeatClientHandler extends SimpleChannelInboundHandler {

    private String serviceName;

    public RegistryHeartBeatClientHandler(String serviceName){
        this.serviceName = serviceName;
    }
    //心跳信息
    private static final ByteBuf HEARTBEAT_SEQUENCE =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", StandardCharsets.UTF_8));


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        log.info(new String(bytes,StandardCharsets.UTF_8));
    }

    /**
     * 心跳触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(event.state())){
                ctx.writeAndFlush(MessagePrefix.BEAT_SEQUENCE+":"+serviceName).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        //测试宕机检测
        /*double v = Math.random() * 10;
        if (v>9){
            Thread.sleep(10_000);
        }*/
        super.userEventTriggered(ctx,evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer(MessagePrefix.START_SEQUENCE+":"+serviceName,StandardCharsets.UTF_8);

        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client is close");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String){
            System.out.println(msg);
        } else if (msg instanceof ByteBuf) {
            ByteBuf message = (ByteBuf) msg;
            byte[] str = new byte[message.readableBytes()];
            message.readBytes(str);
            System.out.println(new String(str,StandardCharsets.UTF_8));
        }
    }

}
