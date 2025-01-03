package com.simsimbookstore.apiserver.reviews.reviewcomment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCommentRequestDTO {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(min = 5, message = "최소 5글자 이상 입랙해주세요.")
    String content;

}
