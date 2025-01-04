package com.simsimbookstore.apiserver.carts.repository;

import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;

public interface CartCustomRepository {

    CartResponseDto getBookForCart(Long bookId);
}
