package com.simsimbookstore.apiserver.orders.orderbook.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderBookDtoTest {
    private Book dummyBook;
    private Order dummyOrder;
    @BeforeEach
    void setUp() {
        // 간단한 더미 객체 생성 (필요한 최소 필드만)
        dummyBook = Book.builder()
                .bookId(1L)
                .title("Dummy Book")
                .price(new BigDecimal("10000"))
                .build();

        dummyOrder = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-0001")
                .totalPrice(new BigDecimal("10000"))
                .build();
    }
    @Test
    void testOrderBookResponseDtoNoArgsConstructor() {
        // NoArgs 생성자를 사용하여 객체 생성
        OrderBookResponseDto dto = new OrderBookResponseDto();

        // 모든 필드가 null 또는 초기 기본값인지 확인
        assertNull(dto.getOrderBookId(), "orderBookId는 null이어야 합니다.");
        assertNull(dto.getBookTitle(), "bookTitle은 null이어야 합니다.");
        assertNull(dto.getQuantity(), "quantity는 null이어야 합니다.");
        assertNull(dto.getSalePrice(), "salePrice는 null이어야 합니다.");
        assertNull(dto.getDiscountPrice(), "discountPrice는 null이어야 합니다.");
        assertNull(dto.getOrderBookState(), "orderBookState는 null이어야 합니다.");
        assertNull(dto.getPackages(), "packages는 null이어야 합니다.");
        assertNull(dto.getCouponDiscount(), "couponDiscount는 null이어야 합니다.");
    }

    @Test
    void testOrderBookAdminResponseDtoNoArgsConstructor() {
        // NoArgs 생성자를 사용하여 객체 생성
        OrderBookAdminResponseDto dto = new OrderBookAdminResponseDto();

        // 모든 필드가 null 또는 초기 기본값인지 확인
        assertNull(dto.getOrderBookId(), "orderBookId는 null이어야 합니다.");
        assertNull(dto.getBookTitle(), "bookTitle은 null이어야 합니다.");
        assertNull(dto.getQuantity(), "quantity는 null이어야 합니다.");
        assertNull(dto.getSalePrice(), "salePrice는 null이어야 합니다.");
        assertNull(dto.getDiscountPrice(), "discountPrice는 null이어야 합니다.");
        assertNull(dto.getOrderBookState(), "orderBookState는 null이어야 합니다.");
    }

    @Test
    void testValidateRefundable_DeliveryReady() {
        OrderBook orderBook = OrderBook.builder()
                .book(dummyBook)
                .order(dummyOrder)
                .quantity(1)
                .salePrice(BigDecimal.TEN)
                .discountPrice(BigDecimal.ONE)
                .orderBookState(OrderBook.OrderBookState.DELIVERY_READY)
                .build();

        assertTrue(orderBook.validateRefundable(), "OrderBookState가 DELIVERY_READY일 때 refundable이어야 합니다.");
    }

    @Test
    void testValidateRefundable_Completed() {
        OrderBook orderBook = OrderBook.builder()
                .book(dummyBook)
                .order(dummyOrder)
                .quantity(1)
                .salePrice(BigDecimal.TEN)
                .discountPrice(BigDecimal.ONE)
                .orderBookState(OrderBook.OrderBookState.COMPLETED)
                .build();

        assertTrue(orderBook.validateRefundable(), "OrderBookState가 COMPLETED일 때 refundable이어야 합니다.");
    }

    @Test
    void testValidateRefundable_NotRefundable() {
        // refundable 조건에 해당하지 않는 상태 테스트
        OrderBook orderBook = OrderBook.builder()
                .book(dummyBook)
                .order(dummyOrder)
                .quantity(1)
                .salePrice(BigDecimal.TEN)
                .discountPrice(BigDecimal.ONE)
                .orderBookState(OrderBook.OrderBookState.PENDING) // refundable 조건에 해당하지 않음
                .build();

        assertFalse(orderBook.validateRefundable(), "PENDING 상태는 refundable하지 않아야 합니다.");
    }

    @Test
    void testIsPending_True() {
        OrderBook orderBook = OrderBook.builder()
                .book(dummyBook)
                .order(dummyOrder)
                .quantity(1)
                .salePrice(BigDecimal.TEN)
                .discountPrice(BigDecimal.ONE)
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .build();

        assertTrue(orderBook.isPending(), "OrderBookState가 PENDING일 때 isPending()은 true를 반환해야 합니다.");
    }

    @Test
    void testIsPending_False() {
        // PENDING이 아닌 상태에서 isPending()이 false 반환하는지 테스트
        OrderBook orderBook = OrderBook.builder()
                .book(dummyBook)
                .order(dummyOrder)
                .quantity(1)
                .salePrice(BigDecimal.TEN)
                .discountPrice(BigDecimal.ONE)
                .orderBookState(OrderBook.OrderBookState.COMPLETED)
                .build();

        assertFalse(orderBook.isPending(), "OrderBookState가 COMPLETED일 때 isPending()은 false를 반환해야 합니다.");
    }
}