package com.nttdata.bootcamp.activeoperationsservice.model.dto.request;

import com.nttdata.bootcamp.activeoperationsservice.model.BillingDetails;
import com.nttdata.bootcamp.activeoperationsservice.model.Operation;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreditUpdateRequestDTO {
    private String id;
    private String status;
    private Double fullGrantedAmount;
    private Double availableAmount;
    private Date dueDate;
    private ArrayList<Operation> operations;
    private BillingDetails billingDetails;
}
