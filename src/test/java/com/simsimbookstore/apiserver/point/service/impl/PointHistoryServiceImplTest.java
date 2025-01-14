package com.simsimbookstore.apiserver.point.service.impl;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.dto.ReviewPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.entity.ReviewPointManage;
import com.simsimbookstore.apiserver.point.repository.OrderPointManageRepository;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.repository.ReviewPointManageRepository;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import com.simsimbookstore.apiserver.reviews.reviewimage.repository.ReviewImagePathRepository;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointHistoryServiceImplTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private PointPolicyService pointPolicyService;

    @Mock
    private ReviewPointManageRepository reviewPointManageRepository;

    @Mock
    private OrderPointManageRepository orderPointManageRepository;

    @Mock
    private ReviewImagePathRepository reviewImagePathRepository;

    @InjectMocks
    private PointHistoryServiceImpl pointHistoryService;

    Long userId;
    Long bookId;
    Long orderId;
    Long reviewId;
    Review review;
    Order order;
    User user;
    Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        orderId = 1L;
        bookId = 1L;
        reviewId = 1L;
        ReviewRequestDTO dto = new ReviewRequestDTO(5, "Great Book", "Loved it!");
        user = User.builder().userId(userId).build();
        book = Book.builder().bookId(bookId).build();

        review = Review.builder()
                .reviewId(1L)
                .score(dto.getScore())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .book(book)
                .build();

        String generatedOrderNumber = "20250102-000001";

        order = Order.builder()
                .orderId(1L)
                .orderNumber(generatedOrderNumber)
                .totalPrice(BigDecimal.valueOf(100000))
                .pointUse(BigDecimal.valueOf(5000))
                .pointUse(BigDecimal.valueOf(100))
                .originalPrice(BigDecimal.valueOf(100000))
                .build();
    }


    @Test
    void testCalculateEarnOrderPoints() {

        Tier tier = Tier.STANDARD;
        BigDecimal originalPrice = BigDecimal.valueOf(100000);
        BigDecimal earningRate = BigDecimal.valueOf(0.01);

        when(userService.getUserTier(userId)).thenReturn(tier);
        when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(order));
        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD))
                .thenReturn(PointPolicyResponseDto.builder()
                        .earningMethod(PointPolicy.EarningMethod.ORDER_STANDARD)
                        .earningType(PointPolicy.EarningType.RATE)
                        .earningValue(BigDecimal.valueOf(0.01))
                        .available(true)
                        .description("order standard")
                        .build());

        BigDecimal earnedPoints = pointHistoryService.calculateEarnOrderPoints(
                new OrderPointCalculateRequestDto(userId, orderId));


        assertEquals(originalPrice.multiply(earningRate), earnedPoints);
        verify(userService).getUserTier(userId);
        verify(orderRepository).findById(orderId);
        verify(pointPolicyService).getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD);
    }

    @Test
    void testCalculateEarnReviewPoints() {

        BigDecimal earningValue = BigDecimal.valueOf(500);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.REVIEW))
                .thenReturn(PointPolicyResponseDto.builder()
                        .earningMethod(PointPolicy.EarningMethod.REVIEW)
                        .earningType(PointPolicy.EarningType.FIX)
                        .earningValue(earningValue)
                        .available(true)
                        .description("review points")
                        .build());

        BigDecimal earnedPoints = pointHistoryService.calculateEarnReviewPoints(
                new ReviewPointCalculateRequestDto(1L, reviewId));

        // then
        assertEquals(earningValue, earnedPoints);
        verify(reviewRepository).findById(reviewId);
        verify(pointPolicyService).getPolicy(PointPolicy.EarningMethod.REVIEW);
    }

    @Test
    void testCalculateEarnSignupPoints() {
        // given
        BigDecimal earningValue = BigDecimal.valueOf(5000);

        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.SIGNUP))
                .thenReturn(PointPolicyResponseDto.builder()
                        .earningMethod(PointPolicy.EarningMethod.SIGNUP)
                        .earningType(PointPolicy.EarningType.FIX)
                        .earningValue(earningValue)
                        .available(true)
                        .description("Signup points")
                        .build());

        // when
        BigDecimal earnedPoints = pointHistoryService.calculateEarnSignupPoints();

        // then
        assertEquals(earningValue, earnedPoints);
        verify(pointPolicyService).getPolicy(PointPolicy.EarningMethod.SIGNUP);
    }

    @Test
    void testReviewPoint() {

        BigDecimal earnPoints = BigDecimal.valueOf(500);

        User user = User.builder().userId(userId).build();
        Review review = Review.builder().reviewId(reviewId).content("Great review").build();
        PointHistory pointHistory = PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(earnPoints.intValue())
                .created_at(LocalDateTime.now())
                .user(user)
                .build();

        ReviewPointManage reviewPointManage = ReviewPointManage.builder()
                .pointHistory(pointHistory)
                .review(review)
                .build();

        when(userService.getUser(userId)).thenReturn(user);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);
        when(reviewPointManageRepository.save(any(ReviewPointManage.class))).thenReturn(reviewPointManage);
        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.REVIEW))
                .thenReturn(PointPolicyResponseDto.builder()
                        .earningMethod(PointPolicy.EarningMethod.REVIEW)
                        .earningType(PointPolicy.EarningType.FIX)
                        .earningValue(earnPoints)
                        .available(true)
                        .description("Review points")
                        .build());
        
        when(reviewImagePathRepository.findByReview(any())).thenReturn(null);
        PointHistory result = pointHistoryService.reviewPoint(new ReviewPointCalculateRequestDto(userId, reviewId));




        assertNotNull(result);
        assertEquals(PointHistory.PointType.EARN, result.getPointType());
        assertEquals(earnPoints.intValue(), result.getAmount());
        assertEquals(user, result.getUser());

        verify(userService).getUser(userId);
        verify(reviewRepository, times(2)).findById(reviewId);
        verify(pointHistoryRepository).save(any(PointHistory.class));
        verify(reviewPointManageRepository).save(any(ReviewPointManage.class));
    }

    @Test
    void testSignupPoint() {

        BigDecimal signPointValue = BigDecimal.valueOf(5000);

        PointPolicyResponseDto policyResponseDto = PointPolicyResponseDto.builder()
                .earningMethod(PointPolicy.EarningMethod.SIGNUP)
                .earningType(PointPolicy.EarningType.FIX)
                .earningValue(signPointValue)
                .available(true)
                .description("Signup points")
                .build();

        PointHistory expectedPointHistory = PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(signPointValue.intValue())
                .created_at(LocalDateTime.now())
                .user(user)
                .build();

        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.SIGNUP)).thenReturn(policyResponseDto);
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(expectedPointHistory);

        PointHistory result = pointHistoryService.signupPoint(user);

        assertNotNull(result);
        assertEquals(PointHistory.PointType.EARN, result.getPointType());
        assertEquals(signPointValue.intValue(), result.getAmount());
        assertEquals(user, result.getUser());

        verify(pointPolicyService).getPolicy(PointPolicy.EarningMethod.SIGNUP);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }
    @Test
    void testOrderPoint_WhenPointHistoryIsNotNull() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        BigDecimal orderTotalPrice = BigDecimal.valueOf(100000);
        BigDecimal originalPrice = BigDecimal.valueOf(100000);
        BigDecimal pointUse = BigDecimal.valueOf(5000);
        BigDecimal earnPoints = BigDecimal.valueOf(1000);

        Tier tier = Tier.STANDARD;

        when(userService.getUserTier(userId)).thenReturn(tier);

        User user = User.builder().userId(userId).build();
        Order order = Order.builder()
                .orderId(orderId)
                .originalPrice(originalPrice)
                .totalPrice(orderTotalPrice)
                .pointUse(pointUse)
                .build();

        PointHistory expectedEarnPointHistory = PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(earnPoints.intValue())
                .created_at(LocalDateTime.now())
                .user(user)
                .build();

        PointHistory expectedDeductPointHistory = PointHistory.builder()
                .pointType(PointHistory.PointType.DEDUCT)
                .amount(-pointUse.intValue())
                .created_at(LocalDateTime.now())
                .user(user)
                .build();

        OrderPointManage orderPointManage = OrderPointManage.builder()
                .pointHistory(expectedEarnPointHistory)
                .order(order)
                .build();

        // Mock 설정
        when(userService.getUser(userId)).thenReturn(user);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD))
                .thenReturn(PointPolicyResponseDto.builder()
                        .earningMethod(PointPolicy.EarningMethod.ORDER_STANDARD)
                        .earningType(PointPolicy.EarningType.RATE)
                        .earningValue(BigDecimal.valueOf(0.01))
                        .available(true)
                        .description("Order points")
                        .build());
        when(pointHistoryRepository.save(any(PointHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 전달된 객체 반환
        when(orderPointManageRepository.save(any(OrderPointManage.class))).thenReturn(orderPointManage);

        // when
        PointHistory result = pointHistoryService.orderPoint(
                OrderPointRequestDto.builder()
                        .userId(userId)
                        .orderId(orderId)
                        .build());

        // then
        assertNotNull(result, "Resulting PointHistory should not be null");
        assertEquals(PointHistory.PointType.EARN, result.getPointType(), "Point type should be EARN");
        assertEquals(earnPoints.intValue(), result.getAmount(), "Earned points amount should match");
        assertEquals(user, result.getUser(), "User should match the expected user");

        verify(userService).getUser(userId);
        verify(orderRepository, times(2)).findById(orderId);
        verify(pointHistoryRepository, times(2)).save(any(PointHistory.class)); // Earn & Deduct
        verify(orderPointManageRepository, times(2)).save(any(OrderPointManage.class));
    }


}