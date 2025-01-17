package com.simsimbookstore.apiserver.orders.delivery.service.impl;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryDetailResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.exception.DeliveryNotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.exception.DeliveryStateUpdateException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import java.util.EnumSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    


    public DeliveryServiceImpl(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public Delivery createDelivery(DeliveryRequestDto deliveryRequestDto) {
        Delivery delivery = deliveryRequestDto.toEntity();
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDetailResponseDto getDeliveryById(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with ID: " + deliveryId));

        return DeliveryDetailResponseDto.fromEntity(delivery);
    }


    @Override
    public DeliveryResponseDto updateDeliveryState(Long deliveryId, Delivery.DeliveryState newDeliveryState) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with ID: " + deliveryId));

        validateDeliveryState(newDeliveryState);

        delivery.updateDeliveryState(newDeliveryState);
        deliveryRepository.save(delivery);

        return DeliveryResponseDto.fromEntity(delivery);
    }

    @Override
    public DeliveryResponseDto findByTrackingNumber(Integer trackingNumber) {
        Delivery findDelivery = deliveryRepository.findByTrackingNumber(trackingNumber).orElseThrow(
                () -> new DeliveryNotFoundException("Delivery not found with tracking number: " + trackingNumber));

        return DeliveryResponseDto.fromEntity(findDelivery);
    }

    @Override
    public void deleteDelivery(Long deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) {
            throw new DeliveryNotFoundException("Delivery not found with ID: " + deliveryId);
        }
        deliveryRepository.deleteById(deliveryId);
    }

    @Override
    public PageResponse<DeliveryResponseDto> getAllDelivery(Pageable pageable) {
        Page<Delivery> deliveryPage = deliveryRepository.findAll(pageable);

        Page<DeliveryResponseDto> dtoPage = deliveryPage.map(DeliveryResponseDto::fromEntity);

        return new PageResponse<DeliveryResponseDto>().getPageResponse(
                pageable.getPageNumber() + 1,
                10,
                dtoPage
        );
    }

    @Override
    public PageResponse<DeliveryResponseDto> getDeliveriesByState (Delivery.DeliveryState state, Pageable pageable) {
        Page<Delivery> deliveryPage = deliveryRepository.findByDeliveryState(state, pageable);

        Page<DeliveryResponseDto> dtoPage = deliveryPage.map(DeliveryResponseDto::fromEntity);

        return new PageResponse<DeliveryResponseDto>().getPageResponse(
                pageable.getPageNumber() + 1,
                10,
                dtoPage
        );
    }

    @Override
    public DeliveryResponseDto updateTrackingNumber(Long deliveryId, Integer trackingNumber) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.setTrackingNumber(trackingNumber);  // 트래킹 번호 업데이트
        deliveryRepository.save(delivery);

        return DeliveryResponseDto.fromEntity(delivery);
    }


    private void validateDeliveryState(Delivery.DeliveryState newDeliveryState) {
        if (newDeliveryState == null) {
            throw new DeliveryStateUpdateException("새로운 배송 상태는 null 일 수 없습니다.");
        }

        if (!EnumSet.allOf(Delivery.DeliveryState.class).contains(newDeliveryState)) {
            throw new DeliveryStateUpdateException("유효하지 않은 배송 상태입니다: " + newDeliveryState);
        }
    }

}
