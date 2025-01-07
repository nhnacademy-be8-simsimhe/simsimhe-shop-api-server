package com.simsimbookstore.apiserver.orders.order.service.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.OrderNumberService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberOrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderNumberService orderNumberService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private OrderTotalService orderTotalService;

    @InjectMocks
    private MemberOrderServiceImpl memberOrderService;

    @Test
    void testCreateOrder_Success() {
        User mockUser = User.builder()
                .userId(1001L)
                .userName("Test User")
                .build();

        Delivery mockDelivery = Delivery.builder()
                .deliveryId(999L)
                .build();

        String generatedOrderNumber = "20250102-000001";

        Order mockOrder = Order.builder()
                .orderId(1234L)
                .orderNumber(generatedOrderNumber)
                .totalPrice(BigDecimal.valueOf(100000))
                .build();

        MemberOrderRequestDto requestDto = MemberOrderRequestDto.builder()
                .userId(1001L)
                .deliveryId(999L)
                .originalPrice(BigDecimal.valueOf(120000))
                .pointUse(BigDecimal.valueOf(20000))
                .totalPrice(BigDecimal.valueOf(100000))
                .deliveryDate(LocalDate.now())
                .orderEmail("test@example.com")
                .deliveryPrice(BigDecimal.valueOf(5000))
                .phoneNumber("01012345678")
                .build();

        when(userRepository.findById(requestDto.getUserId()))
                .thenReturn(Optional.of(mockUser));

        when(deliveryRepository.findById(requestDto.getDeliveryId()))
                .thenReturn(Optional.of(mockDelivery));

        when(orderNumberService.generateOrderNo())
                .thenReturn(generatedOrderNumber);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(mockOrder);
        when(orderTotalService.calculateDeliveryPrice(any())).thenReturn(BigDecimal.valueOf(3000));
        OrderResponseDto responseDto = memberOrderService.createOrder(requestDto);

        assertNotNull(responseDto);
        assertEquals(mockOrder.getOrderId(), responseDto.getOrderId());
        assertEquals(mockOrder.getOrderNumber(), responseDto.getOrderNumber());
        assertEquals(mockOrder.getTotalPrice(), responseDto.getTotalPrice());

        verify(userRepository, times(1)).findById(requestDto.getUserId());
        verify(deliveryRepository, times(1)).findById(requestDto.getDeliveryId());
        verify(orderNumberService, times(1)).generateOrderNo();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_Failure_UserNotFound() {
        MemberOrderRequestDto requestDto = MemberOrderRequestDto.builder()
                .userId(1001L)
                .deliveryId(999L)
                .build();

        when(userRepository.findById(requestDto.getUserId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> memberOrderService.createOrder(requestDto)
        );

        assertEquals("User not found. ID=1001", exception.getMessage());

        verify(userRepository, times(1)).findById(requestDto.getUserId());
        verify(deliveryRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_Failure_DeliveryNotFound() {
        User mockUser = User.builder()
                .userId(1001L)
                .userName("Test User")
                .build();

        MemberOrderRequestDto requestDto = MemberOrderRequestDto.builder()
                .userId(1001L)
                .deliveryId(999L)
                .build();

        when(userRepository.findById(requestDto.getUserId()))
                .thenReturn(Optional.of(mockUser));

        when(deliveryRepository.findById(requestDto.getDeliveryId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> memberOrderService.createOrder(requestDto)
        );

        assertNotNull(exception);

        verify(userRepository, times(1)).findById(requestDto.getUserId());
        verify(deliveryRepository, times(1)).findById(requestDto.getDeliveryId());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
