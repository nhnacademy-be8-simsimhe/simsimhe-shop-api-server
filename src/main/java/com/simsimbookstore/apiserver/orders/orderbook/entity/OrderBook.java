package com.simsimbookstore.apiserver.orders.orderbook.entity;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_books")
public class OrderBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_book_id")
    private Long orderBookId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;

    @Column(name = "discount_price", nullable = false)
    private BigDecimal discountPrice;

    @Setter
    @Column(name = "order_book_state")
    @Enumerated(EnumType.STRING)
    private OrderBookState orderBookState;

    @OneToMany(mappedBy = "orderBook", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Packages> packages = new ArrayList<>();

    @OneToOne(mappedBy = "orderBook", cascade = CascadeType.ALL)
    private CouponDiscount couponDiscount;


    public enum OrderBookState {
        PENDING,          //결제대기
        DELIVERY_READY,   // 배송대기
        IN_DELIVERY,      // 배송중
        COMPLETED,        // 완료
        RETURNED,         // 반품
        CANCELED          // 결제취소
    }

    public void updateOrderBookState(OrderBookState newOrderBookState) {
        this.orderBookState = newOrderBookState;
    }

    // 연관 관계 메서드
    public void addPackage(Packages pkg) {
        this.packages.add(pkg);
        pkg.setOrderBook(this); // 패키지의 OrderBook 설정
    }

    public void setCouponDiscount(CouponDiscount couponDiscount) {
        this.couponDiscount = couponDiscount;
        if (couponDiscount != null) {
            couponDiscount.setOrderBook(this); // 쿠폰의 OrderBook 설정
        }
    }

    public boolean validateRefundable() {
        return this.orderBookState == OrderBookState.DELIVERY_READY
                || this.orderBookState == OrderBookState.COMPLETED;
    }

    public boolean isPending() {
        return this.orderBookState == OrderBookState.PENDING;
    }

    public void cancel() {
        this.orderBookState = OrderBookState.CANCELED;
    }
}
