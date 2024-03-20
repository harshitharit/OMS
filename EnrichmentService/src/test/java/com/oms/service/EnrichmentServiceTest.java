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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrichmentServiceTest {

    @Mock
    private SendMessageToKafka messageToKafka;

    @Mock
    private EnrichmentRepository enrichmentRepository;
    @Mock
    private EnrichmentModel enrichmentModel;

    @InjectMocks
    private EnrichmentService enrichmentService;

    @Test
    void testProcessMessage() throws JSONException, JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountNumber", 75892449512L);
        jsonObject.put("cifNumber", 74212860153L);
        String recordValue = jsonObject.toString();
        ConsumerRecord<Long, Object> record = new ConsumerRecord<>("topic", 1, 1L, 1L, recordValue);
        EnrichmentModel enrichmentModel = new EnrichmentModel();
        enrichmentModel.setCifNumber(74212860153L);
        enrichmentModel.setAccountNumber(75892449512L);
        when(enrichmentRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(enrichmentModel));
        enrichmentService.processMessage(record);
        verify(messageToKafka, times(1)).sendMessageToTopic(anyString(), anyString());
    }

    @Test
    void testProcessMessage_Exception() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountNumber", 75892449512L);
        jsonObject.put("cifNumber", 74212860153L);
        String recordValue = jsonObject.toString();
        ConsumerRecord<Long, Object> record = new ConsumerRecord<>("topic", 1, 1L, 1L, recordValue);

        when(enrichmentRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> enrichmentService.processMessage(record));
    }

    @Test
    void testProcessAndSendmessagetoTopic() throws JSONException, JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountNumber", 75892449512L);
        jsonObject.put("cifNumber", 74212860153L);
        String recordValue = jsonObject.toString();
        ConsumerRecord<Long, Object> record = new ConsumerRecord<>("topic", 1, 1L, 1L, recordValue);

        EnrichmentModel enrichmentModel = new EnrichmentModel();
        enrichmentModel.setCifNumber(74212860153L);
        enrichmentModel.setAccountNumber(75892449512L);
        when(enrichmentRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(enrichmentModel));

        enrichmentService.processMessage(record);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageToKafka, times(1)).sendMessageToTopic(topicCaptor.capture(), messageCaptor.capture());

        assertEquals("enrichment-topic", topicCaptor.getValue());

        ObjectMapper mapper = new ObjectMapper();
        EnrichmentModel capturedEnrichmentModel = mapper.readValue(messageCaptor.getValue(), EnrichmentModel.class);
        assertEquals(74212860153L, capturedEnrichmentModel.getCifNumber());
        assertEquals(75892449512L, capturedEnrichmentModel.getAccountNumber());
    }
}