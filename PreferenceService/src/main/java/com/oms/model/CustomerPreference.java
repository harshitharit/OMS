package com.oms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Preference")
public class CustomerPreference {
    @Id
    private String id;
    private long accountNumber;
    private long cifNumber;
    private String name;
    private String preferredchannel;
    private String preferredAddress;
}
