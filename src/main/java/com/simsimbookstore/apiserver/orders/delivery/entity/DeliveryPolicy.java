package com.simsimbookstore.apiserver.orders.delivery.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "delivery_policies")
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_policy_id")
    private Long deliveryPolicyId;

    @Column(name = "delivery_policy_name", nullable = false, length = 100)
    private String deliveryPolicyName;

    @Column(name = "delivery_price", nullable = false)
    private BigDecimal deliveryPrice;

    @Column(name ="policy_standard_price")
    private BigDecimal policyStandardPrice;

    @Column(name = "standard_policy")
    private boolean standardPolicy;



    public void changeStandardPolicy() {
        this.standardPolicy = !this.standardPolicy;
    }

    public void changePolicyToFalse() {
        this.standardPolicy = false;
    }

}
