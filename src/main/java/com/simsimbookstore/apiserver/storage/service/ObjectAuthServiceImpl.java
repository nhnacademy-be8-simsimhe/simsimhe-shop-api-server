package com.simsimbookstore.apiserver.storage.service;

import com.simsimbookstore.apiserver.storage.config.ObjectStorageConfig;
import com.simsimbookstore.apiserver.storage.dto.TokenRequestDto;
import com.simsimbookstore.apiserver.storage.exception.ObjectStorageAuthException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ObjectAuthServiceImpl implements ObjectAuthService {

    private final String authUrl;
    private final String tenantId;
    private final String username;
    private final String password;
    private final RestTemplate restTemplate;

    public ObjectAuthServiceImpl(ObjectStorageConfig objectStorageConfig, RestTemplate restTemplate) {
        this.authUrl = objectStorageConfig.getAuthUrl();
        this.tenantId = objectStorageConfig.getTenantId();
        this.username = objectStorageConfig.getUsername();
        this.password = objectStorageConfig.getPassword();
        this.restTemplate = restTemplate;
    }

    @Override
    public String requestToken() {
        String identityUrl = authUrl + "/tokens";
        HttpEntity<TokenRequestDto> httpEntity = buildHttpEntity();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    identityUrl, HttpMethod.POST, httpEntity, String.class);

            validateResponse(response);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new ObjectStorageAuthException("Fail to get request token: ");
        } catch (Exception ex) {
            throw new ObjectStorageAuthException("unexpected error request token");
        }
    }

    private HttpEntity<TokenRequestDto> buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .tenantId(tenantId)
                .username(username)
                .password(password)
                .build();

        return new HttpEntity<>(tokenRequestDto, headers);
    }

    private void validateResponse(ResponseEntity<String> response) {
        if (response == null) {
            throw new ObjectStorageAuthException("Response is null");
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ObjectStorageAuthException("Invalid response status: " + response.getStatusCode());
        }

        String body = response.getBody();
        if (body == null || body.isEmpty()) { // null 검사 추가
            throw new ObjectStorageAuthException("Response body is null or empty");
        }
    }

}