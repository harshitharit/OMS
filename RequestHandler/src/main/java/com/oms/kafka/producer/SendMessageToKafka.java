package com.oms.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class SendMessageToKafka {
	@Autowired
	private KafkaTemplate<String,Object> template;

	public void sendMessageToTopic(String message){
		CompletableFuture<SendResult<String, Object>> future = template.send("request-topic", message);
		future.whenComplete((result,ex)->{
			if (ex == null) {
				System.out.println("Sent message=["+message+"] with offset=["+ result.getRecordMetadata().offset()+"]");
			} else {
				System.out.println("Unable to send message=["+message+"] due to : "+ ex.getMessage());
			}
		});
	}
}

