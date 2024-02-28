package com.oms;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
 @Value("${spring.kafka.producer.bootstrap-servers}")
 private String bootstrapServers;
 @Value("${spring.kafka.producer.key-serializer}")
 private String keySerializer;
 @Value("${spring.kafka.producer.value-serializer}")
 private String valueSerializer;
 @Value("${spring.kafka.consumer.value-deserializer}")
 private String valueDeserializer;
 @Value("${spring.kafka.consumer.key-deserializer}")
 private String keyDeserializer;

 @Bean
	public  SendMessageToKafka messageToKafka(){
		return new SendMessageToKafka();
	}

	@Bean
	public NewTopic createTopic(){
		return new NewTopic("request-topic", 3, (short) 1);
	}
	@Bean
    public Map<String,Object> producerConfig(){
  Map<String,Object> props=new HashMap<>();
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
  
  return props;
}
	@Bean
	public Map<String,Object> consumerConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "ECM-OM");
		return props;
	}
	@Bean
	public ConsumerFactory<String,Object> consumerFactory(){
		return new DefaultKafkaConsumerFactory<>(consumerConfig());
	}
	@Bean
	public ProducerFactory<String,Object> producerFactory(){
		return new DefaultKafkaProducerFactory<>(producerConfig());
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}