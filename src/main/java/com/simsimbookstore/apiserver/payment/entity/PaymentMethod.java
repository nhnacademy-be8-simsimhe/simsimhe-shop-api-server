package com.simsimbookstore.apiserver.payment.entity;

import com.simsimbookstore.apiserver.payment.dto.PaymentMethodResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_methods")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentMethod {
    @Id
    @Column(name = "payment_method_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;

    @Column(nullable = false)
    private String paymentMethod;

    public static PaymentMethod createPaymentMethod(PaymentMethodResponse paymentMethodResponse){
        return new PaymentMethod(
                paymentMethodResponse.getPaymentMethodId(),
                paymentMethodResponse.getPaymentMethod()
        );
    }
}
