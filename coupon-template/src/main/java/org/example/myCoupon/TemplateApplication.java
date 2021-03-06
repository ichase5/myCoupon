package org.example.myCoupon;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling //定時任務
@EnableDiscoveryClient
@SpringBootApplication
public class TemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class,args);
    }


}


