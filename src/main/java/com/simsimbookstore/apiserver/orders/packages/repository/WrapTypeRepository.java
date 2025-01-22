package com.simsimbookstore.apiserver.orders.packages.repository;

import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WrapTypeRepository extends JpaRepository<WrapType, Long> {
    List<WrapType> findAllByIsAvailableTrue();
}
