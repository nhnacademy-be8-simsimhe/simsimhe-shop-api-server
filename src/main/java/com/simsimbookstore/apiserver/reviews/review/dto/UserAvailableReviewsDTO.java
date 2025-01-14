package com.simsimbookstore.apiserver.reviews.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserAvailableReviewsDTO {
    private Long bookId;
    private String title;
    private String contributor;
    private String bookImagePath;
    private LocalDateTime orderDate;

}
