package com.simsimbookstore.apiserver.orders.delivery.entity;


import com.simsimbookstore.apiserver.orders.order.OrderBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Table(name = "returns")
@Entity
public class Return {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;

    @Column(name = "return_reason")
    private String returnReason;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "return_state", nullable = false)
    private String returnState;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "refund", nullable = false)
    private Boolean refund;

    @Column(name = "damageed", nullable = false)
    private Boolean damaged;

    @OneToOne
    private Delivery delivery;

    @OneToOne
    private OrderBook orderBook;


    public enum ReturnState {
        RETURN_REQUESTED,         // 반품요청
        RETURN_APPROVED,          // 반품승인
        RETURN_REJECTED,          // 반품거절
        RETURN_SHIPPED_BACK,      // 반송
        RETURN_ITEM_INSPECTING,   // 반송상품확인중
        RETURN_COMPLETED,         // 반품완료
        REFUND_COMPLETED          // 환불완료
    }


}
