//package com.simsimbookstore.apiserver.coupons.coupon.repository;
//
//import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
//import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
//import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
//import com.simsimbookstore.apiserver.users.user.entity.User;
//import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//
//@DataJpaTest
//@ExtendWith(MockitoExtension.class)
//class CouponRepositoryTest {
//    @Autowired
//    private CouponRepository couponRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User mockUser;
//
//
//
//    @BeforeEach
//    void setUp() {
//        // Mock객체 생성
//        mockUser = mock(User.class);
//
//        CouponType mockCouponType = mock(CouponType.class);
//
//        Mockito.when(mockUser.getUserId()).thenReturn(1L);
//
//        //마사용 쿠폰 객체
//        Coupon coupon1 = Coupon.builder()
//                .user(mockUser)
//                .issueDate(LocalDateTime.now().minusDays(3))
//                .deadline(LocalDateTime.now().plusDays(1))
//                .couponStatus(CouponStatus.UNUSED)
//                .couponType(mockCouponType)
//                .build();
//
//        //사용 쿠폰 객체
//        Coupon coupon2 = Coupon.builder()
//                .user(mockUser)
//                .issueDate(LocalDateTime.now().minusDays(3))
//                .deadline(LocalDateTime.now().plusDays(1))
//                .useDate(LocalDateTime.now().minusDays(1))
//                .couponStatus(CouponStatus.USED)
//                .couponType(mockCouponType)
//                .build();
//
//        //만료 쿠폰 객체
//        Coupon coupon3 = Coupon.builder()
//                .user(mockUser)
//                .issueDate(LocalDateTime.now().minusDays(3))
//                .deadline(LocalDateTime.now().minusDays(1))
//                .couponStatus(CouponStatus.EXPIRED)
//                .couponType(mockCouponType)
//                .build();
//
//        couponRepository.save(coupon1);
//        couponRepository.save(coupon2);
//        couponRepository.save(coupon3);
//    }
//
//    @Test
//    void testFindByUserUserId() {
//        List<Coupon> coupons = couponRepository.findByUserUserId(mockUser.getUserId());
//
//        Assertions.assertThat(coupons).isNotEmpty();
//        Assertions.assertThat(coupons).hasSize(3);
//
//    }
//}