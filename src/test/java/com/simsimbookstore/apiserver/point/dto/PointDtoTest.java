package com.simsimbookstore.apiserver.point.dto;

import static org.junit.jupiter.api.Assertions.*;


import com.simsimbookstore.apiserver.point.entity.PointHistory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PointDtoTest {
    @Test
    void testPointPolicyResponseDtoNoArgs() {
        PointPolicyResponseDto dto = new PointPolicyResponseDto();

        assertNull(dto.getPointPolicyId());
        assertNull(dto.getEarningMethod());
        assertNull(dto.getEarningValue());
        assertNull(dto.getDescription());
        assertFalse(dto.isAvailable()); // boolean 기본값 false
        assertNull(dto.getEarningType());
    }

    @Test
    void testPointPolicyResponseDtoFromEntity_NullEntity() {
        PointPolicyResponseDto dto = PointPolicyResponseDto.fromEntity(null);
        assertNull(dto);
    }

    @Test
    void testPointHistoryResponseDtoToString() {
        PointHistoryResponseDto dto = PointHistoryResponseDto.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(100)
                .createdAt(LocalDateTime.of(2023, 1, 1, 12, 0))
                .sourceType("ORDER")
                .orderId(10L)
                .reviewId(null)
                .description("Test description")
                .build();

        String expected = "EARN 100 2023-01-01T12:00 ORDER 10 null Test description";

        assertEquals(expected, dto.toString());
    }

    @Test
    void testOrderPointRequestDtoNoArgs() {
        // NoArgsConstructor를 이용해 객체 생성
        OrderPointRequestDto dto = new OrderPointRequestDto();

        // 모든 필드가 null인지 확인
        assertNull(dto.getUserId());
        assertNull(dto.getOrderId());
    }

    @Test
    void testOrderPointCalculateRequestDtoNoArgs() {
        // NoArgsConstructor를 이용해 객체 생성
        OrderPointCalculateRequestDto dto = new OrderPointCalculateRequestDto();

        // 모든 필드가 null인지 확인
        assertNull(dto.getUserId());
        assertNull(dto.getOrderId());
    }
}