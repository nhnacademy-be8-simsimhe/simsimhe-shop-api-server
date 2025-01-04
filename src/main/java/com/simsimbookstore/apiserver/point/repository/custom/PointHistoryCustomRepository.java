package com.simsimbookstore.apiserver.point.repository.custom;

import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryCustomRepository {

    Page<PointHistoryResponseDto> getPointHistoriesByUserId(Long userId, Pageable pageable);
}
