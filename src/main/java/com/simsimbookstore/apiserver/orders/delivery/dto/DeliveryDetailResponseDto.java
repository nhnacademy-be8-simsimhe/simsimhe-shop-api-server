package com.simsimbookstore.apiserver.orders.delivery.dto;

import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDetailResponseDto {
    private Long deliveryId;
    private String deliveryState;
    private String deliveryReceiver;
    private String receiverPhoneNumber;
    private String postalCode;
    private Integer trackingNumber;
    private String roadAddress;
    private String detailedAddress;
    private String reference;
    private List<String> availableStates; // 수정 가능한 상태 목록

    public static DeliveryDetailResponseDto fromEntity(Delivery delivery) {
        return DeliveryDetailResponseDto.builder()
                .trackingNumber(delivery.getTrackingNumber())
                .deliveryId(delivery.getDeliveryId())
                .deliveryState(delivery.getDeliveryState().name())
                .deliveryReceiver(delivery.getDeliveryReceiver())
                .receiverPhoneNumber(delivery.getReceiverPhoneNumber())
                .postalCode(delivery.getPostalCode())
                .roadAddress(delivery.getRoadAddress())
                .detailedAddress(delivery.getDetailedAddress())
                .reference(delivery.getReference())
                .availableStates(Arrays.stream(Delivery.DeliveryState.values())
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .build();
    }
}
