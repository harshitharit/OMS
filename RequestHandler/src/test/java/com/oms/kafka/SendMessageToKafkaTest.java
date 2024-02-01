package com.oms.kafka;

import com.oms.kafka.producer.SendMessageToKafka;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class SendMessageToKafkaTest {
    @InjectMocks
    private SendMessageToKafka sendMessageToKafka;
    @Mock
    private KafkaTemplate<String, Object> template;

    @Test
    void testSendMessageToTopic() throws InterruptedException {
        String message = "Hi harshit";
        sendMessageToKafka.sendMessageToTopic(message);
        sendMessageToKafka.sendMessageToTopic("request-topic");
        Thread.sleep(2000);
    }
}