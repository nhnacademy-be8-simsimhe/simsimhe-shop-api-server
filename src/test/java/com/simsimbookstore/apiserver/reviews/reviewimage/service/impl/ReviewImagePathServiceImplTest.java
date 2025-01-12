package com.simsimbookstore.apiserver.reviews.reviewimage.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewimage.dto.ReviewImgPathResponseDTO;
import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import com.simsimbookstore.apiserver.reviews.reviewimage.repository.ReviewImagePathRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReviewImagePathServiceImplTest {
    @InjectMocks
    private ReviewImagePathServiceImpl reviewImagePathService;

    @Mock
    private ReviewImagePathRepository reviewImagePathRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private Review review;
    private ReviewImagePath reviewImagePath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        review = Review.builder()
                .reviewId(1L)
                .title("Great Book")
                .content("This book is amazing!")
                .build();

        reviewImagePath = ReviewImagePath.builder()
                .reviewImagePathId(1L)
                .imageName("/images/review1/image1.jpg")
                .review(review)
                .build();
    }

    @Test
    @DisplayName("리뷰 이미지 추가 성공")
    void createReviewImage_success() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewImagePathRepository.save(any(ReviewImagePath.class))).thenReturn(reviewImagePath);

        List<ReviewImgPathResponseDTO> result = reviewImagePathService.createReviewImage(1L, List.of("/images/review1/image1.jpg"));

        assertThat(result).isNotNull();
        assertThat(result.get(0).getImageName()).isEqualTo("/images/review1/image1.jpg");
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewImagePathRepository, times(1)).save(any(ReviewImagePath.class));
    }

    @Test
    @DisplayName("리뷰 이미지 추가 실패 - 존재하지 않는 리뷰")
    void createReviewImage_reviewNotFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewImagePathService.createReviewImage(1L, List.of("/images/review1/image1.jpg")))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewImagePathRepository, never()).save(any(ReviewImagePath.class));
    }

    @Test
    @DisplayName("리뷰 이미지 삭제 성공")
    void deleteReviewImage_success() {
        when(reviewImagePathRepository.findById(anyLong())).thenReturn(Optional.of(reviewImagePath));

        reviewImagePathService.deleteReviewImage(1L);

        verify(reviewImagePathRepository, times(1)).findById(1L);
        verify(reviewImagePathRepository, times(1)).delete(reviewImagePath);
    }

    @Test
    @DisplayName("리뷰 이미지 삭제 실패 - 존재하지 않는 이미지")
    void deleteReviewImage_notFound() {
        when(reviewImagePathRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewImagePathService.deleteReviewImage(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이미지입니다.");
        verify(reviewImagePathRepository, times(1)).findById(1L);
        verify(reviewImagePathRepository, never()).delete(any(ReviewImagePath.class));
    }

    @Test
    @DisplayName("특정 리뷰에 대한 이미지 목록 조회 성공")
    void getImagesByReviewId_success() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewImagePathRepository.findByReview(any(Review.class)))
                .thenReturn(List.of(reviewImagePath));

        List<ReviewImagePath> result = reviewImagePathService.getImagesByReviewId(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageName()).isEqualTo("/images/review1/image1.jpg");

        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewImagePathRepository, times(1)).findByReview(review);
    }

    @Test
    @DisplayName("특정 리뷰에 대한 이미지 목록 조회 실패 - 존재하지 않는 리뷰")
    void getImagesByReviewId_notFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewImagePathService.getImagesByReviewId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");

        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewImagePathRepository, never()).findByReview(any(Review.class));
    }
}