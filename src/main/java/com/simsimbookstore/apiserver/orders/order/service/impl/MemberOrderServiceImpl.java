package com.simsimbookstore.apiserver.orders.order.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.order.service.OrderNumberService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberOrderServiceImpl implements MemberOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderNumberService orderNumberService;
    private final DeliveryRepository deliveryRepository;
    private final OrderTotalService orderTotalService;


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

}
