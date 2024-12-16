package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "book_coupons")
public class BookCoupon {
    @EmbeddedId
    private BookCouponPk bookCouponPk;

    @MapsId("couponTypeId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_type_id")
    private CouponType couponType;

//    @MapsId("bookId")
//    @ManyToOne
//    @JoinColumn(name = "book_id")
//    private Book book;

    @Builder
    public BookCoupon(BookCouponPk bookCouponPk, CouponType couponType
//            , Book book
    ) {
        this.bookCouponPk = bookCouponPk;
        this.couponType = couponType;
//        this.book = book;
    }
}
