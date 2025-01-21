package com.simsimbookstore.apiserver.orders.order.detail.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.repository.CouponDiscountRepository;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailResponseDto;
import com.simsimbookstore.apiserver.orders.order.detail.repository.OrderBookQueryRepository;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;

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

    @InjectMocks
    private OrderDetailService orderDetailService;

    private Order mockOrder;
    private Delivery mockDelivery;
    private Payment mockPayment;
    private OrderBook mockOrderBook;
    private Book mockBook;

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
}