package com.oms.repository;

import com.oms.model.EnrichmentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PreferenceReopsitory extends MongoRepository<PreferenceReopsitory, String>{

    Optional<EnrichmentModel> findBychannel(String channel);
}
