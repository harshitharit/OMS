package com.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RequestHandlerApplication {
	public static void main(String[] args) {
		SpringApplication.run(RequestHandlerApplication.class, args);
		System.out.println("Request Handler is running...");
	}
}
