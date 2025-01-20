package com.simsimbookstore.apiserver.orders.order.service.impl;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderAdminResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderOrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderStateUpdateDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.order.service.OrderNumberService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookAdminResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberOrderServiceImpl implements MemberOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderNumberService orderNumberService;
    private final DeliveryRepository deliveryRepository;
    private final OrderTotalService orderTotalService;
    private final OrderBookService orderBookService;


    @Override
    public OrderResponseDto createOrder(MemberOrderRequestDto requestDto) {

        String orderNumber = orderNumberService.generateOrderNo();

        //유저 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found. ID=" + requestDto.getUserId()));

        BigDecimal deliveryPrice = orderTotalService.calculateDeliveryPrice(requestDto.getTotalPrice());

        //Order 엔티티 생성
        Order order = Order.builder()
                .user(user)
                .delivery(deliveryRepository.findById(requestDto.getDeliveryId()).orElseThrow(()-> new NotFoundException("delivery not found. ID=" + requestDto.getDeliveryId())))
                .orderNumber(orderNumber)
                .orderDate(LocalDateTime.now())
                .originalPrice(requestDto.getOriginalPrice())
                .pointUse(requestDto.getPointUse())
                .totalPrice(requestDto.getTotalPrice())
                .deliveryDate(requestDto.getDeliveryDate())
                .orderEmail(requestDto.getOrderEmail())
                .deliveryPrice(deliveryPrice)
                .pointEarn(0)
                .phoneNumber(requestDto.getPhoneNumber())
                .senderName(requestDto.getSenderName())
                .orderState(Order.OrderState.PENDING)  // 초기 상태
                .build();
        Order savedOrder = orderRepository.save(order);

        return OrderResponseDto.builder()
                .orderId(savedOrder.getOrderId())
                .orderNumber(savedOrder.getOrderNumber())
                .totalPrice(savedOrder.getTotalPrice())
                .build();
    }

    @Override
    public OrderResponseDto getOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("order not found"));
        return OrderResponseDto.fromEntity(order);
    }

    @Override
    public OrderOrderBookResponseDto getOrderOrderBook(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderAdminResponseDto orderResponseDto = OrderAdminResponseDto.builder()
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .orderState(order.getOrderState())
                .totalPrice(order.getTotalPrice()).build();

        List<OrderBook> orderBooks = orderBookService.getOrderBooks(orderId);

        List<OrderBookAdminResponseDto> orderBookAdminResponseDtos = orderBooks.stream()
                .map(ob -> OrderBookAdminResponseDto.builder()
                        .orderBookId(ob.getOrderBookId())
                        .bookTitle(ob.getBook().getTitle())
                        .quantity(ob.getQuantity())
                        .salePrice(ob.getSalePrice())
                        .discountPrice(ob.getDiscountPrice())
                        .orderBookState(ob.getOrderBookState())
                        .build()).toList();

        return OrderOrderBookResponseDto.builder()
                .orderResponseDto(orderResponseDto)
                .orderBookAdminResponseDtos(orderBookAdminResponseDtos)
                .build();
    }

    @Override
    public PageResponse<OrderResponseDto> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);

        Page<OrderResponseDto> dtoPage = orderPage.map(OrderResponseDto::fromEntity);

        return new PageResponse<OrderResponseDto>().getPageResponse(
                pageable.getPageNumber() + 1,
                10,
                dtoPage
        );
    }

    @Override
    public OrderAdminResponseDto updateOrder(OrderStateUpdateDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new NotFoundException("order not found. ID=" + dto.getOrderId()));
        order.setOrderState(dto.getOrderState());
        orderRepository.save(order);
        return OrderAdminResponseDto.fromEntity(order);
    }



}
