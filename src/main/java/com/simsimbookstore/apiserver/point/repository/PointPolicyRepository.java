package com.simsimbookstore.apiserver.point.repository;

import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    List<PointPolicy> findPointPolicyByEarningMethodAndIsAvailableTrue(PointPolicy.EarningMethod EarningMethod);
}
