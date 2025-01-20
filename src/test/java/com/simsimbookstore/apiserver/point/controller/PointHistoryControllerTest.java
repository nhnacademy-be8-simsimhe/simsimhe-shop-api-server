package com.simsimbookstore.apiserver.point.controller;



import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.point.dto.PointHistoryResponseDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.dto.ReviewPointCalculateRequestDto;
import com.simsimbookstore.apiserver.point.entity.PointHistory;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointHistoryController.class)
class PointHistoryControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PointHistoryService pointHistoryService() {
            return Mockito.mock(PointHistoryService.class);
        }
        @Bean
        public PointPolicyService pointPolicyService() {
            return Mockito.mock(PointPolicyService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointHistoryService pointHistoryService;

    @Autowired
    private PointPolicyService pointPolicyService;


    @Test
    @DisplayName("GET /api/shop/points/history/{userId} - 사용자 포인트 히스토리 조회 성공")
    void getUserPointHistorySuccess() throws Exception {
        // given
        Long userId = 1L;
        PointHistoryResponseDto historyDto = PointHistoryResponseDto.builder()
                .pointType(PointHistory.PointType.EARN)  // 예시 값
                .amount(100)
                .createdAt(LocalDateTime.now())
                .sourceType("ORDER")
                .orderId(123L)
                .reviewId(null)
                .description("Test Description")
                .build();

        PageResponse<PointHistoryResponseDto> pageResponse = PageResponse.<PointHistoryResponseDto>builder()
                .data(List.of(historyDto))
                .currentPage(1)
                .startPage(1)
                .endPage(1)
                .totalPage(1)
                .totalElements(1L)
                .build();

        when(pointHistoryService.getUserPointHistory(Mockito.eq(userId), any(Pageable.class)))
                .thenReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/shop/points/history/{userId}", userId)
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].amount").value(100))
                .andExpect(jsonPath("$.data[0].sourceType").value("ORDER"));

        verify(pointHistoryService, times(1))
                .getUserPointHistory(Mockito.eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/shop/points/{userId} - 사용자 포인트 조회 성공")
    void getPointSuccess() throws Exception {
        Long userId = 1L;
        BigDecimal userPoints = BigDecimal.valueOf(5000);

        when(pointHistoryService.getUserPoints(userId)).thenReturn(userPoints);

        mockMvc.perform(get("/api/shop/points/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("5000"));

        verify(pointHistoryService, times(1)).getUserPoints(userId);
    }

    @Test
    @DisplayName("GET /api/shop/points/{userId}/policy - 사용자 포인트 정책 조회 성공")
    void getUserPointPolicySuccess() throws Exception {
        Long userId = 1L;
        PointPolicyResponseDto policyDto = PointPolicyResponseDto.builder()
                .pointPolicyId(1L)
                .earningMethod(null)
                .earningValue(BigDecimal.valueOf(0.1))
                .description("Test Policy")
                .available(true)
                .earningType(null)
                .build();

        when(pointPolicyService.getUserPolicy(userId)).thenReturn(policyDto);

        mockMvc.perform(get("/api/shop/points/{userId}/policy", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("0.1"));

        verify(pointPolicyService, times(1)).getUserPolicy(userId);
    }

    @Test
    @DisplayName("POST /api/shop/points/reviewPoint - 리뷰 포인트 적립 성공 테스트")
    void earnReviewPointSuccess() throws Exception {
        // given
        Long userId = 1L;
        Long reviewId = 123L;
        Long pointHistoryId = 555L;

        // PointHistory 객체 모킹
        PointHistory mockPointHistory = PointHistory.builder()
                .pointHistoryId(pointHistoryId)
                .build();

        // service.reviewPoint 호출 시 모의 객체 반환 설정
        when(pointHistoryService.reviewPoint(any(ReviewPointCalculateRequestDto.class)))
                .thenReturn(mockPointHistory);

        // when & then: POST 요청 수행
        mockMvc.perform(post("/api/shop/points/reviewPoint")
                        .param("userId", userId.toString())
                        .param("reviewId", reviewId.toString()))
                .andExpect(status().isCreated())
                .andExpect(content().string(pointHistoryId.toString()));

        // 서비스가 reviewPoint 메서드를 한 번 호출했는지 검증
        verify(pointHistoryService, times(1)).reviewPoint(any(ReviewPointCalculateRequestDto.class));
    }
}