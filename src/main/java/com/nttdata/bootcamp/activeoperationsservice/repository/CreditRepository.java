package com.nttdata.bootcamp.activeoperationsservice.repository;

import com.nttdata.bootcamp.activeoperationsservice.model.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findCreditsByCustomerId(String id);
}
