package com.example.rtlemul;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;


@Slf4j
public class MyNettyClient {

    private final Bootstrap bootstrap = new Bootstrap();
    private final SocketAddress addr;
    private Channel hubChannel;
    private final Timer timer;

    public MyNettyClient(String host, int port, Timer timer ) {
        this( new InetSocketAddress( host, port ), timer );
    }
    public MyNettyClient(SocketAddress addr, Timer timer ) {
        this.addr = addr;
        this.timer = timer;
        bootstrap.group( new NioEventLoopGroup() );
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast( createNMMessageHandler() );
            }
        });

        scheduleConnect( 1000 );
    }

    private ChannelHandler  createNMMessageHandler() {
        return new ChannelInboundHandlerAdapter () {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                ByteBuf buf = (ByteBuf)msg;
                int n = buf.readableBytes();
                if( n > 0 ) {
                    byte[] b = new byte[n];
                    buf.readBytes(b);
                    log.info(""+ new String( b ));
                }
            }

        };
    }

    private void scheduleConnect( long millis ) {

        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                doConnect();
            }
        }, millis );
    }

    public void doConnect() {
        try {
            ChannelFuture f = bootstrap.connect(  addr );
            f.addListener( new ChannelFutureListener() {
                @Override public void operationComplete(ChannelFuture future) {
                    if( !future.isSuccess() ) {//if is not successful, reconnect
                        future.channel().close();
                        close();

                        bootstrap.connect(  addr ).addListener(this);
                    } else {//good, the connection is ok
                        hubChannel = future.channel();
                        //add a listener to detect the connection lost
                        addCloseDetectListener( hubChannel );
                       // connectionEstablished();
                    }
                }

                private void addCloseDetectListener(Channel channel) {
                    //if the channel connection is lost, the ChannelFutureListener.operationComplete() will be called
                    channel.closeFuture().addListener((ChannelFutureListener) future -> {
                        log.info("connection lost");
                        close();//не помогло
                        scheduleConnect( 1000 );
                    });
                }
            });
        }catch( Exception ex ) {
            scheduleConnect( 1000 );

        }
       /* finally {
            close();
        }*/
    }

    public void send(String msg) throws InterruptedException {
        if( hubChannel != null && hubChannel.isActive() ) {
            ByteBuf buf = hubChannel.alloc().buffer().writeBytes( msg.getBytes() );
            hubChannel.writeAndFlush( buf );
        } else {
            close();//не помогло
            scheduleConnect( 1000 );
            Thread.sleep(10000);
            send(msg);
        }
    }

    public void close() {
        try {

            hubChannel.close().sync();
            hubChannel.closeFuture();//не помогло
           // hubChannel.eventLoop().shutdownGracefully();
            hubChannel.close();//не помогло
           // bootstrap.;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
