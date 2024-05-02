package com.oms.service;

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
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
public class PreferenceService {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceService.class);
    private final SendMessageToKafka messageToKafka;
    private final PreferenceRepository preferenceRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    public PreferenceService(SendMessageToKafka messageToKafka, PreferenceRepository preferenceRepository) {
        this.messageToKafka = messageToKafka;
        this.preferenceRepository = preferenceRepository;
    }

    Map<String, Object> parseMessage(String message) {
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, new TypeReference<>() {
            });
            if (!messageMap.containsKey("accountNumber") || !messageMap.containsKey("cifNumber")) {
                throw new PreferenceException("Missing accountNumber or cifNumber in message");
            }
            return messageMap;
        } catch (IOException e) {
            throw new PreferenceException("Error while processing message", e);
        }
    }
    @KafkaListener(topics = "enrichment-topic", groupId = "ECMOM")
    public void processMessage(ConsumerRecord<Long, Object> consumerRecord) {
        try {
            String message = consumerRecord.value().toString();
            Map<String, Object> messageMap = parseMessage(message);
            Long cifNumber = Long.valueOf(String.valueOf(messageMap.get("cifNumber")));
            Long accountNumber = Long.valueOf(String.valueOf(messageMap.get("accountNumber")));
            logger.info("Searching for preferences with CIF Number:{} and Account Number: {}", accountNumber, cifNumber);
            List<CustomerPreference> preferences = preferenceRepository.findByAccountNumberAndCifNumber(accountNumber, cifNumber);
            if (preferences.isEmpty()) {
                logger.warn("No preferences found for CIF Number:{} and Account Number: {}", accountNumber, cifNumber);
                return;
            }
            logger.info("Number of preferences found: {}", preferences.size());

            List<String> preferenceMessages = preferences.parallelStream()
                    .map(preference -> setPreference(preference.getPreferredchannel(), preference.getPreferredAddress(), preference.getName()))
                    .collect(Collectors.toList());

            List<CompletableFuture<SendResult<String, Object>>> futures = messageToKafka.sendMessagesToTopic("preference-topic", preferenceMessages);

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            throw new PreferenceException("Error while processing message", e);
        }
    }

    public String setPreference(String preferredChannel, String preferredAddress, String name) {
        validateInput(preferredChannel, "Preferredchannel");
        validateInput(preferredAddress, "PreferredAddress");
        validateInput(name, "Name");
        return String.format("Preferredchannel: %s PreferredAddress: %s Name: %s", preferredChannel, preferredAddress, name);
    }

    private void validateInput(String input, String fieldName) {
        if (input == null || input.isEmpty()) {
            throw new PreferenceException("Invalid preference data: " + fieldName + " is null or empty", null);
        }
    }
}
