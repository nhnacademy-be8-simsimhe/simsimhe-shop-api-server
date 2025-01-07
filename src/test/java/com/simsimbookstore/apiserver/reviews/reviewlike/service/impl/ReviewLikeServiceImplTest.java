package com.simsimbookstore.apiserver.reviews.reviewlike.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewlike.entity.ReviewLike;
import com.simsimbookstore.apiserver.reviews.reviewlike.repository.ReviewLikeRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewLikeServiceImplTest {

    @InjectMocks
    private ReviewLikeServiceImpl reviewLikeService;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private User user;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 데이터 생성
        user = User.builder()
                .userId(1L)
                .userName("testUser")
                .build();

        review = Review.builder()
                .reviewId(1L)
                .title("testReview")
                .build();
    }

    @Test
    void createReviewLike_success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());

        // when
        reviewLikeService.createReviewLike(1L, 1L);

        // then
        verify(reviewLikeRepository, times(1)).save(any(ReviewLike.class));
    }

    @Test
    void createReviewLike_alreadyLiked() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.findByUserAndReview(user, review))
                .thenReturn(Optional.of(ReviewLike.builder().build()));

        // when & then
        assertThatThrownBy(() -> reviewLikeService.createReviewLike(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 좋아요를 누른 리뷰입니다.");
    }

    @Test
    void deleteReviewLike_success() {
        // given
        ReviewLike reviewLike = ReviewLike.builder()
                .user(user)
                .review(review)
                .created_at(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.findByUserAndReview(user, review)).thenReturn(Optional.of(reviewLike));

        // when
        reviewLikeService.deleteReviewLike(1L, 1L);

        // then
        verify(reviewLikeRepository, times(1)).delete(reviewLike);
    }

    @Test
    void deleteReviewLike_notFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewLikeService.deleteReviewLike(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("좋아요가 존재하지 않습니다.");
    }

    @Test
    void getReviewLikeCount_success() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.countByReview(review)).thenReturn(5L);

        // when
        long count = reviewLikeService.getReviewLikeCount(1L);

        // then
        assertThat(count).isEqualTo(5L);
    }

    @Test
    void getReviewLikeCount_notFound() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewLikeService.getReviewLikeCount(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 리뷰입니다.");
    }

}