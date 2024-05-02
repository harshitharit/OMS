package com.oms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = KafkaConfig.class)
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
    void shouldSendListOfMessagesToTopicSuccessfully() throws Exception {
        String topic = "testTopic";
        List<String> messages = Arrays.asList("Hi Harshit", "Hello World");
        SendResult<String, Object> sendResult = mock(SendResult.class);
        when(kafkaTemplate.send(any(String.class), any(Object.class))).thenReturn(CompletableFuture.completedFuture(sendResult));

        List<CompletableFuture<SendResult<String, Object>>> resultFutures = sendMessageToKafka.sendMessagesToTopic(topic, messages);

        // Wait for all futures to complete
        CompletableFuture.allOf(resultFutures.toArray(new CompletableFuture[0])).join();

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate, times(messages.size())).send(topicCaptor.capture(), messageCaptor.capture());

        List<String> capturedTopics = topicCaptor.getAllValues();
        List<Object> capturedMessages = messageCaptor.getAllValues();

        // Verify that the correct topic and messages were sent
        assertEquals(capturedTopics.size(), messages.size());
        assertEquals(capturedMessages, messages);
        for (String capturedTopic : capturedTopics) {
            assertEquals(topic, capturedTopic);
        }
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

    @Test
    void shouldHandleExceptionWhenSendingListOfMessages() {
        String topic = "testTopic";
        List<String> messages = Arrays.asList("Hi Harshit", "Hello World");
        when(kafkaTemplate.send(any(String.class), any(Object.class))).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> {
            sendMessageToKafka.sendMessagesToTopic(topic, messages);
        });
        verify(kafkaTemplate, times(1)).send(any(String.class), any(Object.class));
    }
}
