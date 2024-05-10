package com.oms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.SendMessageToKafka;
import com.oms.exception.PreferenceException;
import com.oms.model.CustomerPreference;
import com.oms.repository.PreferenceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
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
        public Map<String, Object> parseMessage(String message) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(message, new TypeReference<Map<String, Object>>(){});
            } catch (IOException e) {
                logger.error("Error while processing message: {}", e.getMessage());
                throw new PreferenceException("Error while processing message", e);
            }
        }
    @KafkaListener(topics = "enrichment-topic", groupId = "ECMOM")
    public void processMessage(ConsumerRecord<Long, Object> consumerRecord) {
        try {
            String message = consumerRecord.value().toString();
            Map<String, Object> messageMap = parseMessage(message);
            if (messageMap == null) {
                logger.warn("Skipping message due to invalid format: {}", message);
                return;
            }
            if (!messageMap.containsKey("accountNumber") || !messageMap.containsKey("cifNumber")) {
                logger.warn("Skipping message due to missing account number or CIF number: {}", messageMap);
                return;
            }
            Long cifNumber = Long.valueOf(String.valueOf(messageMap.get("cifNumber")));
            Long accountNumber = Long.valueOf(String.valueOf(messageMap.get("accountNumber")));
            logger.info("Searching for preferences with CIF Number:{} and Account Number: {}", accountNumber, cifNumber);
            List<CustomerPreference> preferences = preferenceRepository.findByAccountNumberAndCifNumber(accountNumber, cifNumber);
            if (preferences.isEmpty()) {
                logger.warn("No preferences found for CIF Number:{} and Account Number: {}", accountNumber, cifNumber);
                return;
            }
            logger.info("Number of preferences found: {}", preferences.size());
            for (CustomerPreference preference : preferences) {
                String preferenceMessage = formatPreferenceMessage(preference);
                sendMessageToTopic("preference-topic", preferenceMessage);
            }
        } catch (Exception e) {
            logger.error("Error while processing message: {}", e.getMessage());
            throw new PreferenceException("Error while processing message", e);
        }
    }

    public void sendMessageToTopic(String topic, String message) {
        messageToKafka.sendMessageToTopic(topic, message);
        logger.info("Message sent to topic: {}", topic);
    }

    public String formatPreferenceMessage(CustomerPreference preference) {
        validatePreference(preference);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> preferenceMap = new HashMap<>();
        preferenceMap.put("AccountNumber", String.valueOf(preference.getAccountNumber()));
        preferenceMap.put("CifNumber", String.valueOf(preference.getCifNumber()));
        preferenceMap.put("Preferredchannel", preference.getPreferredchannel());
        preferenceMap.put("PreferredAddress", preference.getPreferredAddress());
        preferenceMap.put("Name", preference.getName());
        try {
            return mapper.writeValueAsString(preferenceMap);
        } catch (JsonProcessingException e) {
            throw new PreferenceException("Error while formatting preference message", e);
        }
    }

    void validatePreference(CustomerPreference preference) {
        Map<String, String> fieldsToValidate = Map.of(
                "Preferredchannel", preference.getPreferredchannel(),
                "PreferredAddress", preference.getPreferredAddress(),
                "Name", preference.getName()
        );
        fieldsToValidate.forEach(this::validateInput);
    }
    void validateInput(String input, String fieldName) {
        if (input == null || input.isEmpty()) {
            throw new PreferenceException("Invalid preference data: " + fieldName + " is null or empty", null);
        }
    }
}
