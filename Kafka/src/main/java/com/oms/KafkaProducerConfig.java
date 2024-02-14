package com.oms;

import com.oms.SendMessageToKafka;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
 @Value("${spring.kafka.producer.bootstrap-servers}")
 private String bootstrapServers;
 @Value("${spring.kafka.producer.key-serializer}")
 private String keySerializer;
 @Value("${spring.kafka.producer.value-serializer}")
 private String valueSerializer;
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
	public ProducerFactory<String,Object> producerFactory(){
		return new DefaultKafkaProducerFactory<>(producerConfig());
	}

	@Bean
	public KafkaTemplate<String,Object> kafkaTemplate(){
		return new KafkaTemplate<>(producerFactory());
	}
	@Bean
	public NewTopic enrichmentTopic(){
		return new NewTopic("Enrichment-topic", 3, (short) 1);
	}

	@Bean
	public NewTopic preferencetopic(){
		return new NewTopic("Prefernce-topic", 3, (short) 1);
	}

	@Bean
	public NewTopic generatorTopic(){
		return new NewTopic("Generator-topic", 3, (short) 1);
	}
	@Bean
	public NewTopic distributionTopic(){
		return new NewTopic("Distribution-topic", 3, (short) 1);
	}
}