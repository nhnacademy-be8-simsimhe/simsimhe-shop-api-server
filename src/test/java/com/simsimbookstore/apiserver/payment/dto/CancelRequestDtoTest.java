package com.simsimbookstore.apiserver.payment.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CancelRequestDtoTest {

    @Test
    void testDefaultConstructorSetsRefundedAt() {
        // when
        CancelRequestDto dto = new CancelRequestDto();

        Assertions.assertNotNull(dto.getRefundedAt());

    }

    @Test
    void testSetterAndGetter() {
        // given
        CancelRequestDto dto = new CancelRequestDto();
        String expectedReason = "고객 요청";
        Long expectedOrderBookId = 123L;
        String expectedOrderNumber = "ORDER12345";
        Long expectedUserId = 999L;
        LocalDateTime expectedRefundedAt = LocalDateTime.of(2024, 1, 22, 12, 0, 0);

        // when
        dto.setCancelReason(expectedReason);
        dto.setOrderBookId(expectedOrderBookId);
        dto.setOrderNumber(expectedOrderNumber);
        dto.setUserId(expectedUserId);
        dto.setRefundedAt(expectedRefundedAt);

        // then
        Assertions.assertEquals(dto.getCancelReason(),expectedReason);
        Assertions.assertEquals(dto.getOrderBookId(),expectedOrderBookId);
        Assertions.assertEquals(dto.getOrderNumber(),expectedOrderNumber);
        Assertions.assertEquals(dto.getUserId(),expectedUserId);
        Assertions.assertEquals(dto.getRefundedAt(),expectedRefundedAt);

    }


}