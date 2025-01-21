package com.simsimbookstore.apiserver.orders.order.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderAdminResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderOrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderStateUpdateDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.OrderNumberService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Mock
    private OrderBookService orderBookService;

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
    @DisplayName("getOrder - 주문 조회 성공 테스트")
    void testGetOrder_Success() {
        Order order = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-1")
                .totalPrice(BigDecimal.valueOf(5000))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponseDto dto = memberOrderService.getOrder(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getOrderId());
        assertEquals("ORD-1", dto.getOrderNumber());
        assertEquals(BigDecimal.valueOf(5000), dto.getTotalPrice());

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getOrder - 주문 조회 실패 NotFoundException")
    void testGetOrder_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> memberOrderService.getOrder(999L));
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("getOrderOrderBook - 성공 케이스")
    void testGetOrderOrderBook_Success() {
        Order order = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-1")
                .orderState(Order.OrderState.PENDING)
                .totalPrice(BigDecimal.valueOf(10000))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // 모킹: orderBookService.getOrderBooks(1L) 호출 시 빈 리스트 반환
        when(orderBookService.getOrderBooks(1L)).thenReturn(List.of());

        OrderOrderBookResponseDto response = memberOrderService.getOrderOrderBook(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderResponseDto().getOrderId());
        assertTrue(response.getOrderBookAdminResponseDtos().isEmpty());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderBookService, times(1)).getOrderBooks(1L);
    }

    @Test
    @DisplayName("getAllOrders - 페이징 조회 성공 테스트")
    void testGetAllOrders_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-1")
                .totalPrice(BigDecimal.valueOf(10000))
                .build();

        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        PageResponse<OrderResponseDto> response = memberOrderService.getAllOrders(pageable);

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals(1L, response.getData().get(0).getOrderId());

        verify(orderRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("updateOrder - 주문 상태 업데이트 성공 테스트")
    void testUpdateOrder_Success() {
        OrderStateUpdateDto dto = OrderStateUpdateDto.builder()
                .orderId(1L)
                .orderState(Order.OrderState.COMPLETED)
                .build();

        Order order = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-1")
                .totalPrice(BigDecimal.valueOf(10000))
                .orderState(Order.OrderState.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderAdminResponseDto response = memberOrderService.updateOrder(dto);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(Order.OrderState.COMPLETED, response.getOrderState());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("updateOrder - 주문 상태 업데이트 실패 NotFoundException")
    void testUpdateOrder_NotFound() {
        OrderStateUpdateDto dto = OrderStateUpdateDto.builder()
                .orderId(999L)
                .orderState(Order.OrderState.COMPLETED)
                .build();

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memberOrderService.updateOrder(dto));

        assertTrue(exception.getMessage().contains("order not found. ID=999"));
        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
