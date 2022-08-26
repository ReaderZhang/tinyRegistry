package com.qqz.handler;

import com.qqz.RegistryServer;
import com.qqz.constant.MessagePrefix;
import com.qqz.holder.ServerHolder;
import com.qqz.pojo.BasicService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qqz @Date:2022/8/26
 * @usage 服务端心跳handler
 */

@Slf4j
public class RegistryHeartBeatServerHandler extends SimpleChannelInboundHandler {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String){
            String message = (String) msg;
            String str;
            String address = ctx.channel().remoteAddress().toString();
            //前缀为心跳
            if (message.startsWith(MessagePrefix.BEAT_SEQUENCE)){
                str = message.substring(5);
                log.info("receive from {} heartbeat",address);
                ServerHolder.machineMapping.get(str).get(address).beat();
                //前缀为开始
            }else if (message.startsWith(MessagePrefix.START_SEQUENCE)){
                str = message.substring(6);
                log.info("receive from {} register",address);
                BasicService service = new BasicService(System.currentTimeMillis());
                if (ServerHolder.machineMapping.get(str) == null){
                    ConcurrentHashMap<String, BasicService> map = new ConcurrentHashMap<>();
                    map.put(address,service);
                    ServerHolder.machineMapping.put(str,map);
                }
                ServerHolder.machineMapping.get(str).put(address,service);
                //前缀为销毁
            } else if (message.startsWith(MessagePrefix.REMOVE_SEQUENCE)) {
                str = message.substring(5);
                ServerHolder.machineMapping.get(str).remove(address);
            }
        } else if (msg instanceof ByteBuf) {
            ByteBuf message = (ByteBuf) msg;
            byte[] bytes = new byte[message.readableBytes()];
            log.info("receive from client {}",new String(bytes,StandardCharsets.UTF_8));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("server is active");
        ByteBuf msg = Unpooled.copiedBuffer("Received from server.Hello client", StandardCharsets.UTF_8);
        ctx.writeAndFlush(msg);
    }
}
