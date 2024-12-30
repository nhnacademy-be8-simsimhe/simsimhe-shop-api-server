package com.simsimbookstore.apiserver.point.service.impl;


import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.point.entity.OrderPointManage;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.repository.PointHistoryRepository;
import com.simsimbookstore.apiserver.point.repository.PointPolicyRepository;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHistoryServiceImpl {
    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final UserRepository userRepository;
    //private final ReviewRepository reviewRepository;

//    public PointHistory addReviewPoint(Long userId, Long reviewId, Integer points) {
//    }

    public PointHistory addOrderPoint(Long userId, Long orderId, Integer points) {
        // 1. PointHistory 엔티티 생성
        Optional<PointPolicy> byId = pointPolicyRepository.findById(1L);
        PointHistory pointHistory = PointHistory.builder()
                .pointType(PointHistory.PointType.EARN) // EARN 또는 DEDUCT
                .amount(points)
                .created_at(LocalDateTime.now())
                .user(userRepository.findById(userId).orElseThrow( () -> new NotFoundException("user Not Found"))) // 유저 정보
                .pointPolicy(byId.orElseThrow()) // 포인트 정책 설정
                .build();

        // 2. PointHistory 저장
        PointHistory savedPointHistory = pointHistoryRepository.save(pointHistory);

        // 3. OrderPointManage 엔티티 생성 및 저장
//        OrderPointManage orderPointManage = OrderPointManage.builder()
//                .pointHistory(savedPointHistory)
//                .order(new Order(orderId))
//                .build();
//        orderPointManageRepository.save(orderPointManage);

        return savedPointHistory;
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

    // Delete: 포인트 기록 삭제
    public void deletePointHistory(Long pointHistoryId) {
        pointHistoryRepository.deleteById(pointHistoryId);
    }

//    private String getSource(PointHistory pointHistory) {
//        if (pointHistory.getReviewPointManage() != null) {
//            return "리뷰 ID: " + pointHistory.getReviewPointManage().getReview().getReviewId();
//        } else if (pointHistory.getOrderPointManage() != null) {
//            return "주문 ID: " + pointHistory.getOrderPointManage().getOrder().getOrderId();
//        } else {
//            return "기타";
//        }
//    }
}
