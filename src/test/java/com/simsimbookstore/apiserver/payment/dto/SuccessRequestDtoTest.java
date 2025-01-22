package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SuccessRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testNoArgsConstructor() {
        // 기본 생성자 테스트
        SuccessRequestDto dto = new SuccessRequestDto();

        assertNotNull(dto);
        assertNull(dto.getPaymentKey());
        assertNull(dto.getOrderId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        String paymentKey = "PAY12345";
        String orderId = "ORD-20240122-001";
        BigDecimal amount = new BigDecimal("150000.50");
        PaymentMethodResponse paymentMethod = new PaymentMethodResponse(1L, "CREDIT_CARD");

        // When
        SuccessRequestDto dto = new SuccessRequestDto(paymentKey, orderId, amount, paymentMethod);

        // Then
        assertEquals(paymentKey, dto.getPaymentKey());
        assertEquals(orderId, dto.getOrderId());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(1L, dto.getPaymentMethod().getPaymentMethodId());
        assertEquals("CREDIT_CARD", dto.getPaymentMethod().getPaymentMethod());
    }

    @Test
    void testJsonSerialization() throws JsonProcessingException {
        // Given
        SuccessRequestDto dto = new SuccessRequestDto(
                "PAY67890",
                "ORD-20240122-002",
                new BigDecimal("200000.75"),
                new PaymentMethodResponse(2L, "BANK_TRANSFER")
        );

        // When
        String json = objectMapper.writeValueAsString(dto);

        // Then
        assertTrue(json.contains("\"paymentKey\":\"PAY67890\""));
        assertTrue(json.contains("\"orderId\":\"ORD-20240122-002\""));
        assertTrue(json.contains("\"amount\":200000.75"));
        assertTrue(json.contains("\"paymentMethod\":{\"paymentMethodId\":2,\"paymentMethod\":\"BANK_TRANSFER\"}"));
    }



}