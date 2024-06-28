package com.chord.servicespringbootprovider;

import com.chord.myrpc.springboot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class ServiceSpringbootProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSpringbootProviderApplication.class, args);
    }

}
