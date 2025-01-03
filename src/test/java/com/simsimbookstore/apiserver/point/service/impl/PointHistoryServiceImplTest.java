package com.simsimbookstore.apiserver.point.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.dto.*;
import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.OrderPointManageRepository;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceImplTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private PointPolicyService pointPolicyService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderPointManageRepository orderPointManageRepository;

    @InjectMocks
    private PointHistoryServiceImpl pointHistoryServiceImpl;

    private User mockUser;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        Grade standardGrade = Grade.builder()
                .gradeId(1L)
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.ZERO)
                .maxAmount(BigDecimal.valueOf(999999))
                .build();

        mockUser = User.builder()
                .userId(100L)
                .userName("TestUser")
                .mobileNumber("010-1234-5678")
                .email("testuser@example.com")
                .birth(LocalDate.of(1998, 10, 3))
                .grade(standardGrade)
                .build();

        mockOrder = Order.builder()
                .orderId(100L)
                .originalPrice(BigDecimal.valueOf(10000))
                .pointUse(BigDecimal.valueOf(500))
                .build();
    }

    /**
     * Test: orderPoint(...) 성공 테스트
     */
    @Test
    void orderPoint_Success() {
        // given
        OrderPointRequestDto requestDto = OrderPointRequestDto.builder()
                .userId(mockUser.getUserId())
                .orderId(mockOrder.getOrderId())
                .build();

        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(requestDto.getOrderId())).thenReturn(Optional.of(mockOrder));

        // PointPolicyResponseDto 생성
        PointPolicyResponseDto mockPolicy = PointPolicyResponseDto.builder()
                .earningMethod(PointPolicy.EarningMethod.ORDER_STANDARD)
                .earningValue(BigDecimal.valueOf(0.01))
                .description("Standard earning policy")
                .isAvailable(true)
                .earningType(PointPolicy.EarningType.RATE)
                .build();

        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD)).thenReturn(mockPolicy);

        PointHistory usedPointHistory = PointHistory.builder()
                .pointHistoryId(1L)
                .pointType(PointHistory.PointType.DEDUCT)
                .amount(500)
                .user(mockUser)
                .build();

        PointHistory earnedPointHistory = PointHistory.builder()
                .pointHistoryId(2L)
                .pointType(PointHistory.PointType.EARN)
                .amount(100)
                .user(mockUser)
                .build();

        when(pointHistoryRepository.save(any(PointHistory.class)))
                .thenReturn(usedPointHistory, earnedPointHistory);

        OrderPointManage orderPointManage = OrderPointManage.builder()
                .pointHistory(earnedPointHistory)
                .order(mockOrder)
                .build();

        when(orderPointManageRepository.save(any(OrderPointManage.class))).thenReturn(orderPointManage);

        // when
        PointHistory result = pointHistoryServiceImpl.orderPoint(requestDto);

        // then
        assertNotNull(result);
        assertEquals(earnedPointHistory.getPointHistoryId(), result.getPointHistoryId());
        assertEquals(earnedPointHistory.getAmount(), result.getAmount());
        verify(userRepository, times(2)).findById(requestDto.getUserId());
        verify(orderRepository, times(2)).findById(requestDto.getOrderId());
        verify(pointHistoryRepository, times(2)).save(any(PointHistory.class));
        verify(orderPointManageRepository, times(2)).save(any(OrderPointManage.class));
    }

    /**
     * Test: orderPoint(...) Order Not Found
     */
    @Test
    void orderPoint_OrderNotFound() {
        // given
        OrderPointRequestDto requestDto = OrderPointRequestDto.builder()
                .userId(mockUser.getUserId())
                .orderId(99999L) // Non-existent orderId
                .build();

        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(requestDto.getOrderId())).thenReturn(Optional.empty());

        // when + then
        assertThrows(NotFoundException.class,
                () -> pointHistoryServiceImpl.orderPoint(requestDto));
        verify(orderRepository, times(1)).findById(requestDto.getOrderId());
    }

    /**
     * Test: validateUsePoints(...) 실패 (잔여 포인트 부족)
     */
    @Test
    void validateUsePoints_InsufficientBalance() {
        // given
        Long userId = mockUser.getUserId();
        BigDecimal requestedPoints = BigDecimal.valueOf(2000);

        when(pointHistoryRepository.sumAmountByUserId(userId)).thenReturn(Optional.of(1000));

        // when + then
        assertThrows(IllegalArgumentException.class,
                () -> pointHistoryServiceImpl.validateUsePoints(userId, requestedPoints),
                "잔여 포인트 부족 예외가 발생해야 합니다.");
        verify(pointHistoryRepository, times(1)).sumAmountByUserId(userId);
    }

    /**
     * Test: updatePoint(...) 성공 테스트
     */
    @Test
    void updatePoint_Success() {
        // given
        Long pointHistoryId = 1L;
        Integer newAmount = 300;

        PointHistory existingHistory = PointHistory.builder()
                .pointHistoryId(pointHistoryId)
                .amount(200)
                .build();

        when(pointHistoryRepository.findById(pointHistoryId)).thenReturn(Optional.of(existingHistory));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(existingHistory);

        // when
        PointHistory updatedHistory = pointHistoryServiceImpl.updatePoint(pointHistoryId, newAmount);

        // then
        assertNotNull(updatedHistory);
        assertEquals(newAmount, updatedHistory.getAmount());
        verify(pointHistoryRepository, times(1)).findById(pointHistoryId);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    /**
     * Test: calculateEarnOrderPoints(...) 성공 테스트
     */
    @Test
    void calculateEarnOrderPoints_Success() {
        // given
        OrderPointCalculateRequestDto requestDto = OrderPointCalculateRequestDto.builder()
                .userId(mockUser.getUserId())
                .orderId(mockOrder.getOrderId())
                .build();

        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(mockOrder.getOrderId())).thenReturn(Optional.of(mockOrder));

        // PointPolicyResponseDto 생성
        PointPolicyResponseDto mockPolicy = PointPolicyResponseDto.builder()
                .earningMethod(PointPolicy.EarningMethod.ORDER_STANDARD)
                .earningValue(BigDecimal.valueOf(0.01)) // 1%
                .description("Standard earning policy")
                .isAvailable(true)
                .earningType(PointPolicy.EarningType.RATE)
                .build();

        when(pointPolicyService.getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD)).thenReturn(mockPolicy);

        // when
        BigDecimal result = pointHistoryServiceImpl.calculateEarnOrderPoints(requestDto);

        // then
        assertNotNull(result);
        assertEquals(100, result.intValue()); // 10,000 * 0.01 = 100
    }
}