package com.simsimbookstore.apiserver.coupons.bookcoupon.entity;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "book_coupons")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DiscriminatorValue("BookCoupon")
public class BookCoupon extends CouponType {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

}
