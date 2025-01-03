package com.simsimbookstore.apiserver.point.repository;

import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPointManageRepository extends JpaRepository<OrderPointManage, Long> {
}
