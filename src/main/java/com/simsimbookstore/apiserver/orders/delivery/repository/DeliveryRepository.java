package com.simsimbookstore.apiserver.orders.delivery.repository;

import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByTrackingNumber(Integer trackingNumber);
}
