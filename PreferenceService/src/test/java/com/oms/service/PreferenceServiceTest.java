package com.oms.service;

import com.oms.SendMessageToKafka;
import com.oms.exception.PreferenceException;
import com.oms.model.CustomerPreference;
import com.oms.repository.PreferenceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferenceServiceTest {

    @Mock
    private SendMessageToKafka messageToKafka;

    @Mock
    private PreferenceRepository preferenceRepository;

    @InjectMocks
    private PreferenceService preferenceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testParseMessage() throws Exception {
        String message = "{\"key\":\"value\"}";
        Map<String, Object> result = preferenceService.parseMessage(message);
        assertEquals(1, result.size());
        assertEquals("value", result.get("key"));
    }

    @Test
    void testProcessMessage() {
        ConsumerRecord<Long, Object> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"accountNumber\":\"123\",\"cifNumber\":\"456\"}");

        CustomerPreference preference = new CustomerPreference();
        preference.setPreferredchannel("Email");
        preference.setPreferredAddress("test@example.com");
        preference.setName("Test User");
        when(preferenceRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(preference));

        preferenceService.processMessage(consumerRecord);

        verify(messageToKafka, times(1)).sendMessageToTopic(anyString(), anyString());
    }

    @Test
    void testSendMessageToTopic() {
        preferenceService.sendMessageToTopic("test-topic", "test-message");
        verify(messageToKafka, times(1)).sendMessageToTopic("test-topic", "test-message");
    }

    @Test
    void testFormatPreferenceMessage() {
        CustomerPreference preference = new CustomerPreference();
        preference.setPreferredchannel("Email");
        preference.setPreferredAddress("test@example.com");
        preference.setName("Test User");

        String result = preferenceService.formatPreferenceMessage(preference);
        assertEquals("Preferredchannel: Email PreferredAddress: test@example.com Name: Test User", result);
    }

    @Test
    void testValidatePreference() {
        CustomerPreference preference = new CustomerPreference();
        preference.setPreferredchannel("Email");
        preference.setPreferredAddress("test@example.com");
        preference.setName("Test User");

        assertDoesNotThrow(() -> preferenceService.validatePreference(preference));
    }

    @Test
    void testValidateInput() {
        assertThrows(PreferenceException.class, () -> preferenceService.validateInput(null, "TestField"));
        assertThrows(PreferenceException.class, () -> preferenceService.validateInput("", "TestField"));
    }
    @Test
    void testProcessMessageThrowsExceptionForInvalidMessage() {
        ConsumerRecord<Long, Object> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("invalid message");

        assertThrows(PreferenceException.class, () -> preferenceService.processMessage(consumerRecord));
    }
}
