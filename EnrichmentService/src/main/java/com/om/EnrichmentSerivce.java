package com.om;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnrichmentSerivce {
    public static void main(String[] args) {
        SpringApplication.run(EnrichmentSerivce.class,args);
        System.out.println("Enrichment Service is running...");
    }
}