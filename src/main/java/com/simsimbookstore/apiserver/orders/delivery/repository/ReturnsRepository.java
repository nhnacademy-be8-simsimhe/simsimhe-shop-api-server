package com.simsimbookstore.apiserver.orders.delivery.repository;

import com.simsimbookstore.apiserver.orders.delivery.entity.Returns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnsRepository extends JpaRepository<Returns, Long> {
}
