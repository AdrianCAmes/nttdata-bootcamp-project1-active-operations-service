package com.nttdata.bootcamp.activeoperationsservice.business.impl;

import com.nttdata.bootcamp.activeoperationsservice.business.CreditService;
import com.nttdata.bootcamp.activeoperationsservice.config.Constants;
import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.Customer;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import com.nttdata.bootcamp.activeoperationsservice.repository.CreditRepository;
import com.nttdata.bootcamp.activeoperationsservice.utils.CreditUtils;
import com.nttdata.bootcamp.activeoperationsservice.utils.CustomerUtils;
import com.nttdata.bootcamp.activeoperationsservice.utils.errorhandling.BusinessLogicException;
import com.nttdata.bootcamp.activeoperationsservice.utils.errorhandling.ElementBlockedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final WebClient.Builder webClientBuilder;
    private final Constants constants;
    private final CreditUtils creditUtils;
    private final CustomerUtils customerUtils;

    @Override
    public Mono<Credit> create(CreditCreateRequestDTO creditDTO) {
        log.info("Start of operation to create a credit");

        if (creditDTO.getCustomerId() == null || !creditDTO.getCustomerId().isBlank()) {
            Mono<Credit> createdCredit = findByIdCustomerService(creditDTO.getCustomerId())
                    .flatMap(retrievedCustomer -> {
                        log.info("Validating credit");
                        return creditToCreateValidation(creditDTO, retrievedCustomer);
                    })
                    .flatMap(validatedCustomer -> {
                        Credit creditToCreate = creditUtils.creditCreateRequestDTOToCredit(creditDTO);
                        Customer customer = customerUtils.customerCustomerServiceDTOToCustomer(validatedCustomer);

                        creditToCreate.setCustomer(customer);
                        creditToCreate.setStatus(constants.getSTATUS_ACTIVE());
                        creditToCreate.setAvailableAmount(creditToCreate.getFullGrantedAmount());

                        log.info("Creating new credit: [{}]", creditToCreate.toString());
                        return creditRepository.insert(creditToCreate);
                    })
                    .switchIfEmpty(Mono.error(new NoSuchElementException("Customer does not exist")));

            log.info("End of operation to create a credit");
            return createdCredit;
        } else {
            log.warn("Credit does not contain a customer id");
            log.warn("Proceeding to abort create credit");
            return Mono.error(new IllegalArgumentException("Credit does not contain customer id"));
        }
    }

    @Override
    public Mono<Credit> findById(String id) {
        log.info("Start of operation to find a credit by id");

        log.info("Retrieving credit with id: [{}]", id);
        Mono<Credit> retrievedCredit = creditRepository.findById(id);
        log.info("Credit with id: [{}] was retrieved successfully", id);

        log.info("End of operation to find a credit by id");
        return retrievedCredit;
    }

    @Override
    public Flux<Credit> findAll() {
        log.info("Start of operation to retrieve all credits");

        log.info("Retrieving all credits");
        Flux<Credit> retrievedCredit = creditRepository.findAll();
        log.info("All credits retrieved successfully");

        log.info("End of operation to retrieve all credits");
        return retrievedCredit;
    }

    @Override
    public Mono<Credit> update(CreditUpdateRequestDTO creditDTO) {
        log.info("Start of operation to update a credit");

        Mono<Credit> updatedCredit = findById(creditDTO.getId())
                .flatMap(retrievedCredit -> {
                    log.info("Validating credit");
                    return creditToUpdateValidation(creditDTO, retrievedCredit);
                })
                .flatMap(validatedCredit -> {
                    Credit creditToUpdate = creditUtils.fillCreditWithCreditUpdateRequestDTO(validatedCredit, creditDTO);

                    log.info("Updating credit: [{}]", creditToUpdate.toString());
                    Mono<Credit> nestedUpdatedCredit = creditRepository.save(creditToUpdate);
                    log.info("Credit with id: [{}] was successfully updated", creditToUpdate.getId());

                    return nestedUpdatedCredit;
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Credit does not exist")));

        log.info("End of operation to update an credit");
        return updatedCredit;
    }

    @Override
    public Mono<Credit> removeById(String id) {
        log.info("Start of operation to remove a credit");

        log.info("Deleting credit with id: [{}]", id);
        Mono<Credit> removedAccount = findById(id)
                .flatMap(retrievedAccount -> creditRepository.deleteById(retrievedAccount.getId()).thenReturn(retrievedAccount));
        log.info("Credit with id: [{}] was successfully deleted", id);

        log.info("End of operation to remove a credit");
        return removedAccount;
    }

    @Override
    public Mono<CustomerCustomerServiceResponseDTO> findByIdCustomerService(String id) {
        log.info("Start of operation to retrieve customer with id [{}] from customer-info-service", id);

        log.info("Retrieving customer");
        String url = constants.getCUSTOMER_INFO_SERVICE_URL() + "/customers/" + id;
        Mono<CustomerCustomerServiceResponseDTO> retrievedCustomer = webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .build())
                .retrieve()
                .onStatus(httpStatus -> httpStatus == HttpStatus.NOT_FOUND, clientResponse -> Mono.empty())
                .bodyToMono(CustomerCustomerServiceResponseDTO.class);
        log.info("Customer retrieved successfully");

        log.info("End of operation to retrieve customer with id: [{}]", id);
        return retrievedCustomer;
    }

    @Override
    public Flux<Credit> findByCustomerId(String id) {
        log.info("Start of operation to retrieve all credits of the customer with id: [{}]", id);

        log.info("Retrieving credits");
        Flux<Credit> retrievedAccount = creditRepository.findCreditsByCustomerId(id);
        log.info("Credits retrieved successfully");

        log.info("End of operation to retrieve credits of the customer with id: [{}]", id);
        return retrievedAccount;
    }

    //region Private Helper Functions

    private Mono<CustomerCustomerServiceResponseDTO> creditToCreateValidation(CreditCreateRequestDTO creditForCreate, CustomerCustomerServiceResponseDTO customerFromMicroservice) {
        log.info("Customer exists in database");

        if (customerFromMicroservice.getStatus().contentEquals(constants.getSTATUS_BLOCKED()))
        {
            log.warn("Customer have blocked status");
            log.warn("Proceeding to abort create credit");
            return Mono.error(new ElementBlockedException("The customer have blocked status"));
        }

        if (creditForCreate.getBillingDetails() == null) {
            log.warn("Credit does not contain billing details");
            log.warn("Proceeding to abort update account");
            return Mono.error(new IllegalArgumentException("Credit does not billing details"));
        }

        if (customerFromMicroservice.getType().contentEquals(constants.getCUSTOMER_PERSONAL_TYPE()))
        {
            return findByCustomerId(customerFromMicroservice.getId())
                    .filter(retrievedAccount -> retrievedAccount.getStatus().contentEquals(constants.getSTATUS_ACTIVE()))
                    .hasElements()
                    .flatMap(haveAnAccount -> {
                        if (haveAnAccount) {
                            log.warn("Can not create more than one credit for a personal customer");
                            log.warn("Proceeding to abort create credit");
                            return Mono.error(new BusinessLogicException("Customer already have one credit"));
                        }
                        else {
                            log.info("Credit successfully validated");
                            return Mono.just(customerFromMicroservice);
                        }
                    });
        } else {
            log.info("Credit successfully validated");
            return Mono.just(customerFromMicroservice);
        }
    }

    private Mono<Credit> creditToUpdateValidation(CreditUpdateRequestDTO creditForUpdate, Credit creditInDatabase) {
        log.info("Credit exists in database");

        if (creditInDatabase.getCustomer().getStatus().contentEquals(constants.getSTATUS_BLOCKED())) {
            log.warn("Customer have blocked status");
            log.warn("Proceeding to abort update credit");
            return Mono.error(new ElementBlockedException("The customer have blocked status"));
        }

        if (creditForUpdate.getBillingDetails() == null) {
            log.warn("Credit does not contain billing details");
            log.warn("Proceeding to abort update account");
            return Mono.error(new IllegalArgumentException("Credit does not billing details"));
        }

        if (creditForUpdate.getAvailableAmount() > creditInDatabase.getFullGrantedAmount()) {
            log.warn("The credit available amount can not be bigger than the full granted amount");
            log.warn("Proceeding to abort update credit");
            return Mono.error(new BusinessLogicException("Available amount greater than full granted amount"));
        }

        log.info("Credit successfully validated");
        return Mono.just(creditInDatabase);
    }
    //endregion
}