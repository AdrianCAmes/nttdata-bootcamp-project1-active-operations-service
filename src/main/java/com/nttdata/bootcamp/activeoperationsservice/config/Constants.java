package com.nttdata.bootcamp.activeoperationsservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Constants {
    @Value("${constants.eureka.service-url.customer-info-service}")
    private String customerInfoServiceUrl;

    @Value("${constants.eureka.service-url.gateway-service}")
    private String gatewayServiceUrl;

    @Value("${constants.eureka.service-url.prefix}")
    private String urlPrefix;

    @Value("${constants.customer.personal-group}")
    private String customerPersonalGroup;

    @Value("${constants.customer.business-group}")
    private String customerBusinessGroup;

    @Value("${constants.status.blocked}")
    private String statusBlocked;

    @Value("${constants.status.active}")
    private String statusActive;

    @Value("${constants.operation.consumption-type}")
    private String operationConsumptionType;

    @Value("${constants.operation.payment-type}")
    private String operationPaymentType;

    @Value("${constants.billing-order.paid-status}")
    private String billingOrderPaid;

    @Value("${constants.billing-order.unpaid-status}")
    private String billingOrderUnpaid;

    @Value("${constants.circuit-breaker.customer-info-service.name}")
    private String customersServiceCircuitBreakerName;

    @Value("${constants.circuit-breaker.customer-info-service.timeout}")
    private Integer customersServiceCircuitBreakerTimeout;
}
