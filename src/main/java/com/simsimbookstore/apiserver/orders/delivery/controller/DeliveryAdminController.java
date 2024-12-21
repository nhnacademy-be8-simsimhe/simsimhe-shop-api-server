package com.simsimbookstore.apiserver.orders.delivery.controller;


import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/deliveries")
public class DeliveryAdminController {

    private final DeliveryService deliveryService;

    public DeliveryAdminController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        DeliveryResponseDto deliveryById = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(deliveryById);
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
}
