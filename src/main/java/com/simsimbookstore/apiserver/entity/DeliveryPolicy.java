package com.simsimbookstore.apiserver.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_policies")
@NoArgsConstructor
public class DeliveryPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_policy_id")
    private Long deliveryPolicyId;

    @Column(name = "delivery_policy_name", nullable = false, length = 100)
    private String deliveryPolicyName;


    @Column(name ="policy_standard_price")
    private BigDecimal policyStandardPrice;

    @Column(name = "standard_policy")
    private boolean standardPolicy;
}
