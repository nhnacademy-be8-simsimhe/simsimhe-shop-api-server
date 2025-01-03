package com.simsimbookstore.apiserver.orders.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
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
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
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

    @InjectMocks
    private OrderFacadeImpl orderFacadeService;

    @Test
    void testCreateOrderFacade_Success() {

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

        List<OrderBookRequestDto> orderBookReqList = new ArrayList<>();
        orderBookReqList.add(OrderBookRequestDto.builder()
                .bookId(501L)
                .quantity(2)
                .build());

        OrderFacadeRequestDto facadeRequestDto = new OrderFacadeRequestDto(
                deliveryRequestDto,
                orderReqDto,
                orderBookReqList
        );

        when(deliveryService.createDelivery(deliveryRequestDto))
                .thenReturn(mockDelivery);

        when(memberOrderService.createOrder(orderReqDto))
                .thenReturn(mockOrderResponse);

        when(orderBookService.createOrderBooks(anyList()))
                .thenReturn(Collections.emptyList());

        OrderFacadeResponseDto result = orderFacadeService.createPrepareOrder(facadeRequestDto);

        verify(deliveryService, times(1)).createDelivery(deliveryRequestDto);
        verify(memberOrderService, times(1)).createOrder(orderReqDto);
        verify(orderBookService, times(1)).createOrderBooks(anyList());

        // 2) result에 기대하는 값이 들어있는지
        assertNotNull(result);
        assertEquals(mockOrderResponse.getOrderNumber(), result.getOrderNumber());
        assertEquals(mockOrderResponse.getTotalPrice(), result.getTotalPrice());

    }
}
