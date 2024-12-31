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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceImplTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private PointPolicyService pointHistoryService;
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

        // 클래스 필드에 직접 대입
        this.mockUser = User.builder()
                .userId(100L)
                .userName("TestUser")
                .mobileNumber("010-1234-5678")
                .email("testuser@example.com")
                .birth(LocalDate.of(1998, 10, 3))
                .grade(standardGrade)
                .build();

        // Order도 마찬가지로 클래서 필드를 사용
        this.mockOrder = Order.builder()
                .orderId(100L)
                .originalPrice(BigDecimal.valueOf(10000))
                .build();
    }

    /**
     * ==============================
     * 1) calculateEarnOrderPoints(...) 직접 테스트
     * ==============================
     */
    @Test
    void calculateEarnOrderPoints_NormalTier() throws Exception {
        // given
        // Dto 준비
        OrderPointCalculateRequestDto requestDto = OrderPointCalculateRequestDto.builder()
                .userId(mockUser.getUserId())
                .orderId(mockOrder.getOrderId())
                .build();

        // User, Order 모킹
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(mockOrder.getOrderId())).thenReturn(Optional.of(mockOrder));

        // NORMAL → ORDER_STANDARD 정책
        PointPolicy mockPolicy = PointPolicy.builder()
                .earningValue(BigDecimal.valueOf(0.01)) // 1%
                .build();
        when(pointHistoryService.getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD))
                .thenReturn(PointPolicyResponseDto.fromEntity(mockPolicy));

        // private 메서드를 Reflection으로 가져오기
        Method method = PointHistoryServiceImpl.class
                .getDeclaredMethod("calculateEarnOrderPoints", OrderPointCalculateRequestDto.class);
        method.setAccessible(true);

        // when
        BigDecimal result = (BigDecimal) method.invoke(pointHistoryServiceImpl, requestDto);

        // then
        // originalPrice = 10000, rate=0.01 => 100
        assertNotNull(result);
        assertEquals(100, result.intValue());

        // verify
        verify(userRepository, times(1)).findById(mockUser.getUserId());
        verify(orderRepository, times(1)).findById(mockOrder.getOrderId());
        verify(pointHistoryService, times(1))
                .getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD);
    }

    /**
     * ==============================
     * 2) addOrderPoint(...) 테스트
     * ==============================
     */
    @Test
    void addOrderPoint_Success() {
        // given
        OrderPointRequestDto requestDto = OrderPointRequestDto.builder()
                .userId(mockUser.getUserId())
                .orderId(mockOrder.getOrderId())
                .build();

        // user, order 조회 stubbing
        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(requestDto.getOrderId())).thenReturn(Optional.of(mockOrder));

        // 정책 조회 Stubbing
        PointPolicy mockPolicy = PointPolicy.builder()
                .earningValue(BigDecimal.valueOf(0.01)) // 1% 적립
                .build();
        when(pointHistoryService.getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD))
                .thenReturn(PointPolicyResponseDto.fromEntity(mockPolicy));

        // pointHistoryRepository.save(...) stubbing
        PointHistory savedHistory = PointHistory.builder()
                .pointHistoryId(999L)
                .pointType(PointHistory.PointType.EARN)
                .amount(100)
                .created_at(LocalDateTime.now())
                .user(mockUser)
                .build();
        when(pointHistoryRepository.save(any(PointHistory.class)))
                .thenReturn(savedHistory);

        OrderPointManage savedOrderPointManage = OrderPointManage.builder()
                .orderPointId(888L)
                .pointHistory(savedHistory)
                .order(mockOrder)
                .build();
        when(orderPointManageRepository.save(any(OrderPointManage.class)))
                .thenReturn(savedOrderPointManage);

        // when
        PointHistory result = pointHistoryServiceImpl.addOrderPoint(requestDto);

        // then
        assertNotNull(result);
        assertEquals(999L, result.getPointHistoryId());
        assertEquals(PointHistory.PointType.EARN, result.getPointType());
        assertEquals(100, result.getAmount());
        assertEquals(mockUser, result.getUser());

        verify(userRepository, times(2)).findById(requestDto.getUserId());
        verify(orderRepository, times(2)).findById(requestDto.getOrderId());
        verify(pointHistoryService, times(1))
                .getPolicy(PointPolicy.EarningMethod.ORDER_STANDARD);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
        verify(orderPointManageRepository, times(2)).save(any(OrderPointManage.class));
    }

    @Test
    void addOrderPoint_OrderNotFound() {
        // given
        OrderPointRequestDto requestDto = OrderPointRequestDto.builder()
                .userId(mockUser.getUserId())
                .orderId(99999L)
                .build();

        // user는 존재하지만 order가 없다
        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(requestDto.getOrderId())).thenReturn(Optional.empty());

        // when + then
        assertThrows(NotFoundException.class,
                () -> pointHistoryServiceImpl.addOrderPoint(requestDto));
    }

    /**
     * ==============================
     * 3) usePoints(...) 테스트
     * ==============================
     */
    @Test
    void usePoints_Success() {
        // given
        OrderPointUseRequestDto requestDto = OrderPointUseRequestDto.builder()
                .userId(100L)
                .orderId(100L)
                .usePoints(BigDecimal.valueOf(300))
                .build();

        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findById(requestDto.getOrderId())).thenReturn(Optional.of(mockOrder));

        // 보유 포인트 = 1000
        when(pointHistoryRepository.sumAmountByUserId(requestDto.getUserId()))
                .thenReturn(1000);

        // 포인트 차감 후 Save
        PointHistory usedHistory = PointHistory.builder()
                .pointHistoryId(555L)
                .pointType(PointHistory.PointType.DEDUCT)
                .amount(-300)
                .created_at(LocalDateTime.now())
                .user(mockUser)
                .build();
        when(pointHistoryRepository.save(any(PointHistory.class)))
                .thenReturn(usedHistory);

        // when
        PointHistory result = pointHistoryServiceImpl.usePoints(requestDto);

        // then
        assertNotNull(result);
        assertEquals(PointHistory.PointType.DEDUCT, result.getPointType());
        assertEquals(-300, result.getAmount());
        verify(orderPointManageRepository, times(1)).save(any(OrderPointManage.class));
    }

    @Test
    void usePoints_InsufficientBalance() {
        // given
        OrderPointUseRequestDto requestDto = OrderPointUseRequestDto.builder()
                .userId(100L)
                .orderId(100L)
                .usePoints(BigDecimal.valueOf(2000))
                .build();

        // when + then
        assertThrows(NotFoundException.class,
                () -> pointHistoryServiceImpl.usePoints(requestDto),
                "잔여 포인트 부족 예외가 발생해야 합니다."
        );
    }
}