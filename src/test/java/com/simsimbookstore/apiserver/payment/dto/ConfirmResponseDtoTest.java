package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        String json = """
                {
                    "orderId": "ORD-20240122-001",
                    "paymentKey": "PAY12345",
                    "totalAmount": 50000.75,
                    "method": "CARD",
                    "approvedAt": "2024-01-22T12:00:00",
                    "code": "SUCCESS",
                    "message": "Payment approved successfully"
                }
                """;

        ConfirmResponseDto dto = objectMapper.readValue(json, ConfirmResponseDto.class);

        assertNotNull(dto);
        assertEquals("ORD-20240122-001", dto.getOrderId());
        assertEquals("PAY12345", dto.getPaymentKey());
        assertEquals(new BigDecimal("50000.75"), dto.getTotalAmount());
        assertEquals("CARD", dto.getTossReturnMethod());
        assertEquals("2024-01-22T12:00:00", dto.getApprovedAt());
        assertEquals("SUCCESS", dto.getCode());
        assertEquals("Payment approved successfully", dto.getMessage());
    }



}