package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category_coupons")
public class CategoryCoupon {

    @EmbeddedId
    private CategoryCouponPk categoryCouponPk;

//    @MapsId("categoryId")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    private Category category;

    @MapsId("couponTypeId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_type_id")
    private CouponType couponType;

    @Builder
    public CategoryCoupon(CategoryCouponPk categoryCouponPk,
//                          Category category,
                          CouponType couponType) {
        this.categoryCouponPk = categoryCouponPk;
//        this.category = category;
        this.couponType = couponType;
    }
}
