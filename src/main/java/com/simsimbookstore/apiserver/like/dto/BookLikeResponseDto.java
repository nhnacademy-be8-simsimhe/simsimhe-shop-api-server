package com.simsimbookstore.apiserver.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class BookLikeResponseDto {

    private String userName;

    private String isbn;

    private boolean isLiked;
}
