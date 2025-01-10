package com.simsimbookstore.apiserver.reviews.review.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.reviews.review.dto.*;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Review createReview(ReviewRequestDTO requestDTO, Long bookId, Long userId);

    Review updateReview(ReviewRequestDTO dto, Long reviewId);

    Page<Review> getAllReviews(Long bookId, int page, int size);
//    Page<ReviewLikeCountDTO> getReviewsByBookOrderByScore(Long bookId, int page, int size);
//    Page<ReviewLikeCountDTO> getReviewsByBookOrderByLike(Long bookId, int page, int size);
    Page<ReviewLikeCountDTO> getReviewsByBookOrderByRecent(Long bookId, Long userId, int page, int size);

    Page<ReviewLikeCountDTO> getReviewsByUser(Long userId, Long bookId, int page, int size);

    ReviewResponseDTO getReviewById(Long reviewId);

    void deleteReview(Long reviewId);

    Page<UserReviewsDTO> getUserReviews(Long userId, int page, int size);
    Page<UserAvailableReviewsDTO> getAvailableReviews(Long userId, int page, int size);

}
