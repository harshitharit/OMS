package com.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnrichmentSerivceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnrichmentSerivceApplication.class,args);
        System.out.println("Enrichment Service is running...");
    }
}