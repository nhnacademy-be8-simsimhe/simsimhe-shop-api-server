package com.simsimbookstore.apiserver.point.repository.custom;

import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PointHistoryCustomRepository {

    List<PointHistoryResponseDto> getPointHistoriesByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);
}
