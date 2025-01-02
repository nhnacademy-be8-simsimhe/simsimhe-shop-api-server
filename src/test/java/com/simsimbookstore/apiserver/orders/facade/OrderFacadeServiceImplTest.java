package com.simsimbookstore.apiserver.orders.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private OrderFacadeImpl orderFacadeService;

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
                orderBookReqList
        );

        when(deliveryService.createDelivery(deliveryRequestDto))
                .thenReturn(mockDelivery);

        when(memberOrderService.createOrder(orderReqDto))
                .thenReturn(mockOrderResponse);

        when(orderBookService.createOrderBook(any(OrderBookRequestDto.class)))
                .thenReturn(mock(OrderBookResponseDto.class));

        OrderFacadeResponseDto result = orderFacadeService.createPrepareOrder(facadeRequestDto);

        verify(deliveryService, times(1)).createDelivery(deliveryRequestDto);
        verify(memberOrderService, times(1)).createOrder(orderReqDto);
        verify(orderBookService, times(orderBookReqList.size())).createOrderBook(any(OrderBookRequestDto.class));
        verify(pointHistoryService, times(1)).orderPoint(any(OrderPointRequestDto.class));

        assertNotNull(result);
        assertEquals(mockOrderResponse.getOrderNumber(), result.getOrderNumber());
        assertEquals(mockOrderResponse.getTotalPrice(), result.getTotalPrice());
    }
}


