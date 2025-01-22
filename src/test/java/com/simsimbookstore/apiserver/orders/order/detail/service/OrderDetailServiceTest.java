package com.simsimbookstore.apiserver.orders.order.detail.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.repository.CouponDiscountRepository;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.repository.DeliveryRepository;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailResponseDto;
import com.simsimbookstore.apiserver.orders.order.detail.repository.OrderBookQueryRepository;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;

import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.role.entity.Role;
import com.simsimbookstore.apiserver.users.role.entity.RoleName;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.userrole.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderBookRepository orderBookRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CouponDiscountRepository couponDiscountRepository;
    @Mock
    private OrderBookQueryRepository orderBookQueryRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @InjectMocks
    private OrderDetailService orderDetailService;
    Grade testGrade;
    User mockGuest;
    UserRole userRole;

    private Order mockOrder;
    private Delivery mockDelivery;
    private Payment mockPayment;
    private OrderBook mockOrderBook;
    private Book mockBook;
    Order mockOrder2;

    @BeforeEach
    void setUp() {
        mockBook = Book.builder()
                .bookId(1L)
                .title("Test Book")
                .build();
        // 1) Order 엔티티 설정
        mockOrder = new Order();
        mockOrder.setOrderId(1L);
        mockOrder.setOrderNumber("ORDER_001");
        mockOrder.setOrderDate(LocalDateTime.now());
        mockOrder.setOrderState(Order.OrderState.IN_DELIVERY);
        mockOrder.setSenderName("홍길동");
        mockOrder.setPhoneNumber("010-1234-5678");
        mockOrder.setOrderEmail("test@simsim.com");
        mockOrder.setPointEarn(100);
        mockOrder.setPointUse(BigDecimal.TEN);
        mockOrder.setTotalPrice(new BigDecimal("30000"));
        mockOrder.setDeliveryPrice(new BigDecimal("3000"));
        mockOrder.setOriginalPrice(new BigDecimal("35000"));
        // 게스트 여부

        // 2) Delivery 설정
        mockDelivery = new Delivery();
        mockDelivery.setDeliveryReceiver("수령인");
        mockDelivery.setReceiverPhoneNumber("010-9999-8888");
        mockDelivery.setTrackingNumber(123456);
        mockDelivery.setPostalCode("12345");
        mockDelivery.setRoadAddress("서울시 종로구");
        mockDelivery.setDetailedAddress("1-1");
        mockDelivery.setReference("참고");
        mockDelivery.setDeliveryState(Delivery.DeliveryState.IN_PROGRESS);

        mockOrder.setDelivery(mockDelivery);

        // 3) Payment 설정
        mockPayment = new Payment();
        mockPayment.setPaymentId(10L);
        mockPayment.setTossReturnMethod("카드");
        mockPayment.setOrder(mockOrder);

        // 4) OrderBook 설정
        mockOrderBook = OrderBook.builder()
                .orderBookId(200L)
                .book(mockBook)
                .order(mockOrder)  // Order 연결
                .quantity(2)
                .salePrice(new BigDecimal("10000"))
                .discountPrice(new BigDecimal("500"))
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .packages(new ArrayList<>()) // 초기 패키지 리스트
                .couponDiscount(null) // 초기 쿠폰 없음
                .build();

        // 5) Book 설정
        testGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        mockGuest = User.builder()
                .userId(2L)
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(testGrade)
                .userRoleList(new HashSet<>(Collections.singleton(userRole))) // 초기화
                .build();

        userRole = UserRole.builder()
                .userRoleId(2L)
                .user(mockGuest)
                .role(new Role(1L, RoleName.GUEST))
                .build();

        mockOrder2 = new Order();
        mockOrder2.setOrderId(2L);
        mockOrder2.setUser(mockGuest);
        mockOrder2.setOrderNumber("ORDER_002");
        mockOrder2.setOrderDate(LocalDateTime.now());
        mockOrder2.setOrderState(Order.OrderState.COMPLETED);
        mockOrder2.setSenderName("홍길동");
        mockOrder2.setPhoneNumber("010-1234-5678");
        mockOrder2.setOrderEmail("test@simsim.com");
        mockOrder2.setPointEarn(100);
        mockOrder2.setPointUse(BigDecimal.TEN);
        mockOrder2.setTotalPrice(new BigDecimal("30000"));
        mockOrder2.setDeliveryPrice(new BigDecimal("3000"));
        mockOrder2.setOriginalPrice(new BigDecimal("35000"));

    }

    @Test
    void testGetOrderDetail_Success() {
        // given
        String orderNumber = "ORDER_001";
        Long userId = 999L; // 사실상 서비스 로직에서 userId 검증은 안 함 (파라미터로만 사용)

        // Repository Mock 세팅
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findByOrder(mockOrder))
                .thenReturn(Optional.of(mockPayment));
        when(orderBookRepository.findByOrderOrderId(mockOrder.getOrderId()))
                .thenReturn(List.of(mockOrderBook));
        when(bookRepository.findByBookId(mockOrderBook.getOrderBookId()))
                .thenReturn(Optional.of(mockBook));

        // CouponDiscount가 있을 경우
        CouponDiscount mockCoupon = new CouponDiscount();
        mockCoupon.setCouponName("테스트 쿠폰");
        mockCoupon.setDiscountPrice(new BigDecimal("1000"));

        when(couponDiscountRepository.findByOrderBook(mockOrderBook))
                .thenReturn(mockCoupon);

        // WrapType(포장 정보)도 있다고 가정
        WrapType savedWrapType = WrapType.builder()
                .packageTypeId(1L)
                .packageName("Gift Wrap")
                .packagePrice(BigDecimal.valueOf(1000))
                .build();

        when(orderBookQueryRepository.findByPackage(mockOrderBook))
                .thenReturn(List.of(savedWrapType));

        // when
        OrderDetailResponseDto resultDto = orderDetailService.getOrderDetail(userId, orderNumber);

        // then
        assertNotNull(resultDto);

        // OrderDetailInfoDto 확인
        assertEquals("ORDER_001", resultDto.getOrderDetailInfoDto().getOrderNumber());
        assertEquals("홍길동", resultDto.getOrderDetailInfoDto().getSenderName());
        assertEquals("카드", resultDto.getOrderDetailInfoDto().getPaymentMethod());
        assertEquals("수령인", resultDto.getOrderDetailInfoDto().getReceiverName());

        // OrderDetailProducts 확인
        assertEquals(1, resultDto.getOrderDetailProductList().size());
        assertEquals("Test Book", resultDto.getOrderDetailProductList().getFirst().getBookTitle());
        assertEquals("테스트 쿠폰", resultDto.getOrderDetailProductList().getFirst().getCouponName());
        assertEquals(new BigDecimal("1000"), resultDto.getOrderDetailProductList().getFirst().getCouponPrice());
        assertEquals("Gift Wrap", resultDto.getOrderDetailProductList().getFirst().getPackageName());
        assertEquals(new BigDecimal("1000"), resultDto.getOrderDetailProductList().getFirst().getPackagePrice());
    }

    @Test
    void testGetOrderDetail_OrderNotFound() {
        // given
        String orderNumber = "INVALID_ORDER";
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> orderDetailService.getOrderDetail(123L, orderNumber));
    }

    @Test
    void testGetOrderDetail_PaymentNotFound() {
        // given
        String orderNumber = "ORDER_001";
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findByOrder(mockOrder))
                .thenReturn(Optional.empty()); // 결제 정보 없음

        // when & then
        assertThrows(NotFoundException.class,
                () -> orderDetailService.getOrderDetail(123L, orderNumber));
    }

    @Test
    void testGetOrderDetail_BookNotFound() {
        // given
        String orderNumber = "ORDER_001";
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findByOrder(mockOrder))
                .thenReturn(Optional.of(mockPayment));
        when(orderBookRepository.findByOrderOrderId(mockOrder.getOrderId()))
                .thenReturn(List.of(mockOrderBook));
        // Book이 없는 상황
        when(bookRepository.findByBookId(mockOrderBook.getOrderBookId()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> orderDetailService.getOrderDetail(123L, orderNumber));
    }

    @Test
    void testGetGuestOrderDetail_NotGuestOrder() {
        // given
        String orderNumber = "ORDER_003";
        String email = "guest@foo.com";

        // 게스트 주문 아님
        mockOrder.setOrderEmail("test@simsim.com");
        mockOrder.setOrderNumber(orderNumber);

        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder));

        // when & then
        // getGuestOrderDetail 내부에서 IllegalAccessException → RuntimeException 변환
        assertThrows(RuntimeException.class,
                () -> orderDetailService.getGuestOrderDetail(orderNumber, email));
    }

    @Test
    void testGetOrderDetail_NoWrapType() {
        // given
        String orderNumber = "ORDER_001";
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findByOrder(mockOrder))
                .thenReturn(Optional.of(mockPayment));
        when(orderBookRepository.findByOrderOrderId(mockOrder.getOrderId()))
                .thenReturn(List.of(mockOrderBook));
        when(bookRepository.findByBookId(mockOrderBook.getOrderBookId()))
                .thenReturn(Optional.of(mockBook));

        when(orderBookQueryRepository.findByPackage(mockOrderBook))
                .thenReturn(Collections.emptyList()); // WrapType 없음

        // when
        OrderDetailResponseDto resultDto = orderDetailService.getOrderDetail(123L, orderNumber);

        // then
        assertEquals("-", resultDto.getOrderDetailProductList().getFirst().getPackageName());
        assertEquals(BigDecimal.ZERO, resultDto.getOrderDetailProductList().getFirst().getPackagePrice());
    }

    @Test
    void testGetOrderDetail_NoCoupon() {
        // given
        String orderNumber = "ORDER_001";
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findByOrder(mockOrder))
                .thenReturn(Optional.of(mockPayment));
        when(orderBookRepository.findByOrderOrderId(mockOrder.getOrderId()))
                .thenReturn(List.of(mockOrderBook));
        when(bookRepository.findByBookId(mockOrderBook.getOrderBookId()))
                .thenReturn(Optional.of(mockBook));

        when(couponDiscountRepository.findByOrderBook(mockOrderBook))
                .thenReturn(null); // 쿠폰 정보 없음

        // when
        OrderDetailResponseDto resultDto = orderDetailService.getOrderDetail(123L, orderNumber);

        // then
        assertEquals("-", resultDto.getOrderDetailProductList().get(0).getCouponName());
        assertEquals(BigDecimal.ZERO, resultDto.getOrderDetailProductList().get(0).getCouponPrice());
    }

    @Test
    void testGetOrderDetail_EmptyOrderBooks() {
        // given
        String orderNumber = "ORDER_001";
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findByOrder(mockOrder))
                .thenReturn(Optional.of(mockPayment));
        when(orderBookRepository.findByOrderOrderId(mockOrder.getOrderId()))
                .thenReturn(Collections.emptyList()); // 빈 주문 리스트

        // when
        OrderDetailResponseDto resultDto = orderDetailService.getOrderDetail(123L, orderNumber);

        // then
        assertTrue(resultDto.getOrderDetailProductList().isEmpty());
    }

    @Test
    void testGetGuestOrderDetail_Success() {
        // given
        String orderNumber = "ORDER_002";
        String email = "guest@simsim.com";
        mockDelivery = new Delivery();
        mockDelivery.setDeliveryId(1L);
        mockDelivery.setDeliveryReceiver("Receiver Name");
        mockDelivery.setReceiverPhoneNumber("010-9999-8888");
        mockDelivery.setTrackingNumber(12345);
        mockDelivery.setPostalCode("12345");
        mockDelivery.setRoadAddress("Test Road Address");
        mockDelivery.setDetailedAddress("Test Detailed Address");
        mockDelivery.setReference("Test Reference");
        mockDelivery.setDeliveryState(Delivery.DeliveryState.IN_PROGRESS);

        mockPayment = new Payment();
        mockPayment.setPaymentId(10L);
        mockPayment.setTossReturnMethod("카드");
        mockPayment.setOrder(mockOrder2);


        // mockGuest 초기화
        mockGuest = User.builder()
                .userId(2L)
                .userName("John Doe")
                .email(email)
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(testGrade)
                .userRoleList(new HashSet<>(Collections.singleton(userRole))) // 역할 추가
                .build();

        userRole = UserRole.builder()
                .userRoleId(1L)
                .user(mockGuest)
                .role(new Role(1L, RoleName.GUEST))
                .build();

        // userRole과 user 연결
        mockGuest.addUserRole(userRole);

        // mockOrder2 설정
        mockOrder2.setUser(mockGuest);
        mockOrder2.setOrderEmail(email);

        // Delivery 객체 초기화
        mockDelivery = new Delivery();
        mockDelivery.setDeliveryId(1L);
        mockDelivery.setDeliveryReceiver("Receiver Name");
        mockDelivery.setReceiverPhoneNumber("010-9999-8888");
        mockDelivery.setTrackingNumber(12345);
        mockDelivery.setPostalCode("12345");
        mockDelivery.setRoadAddress("Test Road Address");
        mockDelivery.setDetailedAddress("Test Detailed Address");
        mockDelivery.setReference("Test Reference");
        mockDelivery.setDeliveryState(Delivery.DeliveryState.IN_PROGRESS);

        // Order와 Delivery 연결
        mockOrder2.setDelivery(mockDelivery);

        // Payment 객체 초기화 및 모킹 설정
        when(paymentRepository.findByOrder(mockOrder2))
                .thenReturn(Optional.of(mockPayment));

        // Repository Mock 설정
        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(mockOrder2));

        // when
        OrderDetailResponseDto resultDto = orderDetailService.getGuestOrderDetail(orderNumber, email);

        // then
        assertNotNull(resultDto);
        assertEquals(orderNumber, resultDto.getOrderDetailInfoDto().getOrderNumber());
        assertEquals(mockDelivery.getDeliveryId(), resultDto.getOrderDetailInfoDto().getDeliveryId());
    }
}
