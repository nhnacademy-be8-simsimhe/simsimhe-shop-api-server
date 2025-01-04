package com.simsimbookstore.apiserver.point.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.PointPolicyRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class PointPolicyRepositoryTest {

    @Autowired
    private PointPolicyRepository pointPolicyRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    @DisplayName("EarningMethod와 isAvailable = true 조건으로 정책을 조회")
    void findPointPolicyByEarningMethodAndIsAvailableTrue_ShouldReturnMatchingPolicies() {
        PointPolicy policy1 = PointPolicy.builder()
                .earningMethod(PointPolicy.EarningMethod.SIGNUP)
                .earningType(PointPolicy.EarningType.FIX)
                .earningValue(new BigDecimal("100"))
                .available(true)
                .description("Signup reward")
                .build();

        PointPolicy policy2 = PointPolicy.builder()
                .earningMethod(PointPolicy.EarningMethod.REVIEW)
                .earningType(PointPolicy.EarningType.FIX)
                .earningValue(new BigDecimal("200"))
                .available(true)
                .description("Review reward")
                .build();

        PointPolicy policy3 = PointPolicy.builder()
                .earningMethod(PointPolicy.EarningMethod.SIGNUP)
                .earningType(PointPolicy.EarningType.RATE)
                .earningValue(new BigDecimal("10"))
                .available(false)
                .description("Disabled signup reward")
                .build();

        pointPolicyRepository.save(policy1);
        pointPolicyRepository.save(policy2);
        pointPolicyRepository.save(policy3);

        List<PointPolicy> result = pointPolicyRepository.findPointPolicyByEarningMethodAndAvailableTrue(PointPolicy.EarningMethod.SIGNUP);

        assertNotNull(result, "결과 리스트는 null이 아니어야 합니다.");
        assertEquals(1, result.size(), "결과 리스트 크기는 1이어야 합니다.");
        assertEquals("Signup reward", result.getFirst().getDescription(), "첫 번째 정책 설명은 'Signup reward'이어야 합니다.");
    }
}

