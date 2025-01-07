package com.simsimbookstore.apiserver.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TossPaymentConfig {
    @Value("${payment.toss.client_api_key}")
    private String clientKey;

    @Value("${payment.toss.secret_api_key}")
    private String secretKey;

    @Value("${payment.toss.success_url}")
    private String successUrl;

    @Value("${payment.toss.fail_url}")
    private String failUrl;
}
