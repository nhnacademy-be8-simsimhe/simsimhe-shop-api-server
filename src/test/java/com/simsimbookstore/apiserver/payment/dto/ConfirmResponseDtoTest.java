package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfirmResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        // given
        String json = """
                {
                    "orderId": "ORDER123",
                    "paymentKey": "PAYKEY123",
                    "totalAmount": 10000.00,
                    "method": "CARD",
                    "approvedAt": "2024-01-22T10:30:00",
                    "code": "SUCCESS",
                    "message": "Payment approved"
                }
                """;

        // when
        ConfirmResponseDto dto = objectMapper.readValue(json, ConfirmResponseDto.class);

        // then
        Assertions.assertEquals(dto.getOrderId(),"ORDER123");
        Assertions.assertEquals(dto.getPaymentKey(),"PAYKEY123");
        Assertions.assertEquals(dto.getTossReturnMethod(),"CARD");
        Assertions.assertEquals(dto.getCode(),"SUCCESS");
        Assertions.assertEquals(dto.getMessage(),"Payment approved");
    }

}