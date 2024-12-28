package com.simsimbookstore.apiserver.orders.facade;


import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

    private final MemberOrderService memberOrderService;
    private final OrderBookService orderBookService;
    private final DeliveryService deliveryService;

    @Override
    @Transactional
    public OrderFacadeResponseDto createPrepareOrder(OrderFacadeRequestDto facadeRequestDto) {

        //임시저장
        //책 재고랑, 포인트 나중에

        // 1. 배송 생성
        DeliveryRequestDto deliveryReq = facadeRequestDto.getDeliveryRequestDto();
        Delivery delivery = deliveryService.createDelivery(deliveryReq);

        // 2. 주문 생성
        MemberOrderRequestDto orderReq = facadeRequestDto.getMemberOrderRequestDto();
        orderReq.setDeliveryId(delivery.getDeliveryId());

        OrderResponseDto orderResponseDto = memberOrderService.createOrder(orderReq);

        // 3. orderBook 생성 + 포장 + 쿠폰할인
        List<OrderBookRequestDto> orderBookReqs = facadeRequestDto.getOrderBookRequestDtos();
        if (orderBookReqs != null && !orderBookReqs.isEmpty()) {
            for (OrderBookRequestDto obReq : orderBookReqs) {
                obReq.setOrderId(orderResponseDto.getOrderId());
            }
            orderBookService.createOrderBooks(orderBookReqs);
        }

        return OrderFacadeResponseDto.builder()
                .orderNumber(orderResponseDto.getOrderNumber())
                .totalPrice(orderResponseDto.getTotalPrice())
                .orderName("name")
                .email(orderReq.getOrderEmail())
                .phoneNumber(orderReq.getPhoneNumber())
                .build();
    }
}

