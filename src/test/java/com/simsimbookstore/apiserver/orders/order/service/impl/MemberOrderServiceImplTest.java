package com.simsimbookstore.apiserver.orders.order.service.impl;

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
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    @Test
    void testUpdateOrdersToCompleted() {
        // given
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        // 오래된 주문(상태가 아직 COMPLETED가 아님)
        Delivery oldDelivery = Delivery.builder()
                .deliveryState(Delivery.DeliveryState.IN_PROGRESS)
                .build();

        OrderBook oldOrderBook = OrderBook.builder()
                .orderBookState(OrderBook.OrderBookState.IN_DELIVERY)
                .build();

        Order oldOrder = Order.builder()
                .orderState(Order.OrderState.IN_DELIVERY)
                .deliveryDate(LocalDate.now().minusDays(31)) // 31일 전
                .delivery(oldDelivery)
                .build();
        // 테스트에서는 가볍게만 세팅
        List<OrderBook> oldOrderBooks = List.of(oldOrderBook);
        oldOrder.setOrderBooks(oldOrderBooks);

        // 최근 주문(30일 이내 → 업데이트 X)
        Delivery recentDelivery = Delivery.builder()
                .deliveryState(Delivery.DeliveryState.IN_PROGRESS)
                .build();

        OrderBook recentOrderBook = OrderBook.builder()
                .orderBookState(OrderBook.OrderBookState.IN_DELIVERY)
                .build();

        Order recentOrder = Order.builder()
                .orderState(Order.OrderState.IN_DELIVERY)
                .deliveryDate(LocalDate.now().minusDays(10)) // 10일 전
                .delivery(recentDelivery)
                .build();
        recentOrder.setOrderBooks(List.of(recentOrderBook));

        // findAllOrdersNeedToComplete(...) 호출 시, 오래된 주문만 반환되도록 Mock 설정
        when(orderRepository.findAllOrdersNeedToComplete(thirtyDaysAgo))
                .thenReturn(List.of(oldOrder));

        // when
        int updatedCount = memberOrderService.updateOrdersToCompleted();

        // then
        // 오래된 주문 1건만 업데이트되었다고 가정
        assertEquals(1, updatedCount);

        // 오래된 주문, 딜리버리, 오더북 모두 COMPLETED 상태로 바뀌었는지 확인
        assertEquals(Order.OrderState.COMPLETED, oldOrder.getOrderState());
        assertEquals(Delivery.DeliveryState.COMPLETED, oldOrder.getDelivery().getDeliveryState());
        oldOrder.getOrderBooks().forEach(
                ob -> assertEquals(OrderBook.OrderBookState.COMPLETED, ob.getOrderBookState())
        );

        // 최근 주문은 상태 변화 X (여기선 recentOrder가 Mock 반환값에 포함되지 않음)
        assertEquals(Order.OrderState.IN_DELIVERY, recentOrder.getOrderState());
        assertEquals(Delivery.DeliveryState.IN_PROGRESS, recentOrder.getDelivery().getDeliveryState());
        recentOrder.getOrderBooks().forEach(
                ob -> assertEquals(OrderBook.OrderBookState.IN_DELIVERY, ob.getOrderBookState())
        );

        // verify
        verify(orderRepository, times(1)).findAllOrdersNeedToComplete(thirtyDaysAgo);
        // 상태가 변경된 Order를 저장하는 로직이 별도 있다면 save 호출도 검증 (예: orderRepository.saveAll(orders))
        // 여기서는 예시로 생략 (또는 없는 로직일 수도 있음)
    }
}
