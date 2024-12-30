package com.simsimbookstore.apiserver.point.service;

import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import java.math.BigDecimal;
import java.util.List;

public interface PointPolicyService {

    PointPolicy getPolicy(PointPolicy.EarningType earningType);

    List<PointPolicy> getAllPolicies();

    PointPolicy createPolicy(PointPolicy pointPolicy);

    PointPolicy updatePolicy(PointPolicy pointPolicy);

    void deletePolicy(Long pointPolicyId);
}
