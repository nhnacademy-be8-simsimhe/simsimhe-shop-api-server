package com.simsimbookstore.apiserver.payment.dto;

import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PaymentMethodResponseTest {

    @Test
    void testConstructor() {
        // given
        Long expectedId = 1L;
        String expectedMethod = "CREDIT_CARD";

        // when
        PaymentMethodResponse response = new PaymentMethodResponse(expectedId, expectedMethod);

        Assertions.assertEquals(response.getPaymentMethod(),expectedMethod);
        Assertions.assertEquals(response.getPaymentMethodId(),expectedId);
    }

    @Test
    void testChangeEntityToDto() {
        // given
        PaymentMethod paymentMethod = new PaymentMethod(1L, "DEBIT_CARD");

        // when
        PaymentMethodResponse response = PaymentMethodResponse.changeEntityToDto(paymentMethod);

        // then
        Assertions.assertEquals(response.getPaymentMethodId(),1L);
        Assertions.assertEquals(response.getPaymentMethod(),"DEBIT_CARD");

    }

}