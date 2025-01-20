package com.simsimbookstore.apiserver.reviews.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewResponseDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ReviewControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ReviewService reviewService;

    @BeforeEach
    void setup() {

        reviewService = mock(ReviewService.class);
        ReviewController reviewController = new ReviewController(reviewService);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("리뷰 생성 테스트")
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

        mockMvc.perform(post("/api/shop/books/{bookId}/reviews", bookId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"score\": 5,\"title\": \"소년이 온다\",\"content\": \"I loved this book!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.title").value("소년이 온다"))
                .andExpect(jsonPath("$.content").value("I loved this book!"));

        verify(reviewService).createReview(any(ReviewRequestDTO.class), eq(bookId), eq(userId));
    }


    @Test
    @DisplayName("도서 리뷰 목록 조회 테스트 (최근순)")
    void getAllReviewsTest() throws Exception {

        Long bookId = 1L;
        Long userId = 1L;
        int page = 0;
        int size = 10;
        String sort = "latest";

        var reviews = List.of(
                new ReviewLikeCountDTO(1L, "good book", "I loved this book!", LocalDateTime.now(), "mingyeong", 4, 12L, 12L),
                new ReviewLikeCountDTO(2L, "great book", "Interesting", LocalDateTime.now(), "hello", 3, 20L, 9L)
        );

        var pageable = PageRequest.of(page, size);
        var response = new PageImpl<>(reviews, pageable, reviews.size());


        when(reviewService.getReviewsByBookOrderBySort(bookId, userId, page, size, sort))
                .thenReturn(response);


        mockMvc.perform(get("/api/shop/books/{bookId}/reviews", bookId)
                        .param("userId", String.valueOf(userId))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", "latest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].score").value(4))
                .andExpect(jsonPath("$.content[0].title").value("good book"))
                .andExpect(jsonPath("$.content[1].score").value(3))
                .andExpect(jsonPath("$.content[1].title").value("great book"))
                .andExpect(jsonPath("$.content[1].content").value("Interesting"));


        verify(reviewService).getReviewsByBookOrderBySort(bookId, userId, page, size, sort);

    }

    @Test
    @DisplayName("단일 리뷰 조회하기")
    void getReviewById() throws Exception {
        Long bookId = 1L;
        Long reviewId = 1L;
        Long userId = 1L;

        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO(
                reviewId,
                "리뷰입니다.",
                "정말 재밌어요!",
                4,
                userId,
                bookId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(reviewService.getReviewById(reviewId)).thenReturn(reviewResponseDTO);

        mockMvc.perform(get("/api/shop/books/{bookId}/reviews/{reviewId}", bookId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("정말 재밌어요!"))
                .andExpect(jsonPath("$.score").value(4));

    }

    @Test
    @DisplayName("리뷰 수정 테스트")
    void updateReviewTest() throws Exception {
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
        mockMvc.perform(post("/api/shop/books/{bookId}/reviews/{reviewId}", bookId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
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
    void deleteReviewTest() throws Exception {
        Long bookId = 1L;
        Long reviewId = 2L;

        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/api/shop/books/{bookId}/reviews/{reviewId}", bookId, reviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(reviewId);
    }
}