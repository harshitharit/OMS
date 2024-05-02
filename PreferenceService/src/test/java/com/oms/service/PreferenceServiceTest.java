package com.oms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.SendMessageToKafka;
import com.oms.exception.PreferenceException;
import com.oms.model.CustomerPreference;
import com.oms.repository.PreferenceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {

    @Mock
    private SendMessageToKafka messageToKafka;

    @Mock
    private PreferenceRepository preferenceRepository;

    @InjectMocks
    private PreferenceService preferenceService;

    @Test
    @DisplayName("Test for processMessage method with valid data")
    void processMessageTest_ValidData() throws IOException {
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
        verify(messageToKafka, times(1)).sendMessagesToTopic(anyString(), anyList());
    }

    @Test
    @DisplayName("Test for processMessage method with invalid message format")
    void processMessageTest_InvalidMessageFormat() {
        ConsumerRecord<Long, Object> mockConsumerRecord = mock(ConsumerRecord.class);
        when(mockConsumerRecord.value()).thenReturn("Invalid message format");

        assertThrows(PreferenceException.class, () -> preferenceService.processMessage(mockConsumerRecord));

        verifyNoInteractions(preferenceRepository, messageToKafka);
    }


    @Test
    @DisplayName("Test for processMessage method with empty preference list")
    void processMessageTest_EmptyPreferenceList() throws IOException {
        ConsumerRecord<Long, Object> mockConsumerRecord = mock(ConsumerRecord.class);
        when(mockConsumerRecord.value()).thenReturn("{\"accountNumber\":123456789,\"cifNumber\":123456789}");
        when(preferenceRepository.findByAccountNumberAndCifNumber(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        preferenceService.processMessage(mockConsumerRecord);

        verify(preferenceRepository, times(1)).findByAccountNumberAndCifNumber(anyLong(), anyLong());
        verifyNoInteractions(messageToKafka);
    }

    @Test
    @DisplayName("setPreference should throw exception when preferredChannel is null or empty")
    void setPreference_ThrowsException_PreferredChannelNullOrEmpty() {
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference(null, "Jaipur", "Harshit Harit"));
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference("", "Jaipur", "Harshit Harit"));
    }

    @Test
    @DisplayName("setPreference should throw exception when preferredAddress is null or empty")
    void setPreference_ThrowsException_PreferredAddressNullOrEmpty() {
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference("email", null, "Harshit Harit"));
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference("email", "", "Harshit Harit"));
    }

    @Test
    @DisplayName("setPreference should throw exception when name is null or empty")
    void setPreference_ThrowsException_NameNullOrEmpty() {
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference("email", "Jaipur", null));
        assertThrows(PreferenceException.class, () -> preferenceService.setPreference("email", "Jaipur", ""));
    }
    @Test
    @DisplayName("Test parseMessage with missing accountNumber or cifNumber")
    void testParseMessageMissingKey() {
        Map<String, Object> testCases = new HashMap<>();
        testCases.put("missingAccountNumber", "{\"cifNumber\":67890}");
        testCases.put("missingCifNumber", "{\"accountNumber\":12345}");

        for (Map.Entry<String, Object> entry : testCases.entrySet()) {
            String message = (String) entry.getValue();
            String testCaseName = entry.getKey();

            assertThrows(PreferenceException.class, () -> preferenceService.parseMessage(message),
                    "PreferenceException should be thrown when " + testCaseName);
        }
    }
}
