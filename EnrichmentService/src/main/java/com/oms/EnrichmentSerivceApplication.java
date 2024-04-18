package com.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.oms"})
public class EnrichmentSerivceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnrichmentSerivceApplication.class, args);
    }
}
