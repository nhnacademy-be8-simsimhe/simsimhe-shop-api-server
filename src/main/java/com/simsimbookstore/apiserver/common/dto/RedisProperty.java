package com.simsimbookstore.apiserver.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
public class RedisProperty {
    private String host;
    private Integer port;
    private String password;
    private int database;
}
