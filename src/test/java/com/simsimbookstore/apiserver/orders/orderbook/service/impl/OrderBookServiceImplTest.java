package com.simsimbookstore.apiserver.orders.orderbook.service.impl;

import com.simsimbookstore.apiserver.books.book.entity.Book;

import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.book.service.BookManagementService;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.service.CouponDiscountService;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.exception.OrderBookNotFoundException;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.service.PackageService;
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
import java.util.*;


@ExtendWith(MockitoExtension.class)
class OrderBookServiceImplTest {

    @Mock
    private OrderBookRepository orderBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookManagementService bookManagementService;

    @Mock
    private CouponDiscountService couponDiscountService;

    @Mock
    private PackageService packageService;

    @InjectMocks
    private OrderBookServiceImpl orderBookService; // 테스트 대상 서비스

    private Book book;
    private Order order;
    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        // 테스트용 Book 객체 생성
        book = Book.builder()
                .bookId(1L)
                .title("Test Book")
                .build();

        // 테스트용 Order 객체 생성
        order = Order.builder()
                .orderId(100L)
                .build();

        // 테스트용 OrderBook 객체 생성 (ID는 Builder를 통해 설정)
        orderBook = OrderBook.builder()
                .orderBookId(200L)
                .book(book)
                .order(order)
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .packages(new ArrayList<>()) // 초기 패키지 리스트
                .couponDiscount(null) // 초기 쿠폰 없음
                .build();
    }

    @Test
    @DisplayName("createOrderBooks() - 성공적으로 OrderBook 생성 및 쿠폰/패키지 연결")
    void testCreateOrderBooks_Success() {
        // Given
        CouponDiscountRequestDto couponDto = CouponDiscountRequestDto.builder()
                .couponId(1L)
                .couponName("WELCOME")
                .couponType("FIXED")
                .discountPrice(new BigDecimal("1000"))
                .build();

        PackageRequestDto packageDto1 = PackageRequestDto.builder()
                .packageTypeId(10L)
                .packageName("GiftBox")
                .build();

        PackageRequestDto packageDto2 = PackageRequestDto.builder()
                .packageTypeId(11L)
                .packageName("Ribbon")
                .build();

        OrderBookRequestDto dto = OrderBookRequestDto.builder()
                .orderId(1L)
                .bookId(1L)
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState("PENDING")
                .couponDiscountRequestDto(couponDto)
                .packagesRequestDtos(Arrays.asList(packageDto1, packageDto2))
                .build();

        List<OrderBookRequestDto> requestDtos = Collections.singletonList(dto);

        Book book = Book.builder()
                .bookId(1L)
                .title("Test Book")
                .build();

        Order order = Order.builder()
                .orderId(1L)
                .build();

        OrderBook savedOrderBook = OrderBook.builder()
                .orderBookId(200L)
                .book(book)
                .order(order)
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .build();

        CouponDiscount couponDiscount = CouponDiscount.builder()
                .couponDiscountId(1L)
                .couponName("WELCOME")
                .couponType("FIXED")
                .discountPrice(new BigDecimal("1000"))
                .build();

        Packages savedPackage1 = Packages.builder()
                .packageId(1L)
                .packageType("GiftBox")
                .wrapType(WrapType.builder().packageTypeId(10L).packageName("Gift Wrap").build())
                .orderBook(savedOrderBook)
                .build();

        Packages savedPackage2 = Packages.builder()
                .packageId(2L)
                .packageType("Ribbon")
                .wrapType(WrapType.builder().packageTypeId(11L).packageName("Ribbon Wrap").build())
                .orderBook(savedOrderBook)
                .build();

        savedOrderBook.addPackage(savedPackage1);
        savedOrderBook.addPackage(savedPackage2);

        // Mocking
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderBookRepository.save(any(OrderBook.class))).thenReturn(savedOrderBook);
        when(couponDiscountService.createCouponDiscount(couponDto, savedOrderBook))
                .thenAnswer(invocation -> {
                    savedOrderBook.setCouponDiscount(couponDiscount);
                    return CouponDiscountResponseDto.builder()
                            .couponDiscountId(1L)
                            .orderBookId(200L)
                            .couponName("WELCOME")
                            .couponType("FIXED")
                            .discountPrice(new BigDecimal("1000"))
                            .build();
                });
        when(packageService.createPackage(packageDto1, savedOrderBook)).thenReturn(savedPackage1);
        when(packageService.createPackage(packageDto2, savedOrderBook)).thenReturn(savedPackage2);
        when(orderBookRepository.findById(200L)).thenReturn(Optional.of(savedOrderBook));

        // When
        List<OrderBookResponseDto> resultList = orderBookService.createOrderBooks(requestDtos);

        // Then
        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        OrderBookResponseDto responseDto = resultList.getFirst();
        assertEquals(200L, responseDto.getOrderBookId());
        assertEquals("Test Book", responseDto.getBookTitle());
        assertEquals(2, responseDto.getQuantity());
        assertEquals(new BigDecimal("10000"), responseDto.getSalePrice());
        assertEquals(new BigDecimal("500"), responseDto.getDiscountPrice());
        assertEquals("PENDING", responseDto.getOrderBookState());

        // 쿠폰 검증
        assertNotNull(responseDto.getCouponDiscount());
        assertEquals(1L, responseDto.getCouponDiscount().getCouponDiscountId());
        assertEquals("WELCOME", responseDto.getCouponDiscount().getCouponName());
        assertEquals("FIXED", responseDto.getCouponDiscount().getCouponType());
        assertEquals(new BigDecimal("1000"), responseDto.getCouponDiscount().getDiscountPrice());

        // 패키지 검증
        assertNotNull(responseDto.getPackages());
        assertEquals(2, responseDto.getPackages().size());

        PackageResponseDto pkg1 = responseDto.getPackages().getFirst();
        assertEquals(1L, pkg1.getPackageId());
        assertEquals("GiftBox", pkg1.getPackageType());

        PackageResponseDto pkg2 = responseDto.getPackages().get(1);
        assertEquals(2L, pkg2.getPackageId());
        assertEquals("Ribbon", pkg2.getPackageType());

        // Verify
        verify(bookRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderBookRepository, times(1)).save(any(OrderBook.class));
        verify(couponDiscountService, times(1)).createCouponDiscount(couponDto, savedOrderBook);
        verify(packageService, times(1)).createPackage(packageDto1, savedOrderBook);
        verify(packageService, times(1)).createPackage(packageDto2, savedOrderBook);
        verify(orderBookRepository, times(1)).findById(200L);
    }



    @Test
    @DisplayName("createOrderBooks() - Book 미존재 시 예외 발생")
    void testCreateOrderBooks_BookNotFound() {
        // Given
        OrderBookRequestDto dto = OrderBookRequestDto.builder()
                .orderId(order.getOrderId())
                .bookId(999L) // 존재하지 않는 Book ID
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState("PENDING")
                .build();

        List<OrderBookRequestDto> requestDtos = Collections.singletonList(dto);

        // Mocking: Book 존재하지 않음
        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> orderBookService.createOrderBooks(requestDtos));

        // Verify: Repository 메서드 호출 여부 확인
        verify(bookRepository, times(1)).findById(999L);
        verify(bookManagementService, never()).modifyQuantity(anyLong(), anyInt());
        verify(orderRepository, never()).findById(anyLong());
        verify(orderBookRepository, never()).save(any(OrderBook.class));
        verify(couponDiscountService, never()).createCouponDiscount(any(), any(OrderBook.class));
        verify(packageService, never()).createPackage(any(), any(OrderBook.class));
    }

    @Test
    @DisplayName("getPackages() - 성공적으로 패키지 목록 조회")
    void testGetPackages_Success() {
        Long orderBookId = orderBook.getOrderBookId();

        when(orderBookRepository.findById(orderBookId))
                .thenReturn(Optional.of(orderBook));

        Packages pkg1 = Packages.builder()
                .packageId(1L)
                .packageType("GiftBox")
                .orderBook(orderBook)
                .build();

        Packages pkg2 = Packages.builder()
                .packageId(2L)
                .packageType("Ribbon")
                .orderBook(orderBook)
                .build();

        orderBook.getPackages().add(pkg1);
        orderBook.getPackages().add(pkg2);

        List<PackageResponseDto> packages = orderBookService.getPackages(orderBookId);

        assertNotNull(packages);
        assertEquals(2, packages.size());

        PackageResponseDto res1 = packages.getFirst();
        assertEquals(1L, res1.getPackageId());
        assertEquals("GiftBox", res1.getPackageType());

        PackageResponseDto res2 = packages.get(1);
        assertEquals(2L, res2.getPackageId());
        assertEquals("Ribbon", res2.getPackageType());

        verify(orderBookRepository, times(1)).findById(orderBookId);
    }

    @Test
    @DisplayName("getPackages() - OrderBook 미존재 시 예외 발생")
    void testGetPackages_Failure_OrderBookNotFound() {
        // Given
        Long nonExistentOrderBookId = 999L;

        when(orderBookRepository.findById(nonExistentOrderBookId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrderBookNotFoundException.class, () -> orderBookService.getPackages(nonExistentOrderBookId));

        verify(orderBookRepository, times(1)).findById(nonExistentOrderBookId);
    }

    @Test
    @DisplayName("getCouponDiscount() - 쿠폰이 없는 OrderBook 조회 시 null 반환")
    void testGetCouponDiscount_NoCoupon() {

        Long orderBookId = orderBook.getOrderBookId();
        // 쿠폰 없음

        when(orderBookRepository.findById(orderBookId))
                .thenReturn(Optional.of(orderBook));

        CouponDiscountResponseDto dto = orderBookService.getCouponDiscount(orderBookId);

        assertNull(dto);

        verify(orderBookRepository, times(1)).findById(orderBookId);
    }

    @Test
    @DisplayName("getCouponDiscount() - 쿠폰이 있는 OrderBook 조회 시 쿠폰 반환")
    void testGetCouponDiscount_WithCoupon() {
        // Given
        Long orderBookId = orderBook.getOrderBookId();
        CouponDiscount couponDiscount = CouponDiscount.builder()
                .couponDiscountId(1L)
                .couponName("HOLIDAY")
                .couponType("PERCENTAGE")
                .discountPrice(new BigDecimal("500"))
                .orderBook(orderBook)
                .build();

        orderBook.setCouponDiscount(couponDiscount);

        when(orderBookRepository.findById(orderBookId))
                .thenReturn(Optional.of(orderBook));

        // When
        CouponDiscountResponseDto dto = orderBookService.getCouponDiscount(orderBookId);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getCouponDiscountId());
        assertEquals("HOLIDAY", dto.getCouponName());
        assertEquals("PERCENTAGE", dto.getCouponType());
        assertEquals(new BigDecimal("500"), dto.getDiscountPrice());

        // Verify: Repository 메서드 호출 여부 확인
        verify(orderBookRepository, times(1)).findById(orderBookId);
    }

    @Test
    @DisplayName("updateOrderBook() - OrderBook 상태 정상 변경")
    void testUpdateOrderBook_Success() {
        // Given
        Long orderBookId = orderBook.getOrderBookId();
        OrderBook.OrderBookState newState = OrderBook.OrderBookState.COMPLETED;

        when(orderBookRepository.findById(orderBookId))
                .thenReturn(Optional.of(orderBook));

        // Mocking: OrderBook 저장 시, 상태 변경된 OrderBook 반환
        OrderBook updatedOrderBook = OrderBook.builder()
                .orderBookId(orderBookId)
                .book(book)
                .order(order)
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState(newState)
                .packages(new ArrayList<>())
                .couponDiscount(null)
                .build();

        when(orderBookRepository.save(orderBook)).thenReturn(updatedOrderBook);

        // When
        OrderBookResponseDto result = orderBookService.updateOrderBook(orderBookId, newState);

        // Then
        assertNotNull(result);
        assertEquals("COMPLETED", result.getOrderBookState());

        // Verify: Repository 메서드 호출 여부 확인
        verify(orderBookRepository, times(1)).findById(orderBookId);
        verify(orderBookRepository, times(1)).save(orderBook);
    }

    @Test
    @DisplayName("updateOrderBook() - 존재하지 않는 OrderBook 시 예외 발생")
    void testUpdateOrderBook_OrderBookNotFound() {
        // Given
        Long nonExistentOrderBookId = 999L;
        OrderBook.OrderBookState newState = OrderBook.OrderBookState.CANCELED;

        when(orderBookRepository.findById(nonExistentOrderBookId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> orderBookService.updateOrderBook(nonExistentOrderBookId, newState));

        // Verify: Repository 메서드 호출 여부 확인
        verify(orderBookRepository, times(1)).findById(nonExistentOrderBookId);
        verify(orderBookRepository, never()).save(any(OrderBook.class));
    }

    @Test
    @DisplayName("deleteOrderBook() - 정상적으로 OrderBook 삭제")
    void testDeleteOrderBook_Success() {
        // Given
        Long orderBookId = orderBook.getOrderBookId();

        when(orderBookRepository.findById(orderBookId))
                .thenReturn(Optional.of(orderBook));

        // When
        orderBookService.deleteOrderBook(orderBookId);

        // Then
        // Verify: Repository 메서드 호출 여부 확인
        verify(orderBookRepository, times(1)).findById(orderBookId);
        verify(orderBookRepository, times(1)).delete(orderBook);
    }

    @Test
    @DisplayName("deleteOrderBook() - 존재하지 않는 OrderBook 시 예외 발생")
    void testDeleteOrderBook_NotFound() {
        // Given
        Long nonExistentOrderBookId = 999L;

        when(orderBookRepository.findById(nonExistentOrderBookId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrderBookNotFoundException.class, () -> orderBookService.deleteOrderBook(nonExistentOrderBookId));

        // Verify: Repository 메서드 호출 여부 확인
        verify(orderBookRepository, times(1)).findById(nonExistentOrderBookId);
        verify(orderBookRepository, never()).delete(any(OrderBook.class));
    }

    @Test
    @DisplayName("getOrderBook() - 정상적으로 OrderBook 조회")
    void testGetOrderBook_Success() {
        // Given
        Long orderBookId = orderBook.getOrderBookId();

        when(orderBookRepository.findById(orderBookId))
                .thenReturn(Optional.of(orderBook));

        // When
        OrderBookResponseDto dto = orderBookService.getOrderBook(orderBookId);

        // Then
        assertNotNull(dto);
        assertEquals(orderBookId, dto.getOrderBookId());
        assertEquals("Test Book", dto.getBookTitle());
        assertEquals(2, dto.getQuantity());
        assertEquals(new BigDecimal("10000"), dto.getSalePrice());
        assertEquals(new BigDecimal("500"), dto.getDiscountPrice());
        assertEquals("PENDING", dto.getOrderBookState());
        assertNull(dto.getCouponDiscount());
        assertTrue(dto.getPackages().isEmpty());

        // Verify: Repository 메서드 호출 여부 확인
        verify(orderBookRepository, times(1)).findById(orderBookId);
    }

    @Test
    @DisplayName("getOrderBook() - 존재하지 않는 OrderBook 조회 시 예외 발생")
    void testGetOrderBook_NotFound() {
        // Given
        Long nonExistentOrderBookId = 999L;

        when(orderBookRepository.findById(nonExistentOrderBookId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> orderBookService.getOrderBook(nonExistentOrderBookId));

        // Verify: Repository 메서드 호출 여부 확인
        verify(orderBookRepository, times(1)).findById(nonExistentOrderBookId);
    }
}
