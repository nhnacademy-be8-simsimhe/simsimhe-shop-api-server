package com.simsimbookstore.apiserver.elastic.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "elasticsearch")
@Setter
@Getter
public class ElasticProperty {
    String data;
}
