package com.simsimbookstore.apiserver.orders.order.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.order.dto.OrderHistoryResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.history.OrderHistoryServiceImpl;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderHistoryServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderHistoryServiceImpl orderHistoryService;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        Delivery mockDelivery = Delivery.builder()
                .deliveryId(1L)
                .deliveryReceiver("name")
                .deliveryState(Delivery.DeliveryState.READY)
                .build();
        User mockUser = User.builder().userName("Test User").build();
        mockOrder = Order.builder()
                .orderNumber("ORD123")
                .orderDate(LocalDateTime.now())
                .user(mockUser)
                .delivery(mockDelivery) // Delivery가 null인 경우
                .totalPrice(BigDecimal.valueOf(1000))
                .orderState(Order.OrderState.PENDING)
                .build();

    }

    @Test
    void getOrderHistoryWithNullDelivery() {

        // Mock 리포지토리 동작 정의
        when(orderRepository.findByUserUserIdOrderByOrderDateDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockOrder)));


        // 서비스 호출
        PageResponse<OrderHistoryResponseDto> result = orderHistoryService.getOrderHistory(1L, PageRequest.of(0, 10));

        // 결과 검증
        assertEquals(1, result.getTotalElements());
        OrderHistoryResponseDto dto = result.getData().getFirst();
        assertNull(dto.getTrackingNumber(), "Tracking number should be null");
        assertEquals("name", dto.getReceiverName(), "Receiver name should be default");
        assertEquals("Test User", dto.getOrderUserName(), "User name should match the mock user");
    }

    @Test
    void getOrderHistoryWithValidDelivery() {
        // Mock Delivery 추가
        Delivery mockDelivery = Delivery.builder()
                .deliveryReceiver("Test Receiver")
                .trackingNumber(123456)
                .build();

        mockOrder.setDelivery(mockDelivery);

        // Mock 리포지토리 동작 정의
        when(orderRepository.findByUserUserIdOrderByOrderDateDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockOrder)));

        // 서비스 호출
        PageResponse<OrderHistoryResponseDto> result = orderHistoryService.getOrderHistory(1L, PageRequest.of(0, 10));

        // 결과 검증
        assertEquals(1, result.getTotalElements());
        OrderHistoryResponseDto dto = result.getData().getFirst();
        assertEquals("123456", dto.getTrackingNumber(), "Tracking number should match mock delivery");
        assertEquals("Test Receiver", dto.getReceiverName(), "Receiver name should match mock delivery");
    }
}
