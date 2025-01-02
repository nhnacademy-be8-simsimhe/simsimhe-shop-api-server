package com.simsimbookstore.apiserver.point.service;

import com.simsimbookstore.apiserver.point.dto.OrderPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.dto.OrderPointRequestDto;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import java.math.BigDecimal;

public interface PointHistoryService {

    PointHistory orderPoint(OrderPointRequestDto requestDto);

    void validateUsePoints(Long userId, BigDecimal requestedPoints);

    //특정 유저의 포인트 총 합계 조회
    BigDecimal getUserPoints(Long userId);

    PointHistory updatePoint(Long pointHistoryId, Integer newAmount);

    BigDecimal calculateEarnOrderPoints(OrderPointCalculateRequestDto dto);
}
