package com.nttdata.bootcamp.activeoperationsservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Customer {
    private String id;
    private CustomerType customerType;
    private String status;
}