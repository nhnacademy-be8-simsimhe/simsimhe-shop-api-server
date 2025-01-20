package com.simsimbookstore.apiserver.like.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class BookLikeResponseDto {

    private String userName;

    private String isbn;

    private boolean isLiked;
}
