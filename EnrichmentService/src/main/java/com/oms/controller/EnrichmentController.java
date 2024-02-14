package com.oms.controller;


import com.oms.model.EnrichmentModel;
import com.oms.repository.EnrichmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
@RestController
public class EnrichmentController {
    private static final Logger logger = LoggerFactory.getLogger(EnrichmentController.class);
    @Autowired
    private EnrichmentRepository enrichmentRepository;

    @GetMapping("/persons/{accountNumber}")
    public EnrichmentModel getPersonByAccountNumber(@PathVariable Long accountNumber) {
        logger.info("Fetching EnrichmentModel for accountNumber: {}", accountNumber);
        Optional<EnrichmentModel> optionalEnrichmentModel = enrichmentRepository.findByAccountNumber(accountNumber);
        if (optionalEnrichmentModel.isPresent()) {
            return optionalEnrichmentModel.get();
        } else {
            logger.error("EnrichmentModel not found for accountNumber: {}", accountNumber);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EnrichmentModel not found");
        }
    }
}
