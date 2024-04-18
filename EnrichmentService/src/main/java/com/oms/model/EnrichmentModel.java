package com.oms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "Enrichment")
@Getter
@Setter
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
