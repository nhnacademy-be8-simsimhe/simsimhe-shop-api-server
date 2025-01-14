package com.simsimbookstore.apiserver.reviews.reviewcomment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewCommentResponseDTO {


    Long reviewCommentId;

    String content;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    String userName;

    Long userId;

}
