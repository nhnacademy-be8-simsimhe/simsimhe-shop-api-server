package com.simsimbookstore.apiserver.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "order_books")
public class OrderBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_book_id")
    private Long orderBookId;

//    @OneToOne
//    @JoinColumn(name = "book_id")
//    private Book book;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;

    @Column(name = "discount_price", nullable = false)
    private BigDecimal discountPrice;

    @Column(name ="order_book_state")
    @Enumerated(EnumType.STRING)
    private OrderBookState orderBookState;


    public enum OrderBookState {
        PENDING,          // 주문대기
        IN_DELIVERY,      // 배송중
        COMPLETED,        // 완료
        RETURNED,         // 반품
        CANCELED          // 결제취소
    }
}
