package com.simsimbookstore.apiserver.reviews.review.dto;

import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class ReviewUpdateResponseDTO {


    private Long reviewId;
    private String title;
    private String content;
    private int score;
    private Long userId;
    private String userName;
    private Long bookId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewUpdateResponseDTO(Long reviewId, String title, String content, int score, LocalDateTime createdAt, Long userId, Long bookId) {
        this.reviewId = reviewId;
        this.title = title;
        this.content = content;
        this.score = score;
        this.createdAt = createdAt;
        this.userId = userId;
        this.bookId = bookId;
    }


    public ReviewUpdateResponseDTO(Review review) {
        this.reviewId = review.getReviewId();
        this.title = review.getTitle();
        this.content = review.getContent();
        this.score = review.getScore();
        this.updatedAt = review.getUpdateAt();
        this.createdAt = review.getCreatedAt();
    }


}
