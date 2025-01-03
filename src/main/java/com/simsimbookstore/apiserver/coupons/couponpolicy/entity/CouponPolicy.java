package com.simsimbookstore.apiserver.coupons.couponpolicy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Table(name = "coupon_policies")
public class CouponPolicy {

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
    private BigDecimal discountPrice;

    @Column(name = "discount_rate", precision = 10, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Lob
    @Column(name = "policy_description", nullable = false)
    private String policyDescription;

//    @Builder
//    public CouponPolicy(Long couponPolicyId, String couponPolicyName, DisCountType discountType, BigDecimal discountPrice, BigDecimal discountRate, BigDecimal maxDiscountAmount, BigDecimal minOrderAmount, String policyDescription) {
//        this.couponPolicyId = couponPolicyId;
//        this.couponPolicyName = couponPolicyName;
//        this.discountType = discountType;
//        this.discountPrice = discountPrice;
//        this.discountRate = discountRate;
//        this.maxDiscountAmount = maxDiscountAmount;
//        this.minOrderAmount = minOrderAmount;
//        this.policyDescription = policyDescription;
//    }


}
