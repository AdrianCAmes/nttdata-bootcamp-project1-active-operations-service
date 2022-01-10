package com.nttdata.bootcamp.activeoperationsservice.model.dto.response;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BusinessDetailsCustomerServiceResponseDTO {
    private String name;
    private String ruc;
    private AddressCustomerServiceResponseDTO address;
    private ArrayList<PersonDetailsCustomerServiceResponseDTO> representatives;
}