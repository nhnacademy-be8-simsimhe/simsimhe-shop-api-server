package com.simsimbookstore.apiserver.like.service;

import com.simsimbookstore.apiserver.like.dto.BookLikeRequestDto;
import com.simsimbookstore.apiserver.like.dto.BookLikeResponseDto;

public interface BookLikeService {

    BookLikeResponseDto setBookLike(BookLikeRequestDto requestDto);

    Long getUserLikeNum(Long userId);
}
