package com.simsimbookstore.apiserver.orders.delivery.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryPolicyRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import com.simsimbookstore.apiserver.orders.delivery.exception.DeliveryPolicyException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryPolicyRepository;
import com.simsimbookstore.apiserver.orders.delivery.service.impl.DeliveryPolicyServiceImpl;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryPolicyService 단위 테스트")
class DeliveryPolicyServiceTest {

    private DeliveryPolicyService deliveryPolicyService;

    @Mock
    private DeliveryPolicyRepository deliveryPolicyRepository;

    DeliveryPolicy deliveryPolicy, deliveryPolicy2;


    @BeforeEach
    void setUp() {
        deliveryPolicyService = new DeliveryPolicyServiceImpl(deliveryPolicyRepository);
        deliveryPolicy = DeliveryPolicy.builder()
                .deliveryPolicyId(1L)
                .deliveryPolicyName("Standard Policy")
                .policyStandardPrice(BigDecimal.valueOf(1000))
                .standardPolicy(false)
                .build();

        deliveryPolicy2 = DeliveryPolicy.builder()
                .deliveryPolicyId(2L)
                .deliveryPolicyName("Standard Policy")
                .policyStandardPrice(BigDecimal.valueOf(1000))
                .standardPolicy(true)
                .build();
    }

    @Test
    @DisplayName("새로운 DeliveryPolicy 저장 테스트")
    void saveDeliveryPolicyTest() {
        DeliveryPolicyRequestDto dto = new DeliveryPolicyRequestDto("이름", BigDecimal.valueOf(30000), BigDecimal.valueOf(3000), false);
        deliveryPolicyService.save(dto);

        verify(deliveryPolicyRepository, times(1)).save(any(DeliveryPolicy.class));

    }

    @Test
    @DisplayName("기준 배송 정책 토글 테스트")
    void toggleStandardPolicyTest() {
        when(deliveryPolicyRepository.findAll()).thenReturn(List.of(deliveryPolicy, deliveryPolicy2));
        when(deliveryPolicyRepository.findById(1L)).thenReturn(Optional.of(deliveryPolicy));

        deliveryPolicyService.toggleStandardPolicy(1L);

        assertTrue(deliveryPolicy.isStandardPolicy());
        assertFalse(deliveryPolicy2.isStandardPolicy());
    }

    @Test
    @DisplayName("존재하지 않는 배송 정책 ID로 기준 정책 토글 시 예외 발생 테스트")
    void toggleStandardPolicyNotFoundTest() {
        when(deliveryPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        DeliveryPolicyException exception = assertThrows(
                DeliveryPolicyException.class,
                () -> deliveryPolicyService.toggleStandardPolicy(1L)
        );
        assertEquals("Delivery policy not found id: 1", exception.getMessage());
    }

    @Test
    @DisplayName("배송 정책 삭제 테스트")
    void deleteDeliveryPolicyTest() {
        when(deliveryPolicyRepository.existsById(1L)).thenReturn(true);

        deliveryPolicyService.deleteDeliveryPolicy(1L);

        verify(deliveryPolicyRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 배송 정책 삭제 시 예외 발생 테스트")
    void deleteDeliveryPolicyNotFoundTest() {
        when(deliveryPolicyRepository.existsById(1L)).thenReturn(false);

        DeliveryPolicyException exception = assertThrows(
                DeliveryPolicyException.class,
                () -> deliveryPolicyService.deleteDeliveryPolicy(1L)
        );
        assertEquals("Delivery policy not found id: 1", exception.getMessage());
    }

    @Test
    @DisplayName("기준 배송 정책 조회 테스트")
    void testGetStandardPolicy() {
        // given: 기준 정책을 반환하는 Repository 설정
        LinkedList<DeliveryPolicy> standardPolicies = new LinkedList<>();
        standardPolicies.add(deliveryPolicy2); // 기준 정책 세팅
        when(deliveryPolicyRepository.findByStandardPolicyTrue()).thenReturn(standardPolicies);

        // when
        DeliveryPolicy result = deliveryPolicyService.getStandardPolicy();

        // then
        assertNotNull(result, "기준 배송 정책은 null이 아니어야 함");
        assertEquals(deliveryPolicy2, result, "조회된 기준 배송 정책이 예상과 일치해야 함");
        verify(deliveryPolicyRepository, times(1)).findByStandardPolicyTrue();
    }

    @Test
    @DisplayName("모든 배송 정책 조회 테스트")
    void testFindAll() {
        // given: 모든 배송 정책을 반환하는 Repository 설정
        List<DeliveryPolicy> policies = List.of(deliveryPolicy, deliveryPolicy2);
        when(deliveryPolicyRepository.findAll()).thenReturn(policies);

        // when
        List<DeliveryPolicy> result = deliveryPolicyService.findAll();

        // then
        assertNotNull(result, "반환된 배송 정책 목록은 null이 아니어야 함");
        assertEquals(2, result.size(), "배송 정책 목록의 크기가 예상과 일치해야 함");
        assertTrue(result.contains(deliveryPolicy), "결과에 deliveryPolicy가 포함되어야 함");
        assertTrue(result.contains(deliveryPolicy2), "결과에 deliveryPolicy2가 포함되어야 함");
        verify(deliveryPolicyRepository, times(1)).findAll();
    }
}
