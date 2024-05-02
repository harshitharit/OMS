package com.oms.repository;

import com.oms.model.EnrichmentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface EnrichmentRepository extends MongoRepository<EnrichmentModel, Long> {
    List<EnrichmentModel> findByAccountNumberAndCifNumber(Long accountNumber, Long cifNumber);
}
