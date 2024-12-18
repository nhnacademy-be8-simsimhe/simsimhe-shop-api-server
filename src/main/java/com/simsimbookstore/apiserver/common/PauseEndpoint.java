package com.simsimbookstore.apiserver.common;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "pause")  // 엔드포인트 ID를 "pause"로 지정
public class PauseEndpoint {

    private boolean paused = false;

    @ReadOperation
    public String getStatus() {
        return paused ? "Paused" : "Running";
    }

    @WriteOperation
    public void pauseService() {
        paused = true;
        // 서비스 멈추는 로직을 여기에 구현
        System.out.println("Service paused");
    }

    @WriteOperation
    public void resumeService() {
        paused = false;
        // 서비스 재개하는 로직을 여기에 구현
        System.out.println("Service resumed");
    }
}
