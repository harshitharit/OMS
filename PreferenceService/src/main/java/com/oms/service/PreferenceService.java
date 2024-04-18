package com.oms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.SendMessageToKafka;
import com.oms.model.CustomerPreference;
import com.oms.repository.PreferenceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class PreferenceService {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceService.class);
    private final SendMessageToKafka messageToKafka;
    private final  PreferenceRepository preferenceRepository;

    @Autowired
    public PreferenceService(SendMessageToKafka messageToKafka, PreferenceRepository preferenceRepository){
        this.messageToKafka= messageToKafka;
        this.preferenceRepository=preferenceRepository;
    }

    private Map<String, Object> parseMessage(String message) {
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
        if (messageMap.containsKey("accountNumber") && messageMap.containsKey("cifNumber")) {
            Long cifNumber = Long.valueOf(String.valueOf(messageMap.get("cifNumber")));
            Long accountNumber = Long.valueOf(String.valueOf(messageMap.get("accountNumber")));
            logger.info("Searching for preferences with CIF Number:{} and Account Number: {}", accountNumber, cifNumber);
                List<CustomerPreference> preferences = preferenceRepository.findAll();
                logger.info("Number of preferences found: {}", preferences.size());
                if (!preferences.isEmpty()) {
                    CustomerPreference preference = preferences.get(0);
                    setPreference(preference.getPreferredchannel(), preference.getPreferredAddress(),preference.getName());
                } else {
                    logger.info("No preferences found for accountNumber: {} and cifNumber: {}", accountNumber, cifNumber);
                }
            }
        }
    public void setPreference(String Preferredchannel , String PreferredAddress, String name) {
        String preferenceMessage = ("Preferredchannel: " + Preferredchannel + " PreferredAddress: " + PreferredAddress + " Name: " + name);
        messageToKafka.sendMessageToTopic("preference-topic", preferenceMessage);
        logger.info("Sent message : " + preferenceMessage);
    }
}