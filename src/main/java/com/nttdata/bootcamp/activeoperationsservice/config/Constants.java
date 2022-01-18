package com.nttdata.bootcamp.activeoperationsservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Constants {
    @Value("${constants.eureka.service_url.customer_info_service:}")
    private String customerInfoServiceUrl;

    @Value("${constants.eureka.service_url.gateway_service:}")
    private String gatewayServiceUrl;

    @Value("${constants.customer.personal_group:}")
    private String customerPersonalGroup;

    @Value("${constants.customer.business_group:}")
    private String customerBusinessGroup;

    @Value("${constants.status.blocked:}")
    private String statusBlocked;

    @Value("${constants.status.active:}")
    private String statusActive;

    @Value("${constants.operation.consumption_type:}")
    private String operationConsumptionType;

    @Value("${constants.operation.payment_type:}")
    private String operationPaymentType;

    @Value("${constants.billing_order.paid_status:}")
    private String billingOrderPaid;

    @Value("${constants.billing_order.unpaid_status:}")
    private String billingOrderUnpaid;
}
