package com.simsimbookstore.apiserver.orders.delivery.repository;

import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Long> {
    List<DeliveryPolicy> findByStandardPolicyTrue();
}
