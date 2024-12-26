package com.simsimbookstore.apiserver.orders.coupondiscount.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.repository.CouponDiscountRepository;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponDiscountServiceImplTest {

    @Mock
    private CouponDiscountRepository couponDiscountRepository;

    @Mock
    private OrderBookRepository orderBookRepository;

    @InjectMocks
    private CouponDiscountServiceImpl couponDiscountService;

    private OrderBook mockOrderBook;
    private CouponDiscount mockCouponDiscount;

    @BeforeEach
    void setUp() {
        mockOrderBook = OrderBook.builder()
                .orderBookId(100L)
                .quantity(1)
                .salePrice(BigDecimal.valueOf(20000))
                .discountPrice(BigDecimal.ZERO)
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .build();

        mockCouponDiscount = CouponDiscount.builder()
                .couponDiscountId(999L)
                .orderBook(mockOrderBook)
                .couponName("테스트쿠폰")
                .couponType("정액")
                .discountPrice(BigDecimal.valueOf(1000))
                .build();

        //양방향
        mockOrderBook.setCouponDiscount(mockCouponDiscount);
    }

    @Test
    @DisplayName("쿠폰 할인 생성")
    void createCouponDiscount_Success() {
        CouponDiscountRequestDto requestDto = CouponDiscountRequestDto.builder()
                .orderBookId(mockOrderBook.getOrderBookId()) // 100L
                .counponName("신규쿠폰")
                .couponType("TYPE_NEW")
                .discountPrice(BigDecimal.valueOf(2000))
                .build();

        when(orderBookRepository.findById(requestDto.getOrderBookId()))
                .thenReturn(Optional.of(mockOrderBook));

        CouponDiscount savedCouponDiscount = CouponDiscount.builder()
                .couponDiscountId(1000L)
                .orderBook(mockOrderBook)
                .couponName(requestDto.getCounponName())
                .couponType(requestDto.getCouponType())
                .discountPrice(requestDto.getDiscountPrice())
                .build();
        when(couponDiscountRepository.save(any(CouponDiscount.class)))
                .thenReturn(savedCouponDiscount);

        CouponDiscountResponseDto responseDto
                = couponDiscountService.createCouponDiscount(requestDto);

        assertNotNull(responseDto);
        assertEquals(1000L, responseDto.getCouponDiscountId());
        assertEquals("신규쿠폰", responseDto.getCouponName());
        assertEquals(BigDecimal.valueOf(2000), responseDto.getDiscountPrice());

        verify(couponDiscountRepository).delete(mockCouponDiscount);

        verify(couponDiscountRepository).save(any(CouponDiscount.class));
    }

    @Test
    @DisplayName("쿠폰 할인 생성  OrderBook이 존재하지 않을 때 예외")
    void createCouponDiscount_OrderBookNotFound_ThrowsException() {
        CouponDiscountRequestDto requestDto = CouponDiscountRequestDto.builder()
                .orderBookId(999L)
                .counponName("쿠폰이름")
                .couponType("TYPE")
                .discountPrice(BigDecimal.valueOf(1000))
                .build();

        when(orderBookRepository.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> couponDiscountService.createCouponDiscount(requestDto)
        );
        assertEquals("OrderBook not found", thrown.getMessage());
    }

    @Test
    @DisplayName("쿠폰 할인 조회 - 정상 케이스")
    void findById_Success() {
        Long couponDiscountId = 999L;
        when(couponDiscountRepository.findById(couponDiscountId))
                .thenReturn(Optional.of(mockCouponDiscount));

        CouponDiscountResponseDto responseDto
                = couponDiscountService.findById(couponDiscountId);

        assertNotNull(responseDto);
        assertEquals(999L, responseDto.getCouponDiscountId());
        assertEquals("테스트쿠폰", responseDto.getCouponName());
        assertEquals(BigDecimal.valueOf(1000), responseDto.getDiscountPrice());
        verify(couponDiscountRepository).findById(couponDiscountId);
    }

    @Test
    @DisplayName("쿠폰 할인 조회 - 없는 쿠폰 ID일 때 예외")
    void findById_NotFound_ThrowsException() {
        Long notExistCouponId = 9876L;
        when(couponDiscountRepository.findById(notExistCouponId))
                .thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> couponDiscountService.findById(notExistCouponId)
        );
        assertEquals("CouponDiscount not found", thrown.getMessage());
        verify(couponDiscountRepository).findById(notExistCouponId);
    }

    @Test
    @DisplayName("쿠폰 할인 삭제 - 정상 케이스")
    void deleteById_Success() {
        Long couponDiscountId = 999L;
        when(couponDiscountRepository.findById(couponDiscountId))
                .thenReturn(Optional.of(mockCouponDiscount));

        couponDiscountService.deleteById(couponDiscountId);

        assertNull(mockOrderBook.getCouponDiscount());

        verify(couponDiscountRepository).delete(mockCouponDiscount);
    }

    @Test
    @DisplayName("쿠폰 할인 삭제 - 없는 쿠폰 ID일 때 예외")
    void deleteById_NotFound_ThrowsException() {
        Long notExistCouponId = 9876L;
        when(couponDiscountRepository.findById(notExistCouponId))
                .thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> couponDiscountService.deleteById(notExistCouponId)
        );
        assertEquals("CouponDiscount not found", thrown.getMessage());

        verify(couponDiscountRepository, never()).delete(any());
    }
}
