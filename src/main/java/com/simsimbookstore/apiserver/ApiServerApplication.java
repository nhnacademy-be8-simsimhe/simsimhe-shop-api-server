package com.simsimbookstore.apiserver;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(ApiServerApplication.class, args);
    }



}
