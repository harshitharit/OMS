package com.oms;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReceieveMessageFromKafka {
    private final Consumer<String, Object> consumer;

    @Autowired
    public ReceieveMessageFromKafka(ConsumerFactory<String, Object> consumerFactory) {
        Consumer<String, Object> consumer = consumerFactory.createConsumer();
        if (consumer == null) {
            throw new IllegalStateException("ConsumerFactory failed to create a Consumer");
        }
        this.consumer = consumerFactory.createConsumer();
    }

    public void subscribeToTopics(List<String> topics) {
        this.consumer.subscribe(topics);
        this.consumer.seekToBeginning(this.consumer.assignment());
        System.out.println("Subscribed to topics");
    }

    @Scheduled(fixedRate = 1000)
    public List<ConsumerRecord<String, Object>> consumeMessages() {
        ConsumerRecords<String, Object> Message = consumer.poll(Duration.ofMillis(1000));
        List<ConsumerRecord<String, Object>> messages = new ArrayList<>();
        for (ConsumerRecord<String, Object> message : Message) {
            System.out.println("Consumed message: " + message.value());
            messages.add(message);
        }
        return messages;
    }
}
