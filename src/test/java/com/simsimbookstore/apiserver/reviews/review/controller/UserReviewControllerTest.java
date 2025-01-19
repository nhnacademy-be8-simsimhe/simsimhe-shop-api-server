package com.simsimbookstore.apiserver.reviews.review.controller;

import co.elastic.clients.elasticsearch.license.LicenseStatus;
import com.simsimbookstore.apiserver.reviews.review.dto.UserAvailableReviewsDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.UserReviewsDTO;
import com.simsimbookstore.apiserver.reviews.review.service.ReviewService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserReviewController.class)
@ExtendWith(MockitoExtension.class)
class UserReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReviewService reviewService;

    @TestConfiguration
    static class TestConfig{
        @Bean
        public ReviewService reviewService(){
            return mock(ReviewService.class);
        }
    }


    @Test
    @DisplayName("유저가 작성한 리뷰 가져오기")
    void getUserReviews() throws Exception {

        Long userId = 1L;
        int page = 0;
        int size = 10;

        List<UserReviewsDTO> reviewList = List.of(
                new UserReviewsDTO(   1L,
                        101L,
                        "Effective Java",
                        "Joshua Bloch",
                        "/images/effective_java.jpg",
                        "정말 좋은 책입니다!",
                        "이 책은 Java의 깊이를 알려줍니다.",
                        LocalDateTime.now().minusDays(1),
                        "user1",
                        1L,
                        5,
                        20,
                        5,
                        List.of("/images/review1.jpg", "/images/review2.jpg")),

                new UserReviewsDTO(   2L,
                        90L,
                        "위대한 개츠비",
                        "프랜시스 스콧 피츠제럴드",
                        "/images/the_great_gatsby.jpg",
                        "위대한 개츠비!",
                        "굿",
                        LocalDateTime.now(),
                        "user2",
                        2L,
                        4,
                        100,
                        2,
                        List.of("/images/review3.jpg", "/images/review4.jpg"))
        );

        Pageable pageable = PageRequest.of(page, size);

        Page<UserReviewsDTO> userReviews = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(reviewService.getUserReviews(userId, page, size)).thenReturn(userReviews);

        mockMvc.perform(get("/api/shop/users/{userId}/reviews", userId)
                .param("page", "0")
                .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("해당 유저가 작성한 가능한 리뷰 가져오기")
    void getEligibleBooksForReview() throws Exception {

        Long userId = 1L;
        int page = 0;
        int size = 10;

        List<UserAvailableReviewsDTO> reviewList = List.of(
                new UserAvailableReviewsDTO(   1L,
                        "title1",
                        "author1",
                        "/images/title1.jpg",
                        LocalDateTime.now()),
                new UserAvailableReviewsDTO(   101L,
                        "title2",
                        "author2",
                        "/images/title2.jpg",
                        LocalDateTime.now()),

                new UserAvailableReviewsDTO(   70L,
                        "title3",
                        "author3",
                        "/images/title3.jpg",
                        LocalDateTime.now())
        );

        Pageable pageable = PageRequest.of(page, size);

        Page<UserAvailableReviewsDTO> userReviews = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(reviewService.getAvailableReviews(userId, page, size)).thenReturn(userReviews);

        mockMvc.perform(get("/api/shop/users/{userId}/reviews/available", userId)
                        .param("page", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk());

    }





}