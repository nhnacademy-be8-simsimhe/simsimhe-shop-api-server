package com.simsimbookstore.apiserver.orders.orderbook.entity;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name ="order_book_state")
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
}
