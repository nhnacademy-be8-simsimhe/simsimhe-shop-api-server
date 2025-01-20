package com.simsimbookstore.apiserver.reviews.review.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ReviewRequestDTO {


    @NotNull(message = "평점을 입력해주세요.")
    @Min(value = 1, message = "점수는 최소 1점 이상이어야 합니다.")
    @Max(value = 5, message = "점수는 최대 5점 이하여야 합니다.")
    private int score;

    @NotBlank(message = "리뷰 제목을 입력해주세요.")
    @Size(min = 1)
    private String title;

    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    @Size(min = 1, message = "최소 1글자 이상 작성해주세요")
    private String content;


}
