package com.nttdata.bootcamp.activeoperationsservice.expose;

import com.nttdata.bootcamp.activeoperationsservice.business.CreditService;
import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import com.nttdata.bootcamp.activeoperationsservice.model.Operation;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditCreateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditUpdateRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.request.CreditConsumeCreditRequestDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CreditFindBalancesResponseDTO;
import com.nttdata.bootcamp.activeoperationsservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import com.nttdata.bootcamp.activeoperationsservice.utils.errorhandling.BusinessLogicException;
import com.nttdata.bootcamp.activeoperationsservice.utils.errorhandling.CircuitBreakerException;
import com.nttdata.bootcamp.activeoperationsservice.utils.errorhandling.ElementBlockedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class CreditController {
    private final CreditService creditService;

    //region CRUD Endpoints
    @GetMapping("/credits")
    public Flux<Credit> findAllCredits(){
            log.info("Get operation in /credits");
        return creditService.findAll();
    }

    @GetMapping("/credits/{id}")
    public Mono<ResponseEntity<Credit>> findCreditById(@PathVariable("id") String id) {
        log.info("Get operation in /credits/{}", id);
        return creditService.findById(id)
                .flatMap(retrievedCredit -> Mono.just(ResponseEntity.ok(retrievedCredit)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/credits")
    public Mono<ResponseEntity<Credit>> createCredit(@RequestBody CreditCreateRequestDTO creditDTO) {
        log.info("Post operation in /credits");
        return creditService.create(creditDTO)
                .flatMap(createdCredit -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(createdCredit)))
                .onErrorResume(ElementBlockedException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.LOCKED).build()))
                .onErrorResume(BusinessLogicException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .onErrorResume(NoSuchElementException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
                .onErrorResume(CircuitBreakerException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(null)));
    }

    @PutMapping("/credits")
    public Mono<ResponseEntity<Credit>> updateCredit(@RequestBody CreditUpdateRequestDTO creditDTO) {
        log.info("Put operation in /credits");
        return creditService.update(creditDTO)
                .flatMap(createdCredit -> Mono.just(ResponseEntity.ok(createdCredit)))
                .onErrorResume(ElementBlockedException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.LOCKED).build()))
                .onErrorResume(BusinessLogicException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .onErrorResume(NoSuchElementException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/credits/{id}")
    public Mono<ResponseEntity<Credit>> deleteCredit(@PathVariable("id") String id) {
        log.info("Delete operation in /credits/{}", id);
        return creditService.removeById(id)
                .flatMap(removedCredit -> Mono.just(ResponseEntity.ok(removedCredit)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    //endregion

    //region Additional Repository Endpoints
    @GetMapping("customers/{id}/credits")
    public Flux<Credit> findCreditsByCustomerId(@PathVariable("id") String id) {
        log.info("Get operation in /customers/{}/credits", id);
        return creditService.findByCustomerId(id);
    }

    @GetMapping("customers-service/{id}")
    public Mono<ResponseEntity<CustomerCustomerServiceResponseDTO>> findByIdCustomerService(@PathVariable("id") String id) {
        log.info("Get operation in /customers-service/{}", id);
        return creditService.findByIdCustomerService(id)
                .flatMap(retrievedCustomer -> Mono.just(ResponseEntity.ok(retrievedCustomer)))
                .onErrorResume(CircuitBreakerException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build()));
    }
    //endregion

    //region UseCases
    @PostMapping("/credits/operations/consumes")
    public Mono<ResponseEntity<Credit>> consumeCredit(@RequestBody CreditConsumeCreditRequestDTO operationDTO) {
        log.info("Post operation in /credits/operation/consumes");
        return creditService.consumeCredit(operationDTO)
                .flatMap(createdCredit -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(createdCredit)))
                .onErrorResume(ElementBlockedException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.LOCKED).build()))
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .onErrorResume(NoSuchElementException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(null)));
    }

    @PostMapping("/credits/operations/payments/{id}")
    public Mono<ResponseEntity<Credit>> payCredit(@PathVariable("id") String id) {
        log.info("Post operation in /credits/operation/payments");
        return creditService.payCredit(id)
                .flatMap(createdCredit -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(createdCredit)))
                .onErrorResume(NoSuchElementException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(null)));
    }

    @PostMapping("/credits/{id}/generate-billing-order")
    public Mono<ResponseEntity<Credit>> generateBillingOrder(@PathVariable("id") String creditId) {
        log.info("Post operation in /credits/operations/payments/generate-billing-order");
        return creditService.generateBillingOrder(creditId)
                .flatMap(createdCredit -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(createdCredit)))
                .onErrorResume(BusinessLogicException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .onErrorResume(NoSuchElementException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(null)));
    }

    @GetMapping("/credits/{id}/operations")
    public Flux<Operation> findOperationsByCreditId(@PathVariable("id") String id) {
        log.info("Get operation in /credits/{id}/operations", id);
        return creditService.findOperationsByCreditId(id);
    }

    @GetMapping("customers/{id}/credits/balance")
    public Flux<CreditFindBalancesResponseDTO> findBalancesByCustomerId(@PathVariable("id") String id) {
        log.info("Get operation in /customers/{}/credits/balance", id);
        return creditService.findBalancesByCustomerId(id);
    }
    //endregion
}