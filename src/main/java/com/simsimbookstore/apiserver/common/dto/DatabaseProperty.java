package com.simsimbookstore.apiserver.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "database")
@Setter
@Getter
public class DatabaseProperty {
    private String url;
    private String username;
    private String password;
}
