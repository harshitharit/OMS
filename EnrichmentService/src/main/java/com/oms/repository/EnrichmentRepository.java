package com.oms.repository;

import com.oms.model.EnrichmentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EnrichmentRepository extends MongoRepository<EnrichmentModel, Long> {
    Optional<EnrichmentModel> findByAccountNumber(Long accountNumber);
}