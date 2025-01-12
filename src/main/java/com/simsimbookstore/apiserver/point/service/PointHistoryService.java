package com.simsimbookstore.apiserver.point.service;

import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryService {

    Page<PointHistoryResponseDto> getPointHistory(Long userId, Pageable pageable);

    PointHistory orderPoint(OrderPointRequestDto requestDto);

    void validateUsePoints(Long userId, BigDecimal requestedPoints);

    //특정 유저의 포인트 총 합계 조회
    BigDecimal getUserPoints(Long userId);

    PointHistory updatePoint(Long pointHistoryId, Integer newAmount);

    BigDecimal calculateEarnOrderPoints(OrderPointCalculateRequestDto dto);
}
