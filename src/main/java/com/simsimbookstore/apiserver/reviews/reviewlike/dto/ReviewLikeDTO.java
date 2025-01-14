package com.simsimbookstore.apiserver.reviews.reviewlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewLikeDTO {

    private Long reviewLikeId;

    private LocalDateTime created_at;

    private Long reviewId;

    private Long userId;
}
