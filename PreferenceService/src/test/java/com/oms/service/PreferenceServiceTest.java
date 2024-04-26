package com.oms.service;

import com.oms.SendMessageToKafka;
import com.oms.exception.PreferenceException;
import com.oms.model.CustomerPreference;
import com.oms.repository.PreferenceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.meta.When;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
    @DisplayName("parseMessage should return map when valid message is provided")
    void parseMessageShouldReturnMapWhenValidMessageIsProvided() {
        String message = "{\"accountNumber\":123456789,\"cifNumber\":123456789}";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("accountNumber", 123456789);
        expectedMap.put("cifNumber", 123456789);
        Map<String, Object> actualMap = preferenceService.parseMessage(message);
        assertEquals(expectedMap, actualMap);

        if (expectedMap.equals(actualMap)) {
            System.out.println("Test passed: The actual map matches the expected map.");
        } else {
            System.out.println("Test failed: The actual map does not match the expected map.");
        }
    }

    static Stream<Arguments> provideSetPreferenceParameters() {
        return Stream.of(
                Arguments.of("email", "Jaipur", "Harshit Harit", false),
                Arguments.of(null, "Jaipur", "Harshit Harit", true),
                Arguments.of("email", null, "Harshit Harit", true),
                Arguments.of("email", "Jaipur", null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSetPreferenceParameters")
    @DisplayName("setPreference should handle valid and invalid data correctly")
    void shouldHandleValidAndInvalidDataCorrectly(String preferredChannel, String preferredAddress, String name, boolean shouldThrowException) {
        if (shouldThrowException) {
            assertThrows(PreferenceException.class, () -> preferenceService.setPreference(preferredChannel, preferredAddress, name));
        } else {
            preferenceService.setPreference(preferredChannel, preferredAddress, name);
            verify(messageToKafka, times(1)).sendMessageToTopic(anyString(), anyString());
        }
    }
}
