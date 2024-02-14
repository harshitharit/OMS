package com.oms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Enrichment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrichmentModel {
    @Id
    private String id;
    private String name;
    private Long accountNumber;
    private String permanentAddress;
    private String temporaryAddress;
    private String place;
    private Date bankHolderSince;
<<<<<<< HEAD
    private String channel;
=======
    private String channel; 
    private Date dateofBirth;
<<<<<<< HEAD
    private long cifNumber;
=======
>>>>>>> c30754d6e4ccb3097c880ffb996917efb723e4d0
>>>>>>> b084a726a567753c54a9fd2ced478ace36e4e751
}