package com.example.rtlemul;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RtLemulApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RtLemulApplication.class, args);

        Thread.sleep(1000);

        MyRmqClient myRmqClient=new MyRmqClient();
        Thread.sleep(1000);
        myRmqClient.connectToRmq();
        Thread.sleep(1000);

        myRmqClient.receiveFromRmq();


      /*  while(true) {
            Thread.sleep(1750);
            myNettyClient.send("test msg1111arrararar2");}*/
    }

}
