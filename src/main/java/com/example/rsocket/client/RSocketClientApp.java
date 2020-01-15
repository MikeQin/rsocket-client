package com.example.rsocket.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RSocketClientApp {


    public static void main(String[] args) {

        SpringApplication.run(RSocketClientApp.class, args);

        /*new SpringApplicationBuilder()
                .main(RSocketClientApp.class)
                .sources(RSocketClientApp.class)
                .profiles("client")
                .run(args);*/
    }
}