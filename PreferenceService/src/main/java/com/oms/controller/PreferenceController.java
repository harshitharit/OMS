package com.oms.controller;
import com.oms.model.EnrichmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PreferenceController {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceController.class);
    @Autowired
    private EnrichmentModel enrichmentModel;

    @GetMapping("/presons/{channel}")
    public EnrichmentModel getPersonByChannel(String channel) {
        logger.info("Fetching EnrichmentModel for channel: {}", channel);
        return enrichmentModel;

    }
}
