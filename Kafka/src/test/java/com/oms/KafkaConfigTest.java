package com.oms;


import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = {KafkaConfig.class})
@EmbeddedKafka(brokerProperties = {"ListenersPLAINTEXT://localhost:9092"})
class KafkaConfigTest {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Test
    void testCreateTopic() {
        NewTopic topic = kafkaConfig.createTopic();
        assertEquals("request-topic", topic.name());
        assertEquals(1, topic.numPartitions());
        assertEquals((short) 1, topic.replicationFactor());
    }

    @Test
    void testCreateEnrichmentTopic() {
        NewTopic topic = kafkaConfig.createEnrichmentTopic();
        assertEquals("Enrichment-Topic", topic.name());
        assertEquals(1, topic.numPartitions());
        assertEquals((short) 1, topic.replicationFactor());
    }

    @Test
    void testProducerConfig() {
        Map<String, Object> props = kafkaConfig.producerConfig();
        assertEquals("localhost:9092", props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", props.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", props.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void testConsumerConfig() {
        Map<String, Object> props = kafkaConfig.consumerConfig();
        assertEquals("localhost:9092", props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", props.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", props.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals("ECM-OM", props.get(ConsumerConfig.GROUP_ID_CONFIG));
    }

    @Test
    void testConsumerFactory() {
        ConsumerFactory<String, Object> factory = kafkaConfig.consumerFactory();
        assertNotNull(factory);
    }

    @Test
    public void testProducerFactory() {
        ProducerFactory<String, Object> factory = kafkaConfig.producerFactory();
        assertNotNull(factory);
    }

    @Test
    public void testKafkaTemplate() {
        KafkaTemplate<String, Object> template = kafkaConfig.kafkaTemplate();
        assertNotNull(template);
    }
}