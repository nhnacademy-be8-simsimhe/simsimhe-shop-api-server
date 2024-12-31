package com.simsimbookstore.apiserver.users.grade.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Table(name = "grades")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long gradeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, unique = true)
    private Tier tier;

    @Column(name = "min_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", precision = 10, scale = 2)
    private BigDecimal maxAmount;

}
