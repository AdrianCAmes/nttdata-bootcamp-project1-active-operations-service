package com.nttdata.bootcamp.activeoperationsservice.utils.impl;

import com.nttdata.bootcamp.activeoperationsservice.model.Customer;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import com.nttdata.bootcamp.activeoperationsservice.utils.CustomerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerUtilsImpl implements CustomerUtils {
    public Customer customerCustomerServiceDTOToCustomer(CustomerCustomerServiceResponseDTO customerDTO) {
        return Customer.builder()
                .id(customerDTO.getId())
                .type(customerDTO.getType())
                .status(customerDTO.getStatus())
                .build();
    }

    public CustomerCustomerServiceResponseDTO customerToCustomerCustomerServiceResponseDTO(Customer customer) {
        return CustomerCustomerServiceResponseDTO.builder()
                .id(customer.getId())
                .type(customer.getType())
                .status(customer.getStatus())
                .build();
    }
}