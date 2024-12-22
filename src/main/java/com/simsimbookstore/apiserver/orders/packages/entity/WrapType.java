package com.simsimbookstore.apiserver.orders.packages.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "wrap_types")
public class WrapType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageTypeId;

    @Column(name = "wrap_name", nullable = false, length = 100)
    private String packageName;

    @Column(name = "wrap_price", nullable = false)
    private BigDecimal packagePrice;
}
