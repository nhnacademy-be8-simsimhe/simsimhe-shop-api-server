package com.simsimbookstore.apiserver.orders.delivery.controller;


import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryDetailResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryStateUpdateRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryTrackingNumberRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/deliveries")
public class DeliveryAdminController {

    private final DeliveryService deliveryService;

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDetailResponseDto> findById(@PathVariable("id") Long id) {
        DeliveryDetailResponseDto deliveryById = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(deliveryById);
    }

    // 상태 수정
    @PostMapping("/{id}/state")
    public ResponseEntity<DeliveryResponseDto> updateState(
            @PathVariable("id") Long id,
            @RequestBody DeliveryStateUpdateRequestDto requestDto) {
        Delivery.DeliveryState newState = requestDto.getNewState();
        DeliveryResponseDto updatedDelivery = deliveryService.updateDeliveryState(id, newState);
        return ResponseEntity.ok(updatedDelivery);
    }


    @GetMapping("/tracking-numbers/{id}")
    public ResponseEntity<?> findByTrackingNumber(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(deliveryService.findByTrackingNumber(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> updateState(
            @PathVariable("id") Long id,
            @RequestBody DeliveryRequestDto requestDto) {

        Delivery.DeliveryState newDeliveryState = requestDto.getDeliveryState();

        DeliveryResponseDto updatedDelivery = deliveryService.updateDeliveryState(id, newDeliveryState);

        return ResponseEntity.ok(updatedDelivery);
    }

    @GetMapping()
    public ResponseEntity<PageResponse<DeliveryResponseDto>> findAll
            (@RequestParam(defaultValue = "1") int page,
             @RequestParam(defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page-1, size);

        PageResponse<DeliveryResponseDto> deliveryPage = deliveryService.getAllDelivery(pageable);
        return ResponseEntity.ok(deliveryPage);
    }

    @GetMapping("/state")
    public ResponseEntity<PageResponse<DeliveryResponseDto>> getDeliveriesByState(
            @RequestParam("state") Delivery.DeliveryState state, // 상태 필터
            @RequestParam(defaultValue = "1") int page,          // 페이지 번호
            @RequestParam(defaultValue = "15") int size          // 페이지 크기
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        PageResponse<DeliveryResponseDto> response = deliveryService.getDeliveriesByState(state, pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/tracking-number")
    public ResponseEntity<DeliveryResponseDto> updateTrackingNumber(
            @PathVariable("id") Long deliveryId,
            @RequestBody DeliveryTrackingNumberRequestDto requestDto) {

        Integer newTrackingNumber = requestDto.getTrackingNumber();

        DeliveryResponseDto updatedDelivery = deliveryService.updateTrackingNumber(deliveryId, newTrackingNumber);
        return ResponseEntity.ok(updatedDelivery);
    }
}
