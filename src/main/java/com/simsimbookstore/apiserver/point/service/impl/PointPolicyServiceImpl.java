package com.simsimbookstore.apiserver.point.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.point.dto.PointPolicyRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.PointPolicyRepository;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    /**
     * 특정 EarningMethod 로, 사용 가능한( isAvailable = true ) Policy 하나 조회
     */
    @Override
    public PointPolicyResponseDto getPolicy(PointPolicy.EarningMethod earningMethod) {
        PointPolicy policy = pointPolicyRepository.findPointPolicyByEarningMethodAndIsAvailableTrue(earningMethod).getFirst();
        return PointPolicyResponseDto.fromEntity(policy);
    }

    /**
     * 모든 정책 조회
     */
    @Override
    public List<PointPolicyResponseDto> getAllPolicies() {
        List<PointPolicy> policyList = pointPolicyRepository.findAll();

        return policyList.stream()
                .map(PointPolicyResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 정책 생성
     * create, update, delete 같은 쓰기 메서드는 readOnly = false 로 트랜잭션
     */
    @Override
    @Transactional
    public PointPolicyResponseDto createPolicy(PointPolicyRequestDto requestDto) {
        PointPolicy newPolicy = requestDto.toEntity();

        PointPolicy savedPolicy = pointPolicyRepository.save(newPolicy);

        return PointPolicyResponseDto.fromEntity(savedPolicy);
    }

    /**
     * 정책 수정
     */
    @Override
    @Transactional
    public PointPolicyResponseDto updatePolicy(Long policyId, PointPolicyRequestDto requestDto) {
        // 1) DB에서 엔티티 조회
        PointPolicy policy = pointPolicyRepository.findById(policyId).orElseThrow(
                () -> new NotFoundException(String.format("PointPolicy with id %s not found", policyId))
        );

        // 2) 필요한 필드를 갱신 (엔티티 세터 혹은 빌더패턴 재생성 등으로)
        policy.update(
                requestDto.getEarningMethod(),
                requestDto.getEarningType(),
                requestDto.getEarningValue(),
                requestDto.isAvailable(),
                requestDto.getDescription()
        );


        return PointPolicyResponseDto.fromEntity(policy);
    }

    /**
     * 정책 삭제
     */
    @Override
    @Transactional
    public void deletePolicy(Long pointPolicyId) {
        pointPolicyRepository.deleteById(pointPolicyId);
    }
}
