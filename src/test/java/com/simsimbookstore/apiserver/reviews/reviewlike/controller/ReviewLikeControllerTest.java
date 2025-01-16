package com.simsimbookstore.apiserver.reviews.reviewlike.controller;


import com.simsimbookstore.apiserver.reviews.reviewlike.service.ReviewLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    void createLike() throws Exception{

        Long userId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewLikeService).createReviewLike(eq(userId), eq(reviewId));

        mockMvc.perform(post("/api/shop/reviews/{reviewId}/likes", reviewId)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());

    }

    @Test
    void removeLike() throws Exception{

        Long userId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewLikeService).createReviewLike(eq(userId), eq(reviewId));

        mockMvc.perform(delete("/api/shop/reviews/{reviewId}/likes", reviewId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());
    }

    @Test
    void getLikeCount() throws Exception{

        Long reviewId = 2L;
        long likeCount = 10L;


        when(reviewLikeService.getReviewLikeCount(eq(reviewId))).thenReturn(likeCount);


        mockMvc.perform(get("/api/shop/reviews/{reviewId}/likes/count", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/Long").string("10")); // JSON 응답 검증


        verify(reviewLikeService, times(1)).getReviewLikeCount(eq(reviewId));


    }
}