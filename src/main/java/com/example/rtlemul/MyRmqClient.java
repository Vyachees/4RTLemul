package com.example.rtlemul;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Timer;


@Slf4j
public class MyRmqClient {
    private final static String QUEUE_NAME = "spring-boot";
    Channel rmqChannel;


    public Channel connectToRmq() {
        try {
            log.info("Start connecting to Rmq");
            ConnectionFactory factory = new ConnectionFactory();
            factory.setRequestedHeartbeat(30);
            factory.setConnectionTimeout(3000);
            factory.setHost("localhost");
            factory.setPort(5672);
            Connection connection = factory.newConnection();
            rmqChannel = connection.createChannel();
            rmqChannel.queueDeclare(QUEUE_NAME, false, false, false, null);
            log.info("Connected to Rmq. Waiting for messages.");

        }
        catch (Exception e){
           log.info(""+e);

        }
        return rmqChannel;
    }



    MyNettyClient myNettyClient = new MyNettyClient( "127.0.0.1", 50004, new Timer() );

    public void receiveFromRmq() throws Exception {
        if(rmqChannel==null){
            rmqChannel=connectToRmq();
        }


              DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            log.info(" [x] Received '" + message + "'");
            log.info("Try to send message");
                  try {
                      myNettyClient.send(message);
                  } catch (InterruptedException e) {
                     log.info("InterruptedException e"+e);
                  }
              };

        rmqChannel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

    }




}
