package com.simsimbookstore.apiserver.orders.delivery.service;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;

public interface DeliveryService {

    Delivery createDelivery(DeliveryRequestDto deliveryRequestDto);

    DeliveryResponseDto getDeliveryById(Long deliveryId);

    DeliveryResponseDto updateDeliveryState(Long deliveryId, Delivery.DeliveryState newDeliveryState);

    DeliveryResponseDto findByTrackingNumber(Integer trackingNumber);

    void deleteDelivery(Long deliveryId);

}
