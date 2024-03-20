package com.oms.repository;

import com.oms.model.EnrichmentModel;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface EnrichmentRepository extends MongoRepository<EnrichmentModel, Long> {
List<EnrichmentModel> findByAccountNumberAndCifNumber(Long accountNumber, Long cifNumber);
}
