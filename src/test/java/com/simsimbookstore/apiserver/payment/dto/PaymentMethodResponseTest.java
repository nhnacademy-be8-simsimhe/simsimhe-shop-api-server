package com.simsimbookstore.apiserver.payment.dto;

import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentMethodResponseTest {

    @Test
    void testConstructorAndGetter() {
        // Given
        Long paymentMethodId = 1L;
        String paymentMethod = "CREDIT_CARD";

        // When
        PaymentMethodResponse response = new PaymentMethodResponse(paymentMethodId, paymentMethod);

        // Then
        assertEquals(paymentMethodId, response.getPaymentMethodId());
        assertEquals(paymentMethod, response.getPaymentMethod());
    }

    @Test
    void testChangeEntityToDto() {
        // Given (PaymentMethod 엔티티 객체 생성)
        PaymentMethod paymentMethod = new PaymentMethod(2L, "BANK_TRANSFER");

        // When
        PaymentMethodResponse response = PaymentMethodResponse.changeEntityToDto(paymentMethod);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getPaymentMethodId());
        assertEquals("BANK_TRANSFER", response.getPaymentMethod());
    }

}