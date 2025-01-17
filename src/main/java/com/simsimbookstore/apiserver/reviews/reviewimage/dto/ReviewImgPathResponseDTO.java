package com.simsimbookstore.apiserver.reviews.reviewimage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ReviewImgPathResponseDTO {

    private Long reviewImagePathId;

    private String imageName;

    private Long reviewId;
}
