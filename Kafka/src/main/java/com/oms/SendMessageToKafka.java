package com.oms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class SendMessageToKafka {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageToKafka.class);

    @Autowired
    private KafkaTemplate<String, Object> template;
    public List<CompletableFuture<SendResult<String, Object>>> sendMessagesToTopic(String topic, List<String> messages) {
        return messages.stream()
                .map(message -> sendMessageToTopic(topic, message))
                .collect(Collectors.toList());
    }

    public CompletableFuture<SendResult<String, Object>> sendMessageToTopic(String topic, String message) {
        CompletableFuture<SendResult<String, Object>> future = template.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset(), topic);
            } else {
                LOGGER.error("Unable to send message=[{}] due to : {}", message, ex.getMessage());
            }
        });
        return future;
    }
}