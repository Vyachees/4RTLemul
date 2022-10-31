package com.example.rtlemul.sendToRTLhub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Vyacheslav Kirillov
 * @create 2022.10.31 22:33
 **/
@Component
@Slf4j
class  ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("Message from Server: " + msg);

    }

}