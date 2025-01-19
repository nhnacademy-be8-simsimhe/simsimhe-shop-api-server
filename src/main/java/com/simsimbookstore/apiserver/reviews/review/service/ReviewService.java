package com.simsimbookstore.apiserver.reviews.review.service;

import com.simsimbookstore.apiserver.reviews.review.dto.*;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import org.springframework.data.domain.Page;

public interface ReviewService {

    Review createReview(ReviewRequestDTO requestDTO, Long bookId, Long userId);

    Review updateReview(ReviewRequestDTO dto, Long reviewId);


    Page<ReviewLikeCountDTO> getReviewsByBookOrderBySort(Long bookId, Long userId, int page, int size, String sort);

    Page<Review> getReviewsByBook(Long bookId, int page, int size);

    ReviewResponseDTO getReviewById(Long reviewId);

    void deleteReview(Long reviewId);


    boolean isPhotoReview(Long reviewId);

    Page<UserReviewsDTO> getUserReviews(Long userId, int page, int size);

    Page<UserAvailableReviewsDTO> getAvailableReviews(Long userId, int page, int size);
}
