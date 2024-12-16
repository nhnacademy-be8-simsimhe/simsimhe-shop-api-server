package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "point_policies")
public class PointPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_policy_id")
    private Long pointPolicyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "earning_type" , length = 20, nullable = false)
    private EarningType earningType;

    @Column(name = "fix_points")
    private Integer fixPoints;

    @Column(name = "rating")
    private BigDecimal rating;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "earning_form", nullable = false)
    private EarningForm earningForm;

    public enum EarningForm {
        FIX, RATE
    }

    public enum EarningType {
        SIGNUP, REVIEW, PHOTOREVIEW, BOOK
    }

    @Builder
    public PointPolicy(Long pointPolicyId, EarningType earningType, Integer fixPoints, BigDecimal rating, String description, LocalDate createdAt, EarningForm earningForm) {
        this.pointPolicyId = pointPolicyId;
        this.earningType = earningType;
        this.fixPoints = fixPoints;
        this.rating = rating;
        this.description = description;
        this.createdAt = createdAt;
        this.earningForm = earningForm;
    }
}
