package com.simsimbookstore.apiserver.point.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.entity.QOrderPointManage;
import com.simsimbookstore.apiserver.point.entity.QPointHistory;
import com.simsimbookstore.apiserver.point.entity.QReviewPointManage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


@RequiredArgsConstructor
public class PointHistoryCustomRepositoryImpl implements PointHistoryCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PointHistoryResponseDto> getPointHistoriesByUserId(Long userId, Pageable pageable) {
        // Q클래스 선언 (QueryDSL을 위한)
        QPointHistory qPointHistory = QPointHistory.pointHistory;
        QOrderPointManage qOrderPointManage = QOrderPointManage.orderPointManage;
        QReviewPointManage qReviewPointManage = QReviewPointManage.reviewPointManage;

        // 메인 select
        List<PointHistoryResponseDto> content = queryFactory
                .select(Projections.fields(
                        PointHistoryResponseDto.class,
                        qPointHistory.pointHistoryId.as("pointHistoryId"),
                        qPointHistory.pointType.as("pointType"),
                        qPointHistory.amount.as("amount"),
                        qPointHistory.created_at.as("createdAt"),
                        // 주문 ID
                        qPointHistory.pointDescription.as("description"),
                        qOrderPointManage.order.orderId.as("orderId"),
                        // 리뷰 ID
                        qReviewPointManage.review.reviewId.as("reviewId")
                ))
                .from(qPointHistory)
                .leftJoin(qOrderPointManage)
                .on(qOrderPointManage.pointHistory.eq(qPointHistory))
                .leftJoin(qReviewPointManage)
                .on(qReviewPointManage.pointHistory.eq(qPointHistory))
                .where(qPointHistory.user.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qPointHistory.created_at.desc())
                .fetch();

        // 추가: orderId, reviewId 를 보고 "origin" 필드 세팅
        content.forEach(dto -> {
            if (dto.getOrderId() != null) {
                dto.setSourceType("ORDER");
            } else if (dto.getReviewId() != null) {
                dto.setSourceType("REVIEW");
            } else {
                dto.setSourceType("NONE");
            }
        });

        return content;
    }
}

