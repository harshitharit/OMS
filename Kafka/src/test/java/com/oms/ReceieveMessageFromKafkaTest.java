package com.oms;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class ReceieveMessageFromKafkaTest {
    @InjectMocks
    private ReceieveMessageFromKafka receieveMessageFromKafka;
    @Mock
    private ConsumerFactory<String, Object> consumerFactory;
    @Mock
    private KafkaConfig kafkaConfig;

    @Mock
    private Consumer<String, Object> consumer;

    @BeforeEach
    void setUp() {
        consumerFactory = mock(ConsumerFactory.class);
        consumer = mock(Consumer.class);
        when(consumerFactory.createConsumer()).thenReturn(consumer);
        receieveMessageFromKafka = new ReceieveMessageFromKafka(consumerFactory);
    }

    @Test
    void subscribeToTopics() {
        List<String> topics = Arrays.asList("request-Topic", "Enrichment-Topic", "preference-topic", "enrichment-topic");
        receieveMessageFromKafka.subscribeToTopics(topics);
        verify(consumer, times(1)).subscribe(topics);
    }


    @Test
    void consumeMessages() {
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("request-topic", 1, 1, "key", "Hi harshit");
        Map<TopicPartition, List<ConsumerRecord<String, Object>>> recordsMap = new HashMap<>();
        recordsMap.put(new TopicPartition("request-topic", 1), Collections.singletonList(record));
        ConsumerRecords<String, Object> records = new ConsumerRecords<>(recordsMap);
        when(consumer.poll(Duration.ofMillis(1000))).thenReturn(records);
        List<ConsumerRecord<String, Object>> messages = receieveMessageFromKafka.consumeMessages();
        verify(consumer, times(1)).poll(Duration.ofMillis(1000));
        assertEquals(1, messages.size());
        assertEquals("Hi harshit", messages.get(0).value());
    }
}