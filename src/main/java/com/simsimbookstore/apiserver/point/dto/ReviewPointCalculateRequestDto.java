package com.simsimbookstore.apiserver.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewPointCalculateRequestDto {
    Long reviewId;
    Long userId;
}
