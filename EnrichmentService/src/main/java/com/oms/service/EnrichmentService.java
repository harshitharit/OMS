package com.oms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oms.SendMessageToKafka;
import com.oms.model.EnrichmentModel;
import com.oms.repository.EnrichmentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
<<<<<<< HEAD
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
=======
>>>>>>> origin/main
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @KafkaListener(topics = "request-topic", groupId = "ECMOM")
<<<<<<< HEAD
    public void processMessage(@NotNull ConsumerRecord<Long, Object> consumerRecord) {
=======
    public void processMessage(ConsumerRecord<Long, Object> consumerRecord) {
>>>>>>> origin/main
        String message = consumerRecord.value().toString();
        Map<String, Object> messageMap = parseMessage(message);
        Long cifNumber = Long.parseLong(messageMap.get("cifNumber").toString());
        Long accountNumber = Long.parseLong(messageMap.get("accountNumber").toString());
        List<EnrichmentModel> enrichmentModels = fetchData(accountNumber, cifNumber);
        if (!enrichmentModels.isEmpty()) {
            EnrichmentModel enrichmentModel = enrichmentModels.get(0);
<<<<<<< HEAD
            sendMessageToKafka(enrichmentModel);
=======
            sendMessageTokafka(enrichmentModel);
>>>>>>> origin/main
        } else {
            logger.info("No data found for CIF Number: {} and Account Number: {}", cifNumber, accountNumber);
        }
    }

<<<<<<< HEAD
    private @Nullable Map<String, Object> parseMessage(String message) {
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

    private List<EnrichmentModel> fetchData(Long accountNumber, Long cifNumber) {
        return enrichmentRepository.findByAccountNumberAndCifNumber(accountNumber, cifNumber);
    }

<<<<<<< HEAD
    private void sendMessageToKafka(EnrichmentModel enrichmentModel) {
=======
    private void sendMessageTokafka(EnrichmentModel enrichmentModel) {
>>>>>>> origin/main
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
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/main
