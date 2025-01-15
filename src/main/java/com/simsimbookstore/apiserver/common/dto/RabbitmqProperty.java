package com.simsimbookstore.apiserver.common.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
@Setter
@Getter
public class RabbitmqProperty {
    String data;
}
