package com.nttdata.bootcamp.activeoperationsservice.utils;

import com.nttdata.bootcamp.activeoperationsservice.model.Customer;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CustomerCustomerServiceResponseDTO;

public interface CustomerUtils {
    Customer customerCustomerServiceDTOToCustomer(CustomerCustomerServiceResponseDTO customerDTO);
    CustomerCustomerServiceResponseDTO customerToCustomerCustomerServiceResponseDTO(Customer customer);
}
