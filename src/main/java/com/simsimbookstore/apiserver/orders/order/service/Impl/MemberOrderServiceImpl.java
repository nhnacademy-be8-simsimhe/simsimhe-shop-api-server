package com.simsimbookstore.apiserver.orders.order.service.Impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.order.service.OrderNumberService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
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


    @Override
    public OrderResponseDto createOrder(MemberOrderRequestDto requestDto) {

        String orderNumber = orderNumberService.generateOrderNo();

        //유저 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found. ID=" + requestDto.getUserId()));

        //Order 엔티티 생성
        Order order = Order.builder()
                .user(user)
                .delivery(deliveryRepository.findById(requestDto.getDeliveryId()).orElseThrow())
                .orderNumber(orderNumber)
                .orderDate(LocalDateTime.now())
                .originalPrice(requestDto.getOriginalPrice())
                .pointUse(requestDto.getPointUse())
                .totalPrice(requestDto.getTotalPrice())
                .deliveryDate(requestDto.getDeliveryDate())
                .orderEmail(requestDto.getOrderEmail())
                .pointEarn(requestDto.getPointEarn())
                .deliveryPrice(requestDto.getDeliveryPrice())
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
