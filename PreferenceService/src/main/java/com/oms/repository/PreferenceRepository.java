package com.oms.repository;

import com.oms.model.PreferenceModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PreferenceRepository extends MongoRepository<PreferenceModel ,String>
{
    List<PreferenceModel> findByChannel(String channel);

}
