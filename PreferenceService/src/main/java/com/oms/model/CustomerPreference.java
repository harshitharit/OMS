package com.oms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
