package com.simsimbookstore.apiserver.point.controller;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop/points")
@RequiredArgsConstructor
public class PointHistoryController {
    private final PointHistoryService pointHistoryService;

    /**
     * 사용자 포인트 히스토리 조회
     *
     * @param page   페이지 번호 (기본값 1)
     * @param size   페이지 크기 (기본값 5)
     * @param userId 사용자 ID
     * @return 포인트 히스토리 PageResponse
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<PageResponse<PointHistoryResponseDto>> getUserPointHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            @PathVariable(name = "userId") Long userId) {

        Pageable pageable = PageRequest.of(page - 1, size); // 0-based index
        PageResponse<PointHistoryResponseDto> response = pointHistoryService.getUserPointHistory(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> getPoint(@PathVariable Long userId) {
        BigDecimal userPoints = pointHistoryService.getUserPoints(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userPoints);
    }

}
