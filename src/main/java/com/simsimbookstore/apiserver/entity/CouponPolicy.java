package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="coupon_policies")
public class CouponPolicy {
    @Id
    @Column(name = "coupon_policy_id", length = 40)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponPolicyId;

    @Column(name = "coupon_policy_name",length = 100,nullable = false)
    private String couponPolicyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;

    @Column(name = "policy_description", nullable = false)
    private String policyDescription;

    @Builder
    public CouponPolicy(Long couponPolicyId, String couponPolicyName, DiscountType discountType, BigDecimal discountPrice, BigDecimal discountRate, BigDecimal maxDiscountAmount, BigDecimal minOrderAmount, String policyDescription) {
        this.couponPolicyId = couponPolicyId;
        this.couponPolicyName = couponPolicyName;
        this.discountType = discountType;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderAmount = minOrderAmount;
        this.policyDescription = policyDescription;
    }

    public enum DiscountType {
        RATE,
        FIX
    }

}
