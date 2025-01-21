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
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.service.PackageService;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import java.time.LocalDateTime;
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
    User testUser;
    Grade standardGrade;
    Grade royalGrade;

    @BeforeEach
    void setUp() {
        // 테스트용 Book 객체 생성
        book = Book.builder()
                .bookId(1L)
                .title("Test Book")
                .build();

        // 테스트용 User 객체 생성
        testUser = User.builder()
                .userId(1L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .latestLoginDate(LocalDateTime.now())
                .grade(standardGrade)
                .build();

        // 테스트용 Order 객체 생성 (User 연결)
        order = Order.builder()
                .orderId(100L)
                .user(testUser)  // User 설정
                .build();

        // 테스트용 OrderBook 객체 생성
        orderBook = OrderBook.builder()
                .orderBookId(200L)
                .book(book)
                .order(order)  // Order 연결
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .packages(new ArrayList<>()) // 초기 패키지 리스트
                .couponDiscount(null) // 초기 쿠폰 없음
                .build();

        standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        royalGrade = Grade.builder()
                .tier(Tier.ROYAL)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();
    }

    @Test
    @DisplayName("createOrderBooks() - OrderBook 기본 필드 검증")
    void testCreateOrderBooks_BasicFields() {
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

        OrderBook savedOrderBook = OrderBook.builder()
                .orderBookId(200L)
                .book(book)    // Assume 'book' is predefined or mocked accordingly
                .order(order)  // Assume 'order' is predefined or mocked accordingly
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .build();

        // Coupon and Packages setup omitted for brevity...

        // Mocking for basic fields (쿠폰, 패키지 검증 제외)
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderBookRepository.save(any(OrderBook.class))).thenReturn(savedOrderBook);
        when(couponDiscountService.createCouponDiscount(any(), any())).thenReturn(null);
        when(packageService.createPackage(any(), any())).thenReturn(null);
        when(orderBookRepository.findById(200L)).thenReturn(Optional.of(savedOrderBook));

        // When
        List<OrderBookResponseDto> resultList = orderBookService.createOrderBooks(requestDtos);

        // Then
        OrderBookResponseDto responseDto = resultList.getFirst();
        assertAll("기본 필드 검증",
                () -> assertNotNull(resultList),
                () -> assertEquals(1, resultList.size()),
                () -> assertEquals(200L, responseDto.getOrderBookId()),
                () -> assertEquals("Test Book", responseDto.getBookTitle()),
                () -> assertEquals(2, responseDto.getQuantity()),
                () -> assertEquals(new BigDecimal("10000"), responseDto.getSalePrice()),
                () -> assertEquals(new BigDecimal("500"), responseDto.getDiscountPrice()),
                () -> assertEquals("PENDING", responseDto.getOrderBookState())
        );

        verify(bookRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderBookRepository, times(1)).save(any(OrderBook.class));
    }

    @Test
    @DisplayName("createOrderBooks() - 쿠폰 및 패키지 연결 검증")
    void testCreateOrderBooks_CouponAndPackages() {
        // Given
        // [동일한 Given 섹션을 필요에 따라 복사. 이 테스트에서는 쿠폰 및 패키지 관련 설정과 모킹이 중요]
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
                .packagesRequestDtos(List.of(packageDto1, packageDto2))
                .build();

        List<OrderBookRequestDto> requestDtos = Collections.singletonList(dto);

        OrderBook savedOrderBook = OrderBook.builder()
                .orderBookId(200L)
                .book(book)    // Assume 'book' is predefined
                .order(order)  // Assume 'order' is predefined
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

        // Mocking for coupon and packages related operations
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
        OrderBookResponseDto responseDto = resultList.getFirst();
        assertAll("쿠폰 및 패키지 검증",
                () -> {
                    assertNotNull(responseDto.getCouponDiscount(), "CouponDiscount should not be null");
                    assertAll("CouponDiscount assertions",
                            () -> assertEquals(1L, responseDto.getCouponDiscount().getCouponDiscountId()),
                            () -> assertEquals("WELCOME", responseDto.getCouponDiscount().getCouponName()),
                            () -> assertEquals("FIXED", responseDto.getCouponDiscount().getCouponType()),
                            () -> assertEquals(new BigDecimal("1000"), responseDto.getCouponDiscount().getDiscountPrice())
                    );
                },
                () -> {
                    assertNotNull(responseDto.getPackages(), "Packages should not be null");
                    assertEquals(2, responseDto.getPackages().size(), "Packages size mismatch");

                    PackageResponseDto pkg1 = responseDto.getPackages().get(0);
                    PackageResponseDto pkg2 = responseDto.getPackages().get(1);

                    assertAll("First Package assertions",
                            () -> assertEquals(1L, pkg1.getPackageId()),
                            () -> assertEquals("GiftBox", pkg1.getPackageType())
                    );

                    assertAll("Second Package assertions",
                            () -> assertEquals(2L, pkg2.getPackageId()),
                            () -> assertEquals("Ribbon", pkg2.getPackageType())
                    );
                }
        );

        verify(couponDiscountService, times(1)).createCouponDiscount(couponDto, savedOrderBook);
        verify(packageService, times(1)).createPackage(packageDto1, savedOrderBook);
        verify(packageService, times(1)).createPackage(packageDto2, savedOrderBook);
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

        assertThrows(NotFoundException.class, () -> orderBookService.getPackages(nonExistentOrderBookId));

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
        assertThrows(NotFoundException.class, () -> orderBookService.updateOrderBook(nonExistentOrderBookId, newState));

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
        assertThrows(NotFoundException.class, () -> orderBookService.deleteOrderBook(nonExistentOrderBookId));

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

        assertThrows(NotFoundException.class, () -> orderBookService.getOrderBook(nonExistentOrderBookId));

        verify(orderBookRepository, times(1)).findById(nonExistentOrderBookId);
    }

    @Test
    @DisplayName("getOrderName() - 단일 도서 주문 시 올바른 주문명 반환")
    void testGetOrderName_SingleBook() {
        // given
        OrderBookRequestDto dto = OrderBookRequestDto.builder()
                .bookId(1L)
                .quantity(3)
                .build();
        List<OrderBookRequestDto> dtos = Collections.singletonList(dto);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // when
        String orderName = orderBookService.getOrderName(dtos);

        // then
        assertEquals("Test Book 3권", orderName);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getOrderName() - 여러 도서 주문 시 올바른 주문명 반환")
    void testGetOrderName_MultipleBooks() {
        // given
        OrderBookRequestDto dto1 = OrderBookRequestDto.builder()
                .bookId(1L)
                .quantity(1)
                .build();
        OrderBookRequestDto dto2 = OrderBookRequestDto.builder()
                .bookId(1L)
                .quantity(2)
                .build();
        List<OrderBookRequestDto> dtos = Arrays.asList(dto1, dto2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // when
        String orderName = orderBookService.getOrderName(dtos);

        // then
        assertEquals("Test Book 외 1권", orderName);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getOrderBooks() - 주어진 주문 ID에 해당하는 OrderBooks 반환")
    void testGetOrderBooks() {
        // given
        Long orderId = 100L;
        OrderBook ob1 = OrderBook.builder().orderBookId(1L).build();
        OrderBook ob2 = OrderBook.builder().orderBookId(2L).build();
        List<OrderBook> orderBooks = Arrays.asList(ob1, ob2);

        when(orderBookRepository.findByOrderOrderId(orderId)).thenReturn(orderBooks);

        // when
        List<OrderBook> result = orderBookService.getOrderBooks(orderId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(ob1));
        assertTrue(result.contains(ob2));
        verify(orderBookRepository, times(1)).findByOrderOrderId(orderId);
    }

    @Test
    @DisplayName("createOrderBook() - 성공적으로 OrderBookResponseDto 반환")
    void testCreateOrderBook_Success() {
        // given
        OrderBookRequestDto requestDto = OrderBookRequestDto.builder()
                .orderId(1L)
                .bookId(1L)
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState("PENDING")
                .build();

        OrderBook savedOrderBook = OrderBook.builder()
                .orderBookId(200L)
                .book(book)
                .order(order)
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .packages(new ArrayList<>())
                .couponDiscount(null)
                .build();

        // 내부 메서드 호출을 모킹하기 위해 repository와 관련 서비스들의 동작을 설정
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderBookRepository.save(any(OrderBook.class))).thenReturn(savedOrderBook);
        when(orderBookRepository.findById(200L)).thenReturn(Optional.of(savedOrderBook));
        // toOrderBookResponseDto 변환 과정을 단순화하기 위해 savedOrderBook를 기반으로 응답 DTO 직접 생성
        // 실제 서비스 구현에 따라 toOrderBookResponseDto 내부 동작을 모킹할 수 있음

        // when
        OrderBookResponseDto responseDto = orderBookService.createOrderBook(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(200L, responseDto.getOrderBookId());
        assertEquals("PENDING", responseDto.getOrderBookState());
        // 필요한 추가 검증...
    }



}
