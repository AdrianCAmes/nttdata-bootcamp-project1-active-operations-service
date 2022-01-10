package com.nttdata.bootcamp.activeoperationsservice.utils.impl;

import com.nttdata.bootcamp.activeoperationsservice.config.Constants;
import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.Customer;
import com.nttdata.bootcamp.activeoperationsservice.model.Operation;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditConsumeCreditRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CreditFindBalancesResponseDTO;
import com.nttdata.bootcamp.activeoperationsservice.utils.CreditUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditUtilsImpl implements CreditUtils {
    private final Constants constants;

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
    public Credit creditFindBalancesResponseDTOToCredit(CreditFindBalancesResponseDTO creditDTO) {
        return Credit.builder()
                .id(creditDTO.getId())
                .fullGrantedAmount(creditDTO.getFullGrantedAmount())
                .availableAmount(creditDTO.getAvailableAmount())
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
    public CreditFindBalancesResponseDTO creditToCreditFindBalancesResponseDTO(Credit credit) {
        return CreditFindBalancesResponseDTO.builder()
                .id(credit.getId())
                .fullGrantedAmount(credit.getFullGrantedAmount())
                .availableAmount(credit.getAvailableAmount())
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

    @Override
    public Credit fillCreditWithCreditConsumeCreditRequestDTO(Credit credit, CreditConsumeCreditRequestDTO creditDTO) {
        Operation operation = Operation.builder()
                .amount(creditDTO.getAmount())
                .time(new Date())
                .type(constants.getOPERATION_CONSUMPTION_TYPE())
                .operationNumber(UUID.randomUUID().toString())
                .build();

        ArrayList<Operation> operations = credit.getOperations() == null ? new ArrayList<>() : credit.getOperations();
        operations.add(operation);

        credit.setOperations(operations);
        credit.setId(creditDTO.getId());

        return credit;
    }
}
