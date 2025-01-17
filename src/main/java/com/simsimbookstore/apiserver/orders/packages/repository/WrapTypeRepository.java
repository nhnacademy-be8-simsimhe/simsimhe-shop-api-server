package com.simsimbookstore.apiserver.orders.packages.repository;

import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrapTypeRepository extends JpaRepository<WrapType, Long> {
    List<WrapType> findAllByIsAvailableTrue();
}
