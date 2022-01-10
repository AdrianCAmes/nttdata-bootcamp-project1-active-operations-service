package com.nttdata.bootcamp.activeoperationsservice.model;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Operation {
    private String operationNumber;
    private Date time;
    private String type;
    private Double amount;
    private BillingOrder billingOrder;
}