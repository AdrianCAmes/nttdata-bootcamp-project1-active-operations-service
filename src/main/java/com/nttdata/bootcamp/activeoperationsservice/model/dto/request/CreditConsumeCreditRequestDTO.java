package com.nttdata.bootcamp.activeoperationsservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreditConsumeCreditRequestDTO {
    private String id;
    private Double amount;
}
