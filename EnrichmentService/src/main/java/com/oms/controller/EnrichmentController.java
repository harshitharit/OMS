package com.oms.controller;


import com.oms.model.EnrichmentModel;
import com.oms.repository.EnrichmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
public class EnrichmentController {
    @Autowired
    private EnrichmentRepository enrichmentRepository;

    @GetMapping("/persons/{id}")
    public EnrichmentModel getPersonById(@PathVariable String id) {
        Optional<EnrichmentModel> optionalEnrichmentModel = enrichmentRepository.findById(id);
        if (optionalEnrichmentModel.isPresent()) {
            return optionalEnrichmentModel.get();
        } else {
            return null;

        }
    }
}
