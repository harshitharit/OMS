package com.oms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oms.SendMessageToKafka;
import com.oms.model.EnrichmentModel;
import com.oms.repository.EnrichmentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EnrichmentService {
    private static final Logger logger = LoggerFactory.getLogger(EnrichmentService.class);
    private final SendMessageToKafka messageToKafka;
    private final EnrichmentRepository enrichmentRepository;

    @Autowired
    public EnrichmentService(SendMessageToKafka messageToKafka, EnrichmentRepository enrichmentRepository) {
        this.messageToKafka = messageToKafka;
        this.enrichmentRepository = enrichmentRepository;
    }
    private Map<String, Object> parseMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, new TypeReference<Map<String, Object>>(){});
        } catch (IOException e) {
            logger.error("Error while processing message: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
    @KafkaListener(topics = "request-topic", groupId = "ECMOM")
    public void processMessage(ConsumerRecord<Long, Object> consumerRecord) {
        String message = consumerRecord.value().toString();
        Map<String, Object> messageMap = parseMessage(message);
        if (messageMap == null) {
            logger.error("Error parsing message: {}", message);
            return;
        }
        Long cifNumber = Long.parseLong(messageMap.get("cifNumber").toString());
        Long accountNumber = Long.parseLong(messageMap.get("accountNumber").toString());
        List<EnrichmentModel> enrichmentModels = fetchData(accountNumber, cifNumber);
        if (!enrichmentModels.isEmpty()) {
            EnrichmentModel enrichmentModel = enrichmentModels.get(0);
            sendMessageToKafka(enrichmentModel);
        } else {
            logger.info("No data found for CIF Number: {} and Account Number: {}", cifNumber, accountNumber);
        }
    }

    private List<EnrichmentModel> fetchData(Long accountNumber, Long cifNumber) {
        return enrichmentRepository.findByAccountNumberAndCifNumber(accountNumber, cifNumber);
    }
    private void sendMessageToKafka(EnrichmentModel enrichmentModel) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String message = mapper.writeValueAsString(enrichmentModel);
            messageToKafka.sendMessageToTopic("enrichment-topic", message);
            logger.info("Enrichment data sent for CIF Number: {} and Account Number: {}", enrichmentModel.getCifNumber(), enrichmentModel.getAccountNumber());

        } catch (JsonProcessingException e) {
            logger.error("Error while converting EnrichmentModel to JSON: {}", e.getMessage());
        }
    }
}