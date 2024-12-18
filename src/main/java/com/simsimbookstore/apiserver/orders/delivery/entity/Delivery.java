package com.simsimbookstore.apiserver.orders.delivery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deliveries")
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @OneToOne
    @JoinColumn(name = "delivery_policy_id")
    private DeliveryPolicy deliveryPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false)
    private DeliveryState deliveryState;

    @Column(name = "delivery", nullable = false, length = 20)
    private String deliveryReceiver;

    @Column(name = "receiver_phone_number", nullable = false, length = 20)
    private String receiverPhoneNumber;

    @Column(name = "tracking_number", nullable = true)
    private Integer trackingNumber;

    @Column(name = "postal_code", nullable = false, length = 5)
    private String postalCode;

    @Column(name = "road_address", nullable = false, length = 30)
    private String roadAddress;


    @Column(name = "detailed_address", nullable = false, length = 30)
    private String detailedAddress;

    @Column(name = "reference", nullable = true, length = 30)
    private String reference;

    public enum DeliveryState {
        PENDING,        // 배송대기
        READY,          // 배송준비
        IN_PROGRESS,    // 배송중
        COMPLETED,      // 배송완료
        RETURNED,       // 반품
        ERROR           // 배송오류
    }
}
