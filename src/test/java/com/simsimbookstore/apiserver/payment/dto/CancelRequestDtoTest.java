package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CancelRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDefaultRefundedAtValue() {
        // 객체 생성 시 refundedAt이 자동으로 현재 시간으로 설정되는지 확인
        CancelRequestDto dto = new CancelRequestDto();
        assertNotNull(dto.getRefundedAt());

        // refundedAt이 현재 시간과 근접한지 검증 (1초 이내 차이)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(dto.getRefundedAt().isBefore(now.plusSeconds(1)) &&
                dto.getRefundedAt().isAfter(now.minusSeconds(1)));
    }

    @Test
    void testSetterAndGetter() {
        CancelRequestDto dto = new CancelRequestDto();

        dto.setCancelReason("Damaged book");
        dto.setOrderBookId(123L);
        dto.setOrderNumber("ORD-20240122-001");
        dto.setUserId(456L);
        LocalDateTime customTime = LocalDateTime.of(2024, 1, 22, 12, 30);
        dto.setRefundedAt(customTime);

        assertEquals("Damaged book", dto.getCancelReason());
        assertEquals(123L, dto.getOrderBookId());
        assertEquals("ORD-20240122-001", dto.getOrderNumber());
        assertEquals(456L, dto.getUserId());
        assertEquals(customTime, dto.getRefundedAt());
    }

    @Test
    void testConstructorInitializesRefundedAt() {
        CancelRequestDto dto1 = new CancelRequestDto();
        CancelRequestDto dto2 = new CancelRequestDto();

        assertNotNull(dto1.getRefundedAt());
        assertNotNull(dto2.getRefundedAt());

        // 두 개의 객체를 생성했을 때 refundedAt이 다를 수 있음 (밀리초 단위 차이)
        assertNotEquals(dto1.getRefundedAt(), dto2.getRefundedAt());
    }

}