package com.simsimbookstore.apiserver.coupons.couponpolicy.service.impl;

import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyRequestDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyResponseDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.couponpolicy.repository.CouponPolicyRepository;

import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import com.simsimbookstore.apiserver.coupons.exception.AlreadyCouponPolicyUsed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CouponPolicyServiceImplTest {
    @Mock
    private CouponPolicyRepository couponPolicyRepository;
    @Mock
    private CouponTypeRepository couponTypeRepository;
    @InjectMocks
    private CouponPolicyServiceImpl couponPolicyService;

    private CouponPolicy couponPolicy1;
    private CouponPolicy couponPolicy2;

    @BeforeEach
    void setUp() {
        couponPolicy1 = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Fix Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(BigDecimal.valueOf(1000))
                .minOrderAmount(BigDecimal.valueOf(5000))
                .policyDescription("Fix Policy description")
                .build();

        couponPolicy2 = CouponPolicy.builder()
                .couponPolicyId(2L)
                .couponPolicyName("Rate Policy")
                .discountType(DisCountType.RATE)
                .discountRate(BigDecimal.TEN)
                .maxDiscountAmount(BigDecimal.valueOf(10000))
                .minOrderAmount(BigDecimal.valueOf(5000))
                .policyDescription("Rate Policy description")
                .build();

    }
    @Test
    @DisplayName("getAllCouponPolicy - 정상 동작")
    void getAllCouponPolicy() {
        Page<CouponPolicy> couponPolicyPage = new PageImpl<>(List.of(couponPolicy1,couponPolicy2));
        Pageable pageable = PageRequest.of(0, 10);

        when(couponPolicyRepository.findAll(pageable)).thenReturn(couponPolicyPage);

        Page<CouponPolicyResponseDto> result = couponPolicyService.getAllCouponPolicy(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getCouponPolicyName()).isEqualTo("Fix Policy");
        assertThat(result.getContent().get(1).getCouponPolicyName()).isEqualTo("Rate Policy");
    }
    @Test
    @DisplayName("getCouponPolicy - 정상 조회")
    void getCouponPolicy_success() {
        // given
        Long targetId = 1L;
        when(couponPolicyRepository.findById(targetId)).thenReturn(Optional.of(couponPolicy1));

        // when
        CouponPolicyResponseDto response = couponPolicyService.getCouponPolicy(targetId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCouponPolicyName()).isEqualTo("Fix Policy");
        assertThat(response.getDiscountPrice()).isEqualByComparingTo("1000");
    }
    @Test
    @DisplayName("getCouponPolicy - 존재하지 않는 쿠폰정책이면 예외 발생")
    void getCouponPolicy_notFound() {
        // given
        Long invalidId = 99L;
        when(couponPolicyRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponPolicyService.getCouponPolicy(invalidId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("쿠폰정책(id:" + invalidId + ")이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("createCouponPolicy - 정상 생성")
    void createCouponPolicy_success() {
        // given
        // Request DTO를 가정 (필드명과 실제 프로젝트에서 쓰는 이름이 다를 수 있으니 맞춰서 작성)
        CouponPolicyRequestDto requestDto = CouponPolicyRequestDto.builder()
                .couponPolicyName("New Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(BigDecimal.valueOf(500))
                .minOrderAmount(BigDecimal.valueOf(3000))
                .policyDescription("새로운 고정 할인 정책")
                .build();

        // 저장 결과로 반환될 엔티티를 가정
        CouponPolicy savedEntity = CouponPolicy.builder()
                .couponPolicyId(3L)
                .couponPolicyName("New Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(BigDecimal.valueOf(500))
                .minOrderAmount(BigDecimal.valueOf(3000))
                .policyDescription("새로운 고정 할인 정책")
                .build();

        // repository.save(...) 시 savedEntity 리턴
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(savedEntity);

        // when
        CouponPolicyResponseDto responseDto = couponPolicyService.createCouponPolicy(requestDto);

        // then
        assertThat(responseDto.getCouponPolicyId()).isEqualTo(3L);
        assertThat(responseDto.getCouponPolicyName()).isEqualTo("New Policy");
        assertThat(responseDto.getDiscountPrice()).isEqualByComparingTo("500");
        assertThat(responseDto.getPolicyDescription()).isEqualTo("새로운 고정 할인 정책");
    }

    @Test
    @DisplayName("deleteCouponPolicy - 쿠폰 타입에 사용되지 않은 경우 정상 삭제")
    void deleteCouponPolicy_success() {
        // given
        Long policyId = 1L;
        when(couponPolicyRepository.findById(policyId)).thenReturn(Optional.of(couponPolicy1));
        when(couponTypeRepository.findByCouponPolicyCouponPolicyId(policyId)).thenReturn(Collections.emptyList());

        // when
        couponPolicyService.deleteCouponPolicy(policyId);

        // then
        // 제대로 삭제가 호출되는지 검증
        verify(couponPolicyRepository, times(1)).delete(couponPolicy1);
    }

    @Test
    @DisplayName("deleteCouponPolicy - 이미 쿠폰 타입에 사용된 경우 예외 발생")
    void deleteCouponPolicy_alreadyUsed() {
        // given
        Long policyId = 2L;
        when(couponPolicyRepository.findById(policyId)).thenReturn(Optional.of(couponPolicy2));
        // 이미 쿠폰타입 1개 존재한다고 가정
        when(couponTypeRepository.findByCouponPolicyCouponPolicyId(policyId))
                .thenReturn(List.of(CouponType.builder().build()));

        // when & then
        assertThatThrownBy(() -> couponPolicyService.deleteCouponPolicy(policyId))
                .isInstanceOf(AlreadyCouponPolicyUsed.class)
                .hasMessageContaining("쿠폰정책(id:" + policyId + ")으로 쿠폰 타입이 생성되었습니다.");

        // 삭제 메서드가 불리지 않았는지 확인
        verify(couponPolicyRepository, never()).delete(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("deleteCouponPolicy - ID가 null이면 예외 발생")
    void deleteCouponPolicy_nullId() {
        // given
        Long nullId = null;

        // when & then
        assertThatThrownBy(() -> couponPolicyService.deleteCouponPolicy(nullId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID가 null 입니다.");

        // repository.delete()가 불리지 않아야 함
        verify(couponPolicyRepository, never()).delete(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("deleteCouponPolicy - ID가 0 이하이면 예외 발생")
    void deleteCouponPolicy_zeroOrNegativeId() {
        // given
        Long invalidId = 0L;

        // when & then
        assertThatThrownBy(() -> couponPolicyService.deleteCouponPolicy(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID는 0보다 커야합니다.");

        // repository.delete()가 불리지 않아야 함
        verify(couponPolicyRepository, never()).delete(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("deleteCouponPolicy - 쿠폰정책이 존재하지 않는 경우 예외 발생")
    void deleteCouponPolicy_notFound() {
        // given
        Long invalidId = 99L;
        when(couponPolicyRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponPolicyService.deleteCouponPolicy(invalidId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("쿠폰정책(id:" + invalidId + ")이 존재하지 않습니다.");

        // repository.delete()가 불리지 않아야 함
        verify(couponPolicyRepository, never()).delete(any(CouponPolicy.class));
    }

}