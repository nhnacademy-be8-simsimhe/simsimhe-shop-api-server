package com.simsimbookstore.apiserver.coupons.categorycoupon.entity;

import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "category_coupons")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DiscriminatorValue("CategoryCoupon")
public class CategoryCoupon extends CouponType {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
