package com.simsimbookstore.apiserver;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EurekaClientShutdownHandler {
    private final EurekaClient eurekaClient;
    private final ApplicationInfoManager applicationInfoManager;

    @Value("${eureka.client.service-url.defaultZone}")
    private String eurekaServerUrl;

    public EurekaClientShutdownHandler(EurekaClient eurekaClient, ApplicationInfoManager applicationInfoManager) {
        this.eurekaClient = eurekaClient;
        this.applicationInfoManager = applicationInfoManager;
    }

    @PreDestroy
    public void deregister() {
        // 현재 인스턴스 정보 가져오기
        InstanceInfo instanceInfo = applicationInfoManager.getInfo();
        if (instanceInfo == null) {
            return;
        }
        String instanceId = instanceInfo.getInstanceId();
        String appName = instanceInfo.getAppName();
        String url = String.format("%s/apps/%s/%s", eurekaServerUrl.replace("/eureka", ""), appName, instanceId);

        try{
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete(url);
            System.out.println("Successfully deregistered from Eureka: " + url);
        } catch (Exception e){
            System.err.println("Failed to deregister from Eureka: " + e.getMessage());
        }
    }
}
