package com.simsimbookstore.apiserver.point.service.impl;

import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.PointPolicyRepository;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public PointPolicy getPolicy(PointPolicy.EarningType earningType) {
        return pointPolicyRepository.findPointPolicyByEarningTypeAndIsAvailableTrue(earningType)
                .getFirst();
    }

    @Override
    public List<PointPolicy> getAllPolicies() {
        return pointPolicyRepository.findAll();
    }

    @Override
    public PointPolicy createPolicy(PointPolicy pointPolicy) {
        return pointPolicyRepository.save(pointPolicy);
    }

    @Override
    public PointPolicy updatePolicy(PointPolicy pointPolicy) {
        return pointPolicyRepository.save(pointPolicy);
    }

    @Override
    public void deletePolicy(Long pointPolicyId) {
        pointPolicyRepository.deleteById(pointPolicyId);
    }
}
