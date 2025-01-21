package com.simsimbookstore.apiserver.storage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.storage.config.ObjectStorageConfig;
import com.simsimbookstore.apiserver.storage.exception.ObjectStorageAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ObjectAuthServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectStorageConfig objectStorageConfig;


    private ObjectAuthServiceImpl objectAuthService;

    @BeforeEach
    void setUp() {
        when(objectStorageConfig.getAuthUrl()).thenReturn("https://mock-auth-url.com");
        when(objectStorageConfig.getTenantId()).thenReturn("mock-tenant-id");
        when(objectStorageConfig.getUsername()).thenReturn("mock-username");
        when(objectStorageConfig.getPassword()).thenReturn("mock-password");

        objectAuthService = new ObjectAuthServiceImpl(objectStorageConfig, restTemplate);
    }

    @Test
    @DisplayName("정상적으로 토큰이 반환된다")
    void return_request_token() {
        String expectedTokenResponse = "mock-token-response";
        String identityUrl = "https://mock-auth-url.com/tokens";


        when(restTemplate.exchange(
                eq(identityUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedTokenResponse, HttpStatus.OK));

        String actualTokenResponse = objectAuthService.requestToken();

        assertEquals(expectedTokenResponse, actualTokenResponse);
    }

    @Test
    @DisplayName("RestTemplate이 오류 상태를 반환하면 예외가 발생해야 한다")
    void restTemplate_exception() {
        String identityUrl = "https://mock-auth-url.com/tokens";

        when(restTemplate.exchange(
                eq(identityUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ObjectStorageAuthException.class, () -> objectAuthService.requestToken());
    }
}