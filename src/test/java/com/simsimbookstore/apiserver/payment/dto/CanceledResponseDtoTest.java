package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CanceledResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        String json = """
                {
                    "canceledAt": "2024-01-22T12:00:00",
                    "cancelAmount": 10000.50,
                    "transactionKey": "TXN12345"
                }
                """;

        CanceledResponseDto dto = objectMapper.readValue(json, CanceledResponseDto.class);

        assertNotNull(dto);
        assertEquals("2024-01-22T12:00:00", dto.getCanceledAt());
        assertEquals(new BigDecimal("10000.50"), dto.getPaymentCanceledAmount());
        assertEquals("TXN12345", dto.getPaymentCanceledTransactionKey());
    }

    @Test
    void testJsonSerialization() throws JsonProcessingException {
        CanceledResponseDto dto = new CanceledResponseDto();
        dto.setCanceledAt("2024-01-22T12:00:00");
        dto.setPaymentCanceledAmount(new BigDecimal("5000.75"));
        dto.setPaymentCanceledTransactionKey("TXN67890");

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"canceledAt\":\"2024-01-22T12:00:00\""));
        assertTrue(json.contains("\"cancelAmount\":5000.75"));
        assertTrue(json.contains("\"transactionKey\":\"TXN67890\""));
    }



}