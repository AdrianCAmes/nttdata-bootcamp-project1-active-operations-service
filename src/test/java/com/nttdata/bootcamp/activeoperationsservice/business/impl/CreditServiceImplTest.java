package com.nttdata.bootcamp.activeoperationsservice.business.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nttdata.bootcamp.activeoperationsservice.business.CreditService;
import com.nttdata.bootcamp.activeoperationsservice.config.Constants;
import com.nttdata.bootcamp.activeoperationsservice.model.*;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditConsumeCreditRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.*;
import com.nttdata.bootcamp.activeoperationsservice.repository.CreditRepository;
import com.nttdata.bootcamp.activeoperationsservice.utils.CreditUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest()
class CreditServiceImplTest {
    @Autowired
    private CreditService creditService;
    @Autowired
    private CreditUtils creditUtils;
    @Autowired
    private Constants constants;
    @MockBean
    private CreditRepository creditRepository;

    private static MockWebServer mockBackEnd;

    private static CustomerCustomerServiceResponseDTO customerMock1 = new CustomerCustomerServiceResponseDTO();
    private static CustomerCustomerServiceResponseDTO customerMock2 = new CustomerCustomerServiceResponseDTO();

    private static Credit creditMock1 = new Credit();
    private static Credit creditMock2 = new Credit();

    private static Operation operationMock1 = new Operation();

    @BeforeAll
    static void setUp(@Value("${server.port}") int port) throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(port);
    }

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

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void create() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").create();
        mockBackEnd.enqueue(new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(gson.toJson(customerMock1))
                .setResponseCode(HttpStatus.OK.value()));

        when(creditRepository.findCreditsByCustomerId(creditMock1.getCustomer().getId())).thenReturn(Flux.empty());
        when(creditRepository.insert(any(Credit.class))).thenReturn(Mono.just(creditMock1));

        CreditCreateRequestDTO creditToSave = creditUtils.creditToCreditCreateRequestDTO(creditMock1);
        Mono<Credit> savedCredit = creditService.create(creditToSave);

        StepVerifier.create(savedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void findById() {
        when(creditRepository.findById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));

        Mono<Credit> retrievedCredit = creditService.findById(creditMock1.getId());

        StepVerifier.create(retrievedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void findAll() {
        when(creditRepository.findAll()).thenReturn(Flux.just(creditMock1, creditMock2));

        Flux<Credit> retrievedCredits = creditService.findAll() ;

        StepVerifier.create(retrievedCredits)
                .expectSubscription()
                .expectNext(creditMock1)
                .expectNext(creditMock2)
                .verifyComplete();
    }

    @Test
    void update() {
        when(creditRepository.findById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));
        when(creditRepository.save(any(Credit.class))).thenReturn(Mono.just(creditMock1));

        CreditUpdateRequestDTO creditToUpdate = creditUtils.creditToCreditUpdateCreateRequestDTO(creditMock1);
        Mono<Credit> updatedCredit = creditService.update(creditToUpdate);

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void removeById() {
        when(creditRepository.findById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));
        when(creditRepository.deleteById(creditMock1.getId())).thenReturn(Mono.empty());

        Mono<Credit> removedCredit = creditService.removeById(creditMock1.getId());

        StepVerifier.create(removedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void findByIdCustomerService() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").create();
        mockBackEnd.enqueue(new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(gson.toJson(customerMock1))
                .setResponseCode(HttpStatus.OK.value()));

        Mono<CustomerCustomerServiceResponseDTO> retrievedCustomer = creditService.findByIdCustomerService(customerMock1.getId());

        StepVerifier.create(retrievedCustomer)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(customerMock1.toString()))
                .verifyComplete();
    }

    @Test
    void findByCustomerId() {
        when(creditRepository.findCreditsByCustomerId(creditMock1.getCustomer().getId())).thenReturn(Flux.just(creditMock1));

        Flux<Credit> retrievedCredit = creditService.findByCustomerId(creditMock1.getCustomer().getId());

        StepVerifier.create(retrievedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void consumeCredit() {
        when(creditRepository.findById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));
        when(creditRepository.save(any(Credit.class))).thenReturn(Mono.just(creditMock1));

        CreditConsumeCreditRequestDTO creditToUpdate = CreditConsumeCreditRequestDTO.builder()
                .id(creditMock1.getId())
                .amount(250.0)
                .build();
        Mono<Credit> updatedCredit = creditService.consumeCredit(creditToUpdate);

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void payCredit() {
        when(creditRepository.findAll()).thenReturn(Flux.just(creditMock1, creditMock2));
        when(creditRepository.save(any(Credit.class))).thenReturn(Mono.just(creditMock1));

        Mono<Credit> updatedCredit = creditService.payCredit(creditMock1.getOperations().get(0).getBillingOrder().getId());

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void generateBillingOrder() {
        when(creditRepository.findById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));
        when(creditRepository.save(any(Credit.class))).thenReturn(Mono.just(creditMock1));

        Mono<Credit> updatedCredit = creditService.generateBillingOrder(creditMock1.getId());

        StepVerifier.create(updatedCredit)
                .expectSubscription()
                .expectNext(creditMock1)
                .verifyComplete();
    }

    @Test
    void findOperationsByCreditId() {
        when(creditRepository.findById(creditMock1.getId())).thenReturn(Mono.just(creditMock1));

        Flux<Operation> retrievedOperations = creditService.findOperationsByCreditId(creditMock1.getId());

        StepVerifier.create(retrievedOperations)
                .expectSubscription()
                .expectNext(operationMock1)
                .verifyComplete();
    }

    @Test
    void findBalancesByCustomerId() {
        when(creditRepository.findCreditsByCustomerId(creditMock1.getCustomer().getId())).thenReturn(Flux.just(creditMock1));

        Flux<CreditFindBalancesResponseDTO> retrievedBalances = creditService.findBalancesByCustomerId(creditMock1.getCustomer().getId());

        StepVerifier.create(retrievedBalances)
                .expectSubscription()
                .expectNextMatches(retrieved -> retrieved.toString().contentEquals(creditUtils.creditToCreditFindBalancesResponseDTO(creditMock1).toString()))
                .verifyComplete();
    }
}