package com.example.rtlemul.sendToRTLhub;

/**
 * @author Vyacheslav Kirillov
 * @create 2022.10.31 20:35
 **/
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@Slf4j
public final class NettyClient {
    static final String HOST = "127.0.0.1";
    static final int PORT = 50004;

    //EventLoopGroup group = new NioEventLoopGroup();

   // Bootstrap b = new Bootstrap();

    public Channel connectToRTLHub() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group) // Set EventLoopGroup to handle all eventsf for client.
                    .channel(NioSocketChannel.class)// Use NIO to accept new connections.
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
                            // This is our custom client handler which will have logic for chat.
                            p.addLast(new  ClientHandler());
                        }
                    });
            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();
          //  String input = "Frank";
            Channel channel = f.sync().channel();

           // channel.writeAndFlush(input);

            //channel.send
            //    channel.flush();
                // Wait until the connection is closed.
               // f.channel().closeFuture().sync();
            log.info("im returning channel");
            return channel ;
        } catch (Exception e){
            log.info(e+"");
        }
       // return channel;
        return null;
    }



    public void sendToRTLhub(Channel channel, String msg) throws Exception {
        channel.writeAndFlush(msg);
        channel.flush();

    }



}
