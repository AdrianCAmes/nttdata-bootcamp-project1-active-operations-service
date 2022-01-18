package com.nttdata.bootcamp.activeoperationsservice.utils;

import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditConsumeCreditRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CreditFindBalancesResponseDTO;

public interface CreditUtils {
    Credit creditCreateRequestDTOToCredit(CreditCreateRequestDTO creditDTO);
    Credit creditUpdateRequestDTOToCredit(CreditUpdateRequestDTO creditDTO);
    Credit creditFindBalancesResponseDTOToCredit(CreditFindBalancesResponseDTO creditDTO);
    CreditCreateRequestDTO creditToCreditCreateRequestDTO(Credit credit);
    CreditUpdateRequestDTO creditToCreditUpdateCreateRequestDTO(Credit credit);
    CreditFindBalancesResponseDTO creditToCreditFindBalancesResponseDTO(Credit credit);
    Credit fillCreditWithCreditUpdateRequestDTO(Credit credit, CreditUpdateRequestDTO creditDTO);
    Credit fillCreditWithCreditConsumeCreditRequestDTO(Credit credit, CreditConsumeCreditRequestDTO creditDTO);
}
