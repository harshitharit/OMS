package com.oms;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@Component
public class ReceieveMessageFromKafka {

    private static final Logger logger = LoggerFactory.getLogger(ReceieveMessageFromKafka.class);

    private final Consumer<String, Object> consumer;

    @Autowired
    public ReceieveMessageFromKafka(ConsumerFactory<String, Object> consumerFactory) {
        this.consumer = consumerFactory.createConsumer();
        //subscribeToTopic();
    }
    public void subscribeToTopic(String topic) {
        this.consumer.subscribe(Collections.singletonList(topic));
    }
    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(1000));
        for (ConsumerRecord<String, Object> kafkaRecord : records) {
            logger.info("Consumed message: {}", kafkaRecord.value());
        }
    }
}
//