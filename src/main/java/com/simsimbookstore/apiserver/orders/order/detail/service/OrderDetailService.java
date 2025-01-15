package com.simsimbookstore.apiserver.orders.order.detail.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.repository.CouponDiscountRepository;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailInfoDto;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailProduct;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailResponseDto;
import com.simsimbookstore.apiserver.orders.order.detail.repository.OrderBookQueryRepository;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderRepository orderRepository;
    private final OrderBookRepository orderBookRepository;
    private final PaymentRepository paymentRepository;
    private final BookRepository bookRepository;
    private final CouponDiscountRepository couponDiscountRepository;
    private final OrderBookQueryRepository orderBookQueryRepository;

    public OrderDetailResponseDto getOrderDetail(Long userId, String orderNumber) {
        // orders table의 orderNumber, orderDate, orderState,
        // sender_name, sender_phone_number, sender_email
        // point_use/earn, totalPrice, delivery_price, original_total_price
        Order order =  orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> new NotFoundException("주문이 존재하지 않습니다."));

        // delivery
        Delivery delivery = order.getDelivery();

        // payment_method
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new NotFoundException("결제가 진행되지 않은 주문입니다."));

        // order, delivery, paymentMethod DTO
        OrderDetailInfoDto orderDetailInfoDto = new OrderDetailInfoDto(order, delivery, payment);

        // order_book (책 주문 리스트)
        List<OrderDetailProduct> orderDetailProducts = new ArrayList<>();

        List<OrderBook> orderBooks = orderBookRepository.findAllByOrder(order.getOrderId());
        Book book;
        for(OrderBook orderBook : orderBooks) {
            // 책 제목
            book = bookRepository.findByBookId(orderBook.getOrderBookId()).orElseThrow(() -> new NotFoundException("주문된 책이 없습니다."));
            String bookTitle = book.getTitle();

            // coupon
            CouponDiscount couponDiscount = couponDiscountRepository.findByOrderBook(orderBook);
            String couponName;
            BigDecimal couponPrice;
            if (couponDiscount != null) {
                couponName = couponDiscount.getCouponName();
                couponPrice = couponDiscount.getDiscountPrice();
            } else {
                couponName = "-";
                couponPrice = BigDecimal.ZERO;
            }

            // wrap
            String packageName = null;
            BigDecimal packagePrice = null;

            List<WrapType> wrapTypes = orderBookQueryRepository.findByPackage(orderBook);
            for (WrapType wrapType : wrapTypes) {
                if (wrapType != null) {
                    packageName = wrapType.getPackageName();
                    packagePrice = wrapType.getPackagePrice();
                } else {
                    packageName = "-";
                    packagePrice = BigDecimal.ZERO;
                }
            }

            OrderDetailProduct orderDetailProduct = new OrderDetailProduct(orderBook, bookTitle, couponName, couponPrice, packageName, packagePrice);
            orderDetailProducts.add(orderDetailProduct);
        }

        return new OrderDetailResponseDto(orderDetailInfoDto, orderDetailProducts);
    }
}
