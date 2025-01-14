package com.simsimbookstore.apiserver.point.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.point.dto.PointPolicyRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.PointPolicyRepository;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.service.UserService;
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
    private final UserService userService;

    /**
     * 특정 EarningMethod 로, 사용 가능한( isAvailable = true ) Policy 하나 조회
     */
    @Override
    public PointPolicyResponseDto getPolicy(PointPolicy.EarningMethod earningMethod) {
        PointPolicy policy = pointPolicyRepository.findPointPolicyByEarningMethodAndAvailableTrue(earningMethod).getFirst();
        return PointPolicyResponseDto.fromEntity(policy);
    }

    @Override
    public PointPolicyResponseDto getPolicyById(Long id) {
        return PointPolicyResponseDto.fromEntity(pointPolicyRepository.findById(id).orElseThrow());
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
    @Transactional
    public PointPolicyResponseDto updatePolicy(Long pointPolicyId, PointPolicyRequestDto requestDto) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new NotFoundException("PointPolicy not found with id: " + pointPolicyId));
        pointPolicy.update(requestDto.getEarningMethod(), requestDto.getEarningType(), requestDto.getEarningValue(), requestDto.isAvailable(),
                requestDto.getDescription());
        pointPolicyRepository.save(pointPolicy);

        return PointPolicyResponseDto.fromEntity(pointPolicy);
    }
    /**
     * 정책 삭제
     */
    @Override
    @Transactional
    public void deletePolicy(Long pointPolicyId) {
        pointPolicyRepository.deleteById(pointPolicyId);
    }


    @Override
    public PointPolicyResponseDto getUserPolicy(Long userId) {
        Tier tier = userService.getUserWithGradeAndRoles(userId).getGrade().getTier();

        String orderTier = "ORDER_"+tier.name();

        return getPolicy(PointPolicy.EarningMethod.valueOf(orderTier));
    }
}
