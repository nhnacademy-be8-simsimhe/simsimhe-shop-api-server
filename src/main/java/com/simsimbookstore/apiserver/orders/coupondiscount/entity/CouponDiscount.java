package com.simsimbookstore.apiserver.orders.coupondiscount.entity;


import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupon_discounts")
public class CouponDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponDiscountId;

    // 연관 관계 메서드
    @Setter
    @OneToOne
    @JoinColumn(name = "order_book_id", nullable = false)
    private OrderBook orderBook;

    @OneToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;


    @Column(name = "coupon_name", nullable = false, length = 40)
    private String couponName;

    @Column(name = "coupon_type", nullable = false, length = 100)
    private String couponType;

    @Column(name = "discount_price", nullable = false)
    private BigDecimal discountPrice;

    @Column(name = "coupon_usage")
    private boolean usage;

}
