package com.oms.service;


import com.oms.ReceieveMessageFromKafka;
import com.oms.SendMessageToKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrichmentService {
    @Autowired
   private SendMessageToKafka sendMessageToKafka;

    public void sendMessage(String topic, String message){
        sendMessageToKafka.sendMessageToTopic(topic, message);
    }





}
