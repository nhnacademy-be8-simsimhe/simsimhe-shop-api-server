package com.simsimbookstore.apiserver.orders.order.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
class OrderDtoTest {
    @Test
    void testNoArgsConstructor1() {
        TotalResponseDto dto = new TotalResponseDto();

        assertNull(dto.getTotal(), "total은 null이어야 합니다.");
        assertNull(dto.getDeliveryPrice(), "deliveryPrice는 null이어야 합니다.");
        assertNull(dto.getOriginalPrice(), "originalPrice는 null이어야 합니다.");
        assertNull(dto.getUsePoint(), "usePoint는 null이어야 합니다.");
        assertNull(dto.getAvailablePoints(), "availablePoints는 null이어야 합니다.");
        assertNull(dto.getNotPointUseTotal(), "notPointUseTotal은 null이어야 합니다.");
        assertNull(dto.getCouponDiscountDetails(), "couponDiscountDetails는 null이어야 합니다.");
    }
    @Test
    void testNoArgsConstructor2() {
        OrderResponseDto dto = new OrderResponseDto();

        assertNull(dto.getOrderId(), "orderId는 null이어야 합니다.");
        assertNull(dto.getOrderNumber(), "orderNumber는 null이어야 합니다.");
        assertNull(dto.getTotalPrice(), "totalPrice는 null이어야 합니다.");
    }

    @Test
    void testNoArgsConstructor3() {
        OrderOrderBookResponseDto dto = new OrderOrderBookResponseDto();

        assertNull(dto.getOrderResponseDto(), "orderResponseDto는 null이어야 합니다.");
        assertNull(dto.getOrderBookAdminResponseDtos(), "orderBookAdminResponseDtos는 null이어야 합니다.");
    }

    @Test
    void testNoArgsConstructor4() {
        OrderHistoryResponseDto dto = new OrderHistoryResponseDto();

        assertNull(dto.getOrderNumber(), "orderNumber는 null이어야 합니다.");
        assertNull(dto.getOrderDate(), "orderDate는 null이어야 합니다.");
        assertNull(dto.getOrderName(), "orderName은 null이어야 합니다.");
        assertNull(dto.getOrderAmount(), "orderAmount는 null이어야 합니다.");
        assertNull(dto.getOrderState(), "orderState는 null이어야 합니다.");
        assertNull(dto.getTrackingNumber(), "trackingNumber는 null이어야 합니다.");
        assertNull(dto.getOrderUserName(), "orderUserName은 null이어야 합니다.");
        assertNull(dto.getReceiverName(), "receiverName은 null이어야 합니다.");
    }

    @Test
    void testNoArgsConstructor5() {
        OrderAdminResponseDto dto = new OrderAdminResponseDto();

        assertNull(dto.getOrderId(), "orderId는 null이어야 합니다.");
        assertNull(dto.getOrderNumber(), "orderNumber는 null이어야 합니다.");
        assertNull(dto.getTotalPrice(), "totalPrice는 null이어야 합니다.");
        assertNull(dto.getOrderState(), "orderState는 null이어야 합니다.");
    }

    @Test
    void testBuilderAndGetters() {
        CouponUsageDto dto = CouponUsageDto.builder()
                .bookId(10L)
                .couponId(20L)
                .couponName("Discount 10%")
                .discount(BigDecimal.valueOf(1000))
                .build();

        assertEquals(10L, dto.getBookId());
        assertEquals(20L, dto.getCouponId());
        assertEquals("Discount 10%", dto.getCouponName());
        assertEquals(BigDecimal.valueOf(1000), dto.getDiscount());
    }

    @Test
    void testNoArgsConstructor7() {
        BookListResponseDto dto = new BookListResponseDto();

        assertNull(dto.getBookId(), "bookId는 null이어야 합니다.");
        assertNull(dto.getTitle(), "title은 null이어야 합니다.");
        assertNull(dto.getPrice(), "price는 null이어야 합니다.");
        assertNull(dto.getQuantity(), "quantity는 null이어야 합니다.");
        assertNotNull(dto.getCoupons(), "coupons는 null이 아니어야 합니다.");
        assertEquals(ArrayList.class, dto.getCoupons().getClass(), "coupons는 ArrayList 여야 합니다.");
        assertTrue(dto.getCoupons().isEmpty(), "coupons 리스트는 비어있어야 합니다.");
    }

    @Test
    void testNoArgsConstructor8() {
        CouponUsageDto dto = new CouponUsageDto();

        // 모든 필드가 null인지 확인
        assertNull(dto.getBookId(), "bookId는 기본적으로 null이어야 합니다.");
        assertNull(dto.getCouponId(), "couponId는 기본적으로 null이어야 합니다.");
        assertNull(dto.getCouponName(), "couponName은 기본적으로 null이어야 합니다.");
        assertNull(dto.getDiscount(), "discount는 기본적으로 null이어야 합니다.");
    }
}