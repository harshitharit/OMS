package com.oms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "Enrichment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrichmentModel {
    @Id
    private String id;
    private String name;
    private long accountNumber;
    private String permanentAddress;
    private String currentAddress;
    private String place;
    private LocalDate bankHolderSince;
    private String channel;
    private LocalDate dateofBirth;
    private long cifNumber;
    private String email;
}
