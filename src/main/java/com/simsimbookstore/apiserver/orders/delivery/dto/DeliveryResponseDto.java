package com.simsimbookstore.apiserver.orders.delivery.dto;

import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryResponseDto {

    private Long deliveryId;
    private String deliveryState;
    private String deliveryReceiver;
    private String receiverPhoneNumber;
    private Integer trackingNumber;
    private String postalCode;
    private String roadAddress;
    private String detailedAddress;
    private String reference;

    public static DeliveryResponseDto fromEntity(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .deliveryState(delivery.getDeliveryState().name())
                .deliveryReceiver(delivery.getDeliveryReceiver())
                .receiverPhoneNumber(delivery.getReceiverPhoneNumber())
                .trackingNumber(delivery.getTrackingNumber())
                .postalCode(delivery.getPostalCode())
                .roadAddress(delivery.getRoadAddress())
                .detailedAddress(delivery.getDetailedAddress())
                .reference(delivery.getReference())
                .build();
    }
}

