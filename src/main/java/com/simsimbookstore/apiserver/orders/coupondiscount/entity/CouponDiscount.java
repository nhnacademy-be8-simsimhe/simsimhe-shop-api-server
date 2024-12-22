package com.simsimbookstore.apiserver.orders.coupondiscount.entity;


import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;


@Entity
@Table(name = "coupon_discounts")
public class CouponDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponDiscountId;

    @OneToOne
    @JoinColumn(name = "order_book_id", nullable = false)
    private OrderBook orderBook;

    @Column(name = "coupon_name", nullable = false, length = 40)
    private String couponName;

    @Column(name = "coupon_type", nullable = false , length = 100)
    private String couponType;

    @Column(name = "discount_price", nullable = false)
    private BigDecimal discountPrice;
}
