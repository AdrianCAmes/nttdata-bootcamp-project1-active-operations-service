package com.nttdata.bootcamp.activeoperationsservice.model.dto.request;

import com.nttdata.bootcamp.activeoperationsservice.model.BillingDetails;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreditCreateRequestDTO {
    private String customerId;
    private Double fullGrantedAmount;
    private Date issueDate;
    private Date dueDate;
    private BillingDetails billingDetails;
}