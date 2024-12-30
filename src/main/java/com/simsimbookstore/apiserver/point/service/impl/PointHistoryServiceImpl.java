package com.simsimbookstore.apiserver.point.service.impl;


import static java.time.LocalDateTime.now;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
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
    //private final ReviewRepository reviewRepository;

//    public PointHistory addReviewPoint(Long userId, Long reviewId, Integer points) {
//    }

    public PointHistory addOrderPoint(OrderPointRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow();
        OrderPointCalculateRequestDto dto = OrderPointCalculateRequestDto.builder()
                .userId(requestDto.getUserId())
                .orderId(requestDto.getOrderId())
                .build();

        BigDecimal earnPoints = calculateEarnOrderPoints(dto);

        // 1. PointHistory 엔티티 생성
        pointHistoryRepository.save(PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(earnPoints.intValue())
                .created_at(now())
                .user(user)
                .pointPolicy(null)
                .build());

        // 2. PointHistory 저장

        // 3. OrderPointManage 엔티티 생성 및 저장


        return null;
    }


    //특정 유저의 포인트 총 합계 조회
    public BigDecimal getUserPoints(Long userId) {
        return BigDecimal.valueOf(pointHistoryRepository.sumAmountByUser_UserId(userId));
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

        // 티어를 EarningType으로 매핑
        PointPolicy.EarningType earningType = tierToEarningTypeMap.get(tier);
        if (earningType == null) {
            throw new IllegalArgumentException("Invalid Tier: " + tier);
        }

        // 정책 조회 및 포인트 계산
        BigDecimal rate = pointHistoryService.getPolicy(earningType).getRating();
        return originalPrice.multiply(rate);
    }

    private static final Map<String, PointPolicy.EarningType> tierToEarningTypeMap = Map.of(
            "NORMAL", PointPolicy.EarningType.ORDER_NORMAL,
            "ROYAL", PointPolicy.EarningType.ORDER_ROYAL,
            "GOLD", PointPolicy.EarningType.ORDER_GOLD,
            "PLATINUM", PointPolicy.EarningType.ORDER_PLATINUM
    );

}
