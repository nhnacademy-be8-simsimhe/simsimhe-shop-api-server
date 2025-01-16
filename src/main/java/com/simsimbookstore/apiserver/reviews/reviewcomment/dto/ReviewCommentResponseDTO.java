package com.simsimbookstore.apiserver.reviews.reviewcomment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCommentResponseDTO {


    private Long reviewCommentId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String userName;

    private Long userId;

}
