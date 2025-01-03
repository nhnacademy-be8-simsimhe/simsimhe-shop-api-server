package com.simsimbookstore.apiserver.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;

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
