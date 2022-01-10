package com.nttdata.bootcamp.activeoperationsservice.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreditFindBalancesResponseDTO {
    private String id;
    private Double fullGrantedAmount;
    private Double availableAmount;
}