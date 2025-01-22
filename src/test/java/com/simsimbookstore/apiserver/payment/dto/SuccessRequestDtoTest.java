package com.simsimbookstore.apiserver.payment.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class SuccessRequestDtoTest {

    @Test
    void testNoArgsConstructor() {
        // when
        SuccessRequestDto dto = new SuccessRequestDto();


        Assertions.assertNull(dto.getPaymentMethod());
        Assertions.assertNotNull(dto);
        Assertions.assertNull(dto.getAmount());
        Assertions.assertNull(dto.getOrderId());
        Assertions.assertNull(dto.getPaymentKey());
    }

    @Test
    void testAllArgsConstructor() {
        // given
        String paymentKey = "PAYMENT_123";
        String orderId = "ORDER_456";
        BigDecimal amount = BigDecimal.valueOf(15000.00);
        PaymentMethodResponse paymentMethod = new PaymentMethodResponse(1L, "CREDIT_CARD");

        // when
        SuccessRequestDto dto = new SuccessRequestDto(paymentKey, orderId, amount, paymentMethod);


        Assertions.assertEquals(dto.getPaymentKey(),paymentKey);
        Assertions.assertEquals(dto.getOrderId(),orderId);
        Assertions.assertEquals(dto.getAmount(),amount);
        Assertions.assertEquals(dto.getPaymentMethod(),paymentMethod);

    }


}