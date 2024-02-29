package com.oms;

import com.oms.service.EnrichmentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class EnrichmentSerivceApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EnrichmentSerivceApplication.class,args);
        EnrichmentService enrichmentService = context.getBean(EnrichmentService.class);
        enrichmentService.subscribeToTopics(Arrays.asList("request-topic"));
        enrichmentService.consumeMessages();
    }
}