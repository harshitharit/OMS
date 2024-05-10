package com.oms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.oms.SendMessageToKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class GeneratorService {
    private final SendMessageToKafka messageToKafka;

    @Autowired
    public GeneratorService(SendMessageToKafka messageToKafka) {
        this.messageToKafka = messageToKafka;
    }

    private Map<String, Object> parsePreferenceMessage(String preferenceMessage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(preferenceMessage, new TypeReference<Map<String, Object>>(){});
        } catch (IOException e) {
            log.error("Error while parsing preference message", e);
            throw new RuntimeException("Error while parsing preference message", e);
        }
    }

    @KafkaListener(topics = "preference-topic", groupId = "ECMOM")
    public void generatePdfAndSendToKafka(String preferenceMessage) {
        Map<String, Object> preferenceData = parsePreferenceMessage(preferenceMessage);
        ByteArrayOutputStream pdfOutputStream = generatePdf(preferenceData);
        String pdfMessage = Base64.getEncoder().encodeToString(pdfOutputStream.toByteArray());
        messageToKafka.sendMessageToTopic("generator-topic", pdfMessage);
    }

    private ByteArrayOutputStream generatePdf(Map<String, Object> preferenceData) {
        Document document = new Document();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, pdfOutputStream);
            document.open();
            document.add(new Paragraph("Name: " + preferenceData.get("name")));
            document.add(new Paragraph("Account Number: " + preferenceData.get("accountNumber")));
            document.add(new Paragraph("CIF Number: " + preferenceData.get("cifNumber")));
            document.add(new Paragraph("Preferred Channel: " + preferenceData.get("preferredChannel")));
            document.add(new Paragraph("Preferred Address: " + preferenceData.get("preferredAddress")));
            document.close();
        } catch (DocumentException e) {
            log.error("Error while generating PDF", e);
        }
        return pdfOutputStream;
    }
}
