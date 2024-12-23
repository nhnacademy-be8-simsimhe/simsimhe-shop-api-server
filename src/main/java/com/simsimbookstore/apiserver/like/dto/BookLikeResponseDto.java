package com.simsimbookstore.apiserver.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookLikeResponseDto {

    private String userName;

    private String isbn;

    private boolean isLiked;
}
