package com.simsimbookstore.apiserver.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ReviewPointCalculateRequestDto {
    Long reviewId;
    Long userId;
}
