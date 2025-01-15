package com.simsimbookstore.apiserver.reviews.reviewlike.service;


public interface ReviewLikeService {


    void createReviewLike(Long userId, Long reviewId);
    void deleteReviewLike(Long userId, Long reviewId);
    long getReviewLikeCount(Long reviewId);

}
