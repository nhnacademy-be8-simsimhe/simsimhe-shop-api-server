package com.simsimbookstore.apiserver.reviews.review.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewLikeCountDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.dto.ReviewResponseDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import org.springframework.data.domain.Page;

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




}
