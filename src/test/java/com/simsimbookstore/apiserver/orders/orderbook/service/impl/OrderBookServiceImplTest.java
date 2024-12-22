package com.simsimbookstore.apiserver.orders.orderbook.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.exception.BookOutOfStockException;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test") // test 프로파일 활성화
public class OrderBookServiceImplTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderBookRepository orderBookRepository;

    private OrderBookServiceImpl orderBookService;

    @BeforeEach
    void setUp() {
        orderBookService = new OrderBookServiceImpl(orderBookRepository, bookRepository, orderRepository);

        Book book1 = Book.builder()
                .title("Test Book 1")
                .description("Test Description 1")
                .bookIndex("Index 1")
                .publisher("Test Publisher 1")
                .isbn("1234567890123")
                .quantity(10) // 초기 재고
                .price(new BigDecimal("10000.00"))
                .saleprice(new BigDecimal("8000.00"))
                .publicationDate(LocalDate.now())
                .pages(300)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();
        bookRepository.save(book1);

        Book book2 = Book.builder()
                .title("Test Book 2")
                .description("Test Description 2")
                .bookIndex("Index 2")
                .publisher("Test Publisher 2")
                .isbn("1234567890456")
                .quantity(5) // 초기 재고
                .price(new BigDecimal("12000.00"))
                .saleprice(new BigDecimal("9000.00"))
                .publicationDate(LocalDate.now())
                .pages(200)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();
        bookRepository.save(book2);

        Order order = Order.builder()
                .user(null)
                .delivery(null)
                .orderDate(LocalDateTime.now())
                .originalPrice(new BigDecimal("8000.00"))
                .pointUse(new BigDecimal("100.00"))
                .totalPrice(new BigDecimal("12900.00"))
                .deliveryDate(LocalDate.now())
                .orderEmail("test@example.com")
                .pointEarn(5)
                .deliveryPrice(new BigDecimal("5000.00"))
                .orderState(Order.OrderState.PENDING)
                .build();
        orderRepository.save(order);
    }

    @Test
    void testCreateOrderBooks_Success() {

        List<OrderBookRequestDto> requestDtos = List.of(
                OrderBookRequestDto.builder()
                        .orderId(orderRepository.findAll().getFirst().getOrderId())
                        .bookId(bookRepository.findAll().get(0).getBookId())
                        .quantity(2)
                        .salePrice(new BigDecimal("8000.00"))
                        .discountPrice(new BigDecimal("2000.00"))
                        .orderBookState("PENDING")
                        .build(),
                OrderBookRequestDto.builder()
                        .orderId(orderRepository.findAll().getFirst().getOrderId())
                        .bookId(bookRepository.findAll().get(1).getBookId())
                        .quantity(3)
                        .salePrice(new BigDecimal("9000.00"))
                        .discountPrice(new BigDecimal("3000.00"))
                        .orderBookState("PENDING")
                        .build()
        );

        List<OrderBook> orderBooks = orderBookService.createOrderBooks(requestDtos);

        assertEquals(2, orderBooks.size());

        OrderBook orderBook1 = orderBooks.getFirst();
        assertEquals(2, orderBook1.getQuantity());
        assertEquals(new BigDecimal("8000.00"), orderBook1.getSalePrice());
        assertEquals(new BigDecimal("2000.00"), orderBook1.getDiscountPrice());

        OrderBook orderBook2 = orderBooks.get(1);
        assertEquals(3, orderBook2.getQuantity());
        assertEquals(new BigDecimal("9000.00"), orderBook2.getSalePrice());
        assertEquals(new BigDecimal("3000.00"), orderBook2.getDiscountPrice());

        Book book1 = bookRepository.findById(bookRepository.findAll().get(0).getBookId()).orElseThrow();
        assertEquals(8, book1.getQuantity());

        Book book2 = bookRepository.findById(bookRepository.findAll().get(1).getBookId()).orElseThrow();
        assertEquals(2, book2.getQuantity());
    }

    @Test
    void testCreateOrderBooks_Failure_OutOfStock() {
        List<OrderBookRequestDto> requestDtos = List.of(
                OrderBookRequestDto.builder()
                        .orderId(orderRepository.findAll().getFirst().getOrderId())
                        .bookId(bookRepository.findAll().getFirst().getBookId())
                        .quantity(15) // 재고 초과
                        .salePrice(new BigDecimal("8000.00"))
                        .discountPrice(new BigDecimal("2000.00"))
                        .orderBookState("PENDING")
                        .build()
        );

        assertThrows(BookOutOfStockException.class, () -> orderBookService.createOrderBooks(requestDtos));

        Book book1 = bookRepository.findById(bookRepository.findAll().getFirst().getBookId()).orElseThrow();
        assertEquals(10, book1.getQuantity());
    }
}


