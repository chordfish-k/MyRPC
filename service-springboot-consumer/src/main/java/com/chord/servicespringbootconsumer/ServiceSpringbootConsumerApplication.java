package com.chord.servicespringbootconsumer;

import com.chord.myrpc.springboot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class ServiceSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSpringbootConsumerApplication.class, args);
    }

}
