package com.simsimbookstore.apiserver.coupons.coupon.service.impl;

import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CouponTypeRepository couponTypeRepository;
    @InjectMocks
    private CouponService couponService;

    private User user;
    private CouponType couponType;

    @BeforeEach
    void setUp() {

    }
    @Test
    void getCouponById() {

    }

    @Test
    void getUnusedCouponByCouponType() {
    }

    @Test
    void getCoupons() {
    }

    @Test
    void getUnusedCoupons() {
    }

    @Test
    void getEligibleCoupons() {
    }

    @Test
    void issueCoupons() {
    }

    @Test
    void expireCoupon() {
    }

    @Test
    void useCoupon() {
    }

    @Test
    void deleteCoupon() {
    }

    @Test
    void calDiscountAmount() {
    }
}