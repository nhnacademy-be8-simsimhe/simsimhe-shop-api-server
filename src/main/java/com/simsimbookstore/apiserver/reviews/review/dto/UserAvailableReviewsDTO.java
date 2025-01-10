package com.simsimbookstore.apiserver.reviews.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserAvailableReviewsDTO {
    private Long bookId;
    private String title;
    private String contributor;
    private String bookImagePath;
    private LocalDateTime orderDate;

}
