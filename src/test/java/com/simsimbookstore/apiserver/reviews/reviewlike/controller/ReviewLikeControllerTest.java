package com.simsimbookstore.apiserver.reviews.reviewlike.controller;


import com.simsimbookstore.apiserver.reviews.reviewlike.service.ReviewLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ReviewLikeControllerTest {


    private MockMvc mockMvc;
    ReviewLikeService reviewLikeService;

    @BeforeEach
    void setup() {
        reviewLikeService = mock(ReviewLikeService.class);
        ReviewLikeController reviewLikeController = new ReviewLikeController(reviewLikeService);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewLikeController).build();
    }


    @Test
    @DisplayName("리뷰 좋아요 생성 테스트 성공")
    void createLike() throws Exception {

        Long userId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewLikeService).createReviewLike(userId, reviewId);

        mockMvc.perform(post("/api/shop/reviews/{reviewId}/likes", reviewId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("리뷰 좋아요 삭제 테스트 성공")
    void removeLike() throws Exception {

        Long userId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewLikeService).createReviewLike(userId, reviewId);

        mockMvc.perform(delete("/api/shop/reviews/{reviewId}/likes", reviewId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("리뷰 좋아요 수 조회 성공 테스트")
    void getLikeCount_Success() throws Exception {
        Long userId = 1L;
        Long reviewId = 2L;
        Long expectedCount = 10L;

        when(reviewLikeService.getReviewLikeCount(eq(reviewId))).thenReturn(expectedCount);

        mockMvc.perform(get("/api/shop/reviews/{reviewId}/likes/count", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));

        verify(reviewLikeService, times(1)).getReviewLikeCount(reviewId);
    }


}