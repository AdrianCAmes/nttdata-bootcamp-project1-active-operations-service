package com.nttdata.bootcamp.activeoperationsservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String status;
    private Customer customer;
    private Double fullGrantedAmount;
    private Double availableAmount;
    private String creditCardNumber = UUID.randomUUID().toString();
    private Date issueDate;
    private Date dueDate;
    private ArrayList<Operation> operations;
    private BillingDetails billingDetails;
}