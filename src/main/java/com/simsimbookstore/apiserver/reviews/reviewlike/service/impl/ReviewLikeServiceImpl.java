package com.simsimbookstore.apiserver.reviews.reviewlike.service.impl;

import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.reviews.review.repository.ReviewRepository;
import com.simsimbookstore.apiserver.reviews.reviewlike.entity.ReviewLike;
import com.simsimbookstore.apiserver.reviews.reviewlike.repository.ReviewLikeRepository;
import com.simsimbookstore.apiserver.reviews.reviewlike.service.ReviewLikeService;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class ReviewLikeServiceImpl implements ReviewLikeService {


    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 좋아요 추가
     */
    @Transactional
    public void createReviewLike(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));


        boolean alreadyLiked = reviewLikeRepository.findByUserAndReview(user, review).isPresent();
        if (alreadyLiked) {
            throw new IllegalStateException("이미 좋아요를 누른 리뷰입니다.");
        }


        ReviewLike reviewLike = ReviewLike.builder()
                                .user(user)
                                .review(review)
                                .created_at(LocalDateTime.now())
                                .build();

        reviewLikeRepository.save(reviewLike);
    }

    /**
     * 좋아요 삭제
     */
    @Transactional
    public void deleteReviewLike(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));


        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new NotFoundException("좋아요가 존재하지 않습니다."));

        // 좋아요 삭제
        reviewLikeRepository.delete(reviewLike);
    }

    /**
     * 리뷰에 대한 좋아요 수 조회
     */
    public long getReviewLikeCount(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));
        return reviewLikeRepository.countByReview(review);
    }
}
