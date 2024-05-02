package com.oms.repository;

import com.oms.model.CustomerPreference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PreferenceRepository extends MongoRepository<CustomerPreference, Long> {
    List<CustomerPreference> findByAccountNumberAndCifNumber(long accountNumber, long cifNumber);
}
