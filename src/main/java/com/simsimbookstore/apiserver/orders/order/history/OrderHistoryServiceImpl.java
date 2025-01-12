package com.simsimbookstore.apiserver.orders.order.history;

import com.simsimbookstore.apiserver.orders.order.dto.OrderHistoryResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderRepository orderRepository;

    @Override
    public Page<OrderHistoryResponseDto> getOrderHistory(Long userId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserUserIdOrderByOrderDateDesc(userId, pageable);

        return orderPage.map(this::convertToDto);
    }

    private OrderHistoryResponseDto convertToDto(Order order) {
        return OrderHistoryResponseDto.builder()
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .orderName(order.getOrderName())
                .orderAmount(order.getTotalPrice())
                .orderState(order.getOrderState())
                .trackingNumber(safeGetTrackingNumber(order))
                .orderUserName(safeGetUserName(order))
                .receiverName(safeGetReceiverName(order))
                .build();
    }

    private String safeGetTrackingNumber(Order order) {
        return order.getDelivery() != null && order.getDelivery().getTrackingNumber() != null
                ? order.getDelivery().getTrackingNumber().toString()
                : null;
    }

    private String safeGetUserName(Order order) {
        return order.getUser() != null ? order.getUser().getUserName() : "Unknown User";
    }

    private String safeGetReceiverName(Order order) {
        return order.getDelivery() != null ? order.getDelivery().getDeliveryReceiver() : "Unknown Receiver";
    }
}


