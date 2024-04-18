package com.oms.controller;


import com.oms.model.CustomerPreference;
import com.oms.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class PreferenceController {
    private final DemoService preferenceService;
    @Autowired
    public PreferenceController(DemoService preferenceService){
        this.preferenceService=preferenceService;
    }
    @PostMapping("/insert")
    public ResponseEntity<?> insertPreference(@RequestBody CustomerPreference customerPreference){
       CustomerPreference cpref= preferenceService.insertPreference(customerPreference);
        return new ResponseEntity<>(cpref, HttpStatus.CREATED);
    }

}


