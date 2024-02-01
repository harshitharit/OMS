package com.om;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReceieveMessageFromKafka {

    @KafkaListener(topics = "request-topic", groupId = "group_id")
    public void consumeMessage(ConsumerRecord<String, Object> record) {
        System.out.println("Consumed message: " + record.value());
    }
}