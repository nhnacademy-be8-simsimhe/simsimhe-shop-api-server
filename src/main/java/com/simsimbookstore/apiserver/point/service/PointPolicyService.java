package com.simsimbookstore.apiserver.point.service;

import com.simsimbookstore.apiserver.point.dto.PointPolicyRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface PointPolicyService {


    PointPolicyResponseDto getPolicy(PointPolicy.EarningMethod earningMethod);

    List<PointPolicyResponseDto> getAllPolicies();

    @Transactional
    PointPolicyResponseDto createPolicy(PointPolicyRequestDto requestDto);

    @Transactional
    PointPolicyResponseDto updatePolicy(Long policyId, PointPolicyRequestDto requestDto);

    @Transactional
    void deletePolicy(Long pointPolicyId);
}
