package com.simsimbookstore.apiserver.orders.returns.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.returns.entity.Returns;
import com.simsimbookstore.apiserver.orders.returns.repository.ReturnsRepository;
import com.simsimbookstore.apiserver.orders.returns.service.AdminReturnsService;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import com.simsimbookstore.apiserver.payment.exception.PaymentNotExistException;
import com.simsimbookstore.apiserver.payment.repository.PaymentStatusRepository;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReturnsServiceImpl implements AdminReturnsService {
    private final ReturnsRepository returnsRepository;
    private final PointHistoryService pointHistoryService;
    private final PaymentStatusRepository paymentStatusRepository;
    private final OrderBookRepository orderBookRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ReturnsResponseDto> findReturnList(Pageable pageable) {
        Page<Returns> returns = returnsRepository.findAll(pageable);

        return returns.map(returns1 -> {
            OrderBook orderBook = returns1.getOrderBook();
            return ReturnsResponseDto.builder()
                    .bookId(orderBook.getOrderBookId())
                    .bookTitle(orderBook.getBook().getTitle())
                    .damaged(returns1.getDamaged())
                    .quantity(returns1.getQuantity())
                    .refund(returns1.getRefund())
                    .returnDate(returns1.getReturnDate())
                    .returnReason(returns1.getReturnReason())
                    .returnState(String.valueOf(returns1.getReturnState()))
                    .returnId(returns1.getReturnId())
                    .salePrice(orderBook.getSalePrice())
                    .orderBookId(orderBook.getOrderBookId())
                    .build();
        });
    }

    @Override
    @Transactional
    public void confirmReturn(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findByOrderBookId(orderBookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주문입니다."));
        Long orderId = orderBook.getOrder().getOrderId();
        pointHistoryService.refundPoint(orderId);

        orderBook.updateOrderBookState(OrderBook.OrderBookState.ORDER_CANCELED);

        Payment payment = new Payment();
        PaymentStatus paymentStatus = paymentStatusRepository.findByPaymentStatusName("CANCEL")
                .orElseThrow(() -> new PaymentNotExistException("CANCEL 상태가 존재하지 않습니다."));

        payment.setPaymentStatus(paymentStatus);

        orderBook.getOrder().setOrderState(Order.OrderState.RETURNED);

        Returns returns = returnsRepository.findByOrderBook(orderBook)
                .orElseThrow(() -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다."));

        returns.updateReturn(true, returns.getQuantity(), returns.getDamaged(), Returns.ReturnState.RETURN_APPROVED, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void rejectReturn(Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findByOrderBookId(orderBookId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주문입니다."));
        Long orderId = orderBook.getOrder().getOrderId();

        orderBook.updateOrderBookState(OrderBook.OrderBookState.ORDER_CANCELED);

        Returns returns = returnsRepository.findByOrderBook(orderBook)
                .orElseThrow(() -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다."));

        returns.updateReturn(true, returns.getQuantity(), returns.getDamaged(), Returns.ReturnState.RETURN_REJECTED, LocalDateTime.now());
    }
}
