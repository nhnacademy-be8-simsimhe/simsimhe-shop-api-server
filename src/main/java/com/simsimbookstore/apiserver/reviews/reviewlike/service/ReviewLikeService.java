package com.simsimbookstore.apiserver.reviews.reviewlike.service;

import com.simsimbookstore.apiserver.reviews.review.dto.ReviewRequestDTO;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.reviewlike.entity.ReviewLike;
import org.springframework.data.domain.Page;

public interface ReviewLikeService {


    void createReviewLike(Long userId, Long reviewId);
    void deleteReviewLike(Long userId, Long reviewId);
    long getReviewLikeCount(Long reviewId);

}
