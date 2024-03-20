package com.oms.service;

import com.oms.SendMessageToKafka;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PreferenceServiceTest {

    @InjectMocks
    private PreferenceService preferenceService;

    @Mock
    private SendMessageToKafka messageToKafka;

    @Test
    public void testProcessMessage() {
        ConsumerRecord<Long, Object> consumerRecord = new ConsumerRecord<>("topic", 1, 1L, 1L, "testMessage");
        preferenceService.processMessage(consumerRecord);

        verify(messageToKafka, times(1)).sendMessageToTopic("preference-topic", "preferenceMessage");

    }

}

