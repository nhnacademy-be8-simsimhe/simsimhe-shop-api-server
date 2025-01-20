package com.simsimbookstore.apiserver.orders.coupondiscount.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import com.simsimbookstore.apiserver.orders.coupondiscount.repository.CouponDiscountRepository;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

import org.mockito.*;


@ExtendWith(MockitoExtension.class)
class CouponDiscountServiceImplTest {

    @Mock
    private CouponDiscountRepository couponDiscountRepository;

    @Mock
    private CouponRepository couponRepository;

    private OrderBook orderBook;
    private CouponDiscount existingDiscount;
    private Coupon coupon;
    private CouponDiscountRequestDto requestDto;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private CouponDiscountServiceImpl couponDiscountService;


    @BeforeEach
    void setUp() {
        orderBook = OrderBook.builder()
                .orderBookId(200L)
                .build();

        orderBook = new OrderBook();
        existingDiscount = new CouponDiscount();
        coupon = mock(Coupon.class);
        lenient().when(coupon.getCouponId()).thenReturn(1L);
        requestDto = new CouponDiscountRequestDto();

        // requestDto 필드 설정 (필요한 값 입력)
        requestDto.setCouponId(1L);
        requestDto.setCouponName("Test Coupon");
        requestDto.setCouponType("Test Type");
        requestDto.setDiscountPrice(BigDecimal.valueOf(5000));

        // coupon 필드 설정
        coupon.setCouponId(1L);

        // orderBook에 기존 쿠폰 할인 설정 (테스트 시나리오에 따라 설정)
        orderBook.setCouponDiscount(existingDiscount);
    }

    @Test
    void testCreateCouponDiscount_WithExistingDiscount() {
        // given
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponDiscountRepository.save(any(CouponDiscount.class))).thenAnswer(invocation -> {
            // 저장 시 전달된 CouponDiscount 반환
            CouponDiscount savedDiscount = invocation.getArgument(0);
            // 저장 시점에 orderBook에 새 쿠폰 할인 설정
            orderBook.setCouponDiscount(savedDiscount);
            return savedDiscount;
        });

        // when
        CouponDiscountResponseDto response = couponDiscountService.createCouponDiscount(requestDto, orderBook);

        // then
        // 기존 쿠폰 할인 삭제 검증
        verify(couponDiscountRepository).delete(existingDiscount);

        // 새 쿠폰 할인 생성 및 저장 확인
        verify(couponRepository).findById(1L);
        verify(couponDiscountRepository).save(any(CouponDiscount.class));

        // orderBook에 새 쿠폰 할인이 설정되었는지 확인
        assertNotNull(orderBook.getCouponDiscount(), "새 쿠폰 할인이 설정되어야 함");

        // responseDto 검증
        assertNotNull(response);
        assertEquals(orderBook.getCouponDiscount().getCouponDiscountId(), response.getCouponDiscountId());
        assertEquals(orderBook.getCouponDiscount().getCouponName(), response.getCouponName());
        // 필요한 다른 필드들도 비교 가능
    }


    @Test
    void createCouponDiscount_Success() {

        CouponPolicy couponPolicyFix = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Fixed Discount Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(new BigDecimal("5000"))
                .policyDescription("Fixed discount policy description")
                .build();

        AllCoupon allCoupon = AllCoupon.builder()
                .couponTypeId(102L)
                .couponTypeName("All Category Discount")
                .deadline(LocalDateTime.now().plusDays(10))
                .stacking(false)
                .couponPolicy(couponPolicyFix)
                .build();

        Coupon mockCoupon = Coupon.builder()
                .couponId(1L)
                .issueDate(LocalDateTime.now().minusDays(10))
                .deadline(LocalDateTime.now().plusDays(10))
                .couponStatus(CouponStatus.UNUSED)
                .couponType(allCoupon)
                .user(mock(User.class))
                .build();

        when(couponRepository.findById(1L)).thenReturn(Optional.of(mockCoupon));

        CouponDiscountRequestDto dto = CouponDiscountRequestDto.builder()
                .couponId(1L) // Ensure couponId is provided
                .couponName("WELCOME")
                .couponType("FIXED")
                .discountPrice(new BigDecimal("1000"))
                .build();

        CouponDiscount savedCoupon = CouponDiscount.builder()
                .coupon(mockCoupon)
                .couponDiscountId(1L)
                .orderBook(orderBook)
                .couponName("WELCOME")
                .couponType("FIXED")
                .discountPrice(new BigDecimal("1000"))
                .build();

        when(couponDiscountRepository.save(ArgumentMatchers.any(CouponDiscount.class))).thenReturn(savedCoupon);

        CouponDiscountResponseDto result = couponDiscountService.createCouponDiscount(dto, orderBook);

        assertNotNull(result);
        assertEquals(1L, result.getCouponDiscountId());
        assertEquals("WELCOME", result.getCouponName());
        verify(couponDiscountRepository, times(1)).save(ArgumentMatchers.any(CouponDiscount.class));
    }
    @Test
    void findById_Success() {
        CouponDiscount couponDiscount = CouponDiscount.builder()
                .orderBook(mock((OrderBook.class)))
                .couponDiscountId(10L)
                .couponName("EARLYBIRD")
                .build();

        when(couponDiscountRepository.findById(10L)).thenReturn(Optional.of(couponDiscount));

        CouponDiscountResponseDto result = couponDiscountService.findById(10L);

        assertNotNull(result);
        assertEquals("EARLYBIRD", result.getCouponName());
        verify(couponDiscountRepository, times(1)).findById(10L);
    }

    @Test
    void findById_NotFound() {
        assertThrows(NotFoundException.class,
                () -> couponDiscountService.findById(999L));
    }

    @Test
    void deleteById_Success() {
        CouponDiscount couponDiscount = CouponDiscount.builder()
                .couponDiscountId(20L)
                .orderBook(orderBook)
                .couponName("DELETE_ME")
                .build();

        when(couponDiscountRepository.findById(20L)).thenReturn(Optional.of(couponDiscount));

        couponDiscountService.deleteById(20L);

        assertNull(orderBook.getCouponDiscount());
        verify(couponDiscountRepository, times(1)).findById(20L);
    }
}

