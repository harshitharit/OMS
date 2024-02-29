package com.oms.service;

import com.oms.ReceieveMessageFromKafka;
import com.oms.SendMessageToKafka;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EnrichmentService {
    @Autowired
   private ReceieveMessageFromKafka receieveMessageFromKafka;
    @Autowired
    private SendMessageToKafka messageToKafka;
    public void subscribeToTopics(List<String> topics) {
        receieveMessageFromKafka.subscribeToTopics(topics);
    }
    public void consumeMessages() {
        List<ConsumerRecord<String, Object>> messages = receieveMessageFromKafka.consumeMessages();
        for (ConsumerRecord<String, Object> message : messages) {
            processMessage(message.value());
        }
    }
    public void processMessage(Object message) {
        System.out.println("Processing message: " + message);
    }


    public void sendMessage(String message){
        messageToKafka.sendMessageToTopic("enrichment-topic", message);
       System.out.println("success");
    }
}
