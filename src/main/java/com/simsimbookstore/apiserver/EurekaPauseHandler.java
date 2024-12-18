package com.simsimbookstore.apiserver;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EurekaPauseHandler {

    @Value("${server.port}")
    String port;

    @PreDestroy
    public boolean pauseService() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // /actuator/pause 엔드포인트 호출
            String response = restTemplate.postForObject("http://localhost:"+port+"/management/actuator/pause", null, String.class);

            if ("PAUSED".equals(response)) {
                return true; // 정상적으로 서비스가 `PAUSED` 상태로 전환됨
            }
            return false; // 실패
        } catch (Exception e) {
            // 예외 처리 (서비스가 일시적으로 작동하지 않을 경우 등)
            return false;
        }
    }
}
