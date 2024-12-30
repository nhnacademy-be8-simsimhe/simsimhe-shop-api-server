package com.simsimbookstore.apiserver.payment.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.payment.config.TossPaymentProperties;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentRestTemplate {

    private final HttpHeaders headers;
    private final RestTemplate restTemplate;

    private final String encodedAuth;
    private final String successUrl;
    private final String failUrl;

    public PaymentRestTemplate(TossPaymentProperties tossPaymentProperties) {
        headers = new HttpHeaders();
        restTemplate = new RestTemplate();

        successUrl = tossPaymentProperties.getSuccessUrl();
        failUrl = tossPaymentProperties.getFailUrl();
        String secretKey = tossPaymentProperties.getSecretApiKey() + ":";
        encodedAuth = new String(Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8)));
    }

    // 요청 객체로 결제 요청
    public String requestPayment(OrderFacadeResponseDto dto) {
        URI uri = URI.create("https://api.tosspayments.com/v1/payments");

        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> map = new HashMap<>();
        map.put("method", dto.getMethod());
        map.put("amount", String.valueOf(dto.getTotalPrice()));
        map.put("orderId", dto.getOrderNumber());
        map.put("orderName", dto.getOrderName());
        map.put("successUrl", successUrl);
        map.put("failUrl", failUrl);
        map.put("customerEmail", dto.getEmail());
//        map.put("customerName", dto.getUserName()); // 사용자 이름
        map.put("customerMobilePhone", dto.getPhoneNumber());

        ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request = null;
        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(map), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                String.class
        );
        return responseEntity.getBody();
    }

    // 결제 승인 요청
    public String confirm(SuccessRequestDto success) throws URISyntaxException {
        URI uri = URI.create("https://api.tosspayments.com/v1/payments/confirm");

        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String > map = new HashMap<>();
        map.put("paymentKey", success.getPaymentKey());
        map.put("orderId", success.getOrderId());
        map.put("amount", String.valueOf(success.getAmount()));

        ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request = null;
        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(map), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                String.class
        );
        return responseEntity.getBody();
    }

    // 나중에 멱등키를 사용한다면 키 redis에 15일 저장
    public String adminCanceled(String paymentKey, String cancelReason) {
        URI uri = URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel");

        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ObjectMapper objectMapper = new ObjectMapper();
        HttpEntity<String> request = null;

        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(cancelReason), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                String.class
        );
        return responseEntity.getBody();
    }
}
