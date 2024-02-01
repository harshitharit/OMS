package com.oms.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class SendMessageToKafka {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageToKafka.class);

    @Autowired
    private KafkaTemplate<String,Object> template;

    public CompletableFuture<SendResult<String, Object>> sendMessageToTopic(String message){
        CompletableFuture<SendResult<String, Object>> future = template.send("request-topic", message);
        future.whenComplete((result,ex)->{
            if (ex == null) {
                LOGGER.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            } else {
                LOGGER.error("Unable to send message=[{}] due to : {}", message, ex.getMessage());
            }
        });
        return future;
    }
}