package com.example.rtlemul;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Timer;

@SpringBootApplication
@Slf4j
public class RtLemulApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RtLemulApplication.class, args);

 //       for(int x=0;x<10;x++){
        MyNettyClient myNettyClient = new MyNettyClient(
                //"193.232.108.155",
                //"127.0.0.1",
                "193.232.108.155",
                9218
                //9208
                // 50004
                , new Timer() );
            while(true) {
                Thread.sleep(0);
                myNettyClient.send("test msg1111arrararar2test msg1111arrararar2test msg1111arrararar2test msg1111arrararar2test msg1111arrararar2");}
        }
       // }
}
