package com.simsimbookstore.apiserver.orders.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFacadeServiceImplTest {

    @Mock
    private MemberOrderService memberOrderService;

    @Mock
    private OrderBookService orderBookService;

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PointHistoryService pointHistoryService;


    @InjectMocks
    private OrderFacadeImpl orderFacadeService;

    User mockUser;
    Grade testGrade;

    @BeforeEach
    void setUp() {
        testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        mockUser = User.builder()
                .userId(1L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(testGrade)
                .userRoleList(new HashSet<>())
                .build();

        UserRole userRole = UserRole.builder()
                .userRoleId(1L)
                .user(mockUser)
                .role(new Role(1L, RoleName.USER))
                .build();

        mockUser.addUserRole(userRole);


    }

    @Test
    void testCreatePrepareOrder_Success() {

        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto();
        deliveryRequestDto.setDeliveryState(Delivery.DeliveryState.READY);

        Delivery mockDelivery = Delivery.builder()
                .deliveryId(999L)
                .deliveryState(Delivery.DeliveryState.READY)
                .build();

        MemberOrderRequestDto orderReqDto = new MemberOrderRequestDto();
        orderReqDto.setUserId(1001L);
        orderReqDto.setTotalPrice(BigDecimal.valueOf(100000));

        OrderResponseDto mockOrderResponse = OrderResponseDto.builder()
                .orderId(1234L)
                .orderNumber("20241226-000001")
                .build();
        String method = "CARD";
        List<OrderBookRequestDto> orderBookReqList = List.of(
                OrderBookRequestDto.builder()
                        .bookId(501L)
                        .quantity(2)
                        .build(),
                OrderBookRequestDto.builder()
                        .bookId(502L)
                        .quantity(1)
                        .build()
        );

        OrderFacadeRequestDto facadeRequestDto = new OrderFacadeRequestDto(
                deliveryRequestDto,
                orderReqDto,
                orderBookReqList,
                method
        );

        String mockOrderName = "Test Order Name";
        Order mockOrder = Order.builder()
                .orderId(mockOrderResponse.getOrderId())
                .orderName(null) // 초기 상태
                .build();

        when(deliveryService.createDelivery(deliveryRequestDto))
                .thenReturn(mockDelivery);
        when(memberOrderService.createOrder(orderReqDto))
                .thenReturn(mockOrderResponse);
        when(orderBookService.createOrderBook(any(OrderBookRequestDto.class)))
                .thenReturn(mock(OrderBookResponseDto.class));
        when(orderBookService.getOrderName(orderBookReqList))
                .thenReturn(mockOrderName);
        when(orderRepository.findById(mockOrderResponse.getOrderId()))
                .thenReturn(Optional.of(mockOrder));

        OrderFacadeResponseDto result = orderFacadeService.createPrepareOrder(facadeRequestDto);

        verify(deliveryService, times(1)).createDelivery(deliveryRequestDto);
        verify(memberOrderService, times(1)).createOrder(orderReqDto);
        verify(orderBookService, times(orderBookReqList.size())).createOrderBook(any(OrderBookRequestDto.class));
        verify(orderBookService, times(1)).getOrderName(orderBookReqList);
        verify(orderRepository, times(1)).findById(mockOrderResponse.getOrderId());

        assertNotNull(result);
        assertEquals(mockOrderResponse.getOrderNumber(), result.getOrderNumber());
        assertEquals(mockOrderResponse.getTotalPrice(), result.getTotalPrice());

        assertEquals(mockOrderName, mockOrder.getOrderName());
    }


    @Test
    void testOrderRefund_Success() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = Order.builder()
                .orderId(orderId)
                .orderState(Order.OrderState.DELIVERY_READY)
                .user(mockUser)
                .delivery(Delivery.builder()
                        .deliveryId(101L)
                        .deliveryState(Delivery.DeliveryState.READY)
                        .build())
                .build();

        List<OrderBook> mockOrderBooks = List.of(
                OrderBook.builder()
                        .orderBookId(201L)
                        .orderBookState(OrderBook.OrderBookState.DELIVERY_READY)
                        .build(),
                OrderBook.builder()
                        .orderBookId(202L)
                        .orderBookState(OrderBook.OrderBookState.DELIVERY_READY)
                        .build()
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderBookService.getOrderBooks(orderId)).thenReturn(mockOrderBooks);

        // Act
        Order result = orderFacadeService.orderRefund(orderId);

        // Assert
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderBookService, times(1)).getOrderBooks(orderId);
        verify(pointHistoryService, times(1)).refundPoint(orderId);

        assertEquals(Order.OrderState.PAYMENT_CANCELED, result.getOrderState());
        assertEquals(Delivery.DeliveryState.CANCEL, result.getDelivery().getDeliveryState());
        assertTrue(mockOrderBooks.stream().allMatch(ob -> ob.getOrderBookState() == OrderBook.OrderBookState.CANCELED));
    }

    @Test
    void testOrderRefund_GuestOrder_ThrowsException() {
        // Arrange
        Long orderId = 2L;
        Order mockOrder = mock(Order.class); // Mock 객체로 생성

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(mockOrder.isGuestOrder()).thenReturn(true); // Stub 처리

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderFacadeService.orderRefund(orderId);
        });

        // Verify
        assertEquals("Guest order cannot be refunded to point", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderBookService, never()).getOrderBooks(orderId);
        verify(pointHistoryService, never()).refundPoint(orderId);
    }
}


