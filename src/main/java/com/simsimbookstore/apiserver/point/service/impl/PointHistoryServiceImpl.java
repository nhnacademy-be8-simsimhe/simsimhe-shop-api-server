package com.simsimbookstore.apiserver.point.service.impl;


import static java.time.LocalDateTime.now;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointUseRequestDto;
import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.OrderPointManageRepository;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHistoryServiceImpl {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyService pointHistoryService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderPointManageRepository orderPointManageRepository;

    public PointHistory addOrderPoint(OrderPointRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow();
        OrderPointCalculateRequestDto dto = OrderPointCalculateRequestDto.builder()
                .userId(requestDto.getUserId())
                .orderId(requestDto.getOrderId())
                .build();

        BigDecimal earnPoints = calculateEarnOrderPoints(dto);
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // 1. PointHistory 엔티티 저장
        PointHistory save = pointHistoryRepository.save(PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(earnPoints.intValue())
                .created_at(now())
                .user(user)
                .build());
        // 2. OrderPointManage 엔티티 생성 및

        OrderPointManage orderPointManage = orderPointManageRepository.save(OrderPointManage.builder()
                .pointHistory(save)
                .order(order)
                .build());

        orderPointManageRepository.save(orderPointManage);

        return save;
    }


    /**
     * 포인트 차감
     *
     * @param requestDto
     * @return
     */

    public PointHistory usePoints(OrderPointUseRequestDto requestDto) {
        // 사용자 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 사용 가능한 포인트 조회

        validateUsePoints(requestDto.getUserId(), requestDto.getUsePoints());

        // 주문 조회
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // 포인트 차감 기록 생성 및 저장
        PointHistory usedPointHistory = pointHistoryRepository.save(PointHistory.builder()
                .pointType(PointHistory.PointType.DEDUCT)
                .amount(requestDto.getUsePoints().negate().intValue()) // 음수
                .created_at(LocalDateTime.now())
                .user(user)
                .build());

        // OrderPointManage 엔티티 생성 및 저장
        OrderPointManage orderPointManage = orderPointManageRepository.save(OrderPointManage.builder()
                .pointHistory(usedPointHistory)
                .order(order)
                .build());

        return usedPointHistory;
    }


    public void validateUsePoints(Long userId, BigDecimal requestedPoints) {
        // 사용 가능한 포인트 조회
        BigDecimal availablePoints = getUserPoints(userId);

        // 사용 가능한 포인트와 요청된 포인트 비교
        if (availablePoints.compareTo(requestedPoints) < 0) {
            throw new IllegalArgumentException("Insufficient points. Available points: " + availablePoints);
        }
    }


    //특정 유저의 포인트 총 합계 조회
    public BigDecimal getUserPoints(Long userId) {
        return BigDecimal.valueOf(pointHistoryRepository.sumAmountByUserId(userId));
    }

    // Update: 포인트 수정 (예: 금액 수정)
    public PointHistory updatePoint(Long pointHistoryId, Integer newAmount) {
        PointHistory pointHistory = pointHistoryRepository.findById(pointHistoryId)
                .orElseThrow(() -> new NotFoundException("포인트 기록이 존재하지 않습니다."));
        pointHistory.setAmount(newAmount);
        return pointHistoryRepository.save(pointHistory);
    }


    private BigDecimal calculateEarnOrderPoints(OrderPointCalculateRequestDto dto) {
        // 사용자 등급과 주문 금액 가져오기
        String tier = String.valueOf(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getGrade()
                .getTier());

        BigDecimal originalPrice = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"))
                .getOriginalPrice();

        // 티어를 Map으로  매핑
        PointPolicy.EarningMethod EarningMethod = tierToEarningMethodMap.get(tier);
        if (EarningMethod == null) {
            throw new IllegalArgumentException("Invalid Tier: " + tier);
        }

        // 정책 조회 및 포인트 계산
        BigDecimal rate = pointHistoryService.getPolicy(EarningMethod).getEarningValue();
        return originalPrice.multiply(rate);
    }

    private static final Map<String, PointPolicy.EarningMethod> tierToEarningMethodMap = Map.of(
            "STANDARD", PointPolicy.EarningMethod.ORDER_STANDARD,
            "ROYAL", PointPolicy.EarningMethod.ORDER_ROYAL,
            "GOLD", PointPolicy.EarningMethod.ORDER_GOLD,
            "PLATINUM", PointPolicy.EarningMethod.ORDER_PLATINUM
    );

}
