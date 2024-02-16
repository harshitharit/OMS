package com.oms.service;


import com.oms.SendMessageToKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrichmentService {
    @Autowired
   private SendMessageToKafka sendMessageToKafka;




}
