package com.simsimbookstore.apiserver.reviews.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.ArgumentMatchers.*;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ReviewController.class)
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private QuerydslConfig querydslConfig;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void saveReviewTest() throws Exception {

        Long bookId = 1L;
        Long userId = 2L;

        ReviewRequestDTO request = new ReviewRequestDTO(5, "소년이 온다", "I loved this book!");
        Review response = Review.builder()
                .reviewId(1L)
                .score(5)
                .title("소년이 온다")
                .content("I loved this book!")
                .build();

        when(reviewService.createReview(any(ReviewRequestDTO.class), eq(bookId), eq(userId)))
                .thenReturn(response);

        mockMvc.perform(post("/api/books/{bookId}/reviews", bookId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\": 5,\"title\": \"소년이 온다\",\"content\": \"I loved this book!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.title").value("소년이 온다"))
                .andExpect(jsonPath("$.content").value("I loved this book!"));

        verify(reviewService).createReview(any(ReviewRequestDTO.class), eq(bookId), eq(userId));
    }


    @Test
    @DisplayName("리뷰 목록 조회 테스트")
    void getAllReviewsTest() throws Exception{

        Long bookId = 1L;
        int page = 0;
        int size = 10;

        var reviews = List.of(
                new ReviewLikeCountDTO(1L, "good book", "I loved this book!", LocalDateTime.now(),"mingyeong", 4,12L, 12L),
                new ReviewLikeCountDTO(2L, "great book", "Interesting", LocalDateTime.now(),"hello", 3,20L, 9L)
        );

        var pageable = PageRequest.of(page, size);
        var response = new PageImpl<>(reviews, pageable, reviews.size());


        when(reviewService.getReviewsByBookOrderByRecent(eq(bookId), eq(page), eq(size)))
                .thenReturn(response);



        mockMvc.perform(get("/api/books/{bookId}/reviews", bookId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].score").value(5))
                .andExpect(jsonPath("$.content[0].title").value("good book"))
                .andExpect(jsonPath("$.content[1].score").value(4))
                .andExpect(jsonPath("$.content[1].title").value("great book"))
                .andExpect(jsonPath("$.content[1].content").value("Interesting"));


        verify(reviewService).getReviewsByBookOrderByRecent(eq(bookId), eq(page), eq(size));

    }

    @Test
    @DisplayName("리뷰 수정 테스트")
    void updateReviewTest() throws Exception{
        Long bookId = 1L;
        Long reviewId = 2L;
        ReviewRequestDTO request = new ReviewRequestDTO(4, "Updated Title", "Updated Content");
        Review response = Review.builder()
                .reviewId(reviewId)
                .score(4)
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(reviewService.updateReview(any(ReviewRequestDTO.class), eq(reviewId)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/books/{bookId}/reviews/{reviewId}", bookId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(2L))
                .andExpect(jsonPath("$.score").value(4))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));

        verify(reviewService).updateReview(any(ReviewRequestDTO.class), eq(reviewId));


    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    void deleteReviewTest() throws Exception{
        Long bookId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewService).deleteReview(eq(reviewId));

        mockMvc.perform(delete("/api/books/{bookId}/reviews/{reviewId}", bookId, reviewId))
                .andExpect(status().isNoContent())
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(eq(reviewId));
    }
}