package com.simsimbookstore.apiserver.orders.delivery.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@Table(name = "deliveries")
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false)
    private DeliveryState deliveryState;

    @Column(name = "delivery_receiver", nullable = false, length = 20)
    private String deliveryReceiver;

    @Column(name = "receiver_phone_number", nullable = false, length = 20)
    private String receiverPhoneNumber;

    @Column(name = "tracking_number", nullable = true)
    private Integer trackingNumber;

    @Column(name = "postal_code", nullable = false, length = 5)
    private String postalCode;

    @Column(name = "road_address", nullable = false, length = 255)
    private String roadAddress;


    @Column(name = "detailed_address", nullable = false, length = 255)
    private String detailedAddress;

    @Column(name = "reference", nullable = true, length = 255)
    private String reference;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum DeliveryState {
        PENDING,        // 배송대기
        READY,          // 배송준비
        IN_PROGRESS,    // 배송중
        COMPLETED,      // 배송완료
        RETURNED,       // 반품
        ERROR,      // 배송오류
        CANCEL
    }

    @JsonCreator
    public static DeliveryState fromValue(String value) {
        try {
            return DeliveryState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid delivery state: " + value);
        }
    }

    public void updateDeliveryState(DeliveryState newDeliveryState) {
        this.deliveryState = newDeliveryState;
    }


    public boolean validateRefundable() {
        return this.deliveryState == DeliveryState.READY
                || this.deliveryState == DeliveryState.COMPLETED;
    }

    public boolean isPending() {
        return this.deliveryState == DeliveryState.PENDING;
    }

    public void cancel() {
        this.deliveryState = DeliveryState.CANCEL;
    }
}
