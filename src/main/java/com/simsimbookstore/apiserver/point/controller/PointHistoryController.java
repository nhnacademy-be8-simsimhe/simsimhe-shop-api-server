package com.simsimbookstore.apiserver.point.controller;

import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop/points")
@RequiredArgsConstructor
public class PointHistoryController {
    private final PointHistoryService pointHistoryService;

    /**
     *
     * @param userId 포인트 목록을 가져오고 싶은 user의 아이디
     * @param pageable 페이저블
     * @return
     *     PointHistory.PointType pointType;
     *     Integer amount;
     *     LocalDateTime createdAt;
     *     String sourceType;
     *     Long orderId;
     *     Long reviewId;
     *     String description;
     */

    @GetMapping("/history/{userId}")
    public ResponseEntity<Page<PointHistoryResponseDto>> getPointHistory(@PathVariable Long userId, Pageable pageable) {
        Page<PointHistoryResponseDto> pointHistory = pointHistoryService.getPointHistory(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(pointHistory);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> getPoint(@PathVariable Long userId) {
        BigDecimal userPoints = pointHistoryService.getUserPoints(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userPoints);
    }

}
