package com.oms.service;

import com.oms.SendMessageToKafka;
import com.oms.exception.PreferenceException;
import com.oms.model.CustomerPreference;
import com.oms.repository.PreferenceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.meta.When;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PreferenceServiceTest {
    @InjectMocks
    PreferenceService preferenceService;
    @Mock
    PreferenceRepository preferenceRepository;
    @Mock
    SendMessageToKafka messageToKafka;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
@Test
@DisplayName("Test for processMessage method")
void processMessageTest() {
    ConsumerRecord<Long, Object> mockConsumerRecord = mock(ConsumerRecord.class);
    when(mockConsumerRecord.value()).thenReturn("{\"accountNumber\":123456789,\"cifNumber\":123456789}");
    CustomerPreference mockPreference = mock(CustomerPreference.class);
    when(mockPreference.getPreferredchannel()).thenReturn("email");
    when(mockPreference.getPreferredAddress()).thenReturn("Jaipur");
    when(mockPreference.getName()).thenReturn("Harshit Harit");
    when(preferenceRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong()))
            .thenReturn(Collections.singletonList(mockPreference));
    preferenceService.processMessage(mockConsumerRecord);
    verify(preferenceRepository, times(1)).findByAccountNumberAndCifNumber(anyLong(), anyLong());
    verify(messageToKafka, times(1)).sendMessageToTopic(anyString(), anyString());
}
    @Test
    void processMessageTestWithInvalidMessage() {
        ConsumerRecord<Long, Object> mockConsumerRecord = mock(ConsumerRecord.class);
        when(mockConsumerRecord.value()).thenReturn("{\"accountNumber\":123456789,\"cifNumber\":123456789");

        try {
            preferenceService.processMessage(mockConsumerRecord);
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("setPreference should send message when valid data is provided")
    void setPreferenceShouldSendMessageWhenValidDataIsProvided() {
        String preferredChannel = "email";
        String preferredAddress = "Jaipur";
        String name = "Harshit Harit";
        preferenceService.setPreference(preferredChannel, preferredAddress, name);
        verify(messageToKafka, times(1)).sendMessageToTopic(anyString(), anyString());
    }

    @Test
    @DisplayName("setPreference should throw exception when preferred channel is null")
    void setPreferenceShouldThrowExceptionWhenPreferredChannelIsNull() {
        String preferredChannel = null;
        String preferredAddress = "Jaipur";
        String name = "Harshit Harit";
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference(preferredChannel, preferredAddress, name));
    }

    @Test
    @DisplayName("setPreference should throw exception when preferred address is null")
    void setPreferenceShouldThrowExceptionWhenPreferredAddressIsNull() {
        String preferredChannel = "email";
        String preferredAddress = null;
        String name = "Harshit Harit";
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference(preferredChannel, preferredAddress, name));
    }

    @Test
    @DisplayName("setPreference should throw exception when name is null")
    void setPreferenceShouldThrowExceptionWhenNameIsNull() {
        String preferredChannel = "email";
        String preferredAddress = "Jaipur";
        String name = null;
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference(preferredChannel, preferredAddress, name));
    }
    @Test
    @DisplayName("parseMessage should return map when valid message is provided")
    void parseMessageShouldReturnMapWhenValidMessageIsProvided() {
        String message = "{\"accountNumber\":123456789,\"cifNumber\":123456789}";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("accountNumber", 123456789);
        expectedMap.put("cifNumber", 123456789);
        Map<String, Object> actualMap = preferenceService.parseMessage(message);
        assertEquals(expectedMap, actualMap);

        if(expectedMap.equals(actualMap)) {
            System.out.println("Test passed: The actual map matches the expected map.");
        } else {
            System.out.println("Test failed: The actual map does not match the expected map.");
        }
    }
}