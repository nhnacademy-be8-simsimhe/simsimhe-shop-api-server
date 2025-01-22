package com.simsimbookstore.apiserver.orders.delivery.dto;

import static org.junit.jupiter.api.Assertions.*;



import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeliveryTest {
    @Test
    void testAllArgsConstructor() {
        DeliveryStateUpdateRequestDto dto = new DeliveryStateUpdateRequestDto();

        dto.setNewState(null);

        assertNull(dto.getNewState());
    }

    @Test
    void testAllArgsConstructor2() {
        DeliveryTrackingNumberRequestDto dto = new DeliveryTrackingNumberRequestDto(123456789);

        assertEquals(123456789, dto.getTrackingNumber());
    }

    @Test
    void testSetters() {
        DeliveryPolicyRequestDto dto = new DeliveryPolicyRequestDto();

        dto.setDeliveryPolicyName("Standard Delivery");
        dto.setPolicyStandardPrice(BigDecimal.valueOf(50.0));
        dto.setDeliveryPrice(BigDecimal.valueOf(5.0));
        dto.setStandardPolicy(true);

        assertEquals("Standard Delivery", dto.getDeliveryPolicyName());
        assertEquals(BigDecimal.valueOf(50.0), dto.getPolicyStandardPrice());
        assertEquals(BigDecimal.valueOf(5.0), dto.getDeliveryPrice());
        assertTrue(dto.isStandardPolicy());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        DeliveryDetailResponseDto dto = new DeliveryDetailResponseDto();

        dto.setDeliveryId(1L);
        dto.setDeliveryState("SHIPPED");
        dto.setDeliveryReceiver("John Doe");
        dto.setReceiverPhoneNumber("123-456-7890");
        dto.setPostalCode("12345");
        dto.setTrackingNumber(987654321);
        dto.setRoadAddress("123 Main St");
        dto.setDetailedAddress("Apt 4B");
        dto.setReference("Near the park");
        dto.setAvailableStates(List.of("PENDING", "SHIPPED", "DELIVERED"));

        assertEquals(1L, dto.getDeliveryId());
        assertEquals("SHIPPED", dto.getDeliveryState());
        assertEquals("John Doe", dto.getDeliveryReceiver());
        assertEquals("123-456-7890", dto.getReceiverPhoneNumber());
        assertEquals("12345", dto.getPostalCode());
        assertEquals(987654321, dto.getTrackingNumber());
        assertEquals("123 Main St", dto.getRoadAddress());
        assertEquals("Apt 4B", dto.getDetailedAddress());
        assertEquals("Near the park", dto.getReference());
        assertIterableEquals(List.of("PENDING", "SHIPPED", "DELIVERED"), dto.getAvailableStates());
    }


    @Test
    void testAllArgsConstructor3() {
        // Delivery.DeliveryState의 SHIPPED 값을 사용하여 DTO 객체 생성
        DeliveryStateUpdateRequestDto dto =
                new DeliveryStateUpdateRequestDto(Delivery.DeliveryState.COMPLETED);

        // 생성된 객체의 newState 필드가 SHIPPED인지 검증
        assertEquals(Delivery.DeliveryState.COMPLETED, dto.getNewState(),
                "newState 필드는 COMPLETED 여야 합니다.");
    }

}
