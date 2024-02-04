package com.oms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrefernceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrefernceServiceApplication.class, args);
        System.out.println("Preference Service is running...");
    }
}

