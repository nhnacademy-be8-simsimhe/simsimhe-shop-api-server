package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class BookCouponPk {

    @Column(name = "category_type_id")
    private Long couponTypeId;
    @Column(name = "book_id")
    private Long bookId;

}
