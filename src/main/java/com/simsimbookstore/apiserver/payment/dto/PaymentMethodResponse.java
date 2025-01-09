package com.simsimbookstore.apiserver.payment.dto;

import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentMethodResponse {

    private Long paymentMethodId;

    private String paymentMethod;

    public static PaymentMethodResponse changeEntityToDto(PaymentMethod paymentMethod){

        return new PaymentMethodResponse(
                paymentMethod.getPaymentMethodId(),
                paymentMethod.getPaymentMethod()
        );
    }
}
