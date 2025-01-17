package com.simsimbookstore.apiserver.point.service.impl;


import static java.time.LocalDateTime.now;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.dto.ReviewPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.entity.ReviewPointManage;
import com.simsimbookstore.apiserver.point.repository.OrderPointManageRepository;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.repository.ReviewPointManageRepository;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import com.simsimbookstore.apiserver.reviews.reviewimage.repository.ReviewImagePathRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderPointManageRepository orderPointManageRepository;
    private final ReviewPointManageRepository reviewPointManageRepository;
    private final ReviewImagePathRepository reviewImagePathRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public PageResponse<PointHistoryResponseDto> getUserPointHistory(Long userId, Pageable pageable) {
        List<PointHistoryResponseDto> pointHistories = pointHistoryRepository.getPointHistoriesByUserId(userId, pageable);

        long totalElements = pointHistoryRepository.countByUserId(userId);

        Page<PointHistoryResponseDto> page = new PageImpl<>(pointHistories, pageable, totalElements);

        return new PageResponse<PointHistoryResponseDto>().getPageResponse(
                pageable.getPageNumber() + 1,  // 현재 페이지 (1부터 시작하도록 설정)
                10,  // 최대 페이지 버튼 수
                page // Page 객체
        );
    }


    @Override
    @Transactional
    public PointHistory orderPoint(OrderPointRequestDto requestDto) {
        User user = userService.getUser(requestDto.getUserId());
        OrderPointCalculateRequestDto dto = OrderPointCalculateRequestDto.builder()
                .userId(requestDto.getUserId())
                .orderId(requestDto.getOrderId())
                .build();

        BigDecimal earnPoints = calculateEarnOrderPoints(dto);
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        BigDecimal pointUse = order.getPointUse();
        if (pointUse.compareTo(BigDecimal.ZERO) > 0) {
            PointHistory use = pointHistoryRepository.save(PointHistory.builder()
                    .pointType(PointHistory.PointType.DEDUCT)
                    .amount(-order.getPointUse().intValue())
                    .created_at(now())
                    .user(user)
                    .build());

            orderPointManageRepository.save(OrderPointManage.builder()
                    .pointHistory(use)
                    .order(order)
                    .build());
            log.info("포인트 use 저장 {}", use);
        }

        // 1. PointHistory 엔티티 저장
        PointHistory save = pointHistoryRepository.save(PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(earnPoints.intValue())
                .created_at(now())
                .user(user)
                .build());
        // 2. OrderPointManage 엔티티 생성

        orderPointManageRepository.save(OrderPointManage.builder()
                .pointHistory(save)
                .order(order)
                .build());


        order.setPointEarn(earnPoints.intValue());
        order.setPointUse(pointUse);

        return save;
    }

    @Override
    @Transactional
    public PointHistory reviewPoint(ReviewPointCalculateRequestDto dto) {
        User user = userService.getUser(dto.getUserId());
        BigDecimal earnPoints = calculateEarnReviewPoints(dto);
        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new NotFoundException("Review not found"));

        // 1. PointHistory
        PointHistory save = pointHistoryRepository.save(PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(earnPoints.intValue())
                .created_at(now())
                .user(user)
                .build());

        // 2. ReviewPointManage
        reviewPointManageRepository.save(ReviewPointManage.builder()
                .pointHistory(save)
                .review(review)
                .build());

        return save;
    }

    @Override
    public PointHistory signupPoint(User user) {
        BigDecimal signPointValue = calculateEarnSignupPoints();
        return pointHistoryRepository.save(PointHistory.builder()
                .pointType(PointHistory.PointType.EARN)
                .amount(signPointValue.intValue())
                .pointDescription("SIGNUP POINT")
                .created_at(now())
                .user(user)
                .build());
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
        String tier = userService.getUserTier(dto.getUserId()).toString();

        BigDecimal originalPrice = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"))
                .getOriginalPrice();

        // 티어를 Map으로  매핑
        PointPolicy.EarningMethod earningMethod = tierToEarningMethodMap.get(tier);
        if (earningMethod == null) {
            throw new IllegalArgumentException("Invalid Tier: " + tier);
        }

        // 정책 조회 및 포인트 계산
        BigDecimal rate = pointHistoryService.getPolicy(earningMethod).getEarningValue();
        return originalPrice.multiply(rate);
    }

    @Override
    public BigDecimal refundPoint(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("order Not Found"));
        //total+point use
        BigDecimal pointUse = order.getPointUse();
        BigDecimal totalPrice = order.getTotalPrice();
        BigDecimal save = pointUse.add(totalPrice);
        pointHistoryRepository.save( PointHistory.builder()
                .user(order.getUser())
                .amount(save.intValue())
                .created_at(now())
                .pointType(PointHistory.PointType.EARN)
                .build());
        return save;
    }


    /**
     * 리뷰id, 유저id를 가지고 리뷰포인트 정책에 있는 적립포인트를 반환한다
     */
    public BigDecimal calculateEarnReviewPoints(ReviewPointCalculateRequestDto dto) {
        log.info("userId = {}", dto.getUserId());
        PointPolicy.EarningMethod earningMethod;

        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new NotFoundException("Review not found"));

        List<ReviewImagePath> imagePaths = reviewImagePathRepository.findByReview(review);
        if (imagePaths == null || imagePaths.isEmpty()) {
            // 사진이 없으므로 일반 리뷰
            earningMethod = tierToEarningMethodMap.get("REVIEW");
        } else {
            // 사진이 하나 이상 존재하므로 포토 리뷰
            earningMethod = tierToEarningMethodMap.get("PHOTOREVIEW");
        }

        // earningMethod가 null이면 정책을 찾지 못한 것이므로 예외 처리
        if (earningMethod == null) {
            throw new IllegalArgumentException("해당 리뷰 정책을 찾을 수 없습니다.");
        }

        return pointHistoryService.getPolicy(earningMethod).getEarningValue();
    }

    public BigDecimal calculateEarnSignupPoints() {
        PointPolicy.EarningMethod earningMethod = tierToEarningMethodMap.get("SIGNUP");
        return pointHistoryService.getPolicy(earningMethod).getEarningValue();
    }


    private static final Map<String, PointPolicy.EarningMethod> tierToEarningMethodMap = Map.of(
            "SIGNUP", PointPolicy.EarningMethod.SIGNUP,
            "REVIEW", PointPolicy.EarningMethod.REVIEW,
            "PHOTOREVIEW", PointPolicy.EarningMethod.PHOTOREVIEW,
            "STANDARD", PointPolicy.EarningMethod.ORDER_STANDARD,
            "ROYAL", PointPolicy.EarningMethod.ORDER_ROYAL,
            "GOLD", PointPolicy.EarningMethod.ORDER_GOLD,
            "PLATINUM", PointPolicy.EarningMethod.ORDER_PLATINUM
    );

}
