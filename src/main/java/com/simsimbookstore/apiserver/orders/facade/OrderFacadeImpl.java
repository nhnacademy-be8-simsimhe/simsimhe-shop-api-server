package com.simsimbookstore.apiserver.orders.facade;


import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

    private final MemberOrderService memberOrderService;
    private final OrderBookService orderBookService;
    private final DeliveryService deliveryService;
    private final PointHistoryService pointHistoryService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderFacadeResponseDto createPrepareOrder(OrderFacadeRequestDto facadeRequestDto) {
        // 배송 요청 데이터 로깅
        DeliveryRequestDto deliveryReq = facadeRequestDto.getDeliveryRequestDto();
        log.info("Received DeliveryRequestDto: {}", deliveryReq);

        // 1. 배송 생성
        Delivery delivery = deliveryService.createDelivery(deliveryReq);
        log.info("Created Delivery: {}", delivery);

        // 주문 요청 데이터 로깅
        MemberOrderRequestDto orderReq = facadeRequestDto.getMemberOrderRequestDto();
        orderReq.setDeliveryId(delivery.getDeliveryId());
        log.info("Received MemberOrderRequestDto: {}", orderReq);

        // 2. 주문 생성
        OrderResponseDto orderResponseDto = memberOrderService.createOrder(orderReq);
        log.info("Created Order: {}", orderResponseDto);

        // 3. OrderBookRequestDto 처리
        List<OrderBookRequestDto> orderBookReqs = facadeRequestDto.getOrderBookRequestDtos();
        log.info("OrderBookRequestDtos received: {}", orderBookReqs);

        if (orderBookReqs == null || orderBookReqs.isEmpty()) {
            throw new IllegalArgumentException("OrderBookRequestDtos must not be null or empty.");
        }

        for (OrderBookRequestDto obReq : orderBookReqs) {
            // 주문 ID 설정
            obReq.setOrderId(orderResponseDto.getOrderId());
            log.info("Processing OrderBookRequestDto: {}", obReq);

            // 개별 OrderBook 생성
            try {
                orderBookService.createOrderBook(obReq);
                log.info("Created OrderBook for Order ID: {}", obReq.getOrderId());
            } catch (Exception e) {
                log.error("Failed to create OrderBook for OrderBookRequestDto: {}", obReq, e);
                throw e; // 재발생하여 트랜잭션 롤백
            }
        }

        pointHistoryService.orderPoint(OrderPointRequestDto.builder()
                .orderId(orderResponseDto.getOrderId())
                .userId(facadeRequestDto.getMemberOrderRequestDto().getUserId())
                .build());

        // 4. 응답 생성
        String orderName = orderBookService.getOrderName(orderBookReqs);
        Order byId = orderRepository.findById(orderResponseDto.getOrderId()).orElseThrow();
        byId.setOrderName(orderName);

        OrderFacadeResponseDto response = OrderFacadeResponseDto.builder()
                .orderNumber(orderResponseDto.getOrderNumber())
                .totalPrice(orderResponseDto.getTotalPrice())
                .orderName(orderName)
                .email(orderReq.getOrderEmail())
                .phoneNumber(orderReq.getPhoneNumber())
                .method(facadeRequestDto.getMethod())
                .userName("이름")
                .build();

        log.info("OrderFacadeResponseDto created: {}", response);
        return response;
    }

}

