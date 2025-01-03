package com.simsimbookstore.apiserver.point.service.impl;


import static java.time.LocalDateTime.now;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.OrderPointManageRepository;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyService pointHistoryService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderPointManageRepository orderPointManageRepository;

    @Override
    public Page<PointHistoryResponseDto> getPointHistory(Long userId, Pageable pageable) {
        return pointHistoryRepository.getPointHistoriesByUserId(userId, pageable);

    }


    @Override
    public PointHistory orderPoint(OrderPointRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow();
        OrderPointCalculateRequestDto dto = OrderPointCalculateRequestDto.builder()
                .userId(requestDto.getUserId())
                .orderId(requestDto.getOrderId())
                .build();

        BigDecimal earnPoints = calculateEarnOrderPoints(dto);
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        BigDecimal pointUse = order.getPointUse();

        PointHistory use = pointHistoryRepository.save(PointHistory.builder()
                .pointType(PointHistory.PointType.DEDUCT)
                .amount(-order.getPointUse().intValue())
                .created_at(now())
                .user(user)
                .build());

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


    @Override
    public void validateUsePoints(Long userId, BigDecimal requestedPoints) {
        // 사용 가능한 포인트 조회
        BigDecimal availablePoints = getUserPoints(userId);

        // 사용 가능한 포인트와 요청된 포인트 비교
        if (availablePoints.compareTo(requestedPoints) < 0) {
            throw new IllegalArgumentException("Insufficient points. Available points: " + availablePoints);
        }
    }


    //특정 유저의 포인트 총 합계 조회
    @Override
    public BigDecimal getUserPoints(Long userId) {
        log.info("userId 전달값: {}", userId);
        Integer sum = pointHistoryRepository.sumAmountByUserId(userId).orElse(0);
        log.info("Sum 결과: {}", sum);
        return BigDecimal.valueOf(sum);
    }


// Update: 포인트 수정 (예: 금액 수정)

    @Override
    public PointHistory updatePoint(Long pointHistoryId, Integer newAmount) {
        PointHistory pointHistory = pointHistoryRepository.findById(pointHistoryId)
            .orElseThrow(() -> new NotFoundException("포인트 기록이 존재하지 않습니다."));
        pointHistory.setAmount(newAmount);
        return pointHistoryRepository.save(pointHistory);
    }

    @Override
    public BigDecimal calculateEarnOrderPoints(OrderPointCalculateRequestDto dto) {
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
