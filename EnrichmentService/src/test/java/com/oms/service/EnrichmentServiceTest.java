package com.oms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.SendMessageToKafka;
import com.oms.model.EnrichmentModel;
import com.oms.repository.EnrichmentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrichmentServiceTest {

    @Mock
    private SendMessageToKafka messageToKafka;

    @Mock
    private EnrichmentRepository enrichmentRepository;

    @InjectMocks
    private EnrichmentService enrichmentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testParseMessage() throws IOException {
        String message = "{\"accountNumber\":75892449412,\"cifNumber\":74212869153}";
        Map<String, Object> result = enrichmentService.parseMessage(message);
        assertEquals(2, result.size());
        assertEquals(75892449412L, result.get("accountNumber"));
        assertEquals(74212869153L, result.get("cifNumber"));
    }

    @Test
    void testParseMessage_EmptyMap() throws IOException {
        String message = "Invalid JSON";
        Map<String, Object> result = enrichmentService.parseMessage(message);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessMessage() throws JSONException, JsonProcessingException {
        Long accountNumber = 75892449412L;
        Long cifNumber = 74212869153L;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountNumber", accountNumber);
        jsonObject.put("cifNumber", cifNumber);
        String recordValue = jsonObject.toString();
        ConsumerRecord<Long, Object> record = new ConsumerRecord<>("topic", 1, 1L, 1L, recordValue);
        EnrichmentModel enrichmentModel = new EnrichmentModel();
        enrichmentModel.setCifNumber(accountNumber);
        enrichmentModel.setAccountNumber(cifNumber);
        when(enrichmentRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(enrichmentModel));
        enrichmentService.processMessage(record);
        verify(messageToKafka, times(1)).sendMessageToTopic(anyString(), anyString());
    }

    @Test
    void testProcessMessage_EmptyMap() throws JSONException {
        String message = "Invalid JSON";
        ConsumerRecord<Long, Object> record = new ConsumerRecord<>("topic", 1, 1L, 1L, message);
        enrichmentService.processMessage(record);
        verify(messageToKafka, never()).sendMessageToTopic(anyString(), anyString());
    }

    @Test
    void testProcessMessage_NoDataFound() throws JSONException {
        Long accountNumber = 75892449412L;
        Long cifNumber = 74212869153L;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountNumber", accountNumber);
        jsonObject.put("cifNumber", cifNumber);
        String recordValue = jsonObject.toString();
        ConsumerRecord<Long, Object> record = new ConsumerRecord<>("topic", 1, 1L, 1L, recordValue);
        when(enrichmentRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        enrichmentService.processMessage(record);

        verify(messageToKafka, never()).sendMessageToTopic(anyString(), anyString());
    }

    @Test
    void testProcessAndSendmessagetoTopic() throws JSONException, JsonProcessingException {
        Long accountNumber = 75892449412L;
        Long cifNumber = 74212869153L;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountNumber", accountNumber);
        jsonObject.put("cifNumber", cifNumber);
        String recordValue = jsonObject.toString();
        ConsumerRecord<Long, Object> record = new ConsumerRecord<>("topic", 1, 1L, 1L, recordValue);

        EnrichmentModel enrichmentModel = new EnrichmentModel();
        enrichmentModel.setCifNumber(cifNumber);
        enrichmentModel.setAccountNumber(accountNumber);
        when(enrichmentRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(enrichmentModel));

        enrichmentService.processMessage(record);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageToKafka, times(1)).sendMessageToTopic(eq("enrichment-topic"), messageCaptor.capture());

        JSONObject capturedJsonObject = new JSONObject(messageCaptor.getValue());
        assertEquals(accountNumber, capturedJsonObject.getLong("accountNumber"));
        assertEquals(cifNumber, capturedJsonObject.getLong("cifNumber"));
    }
}