package com.simsimbookstore.apiserver.orders.delivery.service;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryPolicyRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import java.util.List;

public interface DeliveryPolicyService {
    DeliveryPolicy save(DeliveryPolicyRequestDto deliveryPolicyRequestDto);
    void toggleStandardPolicy(Long deliveryPolicyId);
    void deleteDeliveryPolicy(Long deliveryPolicyId);

    DeliveryPolicy getStandardPolicy();

    List<DeliveryPolicy> findAll();
}
