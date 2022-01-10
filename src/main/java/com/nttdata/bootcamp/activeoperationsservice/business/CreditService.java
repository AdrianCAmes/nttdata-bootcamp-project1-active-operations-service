package com.nttdata.bootcamp.activeoperationsservice.business;

import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditService {
    Mono<Credit> create(CreditCreateRequestDTO creditDTO);
    Mono<Credit> findById(String id);
    Flux<Credit> findAll();
    Mono<Credit> update(CreditUpdateRequestDTO creditDTO);
    Mono<Credit> removeById(String id);
    Mono<CustomerCustomerServiceResponseDTO> findByIdCustomerService(String id);
    Flux<Credit> findByCustomerId(String id);
}