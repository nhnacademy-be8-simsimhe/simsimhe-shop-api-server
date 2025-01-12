package com.simsimbookstore.apiserver.orders.coupondiscount.service.impl;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
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

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private CouponDiscountServiceImpl couponDiscountService;

    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        orderBook = OrderBook.builder()
                .orderBookId(200L)
                .build();
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

