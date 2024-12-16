package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CategoryCouponPk implements Serializable {

    @Column(name = "coupon_type_id")
    private Long couponTypeId;
    @Column(name = "category_id")
    private Long categoryId;

}
