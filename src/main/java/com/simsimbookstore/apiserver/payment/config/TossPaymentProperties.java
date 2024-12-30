package com.simsimbookstore.apiserver.payment.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {
    @NotBlank
    private String secretApiKey;

    @NotBlank
    private String clientApiKey;

    @URL
    private String successUrl;

    @URL
    private String failUrl;
}
