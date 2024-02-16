package com.oms.config;

import com.oms.KafkaProducerConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9095", "port=9092"})
class KafkaProducerConfigTest {

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Test
    public void testCreateTopic() {
        NewTopic topic = kafkaProducerConfig.createTopic();
        assertEquals("request-topic", topic.name());
        assertEquals(3, topic.numPartitions());
        assertEquals((short) 1, topic.replicationFactor());
    }

    @Test
    public void testProducerConfig() {
        Map<String, Object> props = kafkaProducerConfig.producerConfig();
        assertEquals("localhost:9092", props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", props.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", props.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

}