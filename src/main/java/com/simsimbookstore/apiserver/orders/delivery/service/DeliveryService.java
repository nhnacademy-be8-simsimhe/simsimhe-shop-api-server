package com.simsimbookstore.apiserver.orders.delivery.service;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryDetailResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface DeliveryService {

    Delivery createDelivery(DeliveryRequestDto deliveryRequestDto);


    @Transactional(readOnly = true)
    DeliveryDetailResponseDto getDeliveryById(Long deliveryId);

    DeliveryResponseDto updateDeliveryState(Long deliveryId, Delivery.DeliveryState newDeliveryState);

    DeliveryResponseDto findByTrackingNumber(Integer trackingNumber);

    void deleteDelivery(Long deliveryId);

    PageResponse<DeliveryResponseDto> getAllDelivery(Pageable pageable);

    PageResponse<DeliveryResponseDto> getDeliveriesByState(Delivery.DeliveryState state, Pageable pageable);

    DeliveryResponseDto updateTrackingNumber(Long deliveryId, Integer trackingNumber);
}
