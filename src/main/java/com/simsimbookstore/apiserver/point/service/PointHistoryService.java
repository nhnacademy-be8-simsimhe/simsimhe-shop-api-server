package com.simsimbookstore.apiserver.point.service;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.dto.ReviewPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.users.localuser.entity.LocalUser;
import com.simsimbookstore.apiserver.users.user.entity.User;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryService {


    PageResponse<PointHistoryResponseDto> getUserPointHistory(Long userId, Pageable pageable);

    PointHistory orderPoint(OrderPointRequestDto requestDto);

    PointHistory reviewPoint(ReviewPointCalculateRequestDto dto);

    PointHistory signupPoint(User user);

    void validateUsePoints(Long userId, BigDecimal requestedPoints);

    //특정 유저의 포인트 총 합계 조회
    BigDecimal getUserPoints(Long userId);

    PointHistory updatePoint(Long pointHistoryId, Integer newAmount);

    BigDecimal calculateEarnOrderPoints(OrderPointCalculateRequestDto dto);

    BigDecimal refundPoint(Long orderId);
}
