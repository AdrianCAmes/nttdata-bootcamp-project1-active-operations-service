package com.nttdata.bootcamp.activeoperationsservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BillingOrder {
    private String id;
    private Double calculatedAmount;
    private Double amountToRefund;
    private String cycle;
    private String status;
}