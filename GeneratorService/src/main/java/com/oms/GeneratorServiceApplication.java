package com.oms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneratorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneratorServiceApplication.class, args);
        System.out.println("GeneratorApplication is running");

    }
}