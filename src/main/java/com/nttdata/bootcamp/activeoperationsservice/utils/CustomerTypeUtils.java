package com.nttdata.bootcamp.activeoperationsservice.utils;

import com.nttdata.bootcamp.activeoperationsservice.model.CustomerType;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CustomerTypeCustomerServiceResponseDTO;

public interface CustomerTypeUtils {
    CustomerTypeCustomerServiceResponseDTO customerTypeToCustomerTypeCustomerServiceResponseDTO(CustomerType customerType);
    CustomerType customerTypeCustomerServiceResponseDTOToCustomerType(CustomerTypeCustomerServiceResponseDTO customerTypeDTO);
}
