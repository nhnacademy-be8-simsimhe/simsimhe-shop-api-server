package com.simsimbookstore.apiserver.payment.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {
    @NotBlank
    private String clientApiKey;

    @NotBlank
    private String secretApiKey;

    @URL
    private String successUrl;

    @URL
    private String failUrl;
}
