package com.example.rtlemul;

/*
 * @author Vyacheslav Kirillov
 * @create 2022.10.27 22:47
 */
import com.example.rtlemul.sendToRTLhub.NettyClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import io.netty.channel.*;

import java.nio.charset.StandardCharsets;

@Component

public class Recv {

    private final static String QUEUE_NAME = "spring-boot";
    NettyClient nettyClient=new NettyClient();


@Bean
    //@EventListener(ApplicationReadyEvent.class)
    public void receiveFromRmq() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


        io.netty.channel.Channel hubChannel = nettyClient.connectToRTLHub();//соединяемся с хабом

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            try {
                nettyClient.sendToRTLhub(hubChannel,message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
          /*  try {
                nettyClient.connectToRTLHub();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }*/
           /* try {
                nettyClient.sendToRTLhub(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }*/



        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}