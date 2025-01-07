package com.simsimbookstore.apiserver.coupons.coupon.service.impl;

import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.bookcategory.entity.BookCategory;
import com.simsimbookstore.apiserver.books.bookcategory.repository.BookCategoryRepository;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.DiscountAmountResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.FixCouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.RateCouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.exception.AlreadyCouponUsed;
import com.simsimbookstore.apiserver.coupons.exception.InsufficientOrderAmountException;
import com.simsimbookstore.apiserver.coupons.coupon.repository.CouponRepository;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import com.simsimbookstore.apiserver.coupons.coupontype.repository.CouponTypeRepository;
import com.simsimbookstore.apiserver.exception.AlreadyExistException;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @Mock
    private BookCategoryRepository bookCategoryRepository;
    @InjectMocks
    private CouponServiceImpl couponService;

    private User user1;
    private User user2;
    private CouponPolicy couponPolicyFix;
    private CouponPolicy couponPolicyRate;
    private BookCoupon bookCoupon;
    private CategoryCoupon categoryCoupon;
    private AllCoupon allCoupon;
    private Book book;

    // 고정된 현재 시간을 사용하여 테스트의 일관성을 유지
    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2024, 1, 1, 0, 0);

    @BeforeEach
    void setUp() {
        user1 = User.builder().userId(1L).userName("user1").build();
        user2 = User.builder().userId(2L).userName("user2").build();

        couponPolicyFix = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Fixed Discount Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(new BigDecimal("5000"))
                .discountRate(null)
                .maxDiscountAmount(null)
                .minOrderAmount(new BigDecimal("20000"))
                .policyDescription("Fixed discount policy description")
                .build();

        couponPolicyRate = CouponPolicy.builder()
                .couponPolicyId(2L)
                .couponPolicyName("Rate Discount Policy")
                .discountType(DisCountType.RATE)
                .discountPrice(null)
                .discountRate(new BigDecimal("20")) //20%
                .maxDiscountAmount(new BigDecimal("8000"))
                .minOrderAmount(new BigDecimal("20000"))
                .policyDescription("Rate discount policy description")
                .build();

        book = Book.builder().bookId(200L).saleprice(new BigDecimal("18000")).build();

        // BookCoupon with period
        bookCoupon = BookCoupon.builder()
                .couponTypeId(100L)
                .couponTypeName("Book Discount")
                .period(30) // 발급일로부터 30일
                .deadline(null)
                .stacking(true)
                .couponPolicy(couponPolicyFix)
                .book(book)
                .build();
        //CategoryCoupon with deadline
        categoryCoupon = CategoryCoupon.builder()
                .couponTypeId(101L)
                .couponTypeName("Category Discount")
                .period(0)
                .deadline(FIXED_NOW.plusDays(45))
                .stacking(false)
                .couponPolicy(couponPolicyRate)
                .category(Category.builder().categoryId(1L).categoryName("Test Category").build())
                .build();
        // AllCoupon with deadline
        allCoupon = AllCoupon.builder()
                .couponTypeId(102L)
                .couponTypeName("All Category Discount")
                .period(0)
                .deadline(FIXED_NOW.plusDays(15))
                .stacking(false)
                .couponPolicy(couponPolicyRate)
                .build();

    }
    @Test
    @DisplayName("getCouponById : 유효한 ID로 쿠폰을 성공적으로 조회")
    void getCouponById_ValidId_ReturnCoupon() {
        Long couponId = 1001L;

        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(allCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(allCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        CouponResponseDto responseDto = couponService.getCouponById(couponId);

        assertNotNull(responseDto);
        assertEquals(couponId, responseDto.getCouponId());
        assertEquals("All Category Discount", responseDto.getCouponTypeName());
        assertEquals("Rate Discount Policy", responseDto.getDisCountType() == DisCountType.FIX ? "Fixed Discount Policy" : "Rate Discount Policy");
        assertEquals(CouponStatus.UNUSED, responseDto.getCouponStatus());
        assertEquals(allCoupon.getDeadline(), responseDto.getDeadline());

        assertTrue(responseDto instanceof RateCouponResponseDto);

        RateCouponResponseDto rateDto = (RateCouponResponseDto) responseDto;
        assertEquals(DisCountType.RATE, rateDto.getDisCountType());
        assertEquals(new BigDecimal("20"), rateDto.getDiscountRate());
        assertEquals(new BigDecimal("8000"), rateDto.getMaxDiscountAmount());
        assertEquals(new BigDecimal("20000"), rateDto.getMinOrderAmount());
    }

    @Test
    @DisplayName("getCouponById : 유효하지 않은 ID로 조회 시 NotFoundException")
    void getCouponById_InvalidId_ThrowsNotFoundException() {
        Long couponId = 9999L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            couponService.getCouponById(couponId);
        });

        assertEquals("쿠폰(id:9999)이 존재하지 않습니다.",notFoundException.getMessage());

    }

    @Test
    @DisplayName("getUnusedCouponByCouponType : 회원이 존재하지 않음")
    void getUnusedCouponByCouponType_UserNotFound_ThrowsNotFoundException() {
        Long userId = 3L; // 존재하지 않은 회원 id
        Long couponTypeId = 100L;

        // 사용자 조회 시 Optional.empty() 반환
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.getUnusedCouponByCouponType(userId, couponTypeId);
        });

        assertEquals("회원(id:3)이 존재하지 않습니다.", exception.getMessage());

        // 쿠폰 타입 조회가 호출되지 않았는지 검증
        verify(couponTypeRepository, never()).findById(anyLong());
        // UNUSED 쿠폰 조회가 호출되지 않았는지 검증
        verify(couponRepository, never()).findUnusedCouponByUserAndType(anyLong(), anyLong());
    }

    @Test
    @DisplayName("getUnusedCouponByCouponType : 쿠폰 타입이 존재하지 않음")
    void getUnusedCouponByCouponType_CouponTypeNotFound_ThrowsNotFoundException() {
        Long userId = 1L; // 존재하는 회원 id
        Long couponTypeId = 999L; // 존재하지 않는 쿠폰 타입 id

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 타입 조회 시 Optional.empty() 반환
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.getUnusedCouponByCouponType(userId, couponTypeId);
        });

        assertEquals("쿠폰 정책(id:999)이 존재하지 않습니다.", exception.getMessage());

        // UNUSED 쿠폰 조회가 호출되지 않았는지 검증
        verify(couponRepository, never()).findUnusedCouponByUserAndType(anyLong(), anyLong());
    }

    @Test
    @DisplayName("getUnusedCouponByCouponType : UNUSED 쿠폰이 존재함 (FIX 할인)")
    void getUnusedCouponByCouponType_UnusedCouponExists_ReturnsFixCouponResponseDto() {
        Long userId = 1L;
        Long couponTypeId = 100L; // BookCoupon with FIX discount

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 타입 조회 시 정상적으로 반환
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(bookCoupon));

        // UNUSED 쿠폰 존재
        Coupon unusedCoupon = Coupon.builder()
                .couponId(1001L)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findUnusedCouponByUserAndType(userId, couponTypeId)).thenReturn(Optional.of(unusedCoupon));

        CouponResponseDto responseDto = couponService.getUnusedCouponByCouponType(userId, couponTypeId);

        assertNotNull(responseDto);
        assertEquals(1001L, responseDto.getCouponId());
        assertEquals("Book Discount", responseDto.getCouponTypeName());
        assertEquals("Fixed Discount Policy", responseDto.getDisCountType() == DisCountType.FIX ? "Fixed Discount Policy" : "Rate Discount Policy");
        assertEquals(CouponStatus.UNUSED, responseDto.getCouponStatus());
        assertEquals(getDeadline(bookCoupon), responseDto.getDeadline());

        // Assert the specific DTO type
        assertTrue(responseDto instanceof FixCouponResponseDto);

        // Cast and assert specific fields
        FixCouponResponseDto fixDto = (FixCouponResponseDto) responseDto;
        assertEquals(DisCountType.FIX, fixDto.getDisCountType());
        assertEquals(new BigDecimal("5000"), fixDto.getDiscountPrice());
        assertEquals(new BigDecimal("20000"), fixDto.getMinOrderAmount());
    }

    @Test
    @DisplayName("getUnusedCouponByCouponType : UNUSED 쿠폰이 존재함 (RATE 할인)")
    void getUnusedCouponByCouponType_UnusedCouponExists_ReturnsRateCouponResponseDto() {
        Long userId = 1L;
        Long couponTypeId = 102L; // AllCoupon with RATE discount

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 타입 조회 시 정상적으로 반환
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(allCoupon));

        // UNUSED 쿠폰 존재
        Coupon unusedCoupon = Coupon.builder()
                .couponId(1002L)
                .user(user1)
                .couponType(allCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(allCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findUnusedCouponByUserAndType(userId, couponTypeId)).thenReturn(Optional.of(unusedCoupon));

        CouponResponseDto responseDto = couponService.getUnusedCouponByCouponType(userId, couponTypeId);

        assertNotNull(responseDto);
        assertEquals(1002L, responseDto.getCouponId());
        assertEquals("All Category Discount", responseDto.getCouponTypeName());
        assertEquals("Rate Discount Policy", responseDto.getDisCountType() == DisCountType.FIX ? "Fixed Discount Policy" : "Rate Discount Policy");
        assertEquals(CouponStatus.UNUSED, responseDto.getCouponStatus());
        assertEquals(getDeadline(allCoupon), responseDto.getDeadline());

        // Assert the specific DTO type
        assertTrue(responseDto instanceof RateCouponResponseDto);

        // Cast and assert specific fields
        RateCouponResponseDto rateDto = (RateCouponResponseDto) responseDto;
        assertEquals(DisCountType.RATE, rateDto.getDisCountType());
        assertEquals(new BigDecimal("20"), rateDto.getDiscountRate());
        assertEquals(new BigDecimal("8000"), rateDto.getMaxDiscountAmount());
        assertEquals(new BigDecimal("20000"), rateDto.getMinOrderAmount());
    }


    @Test
    @DisplayName("getCoupons : 유효한 입력으로 유저의 모든 쿠폰을 성공적으로 조회")
    void getCoupons_ValidInput_ReturnsCouponPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 1L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // Mock Coupons
        Coupon coupon1 = Coupon.builder()
                .couponId(1001L)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        Coupon coupon2 = Coupon.builder()
                .couponId(1002L)
                .user(user1)
                .couponType(categoryCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(categoryCoupon))
                .couponStatus(CouponStatus.USED)
                .useDate(FIXED_NOW.plusDays(5))
                .build();

        List<Coupon> coupons = Arrays.asList(coupon1, coupon2);
        Page<Coupon> couponPage = new PageImpl<>(coupons, pageable, coupons.size());

        when(couponRepository.findByUserUserId(pageable, userId)).thenReturn(couponPage);

        Page<CouponResponseDto> responsePage = couponService.getCoupons(pageable, userId);

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());

        // First Coupon
        CouponResponseDto firstDto = responsePage.getContent().get(0);
        assertTrue(firstDto instanceof FixCouponResponseDto);
        FixCouponResponseDto fixDto = (FixCouponResponseDto) firstDto;
        assertEquals("Book Discount", fixDto.getCouponTypeName());
        assertEquals(DisCountType.FIX, fixDto.getDisCountType());
        assertEquals(new BigDecimal("5000"), fixDto.getDiscountPrice());
        assertEquals(new BigDecimal("20000"), fixDto.getMinOrderAmount());

        // Second Coupon
        CouponResponseDto secondDto = responsePage.getContent().get(1);
        assertTrue(secondDto instanceof RateCouponResponseDto);
        RateCouponResponseDto rateDto = (RateCouponResponseDto) secondDto;
        assertEquals("Category Discount", rateDto.getCouponTypeName());
        assertEquals(DisCountType.RATE, rateDto.getDisCountType());
        assertEquals(new BigDecimal("20"), rateDto.getDiscountRate());
        assertEquals(new BigDecimal("8000"), rateDto.getMaxDiscountAmount());
        assertEquals(new BigDecimal("20000"), rateDto.getMinOrderAmount());
    }

    @Test
    @DisplayName("getCoupons : 유효하지 않은 유저 ID로 조회 시 NotFoundException 발생")
    void getCoupons_UserNotFound_ThrowsNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 3L; // 존재하지 않는 유저 ID

        // 사용자 조회 시 Optional.empty() 반환
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.getCoupons(pageable, userId);
        });

        assertEquals("회원(id:3)이 존재하지 않습니다.", exception.getMessage());

        // 쿠폰 조회가 호출되지 않았는지 검증
        verify(couponRepository, never()).findByUserUserId(any(Pageable.class), anyLong());
    }

    // ### 2.4. `getUnusedCoupons` 메서드 테스트

    @Test
    @DisplayName("getUnusedCoupons : 유효한 입력으로 유저의 UNUSED 쿠폰을 성공적으로 조회")
    void getUnusedCoupons_ValidInput_ReturnsUnusedCouponPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 1L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // UNUSED 쿠폰 존재
        Coupon coupon1 = Coupon.builder()
                .couponId(1001L)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        List<Coupon> coupons = Collections.singletonList(coupon1);
        Page<Coupon> couponPage = new PageImpl<>(coupons, pageable, coupons.size());

        when(couponRepository.findByUserUserIdAndCouponStatus(pageable, userId, CouponStatus.UNUSED))
                .thenReturn(couponPage);

        Page<CouponResponseDto> responsePage = couponService.getUnusedCoupons(pageable, userId);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());

        // UNUSED Coupon
        CouponResponseDto dto = responsePage.getContent().get(0);
        assertTrue(dto instanceof FixCouponResponseDto);
        FixCouponResponseDto fixDto = (FixCouponResponseDto) dto;
        assertEquals("Book Discount", fixDto.getCouponTypeName());
        assertEquals(DisCountType.FIX, fixDto.getDisCountType());
        assertEquals(new BigDecimal("5000"), fixDto.getDiscountPrice());
        assertEquals(new BigDecimal("20000"), fixDto.getMinOrderAmount());
    }

    @Test
    @DisplayName("getUnusedCoupons : 유효하지 않은 유저 ID로 조회 시 NotFoundException 발생")
    void getUnusedCoupons_UserNotFound_ThrowsNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 3L; // 존재하지 않는 유저 ID

        // 사용자 조회 시 Optional.empty() 반환
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.getUnusedCoupons(pageable, userId);
        });

        assertEquals("회원(id:3)이 존재하지 않습니다.", exception.getMessage());

        // UNUSED 쿠폰 조회가 호출되지 않았는지 검증
        verify(couponRepository, never()).findByUserUserIdAndCouponStatus(any(Pageable.class), anyLong(), any(CouponStatus.class));
    }

    // ### 2.5. `getEligibleCoupons` 메서드 테스트

    @Test
    @DisplayName("getEligibleCoupons : 유효한 입력으로 적용 가능한 쿠폰을 성공적으로 조회")
    void getEligibleCoupons_ValidInput_ReturnsEligibleCoupons() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 1L;
        Long bookId = 200L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 책 조회 시 정상적으로 반환
        when(bookRepository.findByBookId(bookId)).thenReturn(Optional.of(book));

        // Eligible Coupons 존재
        Coupon eligibleCoupon1 = Coupon.builder()
                .couponId(1001L)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        Coupon eligibleCoupon2 = Coupon.builder()
                .couponId(1002L)
                .user(user1)
                .couponType(categoryCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(categoryCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        List<Coupon> eligibleCoupons = Arrays.asList(eligibleCoupon1, eligibleCoupon2);
        Page<Coupon> couponPage = new PageImpl<>(eligibleCoupons, pageable, eligibleCoupons.size());

        when(couponRepository.findEligibleCouponToBook(pageable, userId, bookId)).thenReturn(couponPage);

        Page<CouponResponseDto> responsePage = couponService.getEligibleCoupons(pageable, userId, bookId);

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());

        // First Eligible Coupon
        CouponResponseDto firstDto = responsePage.getContent().get(0);
        assertTrue(firstDto instanceof FixCouponResponseDto);
        FixCouponResponseDto fixDto = (FixCouponResponseDto) firstDto;
        assertEquals("Book Discount", fixDto.getCouponTypeName());
        assertEquals(DisCountType.FIX, fixDto.getDisCountType());
        assertEquals(new BigDecimal("5000"), fixDto.getDiscountPrice());
        assertEquals(new BigDecimal("20000"), fixDto.getMinOrderAmount());

        // Second Eligible Coupon
        CouponResponseDto secondDto = responsePage.getContent().get(1);
        assertTrue(secondDto instanceof RateCouponResponseDto);
        RateCouponResponseDto rateDto = (RateCouponResponseDto) secondDto;
        assertEquals("Category Discount", rateDto.getCouponTypeName());
        assertEquals(DisCountType.RATE, rateDto.getDisCountType());
        assertEquals(new BigDecimal("20"), rateDto.getDiscountRate());
        assertEquals(new BigDecimal("8000"), rateDto.getMaxDiscountAmount());
        assertEquals(new BigDecimal("20000"), rateDto.getMinOrderAmount());
    }

    @Test
    @DisplayName("getEligibleCoupons : 유저가 존재하지 않을 때 NotFoundException 발생")
    void getEligibleCoupons_UserNotFound_ThrowsNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 3L; // 존재하지 않는 유저 ID
        Long bookId = 200L;

        // 사용자 조회 시 Optional.empty() 반환
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.getEligibleCoupons(pageable, userId, bookId);
        });

        assertEquals("회원(id:3)이 존재하지 않습니다.", exception.getMessage());

        // 책 조회 및 쿠폰 조회가 호출되지 않았는지 검증
        verify(bookRepository, never()).findByBookId(anyLong());
        verify(couponRepository, never()).findEligibleCouponToBook(any(Pageable.class), anyLong(), anyLong());
    }

    @Test
    @DisplayName("getEligibleCoupons : 책이 존재하지 않을 때 NotFoundException 발생")
    void getEligibleCoupons_BookNotFound_ThrowsNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 1L;
        Long bookId = 9999L; // 존재하지 않는 책 ID

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 책 조회 시 Optional.empty() 반환
        when(bookRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.getEligibleCoupons(pageable, userId, bookId);
        });

        assertEquals("도서(id:9999)이 존재하지 않습니다.", exception.getMessage());

        // 쿠폰 조회가 호출되지 않았는지 검증
        verify(couponRepository, never()).findEligibleCouponToBook(any(Pageable.class), anyLong(), anyLong());
    }

    // ### 2.6. `issueCoupons` 메서드 테스트

    /**
     * 2.6.1. 모든 사용자가 쿠폰을 정상적으로 발급받는 경우
     */
    @Test
    @DisplayName("issueCoupons : 모든 사용자가 정상적으로 쿠폰을 발급받음")
    void issueCoupons_AllValid_IssuesCoupons() {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        Long couponTypeId = 100L;

        // 쿠폰 타입 조회 시 정상적으로 반환
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(bookCoupon));

        // 사용자 조회 시 정상적으로 반환
        User user3 = User.builder()
                .userId(3L)
                .userName("user3")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

        // UNUSED 쿠폰 조회 시 Optional.empty() 반환
        when(couponRepository.findUnusedCouponByUserAndType(anyLong(), eq(couponTypeId)))
                .thenReturn(Optional.empty());

        // 쿠폰 저장 시 반환될 객체 설정
        Coupon coupon1 = Coupon.builder()
                .couponId(1001L)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        Coupon coupon2 = Coupon.builder()
                .couponId(1002L)
                .user(user2)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        Coupon coupon3 = Coupon.builder()
                .couponId(1003L)
                .user(user3)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        // 쿠폰 저장 Mock 설정
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon1, coupon2, coupon3);

        List<Long> issuedCouponIds = couponService.issueCoupons(userIds, couponTypeId);

        assertNotNull(issuedCouponIds);
        assertEquals(3, issuedCouponIds.size());

        // First Issued Coupon
        Long firstId = issuedCouponIds.get(0);
        assertEquals(1001L, firstId);

        // Second Issued Coupon
        Long secondId = issuedCouponIds.get(1);
        assertEquals(1002L, secondId);

        // Third Issued Coupon
        Long thirdId = issuedCouponIds.get(2);
        assertEquals(1003L, thirdId);

        // 쿠폰 저장이 3번 호출되었는지 검증
        verify(couponRepository, times(3)).save(any(Coupon.class));
    }

    /**
     * 2.6.2. 일부 사용자가 이미 UNUSED 쿠폰을 보유한 경우
     */
    @Test
    @DisplayName("issueCoupons : 일부 사용자가 이미 UNUSED 쿠폰을 보유하고 있어 예외 발생")
    void issueCoupons_UserAlreadyHasUnusedCoupon_ThrowsAlreadyExistException() {
        List<Long> userIds = Arrays.asList(1L, 2L);
        Long couponTypeId = 100L;

        // 쿠폰 타입 조회 시 정상적으로 반환
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(bookCoupon));

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        // User 1는 UNUSED 쿠폰을 보유하지 않음
        when(couponRepository.findUnusedCouponByUserAndType(1L, couponTypeId))
                .thenReturn(Optional.empty());

        // User 2는 이미 UNUSED 쿠폰을 보유하고 있음
        Coupon existingCoupon = Coupon.builder()
                .couponId(1001L)
                .user(user2) // User 2가 쿠폰을 보유
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findUnusedCouponByUserAndType(2L, couponTypeId))
                .thenReturn(Optional.of(existingCoupon));

        // save() 메서드 스텁 설정: User 1의 쿠폰 저장 시 반환될 객체
        Coupon savedCouponUser1 = Coupon.builder()
                .couponId(1002L)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.save(any(Coupon.class))).thenReturn(savedCouponUser1);

        // 예외 발생을 검증
        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () -> {
            couponService.issueCoupons(userIds, couponTypeId);
        });

        assertEquals("회원(id:2)은 해당 쿠폰타입(id:100)의 UNUSED 쿠폰을 가지고 있습니다.", exception.getMessage());

        // 쿠폰 저장이 1번 호출되었는지 검증 (User 1)
        verify(couponRepository, times(1)).save(any(Coupon.class));

        // User 1과 User 2에 대한 findUnusedCouponByUserAndType 호출 검증
        verify(couponRepository, times(1)).findUnusedCouponByUserAndType(1L, couponTypeId);
        verify(couponRepository, times(1)).findUnusedCouponByUserAndType(2L, couponTypeId);
    }



    /**
     * 2.6.3. 사용자를 찾을 수 없는 경우
     */
    @Test
    @DisplayName("issueCoupons : 사용자가 존재하지 않아 NotFoundException 발생")
    void issueCoupons_UserNotFound_ThrowsNotFoundException() {
        List<Long> userIds = Arrays.asList(1L, 3L);
        Long couponTypeId = 100L;

        // 쿠폰 타입 조회 시 정상적으로 반환
        when(couponTypeRepository.findById(couponTypeId)).thenReturn(Optional.of(bookCoupon));

        // 사용자 조회 시
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(3L)).thenReturn(Optional.empty()); // User 3는 존재하지 않음

        // save() 메서드 스텁 설정: User 1의 쿠폰 저장 시 반환될 객체
        Coupon savedCouponUser1 = Coupon.builder()
                .couponId(1002L)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.save(any(Coupon.class))).thenReturn(savedCouponUser1);

        // 예외 발생을 검증
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.issueCoupons(userIds, couponTypeId);
        });

        assertEquals("회원(id:3)이 존재하지 않습니다.", exception.getMessage());

        // 쿠폰 저장이 1번 호출되었는지 검증 (User 1)
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    // ### 2.7. `useCoupon` 메서드 테스트

    @Test
    @DisplayName("useCoupon : 유효한 입력으로 쿠폰을 성공적으로 사용")
    void useCoupon_ValidInput_UsesCoupon() {
        Long userId = 1L;
        Long couponId = 1001L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 조회 시 정상적으로 반환
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findByUserUserIdAndCouponId(userId, couponId)).thenReturn(Optional.of(coupon));

        // 쿠폰 상태 변경 후 save() 호출 여부 확인 (실제로는 save() 호출 필요 없음)
        // 따라서 save() 메서드는 Mock 설정에서 제외하거나, 호출 여부를 검증하지 않음
        // Mockito에서는 더티 체킹을 통해 변경 사항이 반영되므로 save() 호출이 필요 없음을 반영

        CouponResponseDto responseDto = couponService.useCoupon(userId, couponId);

        assertNotNull(responseDto);
        assertEquals(CouponStatus.USED, responseDto.getCouponStatus());

        // Assert the specific DTO type
        assertTrue(responseDto instanceof FixCouponResponseDto);

        // Cast and assert specific fields
        FixCouponResponseDto fixDto = (FixCouponResponseDto) responseDto;
        assertEquals(DisCountType.FIX, fixDto.getDisCountType());
        assertEquals(new BigDecimal("5000"), fixDto.getDiscountPrice());
        assertEquals(new BigDecimal("20000"), fixDto.getMinOrderAmount());

        // 쿠폰 조회이 호출되었는지 검증
        verify(couponRepository, times(1)).findByUserUserIdAndCouponId(userId, couponId);
        // save() 메서드는 호출되지 않았음을 검증
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    @DisplayName("useCoupon : 이미 사용된 쿠폰을 사용하려 할 때  AlreadyCouponUsed 발생")
    void useCoupon_CouponAlreadyUsed_ThrowsAlreadyExistException() {
        Long userId = 1L;
        Long couponId = 1002L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 조회 시 이미 사용된 쿠폰 반환
        Coupon usedCoupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.USED)
                .useDate(FIXED_NOW.plusDays(2))
                .build();

        when(couponRepository.findByUserUserIdAndCouponId(userId, couponId)).thenReturn(Optional.of(usedCoupon));

        AlreadyCouponUsed exception = assertThrows(AlreadyCouponUsed.class, () -> {
            couponService.useCoupon(userId, couponId);
        });

        assertEquals("회원(id:1)의 쿠폰(id:1002)은 이미 사용된 쿠폰입니다.", exception.getMessage());

        // 쿠폰 저장이 호출되지 않았는지 검증
        verify(couponRepository, times(1)).findByUserUserIdAndCouponId(userId, couponId);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    @DisplayName("useCoupon : 존재하지 않는 쿠폰을 사용하려 할 때 NotFoundException 발생")
    void useCoupon_CouponNotFound_ThrowsNotFoundException() {
        Long userId = 1L;
        Long couponId = 9999L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 조회 시 Optional.empty() 반환
        when(couponRepository.findByUserUserIdAndCouponId(userId, couponId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.useCoupon(userId, couponId);
        });

        assertEquals("회원(id:1)은 쿠폰(id:9999)을 가지고 있지 않습니다.", exception.getMessage());

        // 쿠폰 저장이 호출되지 않았는지 검증
        verify(couponRepository, times(1)).findByUserUserIdAndCouponId(userId, couponId);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    // ### 2.8. `deleteCoupon` 메서드 테스트

    @Test
    @DisplayName("deleteCoupon : 유효한 입력으로 쿠폰을 성공적으로 삭제")
    void deleteCoupon_ValidInput_DeletesCoupon() {
        Long userId = 1L;
        Long couponId = 1001L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 조회 시 정상적으로 반환
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findByUserUserIdAndCouponId(userId, couponId)).thenReturn(Optional.of(coupon));

        // delete 메서드는 반환값이 없으므로, 아무 동작도 하지 않음
        doNothing().when(couponRepository).delete(coupon);

        // 삭제 수행
        couponService.deleteCoupon(userId, couponId);

        // 쿠폰 조회 및 삭제가 호출되었는지 검증
        verify(couponRepository, times(1)).findByUserUserIdAndCouponId(userId, couponId);
        verify(couponRepository, times(1)).delete(coupon);
    }

    @Test
    @DisplayName("deleteCoupon : 존재하지 않는 쿠폰을 삭제하려 할 때 NotFoundException 발생")
    void deleteCoupon_CouponNotFound_ThrowsNotFoundException() {
        Long userId = 1L;
        Long couponId = 9999L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 조회 시 Optional.empty() 반환
        when(couponRepository.findByUserUserIdAndCouponId(userId, couponId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.deleteCoupon(userId, couponId);
        });

        assertEquals("회원(id:1)은 쿠폰(id:9999)을 가지고 있지 않습니다.", exception.getMessage());

        // 쿠폰 삭제가 호출되지 않았는지 검증
        verify(couponRepository, times(1)).findByUserUserIdAndCouponId(userId, couponId);
        verify(couponRepository, never()).delete(any(Coupon.class));
    }

    // ### 2.9. `calDiscountAmount` 메서드 테스트

    /**
     * 2.9.1. FIX 할인 적용 테스트
     */
    @Test
    @DisplayName("calDiscountAmount : FIX 할인 적용 시 올바른 할인 금액 계산")
    void calDiscountAmount_FixDiscount_ReturnsCorrectDiscount() {
        Long bookId = 200L;
        Integer quantity = 3;
        Long couponId = 1001L;

        // 책 조회 시 정상적으로 반환
        when(bookRepository.findByBookId(bookId)).thenReturn(Optional.of(book));

        // 쿠폰 조회 시 정상적으로 반환
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

//        BookResponseDto bookResponseDto = BookResponseDto.builder()
//                .categoryList(Arrays.asList(Arrays.asList(CategoryResponseDto.builder().categoryId(1L).categoryName("Test Category").build())))
//                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
//        when(bookRepository.getBookDetail(null, bookId)).thenReturn(bookResponseDto);
        when(bookCategoryRepository.findByBookId(bookId)).thenReturn(List.of(BookCategory.builder().catagory(Category.builder().categoryId(1L).build()).build()));


        DiscountAmountResponseDto responseDto = couponService.calDiscountAmount(bookId, quantity, couponId);

        assertNotNull(responseDto);
        assertEquals(bookId, responseDto.getBookId());
        assertEquals(quantity, responseDto.getQuantity());
        assertEquals(new BigDecimal("5000"), responseDto.getDiscountAmount());

        BigDecimal beforeDiscount = book.getSaleprice().multiply(new BigDecimal(quantity)); // 18000 * 3 = 54000
        assertEquals(new BigDecimal("54000"), responseDto.getBeforeCouponDiscount());

        BigDecimal afterDiscount = beforeDiscount.subtract(new BigDecimal("5000")); // 54000 - 5000 = 49000
        assertEquals(new BigDecimal("49000"), responseDto.getAfterCouponDiscount());
    }

    /**
     * 2.9.2. RATE 할인 적용 테스트
     */
    @Test
    @DisplayName("calDiscountAmount : RATE 할인 적용 시 올바른 할인 금액 계산")
    void calDiscountAmount_RateDiscount_ReturnsCorrectDiscount() {
        Long bookId = 200L;
        Integer quantity = 2;
        Long couponId = 1002L;

        // 책 조회 시 정상적으로 반환
        when(bookRepository.findByBookId(bookId)).thenReturn(Optional.of(book));

        // 쿠폰 조회 시 정상적으로 반환
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(categoryCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(categoryCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

//        BookResponseDto bookResponseDto = BookResponseDto.builder()
//                .categoryList(Arrays.asList(Arrays.asList(CategoryResponseDto.builder().categoryId(1L).categoryName("Test Category").build())))
//                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
//        when(bookRepository.getBookDetail(null, bookId)).thenReturn(bookResponseDto);
        when(bookCategoryRepository.findByBookId(bookId)).thenReturn(List.of(BookCategory.builder().catagory(Category.builder().categoryId(1L).build()).build()));
        DiscountAmountResponseDto responseDto = couponService.calDiscountAmount(bookId, quantity, couponId);

        assertNotNull(responseDto);
        assertEquals(bookId, responseDto.getBookId());
        assertEquals(quantity, responseDto.getQuantity());

        BigDecimal beforeDiscount = book.getSaleprice().multiply(new BigDecimal(quantity)); // 18000 * 2 = 36000
        assertEquals(new BigDecimal("36000"), responseDto.getBeforeCouponDiscount());

        // 20% of 36000 = 7200, maxDiscountAmount = 8000, so discountAmount = 7200
        BigDecimal calculatedDiscount = beforeDiscount.multiply(couponPolicyRate.getDiscountRate())
                .divide(new BigDecimal("100"), 1, RoundingMode.HALF_UP); // 7200.0

        BigDecimal expectedDiscount = calculatedDiscount.min(couponPolicyRate.getMaxDiscountAmount()); // 7200.0

        assertEquals(new BigDecimal("7200.0"), responseDto.getDiscountAmount());

        BigDecimal afterDiscount = beforeDiscount.subtract(expectedDiscount); // 36000 - 7200 = 28800
        assertEquals(new BigDecimal("28800.0"), responseDto.getAfterCouponDiscount());
    }

    /**
     * 2.9.3. 최소 주문 금액 미달 시 예외 테스트
     */
    @Test
    @DisplayName("calDiscountAmount : 주문 금액이 최소 주문 금액에 미달할 때 InsufficientOrderAmountException 발생")
    void calDiscountAmount_InsufficientOrderAmount_ThrowsInsufficientOrderAmountException() {
        Long bookId = 200L;
        Integer quantity = 1;
        Long couponId = 1003L;

//        BookResponseDto bookResponseDto = BookResponseDto.builder()
//                .categoryList(Arrays.asList(Arrays.asList(CategoryResponseDto.builder().categoryId(1L).categoryName("Test Category").build())))
//                .build();

        // 책 조회 시 정상적으로 반환
        when(bookRepository.findByBookId(bookId)).thenReturn(Optional.of(book));
//        when(bookRepository.getBookDetail(null, bookId)).thenReturn(bookResponseDto);



        // 쿠폰 조회 시 정상적으로 반환
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // 주문 금액 = 18000, 최소 주문 금액 = 20000
        InsufficientOrderAmountException exception = assertThrows(InsufficientOrderAmountException.class, () -> {
            couponService.calDiscountAmount(bookId, quantity, couponId);
        });

        assertEquals("주문 금액(18000)이 쿠폰 최소 주문 금액(20000)에 못미칩니다.", exception.getMessage());
    }

    @Test
    @DisplayName("calDiscountAmount : 책이 존재하지 않을 때 NotFoundException 발생")
    void calDiscountAmount_BookNotFound_ThrowsNotFoundException() {
        Long bookId = 9999L; // 존재하지 않는 책 ID
        Integer quantity = 2;
        Long couponId = 1001L;

        // 책 조회 시 Optional.empty() 반환
        when(bookRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.calDiscountAmount(bookId, quantity, couponId);
        });

        assertEquals("책(id:9999)이 존재하지 않습니다.", exception.getMessage());

        // 쿠폰 조회가 호출되지 않았는지 검증
        verify(couponRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("calDiscountAmount : 쿠폰이 존재하지 않을 때 NotFoundException 발생")
    void calDiscountAmount_CouponNotFound_ThrowsNotFoundException() {
        Long bookId = 200L;
        Integer quantity = 2;
        Long couponId = 9999L; // 존재하지 않는 쿠폰 ID

        // 책 조회 시 정상적으로 반환
        when(bookRepository.findByBookId(bookId)).thenReturn(Optional.of(book));

        // 쿠폰 조회 시 Optional.empty() 반환
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.calDiscountAmount(bookId, quantity, couponId);
        });

        assertEquals("쿠폰(id:9999)이 존재하지 않습니다.", exception.getMessage());

        // 할인 금액 계산이 호출되지 않았는지 검증
        // (별도의 검증은 필요 없으므로 생략)
    }

    // ### 2.10. `expireCoupon` 메서드 테스트

    @Test
    @DisplayName("expireCoupon : 유효한 입력으로 쿠폰을 성공적으로 만료시킴")
    void expireCoupon_ValidInput_ExpiresCoupon() {
        Long userId = 1L;
        Long couponId = 1001L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 조회 시 정상적으로 반환
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .user(user1)
                .couponType(bookCoupon)
                .issueDate(FIXED_NOW)
                .deadline(getDeadline(bookCoupon))
                .couponStatus(CouponStatus.UNUSED)
                .useDate(null)
                .build();

        when(couponRepository.findByUserUserIdAndCouponId(userId, couponId)).thenReturn(Optional.of(coupon));

        // 더 이상 save() 스텁 설정 및 검증이 필요 없음

        // 서비스 메서드 호출
        CouponResponseDto responseDto = couponService.expireCoupon(userId, couponId);

        // 응답 검증
        assertNotNull(responseDto);
        assertEquals(CouponStatus.EXPIRED, responseDto.getCouponStatus());

        // 구체적인 DTO 타입 검증
        assertTrue(responseDto instanceof FixCouponResponseDto);

        // DTO의 구체적인 필드 검증
        FixCouponResponseDto fixDto = (FixCouponResponseDto) responseDto;
        assertEquals(DisCountType.FIX, fixDto.getDisCountType());
        assertEquals(new BigDecimal("5000"), fixDto.getDiscountPrice());
        assertEquals(new BigDecimal("20000"), fixDto.getMinOrderAmount());

        // 쿠폰 조회 호출 검증
        verify(couponRepository, times(1)).findByUserUserIdAndCouponId(userId, couponId);

        // save() 호출이 없음을 검증
        verify(couponRepository, never()).save(any(Coupon.class));
    }


    @Test
    @DisplayName("expireCoupon : 존재하지 않는 쿠폰을 만료시키려 할 때 NotFoundException 발생")
    void expireCoupon_CouponNotFound_ThrowsNotFoundException() {
        Long userId = 1L;
        Long couponId = 9999L;

        // 사용자 조회 시 정상적으로 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // 쿠폰 조회 시 Optional.empty() 반환
        when(couponRepository.findByUserUserIdAndCouponId(userId, couponId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            couponService.expireCoupon(userId, couponId);
        });

        assertEquals("회원(id:1)은 쿠폰(id:9999)을 가지고 있지 않습니다.", exception.getMessage());

        // 쿠폰 저장이 호출되지 않았는지 검증
        verify(couponRepository, times(1)).findByUserUserIdAndCouponId(userId, couponId);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    // Helper method to calculate deadline based on CouponType's period or deadline
    private LocalDateTime getDeadline(CouponType couponType) {
        if (couponType.getDeadline() != null) {
            return couponType.getDeadline();
        } else {
            return FIXED_NOW.plusDays(couponType.getPeriod());
        }
    }

}