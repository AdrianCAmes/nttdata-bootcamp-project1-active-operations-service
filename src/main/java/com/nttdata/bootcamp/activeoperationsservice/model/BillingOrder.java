package com.nttdata.bootcamp.activeoperationsservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BillingOrder {
    public Double calculatedAmount;
    public String cycle;
    public String status;
}