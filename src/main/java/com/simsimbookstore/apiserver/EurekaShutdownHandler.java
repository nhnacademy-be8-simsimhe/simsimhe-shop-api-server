package com.simsimbookstore.apiserver;

import com.netflix.discovery.EurekaClient;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class EurekaShutdownHandler {
    private final EurekaClient eurekaClient;

    public EurekaShutdownHandler(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    @PreDestroy
    public void deregisterFromEureka() {
        try {
            eurekaClient.shutdown();
        } catch (Exception e) {

        }
    }
}
