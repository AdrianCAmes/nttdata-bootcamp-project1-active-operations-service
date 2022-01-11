package com.nttdata.bootcamp.activeoperationsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableEurekaClient
public class ActiveOperationsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActiveOperationsServiceApplication.class, args);
	}

}
