package com.simsimbookstore.apiserver.orders.delivery.repository;

import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Long> {
    List<DeliveryPolicy> findByStandardPolicyTrue();
}
