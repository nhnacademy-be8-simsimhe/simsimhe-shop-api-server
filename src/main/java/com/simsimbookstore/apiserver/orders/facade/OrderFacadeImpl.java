package com.simsimbookstore.apiserver.orders.facade;

import com.simsimbookstore.apiserver.books.book.service.BookManagementService;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.GuestOrderService;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
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
    private final BookManagementService bookManagementService;
    private final CouponService couponService;
    private final GuestOrderService guestOrderService;

    @Override
    @Transactional
    public OrderFacadeResponseDto createPrepareOrder(OrderFacadeRequestDto facadeRequestDto) {

        Long guestId;
        MemberOrderRequestDto orderReq;
        // 배송 요청 데이터 로깅
        DeliveryRequestDto deliveryReq = facadeRequestDto.getDeliveryRequestDto();
        log.info("Received DeliveryRequestDto: {}", deliveryReq);

        // 1. 배송 생성
        Delivery delivery = deliveryService.createDelivery(deliveryReq);
        log.info("Created Delivery: {}", delivery);

        //비회원 일때
        if (facadeRequestDto.memberOrderRequestDto.getUserId() == null) {
            guestId = guestOrderService.prepareUser(facadeRequestDto);
            orderReq = facadeRequestDto.getMemberOrderRequestDto();
            orderReq.setUserId(guestId);
            orderReq.setDeliveryId(delivery.getDeliveryId());
        } else {
            orderReq = facadeRequestDto.getMemberOrderRequestDto();
            orderReq.setDeliveryId(delivery.getDeliveryId());
        }

        // 주문 요청 데이터 로깅
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
                .userName(facadeRequestDto.getMemberOrderRequestDto().getSenderName())
                .build();

        log.info("OrderFacadeResponseDto created: {}", response);
        return response;
    }

    @Transactional
    public void completeOrder(String orderNumber) {
        // 1. 주문 번호로 Order 조회
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // 2. Order에 속한 OrderBook 리스트 조회
        List<OrderBook> orderBookList = orderBookService.getOrderBooks(order.getOrderId());
        for (OrderBook orderBook : orderBookList) {
            // 3. 재고 수정
            bookManagementService.modifyQuantity(orderBook.getOrderBookId(), -orderBook.getQuantity());
            orderBook.setOrderBookState(OrderBook.OrderBookState.DELIVERY_READY);
            // 4. 쿠폰 사용 처리
            CouponDiscount couponDiscount = orderBook.getCouponDiscount(); // 연관된 CouponDiscount 가져오기
            if (couponDiscount != null) { // CouponDiscount가 있는 경우에만 처리
                Coupon coupon = couponDiscount.getCoupon(); // CouponDiscount에서 Coupon 가져오기
                if (coupon != null) {
                    couponService.useCoupon(order.getUser().getUserId(), coupon.getCouponId());
                }
            }
        }

        // 5. 포인트 적립 처리 회원 주문일 때만
        if (!order.isGuestOrder()) {
            pointHistoryService.orderPoint(OrderPointRequestDto.builder()
                    .orderId(order.getOrderId())
                    .userId(order.getUser().getUserId()) // 사용자 ID로 포인트 적립
                    .build());
        }

        //6. 주문상태 수정
        order.setOrderState(Order.OrderState.DELIVERY_READY);
        order.getDelivery().setDeliveryState(Delivery.DeliveryState.READY);

    }

    @Transactional
    @Override
    public Order orderRefund(Long orderId) {

        // 1. 주문 엔티티 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.isGuestOrder()) {
            throw new IllegalArgumentException("Guest order cannot be refunded to point");
        }

        // 2. 해당 주문에 속한 OrderBook들 조회
        List<OrderBook> orderBooks = orderBookService.getOrderBooks(orderId);

        // 3. 환불 가능 여부 모두 체크
        if (!order.validateRefundable()) {
            throw new IllegalStateException("주문이 환불 불가능한 상태입니다.");
        }

        if (!order.getDelivery().validateRefundable()) {
            throw new IllegalStateException("배송이 환불 불가능한 상태입니다.");
        }

        boolean allOrderBooksRefundable = orderBooks.stream()
                .allMatch(OrderBook::validateRefundable);
        if (!allOrderBooksRefundable) {
            throw new IllegalStateException("일부 상품이 환불 불가능한 상태입니다.");
        }

        // 4. 환불/취소 처리
        order.refund();
        order.getDelivery().cancel();
        orderBooks.forEach(OrderBook::cancel);

        // 5. 포인트로 환불
        pointHistoryService.refundPoint(orderId);

        return order;
    }

}

