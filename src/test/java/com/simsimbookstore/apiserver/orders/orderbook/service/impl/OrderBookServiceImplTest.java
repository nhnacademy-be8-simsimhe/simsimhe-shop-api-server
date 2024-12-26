package com.simsimbookstore.apiserver.orders.orderbook.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.exception.BookOutOfStockException;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.book.service.BookManagementService;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook.OrderBookState;
import com.simsimbookstore.apiserver.orders.orderbook.exception.OrderBookNotFoundException;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class OrderBookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderBookRepository orderBookRepository;

    @Mock
    private BookManagementService bookManagementService;

    @InjectMocks
    private OrderBookServiceImpl orderBookService;

    private Book book1;
    private Book book2;
    private Order order;
    private OrderBook orderBook;

    @BeforeEach
    void setUp() {

        book1 = Book.builder()
                .bookId(1L)
                .title("Test Book 1")
                .description("Test Description 1")
                .bookIndex("Index 1")
                .publisher("Test Publisher 1")
                .isbn("1234567890123")
                .quantity(10)
                .price(new BigDecimal("10000.00"))
                .saleprice(new BigDecimal("8000.00"))
                .publicationDate(LocalDate.now())
                .pages(300)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        book2 = Book.builder()
                .bookId(2L)
                .title("Test Book 2")
                .description("Test Description 2")
                .bookIndex("Index 2")
                .publisher("Test Publisher 2")
                .isbn("1234567890456")
                .quantity(5)
                .price(new BigDecimal("12000.00"))
                .saleprice(new BigDecimal("9000.00"))
                .publicationDate(LocalDate.now())
                .pages(200)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        order = Order.builder()
                .orderId(100L)
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

        orderBook = OrderBook.builder()
                .orderBookId(10L)
                .book(book1)
                .order(order)
                .quantity(2)
                .salePrice(new BigDecimal("8000.00"))
                .discountPrice(new BigDecimal("2000.00"))
                .orderBookState(OrderBookState.PENDING)
                .build();
    }

    @Test
    @DisplayName("오더북 성공 테스트 - BookManagementService 사용")
    void testCreateOrderBooks_Success_WithBookManagementService() {
        List<OrderBookRequestDto> requestDtos = List.of(
                OrderBookRequestDto.builder()
                        .orderId(order.getOrderId())   // 100L
                        .bookId(book1.getBookId())     // 1L
                        .quantity(2)                  // 개수 2
                        .salePrice(new BigDecimal("8000.00"))
                        .discountPrice(new BigDecimal("2000.00"))
                        .orderBookState("PENDING")
                        .build(),
                OrderBookRequestDto.builder()
                        .orderId(order.getOrderId())   // 100L
                        .bookId(book2.getBookId())     // 2L
                        .quantity(3)                  // 개수 3
                        .salePrice(new BigDecimal("9000.00"))
                        .discountPrice(new BigDecimal("3000.00"))
                        .orderBookState("PENDING")
                        .build()
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        when(bookManagementService.modifyQuantity(1L, -2)).thenReturn(8); // 10 - 2 = 8
        when(bookManagementService.modifyQuantity(2L, -3)).thenReturn(2); // 5 - 3 = 2

        OrderBook ob1 = OrderBook.builder()
                .orderBookId(101L)
                .book(book1)
                .order(order)
                .quantity(2)
                .salePrice(new BigDecimal("8000.00"))
                .discountPrice(new BigDecimal("2000.00"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .build();

        OrderBook ob2 = OrderBook.builder()
                .orderBookId(102L)
                .book(book2)
                .order(order)
                .quantity(3)
                .salePrice(new BigDecimal("9000.00"))
                .discountPrice(new BigDecimal("3000.00"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .build();

        List<OrderBook> mockedSavedOrderBooks = List.of(ob1, ob2);
        when(orderBookRepository.saveAll(anyList())).thenReturn(mockedSavedOrderBooks);

        List<OrderBookResponseDto> orderBooks = orderBookService.createOrderBooks(requestDtos);

        assertEquals(2, orderBooks.size());
        assertEquals(2, orderBooks.get(0).getQuantity());
        assertEquals(new BigDecimal("8000.00"), orderBooks.get(0).getSalePrice());
        assertEquals(3, orderBooks.get(1).getQuantity());
        assertEquals(new BigDecimal("9000.00"), orderBooks.get(1).getSalePrice());

        verify(bookRepository, times(2)).findById(anyLong());
        verify(orderRepository, times(2)).findById(order.getOrderId());
        verify(bookManagementService, times(1)).modifyQuantity(1L, -2);
        verify(bookManagementService, times(1)).modifyQuantity(2L, -3);
        verify(orderBookRepository, times(1)).saveAll(anyList());
    }



    @Test
    @DisplayName("OrderBook 생성 실패 - 재고 부족")
    void testCreateOrderBooks_Failure_OutOfStock() {

        List<OrderBookRequestDto> requestDtos = List.of(
                OrderBookRequestDto.builder()
                        .orderId(order.getOrderId())   // 100L
                        .bookId(book1.getBookId())     // 1L
                        .quantity(15)                 // 요청 수량이 재고보다 많음
                        .salePrice(new BigDecimal("8000.00"))
                        .discountPrice(new BigDecimal("2000.00"))
                        .orderBookState("PENDING")
                        .build()
        );

        when(bookRepository.findById(book1.getBookId()))
                .thenReturn(Optional.of(book1));

        when(orderRepository.findById(order.getOrderId()))
                .thenReturn(Optional.of(order));

        doThrow(new BookOutOfStockException("도서의 수량은 음수가 될 수 없습니다"))
                .when(bookManagementService).modifyQuantity(1L, -15);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                orderBookService.createOrderBooks(requestDtos)
        );

        assertTrue(exception.getMessage().contains("Not enough stock"));

        assertEquals(10, book1.getQuantity());

        verify(bookRepository, times(1)).findById(book1.getBookId());
        verify(orderRepository, times(1)).findById(order.getOrderId());
        verify(bookManagementService, times(1)).modifyQuantity(1L, -15);
        verify(orderBookRepository, never()).saveAll(anyList());
    }



    @Test
    @DisplayName("id로 오더북 불러오기")
    void testGetOrderBook_Success() {
        Long orderBookId = orderBook.getOrderBookId(); // 10L

        when(orderBookRepository.findById(orderBookId)).thenReturn(Optional.of(orderBook));

        OrderBookResponseDto orderBook = orderBookService.getOrderBook(orderBookId);

        assertNotNull(orderBook);
        assertEquals(orderBookId, orderBook.getOrderBookId());
        verify(orderBookRepository, times(1)).findById(orderBookId);
    }

    @Test
    @DisplayName("오더북 업데이트 성공")
    void testUpdateOrderBook_Success() {
        Long orderBookId = orderBook.getOrderBookId(); // 10L

        when(orderBookRepository.findById(orderBookId)).thenReturn(Optional.of(orderBook));
        when(orderBookRepository.save(any(OrderBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderBookResponseDto updatedOrderBook = orderBookService.updateOrderBook(orderBookId, OrderBookState.COMPLETED);

        assertNotNull(updatedOrderBook);
        assertEquals(OrderBookState.COMPLETED.toString(), updatedOrderBook.getOrderBookState());
        verify(orderBookRepository, times(1)).findById(orderBookId);
        verify(orderBookRepository, times(1)).save(any(OrderBook.class));
    }

    @Test
    @DisplayName("오더북 삭제 성공 테스트")
    void testDeleteOrderBook_Success() {
        Long orderBookId = orderBook.getOrderBookId(); // 10L

        when(orderBookRepository.findById(orderBookId)).thenReturn(Optional.of(orderBook));
        doNothing().when(orderBookRepository).delete(orderBook);

        orderBookService.deleteOrderBook(orderBookId);

        verify(orderBookRepository, times(1)).findById(orderBookId);
        verify(orderBookRepository, times(1)).delete(orderBook);
    }

    @Test
    @DisplayName("OrderBook에 연결된 패키지 조회 성공")
    void testgetPackages_Success() {

        Long orderBookId = orderBook.getOrderBookId(); // 10L

        Packages package1 = Packages.builder().packageType("Wrap 1").build();
        Packages package2 = Packages.builder().packageType("Wrap 2").build();

        orderBook.getPackages().add(package1);
        orderBook.getPackages().add(package2);

        when(orderBookRepository.findById(orderBookId)).thenReturn(Optional.of(orderBook));

        List<PackageResponseDto> packages = orderBookService.getPackages(orderBookId);

        assertNotNull(packages);
        assertEquals(2, packages.size()); // 패키지 리스트 크기 검증
        assertEquals("Wrap 1", packages.get(0).getPackageType());
        assertEquals("Wrap 2", packages.get(1).getPackageType());

        verify(orderBookRepository, times(1)).findById(orderBookId);
    }

    @Test
    @DisplayName("OrderBook이 존재하지 않을 때 예외 발생")
    void testgetPackages_Failure_OrderBookNotFound() {
        Long noExistentOrderBookId = 999L;

        when(orderBookRepository.findById(noExistentOrderBookId)).thenReturn(Optional.empty());

        assertThrows(OrderBookNotFoundException.class,
                () -> orderBookService.getPackages(noExistentOrderBookId)
        );
        verify(orderBookRepository, times(1)).findById(noExistentOrderBookId);
    }

    @Test
    @DisplayName("OrderBook에 연결된 CouponDiscount 조회 성공")
    void testGetCouponDiscountByOrderBookId_Success() {
        // Given
        Long orderBookId = 10L; // 테스트 ID
        OrderBook orderBook = new OrderBook(); // OrderBook 객체 생성

        CouponDiscount couponDiscount = CouponDiscount.builder()
                .couponDiscountId(1L)
                .couponName("Holiday Sale")
                .couponType("Percentage")
                .discountPrice(new BigDecimal("1000.00"))
                .orderBook(orderBook) // 양방향 관계 설정
                .build();

        // 양방향 연관 관계 설정
        orderBook.setCouponDiscount(couponDiscount);

        when(orderBookRepository.findById(orderBookId)).thenReturn(Optional.of(orderBook));

        OrderBook orderBook1 = orderBookRepository.findById(10L).orElseThrow();


        assertNotNull(orderBook1);
        assertEquals("Holiday Sale", orderBook1.getCouponDiscount().getCouponName());
        assertEquals(new BigDecimal("1000.00"), orderBook1.getCouponDiscount().getDiscountPrice());

    }



    @Test
    @DisplayName("OrderBook에 연결된 CouponDiscount 설정 테스트")
    void testSetCouponDiscountForOrderBook() {
        CouponDiscount couponDiscount = CouponDiscount.builder()
                .couponDiscountId(1L)
                .couponName("Holiday Sale")
                .couponType("Percentage")
                .discountPrice(new BigDecimal("1000.00"))
                .build();

        orderBook.setCouponDiscount(couponDiscount);

        assertNotNull(orderBook.getCouponDiscount());
        assertEquals("Holiday Sale", orderBook.getCouponDiscount().getCouponName());
        assertEquals(orderBook, couponDiscount.getOrderBook()); // 양방향
    }
}
