package com.oms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SendMessageToKafkaTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private SendMessageToKafka sendMessageToKafka;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSendMessageToTopicSuccessfully() {
        String topic = "testTopic";
        String message = "Hi Harshit";
        SendResult<String, Object> sendResult = mock(SendResult.class);
        when(kafkaTemplate.send(any(String.class), any(Object.class))).thenReturn(CompletableFuture.completedFuture(sendResult));
        sendMessageToKafka.sendMessageToTopic(topic, message);
        verify(kafkaTemplate, times(1)).send(topic, message);
    }



    @Test
    void shouldHandleExceptionWhenSendingMessage() {
        String topic = "testTopic";
        String message = "Hi Harshit";
        when(kafkaTemplate.send(any(String.class), any(Object.class))).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> {
            sendMessageToKafka.sendMessageToTopic(topic, message);
        });
        verify(kafkaTemplate, times(1)).send(topic, message);
    }
}