package com.oms.service;


import com.oms.model.CustomerPreference;
import com.oms.repository.PreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    @Autowired
    private PreferenceRepository preferenceRepository;

    public CustomerPreference insertPreference(CustomerPreference customerPreference) {
        return preferenceRepository.save(customerPreference);
    }
}
