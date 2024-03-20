package com.oms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.SendMessageToKafka;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Service
public class PreferenceService {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceService.class);

    @Autowired
    private SendMessageToKafka messageToKafka;

<<<<<<< HEAD
<<<<<<< HEAD
    @Autowired
    public PreferenceService(SendMessageToKafka messageToKafka) {
        this.messageToKafka = messageToKafka;
    }

    Map<String, Object> parseMessage(String message) {
=======
    private Map<String, Object> parseMessage(String message) {
>>>>>>> origin/main
=======
    private Map<String, Object> parseMessage(String message) {
>>>>>>> origin/main
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, new TypeReference<Map<String, Object>>(){});
        } catch (IOException e) {
            logger.error("Error while processing message: {}", e.getMessage());
            return null;
        }
    }

    @KafkaListener(topics = "enrichment-topic", groupId = "ECMOM")
    public void processMessage(ConsumerRecord<Long, Object> consumerRecord) {
        String message = consumerRecord.value().toString();
        Map<String, Object> messageMap = parseMessage(message);
        if (messageMap.containsKey("channel") && messageMap.containsKey("accountNumber") && messageMap.containsKey("cifNumber")) {
            String channel = messageMap.get("channel").toString();
            String cifNumber = messageMap.get("cifNumber").toString();
            String account = messageMap.get("accountNumber").toString();
            logger.info("Fetched Data for preference Service : Channel - " + channel + ", CIF Number - " + cifNumber + ", Account - " + account);
            setPreference(channel);
        }
    }

    public void setPreference(String channel) {
        String preferenceMessage = ("Channel: " + channel );
        messageToKafka.sendMessageToTopic("preference-topic", preferenceMessage);
    }
}
