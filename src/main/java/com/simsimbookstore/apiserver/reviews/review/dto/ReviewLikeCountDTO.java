package com.simsimbookstore.apiserver.reviews.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ReviewLikeCountDTO {
    private Long reviewId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String userName;
    private Long userId;
    private int score;
    private long likeCount;
    private long commentCount;
    private List<String> imagePaths;
    private boolean editable;
    private boolean deletable;
    private boolean userLiked;


    public ReviewLikeCountDTO(Long reviewId, String title, String content, LocalDateTime createdAt, String userName, int score, long likeCount, long commentCount) {
        this.reviewId = reviewId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userName = userName;
        this.score = score;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }


    public ReviewLikeCountDTO(Long reviewId, String title, String content, LocalDateTime createdAt,
                              String userName, int score, long likeCount, long commentCount, String imagePaths) {
        this.reviewId = reviewId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userName = userName;
        this.score = score;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.imagePaths = imagePaths != null ? Arrays.asList(imagePaths.split(",")) : null;
    }


}
