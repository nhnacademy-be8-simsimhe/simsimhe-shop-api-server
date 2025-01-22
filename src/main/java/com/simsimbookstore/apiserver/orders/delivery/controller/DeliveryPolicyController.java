package com.simsimbookstore.apiserver.orders.delivery.controller;


import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryPolicyRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryPolicyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/delivery-policies")
public class DeliveryPolicyController {

    private final DeliveryPolicyService deliveryPolicyService;

    public DeliveryPolicyController(DeliveryPolicyService deliveryPolicyService) {
        this.deliveryPolicyService = deliveryPolicyService;
    }


    @GetMapping
    public ResponseEntity<List<DeliveryPolicy>> getAllDeliveryPolices() {
        return ResponseEntity.status(HttpStatus.OK).body(deliveryPolicyService.findAll());
    }


    @PostMapping
    public ResponseEntity<DeliveryPolicy> createDeliveryPolicy(
            @RequestBody @Valid DeliveryPolicyRequestDto deliveryPolicyRequestDto) {
        log.info("{}", deliveryPolicyRequestDto.getDeliveryPolicyName());
        log.info("{}", deliveryPolicyRequestDto.getDeliveryPrice());
        log.info("{}", deliveryPolicyRequestDto.getPolicyStandardPrice());

        DeliveryPolicy savedPolicy = deliveryPolicyService.save(deliveryPolicyRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPolicy);
    }


    @PostMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleStandardPolicy(@PathVariable Long id) {
        deliveryPolicyService.toggleStandardPolicy(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryPolicy(@PathVariable Long id) {
        deliveryPolicyService.deleteDeliveryPolicy(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
