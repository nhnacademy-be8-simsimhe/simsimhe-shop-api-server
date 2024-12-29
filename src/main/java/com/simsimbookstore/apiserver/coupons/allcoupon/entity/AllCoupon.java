package com.simsimbookstore.apiserver.coupons.allcoupon.entity;

import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Table(name = "all_coupons")
@DiscriminatorValue("AllCoupon")
public class AllCoupon extends CouponType {
}
