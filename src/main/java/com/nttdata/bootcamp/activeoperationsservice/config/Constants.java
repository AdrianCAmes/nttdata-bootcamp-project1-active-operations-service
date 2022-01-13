package com.nttdata.bootcamp.activeoperationsservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Constants {
    @Value("${constants.eureka.service_url.customer_info_service}")
    private String CUSTOMER_INFO_SERVICE_URL;

    @Value("${constants.eureka.service_url.gateway_service}")
    private String GATEWAY_SERVICE_URL;

    @Value("${constants.customer.personal_group}")
    private String CUSTOMER_PERSONAL_GROUP;

    @Value("${constants.customer.business_group}")
    private String CUSTOMER_BUSINESS_GROUP;

    @Value("${constants.status.blocked}")
    private String STATUS_BLOCKED;

    @Value("${constants.status.active}")
    private String STATUS_ACTIVE;

    @Value("${constants.operation.consumption_type}")
    private String OPERATION_CONSUMPTION_TYPE;

    @Value("${constants.operation.payment_type}")
    private String OPERATION_PAYMENT_TYPE;

    @Value("${constants.billing_order.paid_status}")
    private String BILLING_ORDER_PAID;

    @Value("${constants.billing_order.unpaid_status}")
    private String BILLING_ORDER_UNPAID;
}
