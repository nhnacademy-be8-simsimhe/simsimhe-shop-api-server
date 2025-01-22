package com.simsimbookstore.apiserver.coupons.couponpolicy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Table(name = "coupon_policies")
public class CouponPolicy {
    public static final Long WELCOME_POLICY = 2L;

    @Id
    @Column(name = "coupon_policy_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponPolicyId;

    @Column(name = "coupon_policy_name", length = 100, nullable = false)
    private String couponPolicyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DisCountType discountType;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice; //FIX

    @Column(name = "discount_rate", precision = 10, scale = 2)
    private BigDecimal discountRate; // RATE

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount; // RATE

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Lob
    @Column(name = "policy_description", nullable = false)
    private String policyDescription;




}
