package com.nttdata.bootcamp.activeoperationsservice.expose;

import com.nttdata.bootcamp.activeoperationsservice.business.CreditService;
import com.nttdata.bootcamp.activeoperationsservice.config.Constants;
import com.nttdata.bootcamp.activeoperationsservice.model.*;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditConsumeCreditRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.*;
import com.nttdata.bootcamp.activeoperationsservice.utils.CreditUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.ArrayList;
import java.util.Date;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreditControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CreditUtils creditUtils;
    @Autowired
    private Constants constants;
    @MockBean
    private CreditService creditService;

    private static CustomerCustomerServiceResponseDTO customerMock1 = new CustomerCustomerServiceResponseDTO();
    private static CustomerCustomerServiceResponseDTO customerMock2 = new CustomerCustomerServiceResponseDTO();

    private static Credit creditMock1 = new Credit();
    private static Credit creditMock2 = new Credit();

    private static Operation operationMock1 = new Operation();

    @BeforeEach
    void setUpEach() {
        PersonDetailsCustomerServiceResponseDTO representative =  PersonDetailsCustomerServiceResponseDTO.builder()
                .name("Marco")
                .lastname("Cruz")
                .identityNumber("74854687")
                .address(AddressCustomerServiceResponseDTO.builder().
                        number(144)
                        .street("Av. Proceres")
                        .city("Lima")
                        .country("Peru")
                        .build())
                .email("marco.cruz@gmail.com")
                .phoneNumber("5412458")
                .mobileNumber("947854120")
                .birthdate(new Date(2000, 10, 25))
                .build();

        customerMock1 = CustomerCustomerServiceResponseDTO.builder()
                .id("1")
                .customerType(CustomerTypeCustomerServiceResponseDTO
                        .builder()
                        .group(constants.getCustomerPersonalGroup())
                        .subgroup("Mock")
                        .build())
                .status(constants.getStatusActive())
                .personDetails(representative)
                .build();

        ArrayList<PersonDetailsCustomerServiceResponseDTO> representatives = new ArrayList<>();
        representatives.add(representative);
        customerMock2 = CustomerCustomerServiceResponseDTO.builder()
                .id("2")
                .customerType(CustomerTypeCustomerServiceResponseDTO
                        .builder()
                        .group(constants.getCustomerBusinessGroup())
                        .subgroup("Mock")
                        .build())
                .status(constants.getStatusActive())
                .businessDetails(BusinessDetailsCustomerServiceResponseDTO.builder()
                        .name("NTT Data Peru")
                        .ruc("20874563347")
                        .address(AddressCustomerServiceResponseDTO.builder().
                                number(258)
                                .street("Av. Javier Prado")
                                .city("Lima")
                                .country("Peru")
                                .build())
                        .representatives(representatives)
                        .build())
                .build();

        operationMock1 = Operation.builder()
                .operationNumber("123")
                .time(new Date())
                .type(constants.getOperationPaymentType())
                .amount(100.0)
                .billingOrder(BillingOrder.builder()
                        .id("1")
                        .calculatedAmount(220.0)
                        .amountToRefund(200.0)
                        .cycle("01/2022")
                        .status(constants.getBillingOrderUnpaid())
                        .build())
                .build();
        ArrayList<Operation> operationListMock = new ArrayList<>();
        operationListMock.add(operationMock1);

        creditMock1 = Credit.builder()
                .id("1")
                .status(constants.getStatusActive())
                .customer(Customer.builder()
                        .id(customerMock1.getId())
                        .customerType(CustomerType.builder()
                                .group(customerMock1.getCustomerType().getGroup())
                                .subgroup(customerMock1.getCustomerType().getSubgroup())
                                .build())
                        .status(customerMock1.getStatus())
                        .build())
                .fullGrantedAmount(700.0)
                .availableAmount(600.0)
                .creditCardNumber("123")
                .issueDate(new Date())
                .dueDate(new Date())
                .operations(operationListMock)
                .billingDetails(BillingDetails.builder()
                        .interestPercentage(10.0)
                        .closingDay(1)
                        .build())
                .build();

        creditMock2 = Credit.builder()
                .id("2")
                .status(constants.getStatusActive())
                .customer(Customer.builder()
                        .id(customerMock2.getId())
                        .customerType(CustomerType.builder()
                                .group(customerMock2.getCustomerType().getGroup())
                                .subgroup(customerMock2.getCustomerType().getSubgroup())
                                .build())
                        .status(customerMock2.getStatus())
                        .build())
                .fullGrantedAmount(700.0)
                .availableAmount(700.0)
                .creditCardNumber("456")
                .issueDate(new Date())
                .dueDate(new Date())
                .operations(null)
                .billingDetails(BillingDetails.builder()
                        .interestPercentage(10.0)
                        .closingDay(1)
                        .build())
                .build();
    }

    @Test
    void findAllCredits() {
        when(creditService.findAll()).thenReturn(Flux.just(creditMock1, creditMock2));

        Flux<Credit> retrievedCredits = webTestClient
                .get()
                .uri("/api/v1/credits")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(retrievedCredits)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock2.toString()))
                .verifyComplete();
    }

    @Test
    void findCreditById() {
        when(creditService.findById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));

        Flux<Credit> retrievedCredit = webTestClient
                .get()
                .uri("/api/v1/credits/" + creditMock1.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(retrievedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void createCredit() {
        when(creditService.create(any(CreditCreateRequestDTO.class))).thenReturn(Mono.just(creditMock1));

        CreditCreateRequestDTO creditToSave = creditUtils.creditToCreditCreateRequestDTO(creditMock1);
        Flux<Credit> savedCredit = webTestClient
                .post()
                .uri("/api/v1/credits")
                .body(Mono.just(creditToSave), CreditCreateRequestDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(savedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void updateCredit() {
        when(creditService.update(any(CreditUpdateRequestDTO.class))).thenReturn(Mono.just(creditMock1));

        CreditUpdateRequestDTO creditToUpdate = creditUtils.creditToCreditUpdateCreateRequestDTO(creditMock1);
        Flux<Credit> updatedCredit = webTestClient
                .put()
                .uri("/api/v1/credits")
                .body(Mono.just(creditToUpdate), CreditUpdateRequestDTO.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void deleteCredit() {
        when(creditService.removeById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));

        Flux<Credit> removedCredit = webTestClient
                .delete()
                .uri("/api/v1/credits/" + creditMock1.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(removedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void findCreditsByCustomerId() {
        when(creditService.findByCustomerId(customerMock1.getId())).thenReturn(Flux.just(creditMock1));

        Flux<Credit> retrievedCredits = webTestClient
                .get()
                .uri("/api/v1/customers/" + customerMock1.getId() + "/credits")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(retrievedCredits)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void findByIdCustomerService() {
        when(creditService.findByIdCustomerService(customerMock1.getId())).thenReturn(Mono.just(customerMock1));

        Flux<CustomerCustomerServiceResponseDTO> retrievedCustomer = webTestClient
                .get()
                .uri("/api/v1/customers-service/" + customerMock1.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(CustomerCustomerServiceResponseDTO.class)
                .getResponseBody();

        StepVerifier.create(retrievedCustomer)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(customerMock1.toString()))
                .verifyComplete();
    }

    @Test
    void consumeCredit() {
        when(creditService.consumeCredit(any(CreditConsumeCreditRequestDTO.class))).thenReturn(Mono.just(creditMock1));

        CreditConsumeCreditRequestDTO creditToUpdate = CreditConsumeCreditRequestDTO.builder()
                .id(creditMock1.getId())
                .amount(250.0)
                .build();
        Flux<Credit> updatedCredit = webTestClient
                .post()
                .uri("/api/v1/credits/operations/consumes")
                .body(Mono.just(creditToUpdate), CreditConsumeCreditRequestDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void payCredit() {
        when(creditService.payCredit(creditMock1.getOperations().get(0).getBillingOrder().getId())).thenReturn(Mono.just(creditMock1));

        Flux<Credit> updatedCredit = webTestClient
                .post()
                .uri("/api/v1/credits/operations/payments/" + creditMock1.getOperations().get(0).getBillingOrder().getId())
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void generateBillingOrder() {
        when(creditService.generateBillingOrder(creditMock1.getId())).thenReturn(Mono.just(creditMock1));

        Flux<Credit> updatedCredit = webTestClient
                .post()
                .uri("/api/v1/credits/" + creditMock1.getId() + "/generate-billing-order")
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Credit.class)
                .getResponseBody();

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditMock1.toString()))
                .verifyComplete();
    }

    @Test
    void findOperationsByCreditId() {
        when(creditService.findOperationsByCreditId(creditMock1.getId())).thenReturn(Flux.just(operationMock1));

        Flux<Operation> updatedCredit = webTestClient
                .get()
                .uri("/api/v1/credits/" + creditMock1.getId() + "/operations")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Operation.class)
                .getResponseBody();

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(operationMock1.toString()))
                .verifyComplete();
    }

    @Test
    void findBalancesByCustomerId() {
        when(creditService.findBalancesByCustomerId(customerMock1.getId())).thenReturn(Flux.just(creditUtils.creditToCreditFindBalancesResponseDTO(creditMock1)));

        Flux<CreditFindBalancesResponseDTO> retrievedCredits = webTestClient
                .get()
                .uri("/api/v1/customers/" + customerMock1.getId() + "/credits/balance")
                .exchange()
                .expectStatus().isOk()
                .returnResult(CreditFindBalancesResponseDTO.class)
                .getResponseBody();

        StepVerifier.create(retrievedCredits)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditUtils.creditToCreditFindBalancesResponseDTO(creditMock1).toString()))
                .verifyComplete();
    }
}