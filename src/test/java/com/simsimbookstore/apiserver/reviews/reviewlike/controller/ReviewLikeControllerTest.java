package com.simsimbookstore.apiserver.reviews.reviewlike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.reviews.reviewlike.service.ReviewLikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;


@WebMvcTest(ReviewLikeController.class)
@ExtendWith(MockitoExtension.class)
class ReviewLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ReviewLikeService reviewLikeService;

    private ObjectMapper objectMapper = new ObjectMapper();



    @Test
    void createLike() throws Exception{

        Long userId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewLikeService).createReviewLike(eq(userId), eq(reviewId));

        mockMvc.perform(post("/api/reviews/{reviewId}/likes", reviewId)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());

    }

    @Test
    void removeLike() throws Exception{

        Long userId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewLikeService).createReviewLike(eq(userId), eq(reviewId));

        mockMvc.perform(delete("/api/reviews/{reviewId}/likes", reviewId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());
    }

//    @Test
//    void getLikeCount() throws Exception{
//
//        Long reviewId = 2L; // 리뷰 ID
//        long likeCount = 10L; // 좋아요 개수
//
//        // Mocking
//        when(reviewLikeService.getReviewLikeCount(anyLong())).thenReturn(likeCount);
//
//        // API 호출 및 검증
//        mockMvc.perform(get("/api/reviews/{reviewId}/likes/count", reviewId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").value(likeCount)); // JSON 응답 검증
//
//        // Service 호출 검증
//        verify(reviewLikeService, times(1)).getReviewLikeCount(eq(reviewId));
//
//
//    }
}