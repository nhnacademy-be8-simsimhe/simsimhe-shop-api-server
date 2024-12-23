package com.simsimbookstore.apiserver.orders.delivery.service.impl;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryPolicyRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import com.simsimbookstore.apiserver.orders.delivery.exception.DeliveryPolicyException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryPolicyRepository;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryPolicyService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeliveryPolicyServiceImpl implements DeliveryPolicyService {

    private final DeliveryPolicyRepository deliveryPolicyRepository;

    public DeliveryPolicyServiceImpl(DeliveryPolicyRepository deliveryPolicyRepository) {
        this.deliveryPolicyRepository = deliveryPolicyRepository;
    }

    public List<DeliveryPolicy> findAll() {
        return deliveryPolicyRepository.findAll();
    }

    public DeliveryPolicy save(DeliveryPolicyRequestDto deliveryPolicyRequestDto) {
        return deliveryPolicyRepository.save(deliveryPolicyRequestDto.toEntity());
    }

    public void toggleStandardPolicy(Long deliveryPolicyId) {

        for (DeliveryPolicy deliveryPolicy : deliveryPolicyRepository.findAll()) {
            if (deliveryPolicy.isStandardPolicy()) {
                deliveryPolicy.changePolicyToFalse();
                deliveryPolicyRepository.save(deliveryPolicy);
            }
        }

        DeliveryPolicy policy = deliveryPolicyRepository.findById(deliveryPolicyId)
                .orElseThrow(() -> new DeliveryPolicyException("Delivery policy not found id: " + deliveryPolicyId));

        policy.changeStandardPolicy();

        deliveryPolicyRepository.save(policy);
    }

    public void deleteDeliveryPolicy(Long deliveryPolicyId) {
        if (!deliveryPolicyRepository.existsById(deliveryPolicyId)) {
            throw new DeliveryPolicyException("Delivery policy not found id: " + deliveryPolicyId);
        }
        deliveryPolicyRepository.deleteById(deliveryPolicyId);
    }
}
