package com.nttdata.bootcamp.activeoperationsservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BillingDetails {
    private Double interestPercentage;
    private Integer closingDay;
}