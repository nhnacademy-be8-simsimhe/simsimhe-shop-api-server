package com.simsimbookstore.apiserver.point.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "point_policies")
public class PointPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_policy_id")
    private Long pointPolicyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "earning_method")
    private EarningMethod earningMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "earning_type", length = 20, nullable = false)
    private EarningType earningType;

    @Column(name = "earning_value")
    private BigDecimal earningValue;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @Column(name = "description", nullable = false)
    private String description;

    public enum EarningType {
        FIX, RATE
    }

    public enum EarningMethod {
        SIGNUP, REVIEW, PHOTOREVIEW,
        ORDER_STANDARD, ORDER_ROYAL, ORDER_GOLD, ORDER_PLATINUM
    }

    /**
     * 엔티티의 필드를 업데이트하는 도메인 메서드
     */
    public void update(EarningMethod earningMethod,
                       EarningType earningType,
                       BigDecimal earningValue,
                       Boolean available,
                       String description) {
        this.earningMethod = earningMethod;
        this.earningType = earningType;
        this.earningValue = earningValue;
        this.available = available;
        this.description = description;
    }

}
