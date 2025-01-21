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

}
