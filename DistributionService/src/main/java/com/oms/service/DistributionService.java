package com.oms.service;


import com.oms.SendMessageToKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class DistributionService {

    private final SendMessageToKafka messageToKafka;

    @Autowired
    public DistributionService(SendMessageToKafka messageToKafka) {
        this.messageToKafka = messageToKafka;
    }
    private byte[] parseMessage(String message) {
        try {
            return Base64.getDecoder().decode(message);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error while parsing message", e);
        }
    }

    @KafkaListener(topics = "generator-topic", groupId = "ECMOM")
    public void distributePdf(String pdfMessage) {
        byte[] pdfData = parseMessage(pdfMessage);
    }
}
