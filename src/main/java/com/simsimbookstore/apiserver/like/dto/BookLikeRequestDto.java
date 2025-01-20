package com.simsimbookstore.apiserver.like.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookLikeRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long bookId;


}
