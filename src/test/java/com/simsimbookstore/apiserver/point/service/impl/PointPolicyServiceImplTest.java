package com.simsimbookstore.apiserver.point.service.impl;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.point.dto.PointPolicyRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.PointPolicyRepository;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointPolicyServiceImplTest {

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PointPolicyServiceImpl pointPolicyService;

    @Test
    void testGetPolicy_Success() {
        // Arrange
        PointPolicy.EarningMethod earningMethod = PointPolicy.EarningMethod.ORDER_GOLD;
        PointPolicy mockPolicy = PointPolicy.builder()
                .pointPolicyId(1L)
                .earningMethod(earningMethod)
                .earningType(PointPolicy.EarningType.RATE)
                .earningValue(new BigDecimal("10.0"))
                .available(true)
                .description("Gold tier order policy")
                .build();

        when(pointPolicyRepository.findPointPolicyByEarningMethodAndAvailableTrue(earningMethod))
                .thenReturn(List.of(mockPolicy));

        // Act
        PointPolicyResponseDto result = pointPolicyService.getPolicy(earningMethod);

        // Assert
        assertNotNull(result);
        assertEquals(mockPolicy.getPointPolicyId(), result.getPointPolicyId());
        assertEquals(mockPolicy.getEarningMethod(), result.getEarningMethod());
        assertEquals(mockPolicy.getEarningValue(), result.getEarningValue());
        assertEquals(mockPolicy.getDescription(), result.getDescription());
        verify(pointPolicyRepository, times(1))
                .findPointPolicyByEarningMethodAndAvailableTrue(earningMethod);
    }

    @Test
    void testGetPolicyById_Success() {
        // Arrange
        Long policyId = 1L;
        PointPolicy mockPolicy = PointPolicy.builder()
                .pointPolicyId(policyId)
                .earningMethod(PointPolicy.EarningMethod.ORDER_GOLD)
                .earningType(PointPolicy.EarningType.FIX)
                .earningValue(new BigDecimal("5000"))
                .available(true)
                .description("Fixed gold tier policy")
                .build();

        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(mockPolicy));

        // Act
        PointPolicyResponseDto result = pointPolicyService.getPolicyById(policyId);

        // Assert
        assertNotNull(result);
        assertEquals(mockPolicy.getPointPolicyId(), result.getPointPolicyId());
        verify(pointPolicyRepository, times(1)).findById(policyId);
    }

    @Test
    void testCreatePolicy_Success() {
        // Arrange
        PointPolicyRequestDto requestDto = PointPolicyRequestDto.builder()
                .earningMethod(PointPolicy.EarningMethod.REVIEW)
                .earningType(PointPolicy.EarningType.FIX)
                .earningValue(new BigDecimal("100"))
                .available(true)
                .description("Review policy")
                .build();

        PointPolicy mockPolicy = requestDto.toEntity();

        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(mockPolicy);

        // Act
        PointPolicyResponseDto result = pointPolicyService.createPolicy(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(mockPolicy.getPointPolicyId(), result.getPointPolicyId());
        verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
    }

    @Test
    void testUpdatePolicy_Success() {
        // Arrange
        Long policyId = 1L;
        PointPolicyRequestDto requestDto = PointPolicyRequestDto.builder()
                .earningMethod(PointPolicy.EarningMethod.PHOTOREVIEW)
                .earningType(PointPolicy.EarningType.RATE)
                .earningValue(new BigDecimal("20"))
                .available(false)
                .description("Updated photo review policy")
                .build();

        PointPolicy mockPolicy = PointPolicy.builder()
                .pointPolicyId(policyId)
                .earningMethod(PointPolicy.EarningMethod.REVIEW)
                .earningType(PointPolicy.EarningType.FIX)
                .earningValue(new BigDecimal("100"))
                .available(true)
                .description("Original policy")
                .build();

        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(mockPolicy));
        when(pointPolicyRepository.save(mockPolicy)).thenReturn(mockPolicy);

        // Act
        PointPolicyResponseDto result = pointPolicyService.updatePolicy(policyId, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(requestDto.getEarningMethod(), result.getEarningMethod());
        assertEquals(requestDto.getEarningValue(), result.getEarningValue());
        assertEquals(requestDto.getDescription(), result.getDescription());
        assertFalse(result.isAvailable());
        verify(pointPolicyRepository, times(1)).findById(policyId);
        verify(pointPolicyRepository, times(1)).save(mockPolicy);
    }

    @Test
    void testDeletePolicy_Success() {
        // Arrange
        Long policyId = 1L;

        doNothing().when(pointPolicyRepository).deleteById(policyId);

        // Act
        pointPolicyService.deletePolicy(policyId);

        // Assert
        verify(pointPolicyRepository, times(1)).deleteById(policyId);
    }

    @Test
    void testGetUserPolicy_Success() {
        // Arrange
        Long userId = 1L;
        Tier mockTier = Tier.PLATINUM;

        User mockUser = User.builder()
                .userId(userId)
                .grade(Grade.builder().tier(mockTier).build())
                .build();

        PointPolicy mockPolicy = PointPolicy.builder()
                .pointPolicyId(1L)
                .earningMethod(PointPolicy.EarningMethod.ORDER_PLATINUM)
                .earningType(PointPolicy.EarningType.RATE)
                .earningValue(new BigDecimal("15"))
                .available(true)
                .description("Platinum tier policy")
                .build();

        when(userService.getUserWithGradeAndRoles(userId)).thenReturn(mockUser);
        when(pointPolicyRepository.findPointPolicyByEarningMethodAndAvailableTrue(PointPolicy.EarningMethod.ORDER_PLATINUM))
                .thenReturn(List.of(mockPolicy));

        // Act
        PointPolicyResponseDto result = pointPolicyService.getUserPolicy(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockPolicy.getPointPolicyId(), result.getPointPolicyId());
        assertEquals(mockPolicy.getEarningMethod(), result.getEarningMethod());
        verify(userService, times(1)).getUserWithGradeAndRoles(userId);
        verify(pointPolicyRepository, times(1))
                .findPointPolicyByEarningMethodAndAvailableTrue(PointPolicy.EarningMethod.ORDER_PLATINUM);
    }

    @Test
    void testGetAllPolicies_Success() {
        // Arrange
        List<PointPolicy> mockPolicies = List.of(
                PointPolicy.builder()
                        .pointPolicyId(1L)
                        .earningMethod(PointPolicy.EarningMethod.ORDER_GOLD)
                        .earningType(PointPolicy.EarningType.RATE)
                        .earningValue(new BigDecimal("10.0"))
                        .available(true)
                        .description("Gold tier policy")
                        .build(),
                PointPolicy.builder()
                        .pointPolicyId(2L)
                        .earningMethod(PointPolicy.EarningMethod.REVIEW)
                        .earningType(PointPolicy.EarningType.FIX)
                        .earningValue(new BigDecimal("500"))
                        .available(false)
                        .description("Review policy")
                        .build()
        );

        when(pointPolicyRepository.findAll()).thenReturn(mockPolicies);

        // Act
        List<PointPolicyResponseDto> result = pointPolicyService.getAllPolicies();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // 검증: 첫 번째 정책
        PointPolicyResponseDto dto1 = result.getFirst();
        assertEquals(1L, dto1.getPointPolicyId());
        assertEquals(PointPolicy.EarningMethod.ORDER_GOLD, dto1.getEarningMethod());
        assertEquals(new BigDecimal("10.0"), dto1.getEarningValue());
        assertTrue(dto1.isAvailable());
        assertEquals("Gold tier policy", dto1.getDescription());

        // 검증: 두 번째 정책
        PointPolicyResponseDto dto2 = result.get(1);
        assertEquals(2L, dto2.getPointPolicyId());
        assertEquals(PointPolicy.EarningMethod.REVIEW, dto2.getEarningMethod());
        assertEquals(new BigDecimal("500"), dto2.getEarningValue());
        assertFalse(dto2.isAvailable());
        assertEquals("Review policy", dto2.getDescription());

        verify(pointPolicyRepository, times(1)).findAll();
    }
}

