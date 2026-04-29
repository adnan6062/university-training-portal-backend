package com.university.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AssessmentServiceApplication {

    public static void main(String[] args) {
        System.setProperty("http.nonProxyHosts",  "localhost|127.0.0.1|0.0.0.0");
        System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1|0.0.0.0");
        SpringApplication.run(AssessmentServiceApplication.class, args);
    }
}
