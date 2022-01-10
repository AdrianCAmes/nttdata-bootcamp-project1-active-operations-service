package com.nttdata.bootcamp.activeoperationsservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Constants {
    @Value("${constants.eureka.service_url.customer_info_service}")
    private String CUSTOMER_INFO_SERVICE_URL;

    @Value("${constants.customer.personal_type}")
    private String CUSTOMER_PERSONAL_TYPE;

    @Value("${constants.customer.business_type}")
    private String CUSTOMER_BUSINESS_TYPE;

    @Value("${constants.status.blocked}")
    private String STATUS_BLOCKED;

    @Value("${constants.status.active}")
    private String STATUS_ACTIVE;
}
