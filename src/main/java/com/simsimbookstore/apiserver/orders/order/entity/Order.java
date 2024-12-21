package com.simsimbookstore.apiserver.orders.order.entity;

import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne
    private User user;

    @OneToOne
    private Delivery delivery;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "point_use", nullable = false)
    private BigDecimal pointUse;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "order_email", nullable = false, length = 500)
    private String orderEmail;

    @Column(name = "point_earn", nullable = false)
    private Integer pointEarn;

    @Column(name = "delivery_price", nullable = false)
    private BigDecimal deliveryPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state", nullable = false)
    private OrderState orderState;

    public enum OrderState {
        PENDING,           // 주문대기
        IN_DELIVERY,       // 배송중
        COMPLETED,         // 완료
        RETURNED,          // 반품
        ORDER_CANCELED,    // 주문취소
        PAYMENT_CANCELED   // 결제취소
    }

}
