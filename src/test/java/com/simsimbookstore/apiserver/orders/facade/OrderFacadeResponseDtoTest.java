package com.simsimbookstore.apiserver.orders.facade;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OrderFacadeResponseDtoTest {

    @Test
    void testNoArgsConstructor() {
        // NoArgs 생성자를 사용하여 객체 생성
        OrderFacadeResponseDto dto = new OrderFacadeResponseDto();

        // 모든 필드가 null인지 확인
        assertNull(dto.getOrderNumber(), "orderNumber는 기본적으로 null이어야 합니다.");
        assertNull(dto.getOrderName(), "orderName은 기본적으로 null이어야 합니다.");
        assertNull(dto.getEmail(), "email은 기본적으로 null이어야 합니다.");
        assertNull(dto.getPhoneNumber(), "phoneNumber는 기본적으로 null이어야 합니다.");
        assertNull(dto.getUserName(), "userName은 기본적으로 null이어야 합니다.");
        assertNull(dto.getTotalPrice(), "totalPrice는 기본적으로 null이어야 합니다.");
        assertNull(dto.getMethod(), "method는 기본적으로 null이어야 합니다.");
    }
}
