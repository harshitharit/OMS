package com.oms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.SendMessageToKafka;
import com.oms.model.EnrichmentModel;
import com.oms.repository.EnrichmentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class EnrichmentServiceTest {

    @Mock
    private SendMessageToKafka messageToKafka;

    @Mock
    private EnrichmentRepository enrichmentRepository;

    @InjectMocks
    private EnrichmentService enrichmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test

    void testProcessMessage() throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(Collections.singletonList(new EnrichmentModel()));
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
}