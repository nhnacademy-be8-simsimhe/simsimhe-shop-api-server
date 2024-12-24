package com.simsimbookstore.apiserver.reviews.dto;

import java.time.LocalDateTime;

public class ReviewLikeCountDTO {
    long review_id;
    String title;
    String content;
    LocalDateTime createdAt;
    String userName;

    public ReviewLikeCountDTO(long review_id, String title, String content, LocalDateTime createdAt, String userName, long likeCount, long commentCount) {
        this.review_id = review_id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userName = userName;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    long likeCount;
    long commentCount;


}
