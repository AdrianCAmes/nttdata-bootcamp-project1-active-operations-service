package com.nttdata.bootcamp.activeoperationsservice.utils.impl;

import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.Customer;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.utils.CreditUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditUtilsImpl implements CreditUtils {
    @Override
    public Credit creditCreateRequestDTOToCredit(CreditCreateRequestDTO creditDTO) {
        return Credit.builder()
                .customer(Customer.builder().id(creditDTO.getCustomerId()).build())
                .fullGrantedAmount(creditDTO.getFullGrantedAmount())
                .creditCardNumber(UUID.randomUUID().toString())
                .issueDate(creditDTO.getIssueDate())
                .dueDate(creditDTO.getDueDate())
                .billingDetails(creditDTO.getBillingDetails())
                .build();
    }

    @Override
    public Credit creditUpdateRequestDTOToCredit(CreditUpdateRequestDTO creditDTO) {
        return Credit.builder()
                .id(creditDTO.getId())
                .status(creditDTO.getStatus())
                .fullGrantedAmount(creditDTO.getFullGrantedAmount())
                .availableAmount(creditDTO.getAvailableAmount())
                .dueDate(creditDTO.getDueDate())
                .operations(creditDTO.getOperations())
                .billingDetails(creditDTO.getBillingDetails())
                .build();
    }

    @Override
    public CreditCreateRequestDTO creditToCreditCreateRequestDTO(Credit credit) {
        return CreditCreateRequestDTO.builder()
                .customerId(credit.getCustomer().getId())
                .fullGrantedAmount(credit.getFullGrantedAmount())
                .issueDate(credit.getIssueDate())
                .dueDate(credit.getDueDate())
                .billingDetails(credit.getBillingDetails())
                .build();
    }

    @Override
    public CreditUpdateRequestDTO creditToCreditUpdateCreateRequestDTO(Credit credit) {
        return CreditUpdateRequestDTO.builder()
                .id(credit.getId())
                .status(credit.getStatus())
                .fullGrantedAmount(credit.getFullGrantedAmount())
                .availableAmount(credit.getAvailableAmount())
                .dueDate(credit.getDueDate())
                .operations(credit.getOperations())
                .billingDetails(credit.getBillingDetails())
                .build();

    }

    @Override
    public Credit fillCreditWithCreditUpdateRequestDTO(Credit credit, CreditUpdateRequestDTO creditDTO) {
        credit.setId(creditDTO.getId());
        credit.setStatus(creditDTO.getStatus());
        credit.setFullGrantedAmount(creditDTO.getFullGrantedAmount());
        credit.setAvailableAmount(creditDTO.getAvailableAmount());
        credit.setDueDate(creditDTO.getDueDate());
        credit.setOperations(creditDTO.getOperations());
        credit.setBillingDetails(creditDTO.getBillingDetails());
        return credit;
    }
}
