package com.simsimbookstore.apiserver.point.repository;

import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    List<PointPolicy> findPointPolicyByEarningMethodAndAvailableTrue(PointPolicy.EarningMethod EarningMethod);
}
