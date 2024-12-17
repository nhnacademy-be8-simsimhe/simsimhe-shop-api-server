package com.simsimbookstore.apiserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name= "order_point_manages")
public class OrderPointManage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_point_id")
    private Long orderPointId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_history_id", nullable = false)
    private PointHistory pointHistory;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;

    @Builder
    public OrderPointManage(Long orderPointId, PointHistory pointHistory
//            , Order order
    ) {
        this.orderPointId = orderPointId;
        this.pointHistory = pointHistory;
//        this.order = order;
    }
}
