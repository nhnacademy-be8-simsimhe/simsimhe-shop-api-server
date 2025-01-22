package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class CanceledResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        // given
        String json = """
                {
                    "canceledAt": "2024-01-22T10:00:00",
                    "cancelAmount": 5000.00,
                    "transactionKey": "TXN12345"
                }
                """;

        // when
        CanceledResponseDto dto = objectMapper.readValue(json, CanceledResponseDto.class);

        // then
        Assertions.assertEquals(dto.getCanceledAt(), "2024-01-22T10:00:00");
        Assertions.assertEquals(dto.getPaymentCanceledTransactionKey(), "TXN12345");
    }

}