package com.nttdata.bootcamp.activeoperationsservice.utils;

import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;

public interface CreditUtils {
    Credit creditCreateRequestDTOToCredit(CreditCreateRequestDTO creditDTO);
    Credit creditUpdateRequestDTOToCredit(CreditUpdateRequestDTO creditDTO);
    CreditCreateRequestDTO creditToCreditCreateRequestDTO(Credit credit);
    CreditUpdateRequestDTO creditToCreditUpdateCreateRequestDTO(Credit credit);
    Credit fillCreditWithCreditUpdateRequestDTO(Credit credit, CreditUpdateRequestDTO creditDTO);
}
