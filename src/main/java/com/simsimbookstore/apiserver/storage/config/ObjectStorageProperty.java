package com.simsimbookstore.apiserver.storage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "object-storage")
@Setter
@Getter
public class ObjectStorageProperty {
    private String storageUrl;
    private String containerName;
    private String password;
    private String username;
    private String tenantId;
    private String authUrl;
}
