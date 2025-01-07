package com.simsimbookstore.apiserver.reviews.reviewlike.dto;

import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
