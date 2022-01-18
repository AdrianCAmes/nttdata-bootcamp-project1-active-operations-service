package com.nttdata.bootcamp.activeoperationsservice.business.impl;

import com.nttdata.bootcamp.activeoperationsservice.business.CreditService;
import com.nttdata.bootcamp.activeoperationsservice.config.Constants;
import com.nttdata.bootcamp.activeoperationsservice.model.BillingOrder;
import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.Customer;
import com.nttdata.bootcamp.activeoperationsservice.model.Operation;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditConsumeCreditRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CreditFindBalancesResponseDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import com.nttdata.bootcamp.activeoperationsservice.repository.CreditRepository;
import com.nttdata.bootcamp.activeoperationsservice.utils.BillingOrderUtils;
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

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final WebClient.Builder webClientBuilder;
    private final Constants constants;
    private final CreditUtils creditUtils;
    private final CustomerUtils customerUtils;
    private final BillingOrderUtils billingOrderUtils;
    private SecureRandom randomInstance = new SecureRandom();

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
                        creditToCreate.setStatus(constants.getStatusActive());
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
        String url = constants.getCustomerInfoServiceUrl() + "/api/v1/customers/" + id;
        Mono<CustomerCustomerServiceResponseDTO> retrievedCustomer = webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder
                        .host(constants.getGatewayServiceUrl())
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

    @Override
    public Mono<Credit> consumeCredit(CreditConsumeCreditRequestDTO creditDTO) {
        log.info("Start to save a new credit consumption for the credit with id: [{}]", creditDTO.getId());

        Mono<Credit> updatedCredit = creditRepository.findById(creditDTO.getId())
                .flatMap(retrievedCredit -> {
                    log.info("Validating consumption operation");
                    return consumptionValidation(creditDTO, retrievedCredit);
                })
                .flatMap(validatedCredit -> {
                    Double amountToUpdate = validatedCredit.getAvailableAmount() - creditDTO.getAmount();
                    validatedCredit.setAvailableAmount(amountToUpdate);

                    Credit creditToUpdate = creditUtils.fillCreditWithCreditConsumeCreditRequestDTO(validatedCredit, creditDTO);

                    log.info("Doing consumption of [{}] to credit with id [{}]", creditDTO.getAmount(), creditDTO.getId());
                    log.info("Saving consumption into credit: [{}]", creditDTO.toString());
                    Mono<Credit> nestedUpdatedCredit = creditRepository.save(creditToUpdate);
                    log.info("Consumption was successfully saved");

                    return nestedUpdatedCredit;
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Credit does not exist")));

        log.info("End to save a new account operation for the credit with id: [{}]", creditDTO.getId());
        return updatedCredit;
    }

    @Override
    public Mono<Credit> payCredit(String billingOrderId) {
        log.info("Start of operation to pay a billing order");

        log.info("Looking for billing order");
        Mono<Credit> updatedCredit = findAll()
                .filter(retrievedCredit -> {
                    if (retrievedCredit.getOperations() != null) {
                        return retrievedCredit.getOperations()
                                .stream().
                                anyMatch(operation -> operation.getBillingOrder() != null &&
                                        operation.getBillingOrder().getStatus().equals(constants.getBillingOrderUnpaid()) &&
                                        operation.getBillingOrder().getId().contentEquals(billingOrderId));
                    }
                    return false;
                })
                .single()
                .flatMap(retrievedCredit -> {
                    log.info("Billing order exists in database");
                    ArrayList<Operation> operations = retrievedCredit.getOperations();
                    ArrayList<Operation> mappedOperations = new ArrayList<>(operations.stream()
                            .map(operation -> {
                                if (operation.getBillingOrder() != null && operation.getBillingOrder().getId().contentEquals(billingOrderId)) {
                                    log.info("Refunding amount to account");
                                    Double availableAmount = retrievedCredit.getAvailableAmount() + operation.getBillingOrder().getAmountToRefund();
                                    if (availableAmount > retrievedCredit.getFullGrantedAmount()) retrievedCredit.setAvailableAmount(retrievedCredit.getFullGrantedAmount());
                                    else retrievedCredit.setAvailableAmount(availableAmount);

                                    log.info("Updating payment operation");
                                    operation.setTime(new Date());
                                    operation.setOperationNumber(UUID.randomUUID().toString());
                                    operation.setAmount(operation.getBillingOrder().getCalculatedAmount());

                                    BillingOrder billingOrder = operation.getBillingOrder();
                                    billingOrder.setStatus(constants.getBillingOrderPaid());
                                    operation.setBillingOrder(billingOrder);
                                    operation.setOperationNumber(UUID.randomUUID().toString());
                                }
                                return operation;
                            }).collect(Collectors.toList()));

                    retrievedCredit.setOperations(mappedOperations);
                    log.info("Saving payment into credit: [{}]", retrievedCredit.toString());
                    Mono<Credit> nestedUpdatedCredit =  creditRepository.save(retrievedCredit);
                    log.info("Payment was successfully saved");

                    return nestedUpdatedCredit;
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Billing order does not exist")));

        log.info("End of operation to pay a billing order");
        return updatedCredit;
    }

    @Override
    public Mono<Credit> generateBillingOrder(String creditId) {
        log.info("Start to generate a billing order for the credit with id: [{}]", creditId);

        Mono<Credit> updatedCredit = creditRepository.findById(creditId)
                .flatMap(retrievedCredit -> {
                    log.info("Validating credit");
                    return generateBillingOrderValidation(retrievedCredit);
                })
                .flatMap(validatedCredit -> {
                    BillingOrder billingOrder = new BillingOrder();

                    // Generate random amount to refund
                    Double randomAmountToRefund = (validatedCredit.getFullGrantedAmount() - validatedCredit.getAvailableAmount()) * randomInstance.nextDouble();
                    Double roundedAmountToRefund = billingOrderUtils.roundDouble(randomAmountToRefund, 2);
                    billingOrder.setAmountToRefund(roundedAmountToRefund);

                    // Generate amount with interests
                    Double randomConsumeAmountWithInterests = billingOrderUtils.applyInterests(randomAmountToRefund, validatedCredit.getBillingDetails().getInterestPercentage());
                    Double roundedConsumeAmountWithInterests = billingOrderUtils.roundDouble(randomConsumeAmountWithInterests, 2);
                    billingOrder.setCalculatedAmount(roundedConsumeAmountWithInterests);

                    // Set default values for billing order
                    billingOrder.setStatus(constants.getBillingOrderUnpaid());
                    billingOrder.setId(UUID.randomUUID().toString());
                    billingOrder.setCycle(Calendar.MONTH + "/" + Calendar.YEAR);

                    Operation operation = Operation.builder()
                            .type(constants.getOperationPaymentType())
                            .billingOrder(billingOrder)
                            .build();

                    ArrayList<Operation> operations = validatedCredit.getOperations() == null ? new ArrayList<>() : validatedCredit.getOperations();
                    operations.add(operation);
                    validatedCredit.setOperations(operations);

                    log.info("Generating billing order into credit with id: [{}]", creditId);
                    Mono<Credit> nestedUpdatedCredit = creditRepository.save(validatedCredit);
                    log.info("Generation was successful");

                    return nestedUpdatedCredit;
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Credit does not exist")));

        log.info("End to generate a new billing order for the credit with id: [{}]", creditId);
        return updatedCredit;
    }

    @Override
    public Flux<Operation> findOperationsByCreditId(String id) {
        log.info("Start of operation to retrieve all operations from credit with id: [{}]", id);

        log.info("Retrieving all operations");
        Flux<Operation> retrievedOperations = findById(id)
                .filter(retrievedCredit -> retrievedCredit.getOperations() != null)
                .flux()
                .flatMap(retrievedCredit -> Flux.fromIterable(retrievedCredit.getOperations()));
        log.info("Operations retrieved successfully");

        log.info("End of operation to retrieve operations from credit with id: [{}]", id);
        return retrievedOperations;
    }

    @Override
    public Flux<CreditFindBalancesResponseDTO> findBalancesByCustomerId(String id) {
        log.info("Start of operation to retrieve credit balances from customer with id: [{}]", id);

        log.info("Retrieving credit balances");
        Flux<CreditFindBalancesResponseDTO> retrievedBalances = findByCustomerId(id)
                .map(creditUtils::creditToCreditFindBalancesResponseDTO);
        log.info("Credits retrieved successfully");

        log.info("End of operation to retrieve credit balances from customer with id: [{}]", id);
        return retrievedBalances;
    }

    //region Private Helper Functions
    private Mono<CustomerCustomerServiceResponseDTO> creditToCreateValidation(CreditCreateRequestDTO creditForCreate, CustomerCustomerServiceResponseDTO customerFromMicroservice) {
        log.info("Customer exists in database");

        if (customerFromMicroservice.getStatus().contentEquals(constants.getStatusBlocked()))
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

        if (customerFromMicroservice.getCustomerType().getGroup().contentEquals(constants.getCustomerPersonalGroup()))
        {
            return findByCustomerId(customerFromMicroservice.getId())
                    .filter(retrievedAccount -> retrievedAccount.getStatus().contentEquals(constants.getStatusActive()))
                    .hasElements()
                    .flatMap(haveAnAccount -> {
                        if (Boolean.TRUE.equals(haveAnAccount)) {
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

        if (creditInDatabase.getCustomer().getStatus().contentEquals(constants.getStatusBlocked())) {
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

    private Mono<Credit> consumptionValidation(CreditConsumeCreditRequestDTO creditToUpdateOperation, Credit creditInDatabase) {
        log.info("Credit exists in database");

        if (creditInDatabase.getStatus().contentEquals(constants.getStatusBlocked())) {
            log.warn("Credit have blocked status");
            log.warn("Proceeding to abort consumption operation");
            return Mono.error(new ElementBlockedException("The credit have blocked status"));
        }

        if (creditInDatabase.getAvailableAmount() < creditToUpdateOperation.getAmount()) {
            log.info("Credit has insufficient funds");
            log.warn("Proceeding to abort do operation");
            return Mono.error(new IllegalArgumentException("The credit has insufficient funds"));
        }

        log.info("Operation successfully validated");
        return Mono.just(creditInDatabase);
    }

    Mono<Credit> generateBillingOrderValidation(Credit creditInDatabase) {
        log.info("Credit exists in database");

        if (creditInDatabase.getAvailableAmount().equals(creditInDatabase.getFullGrantedAmount())) {
            log.warn("Customer does not have debts for this credit");
            log.warn("Proceeding to abort billing order generation");
            return Mono.error(new BusinessLogicException("Customer does not have debts for this credit"));
        }

        log.info("Operation successfully validated");
        return Mono.just(creditInDatabase);
    }
    //endregion
}