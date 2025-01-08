package com.simsimbookstore.apiserver.carts.service;

import com.simsimbookstore.apiserver.carts.dto.CartRequestDto;
import com.simsimbookstore.apiserver.carts.entity.Cart;
import com.simsimbookstore.apiserver.carts.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Test
    public void testCartToDb() {
        Long userId = 1L;

        // 테스트용 데이터 생성
        CartRequestDto cartRequestDto = CartRequestDto.builder()
                .bookId("1")
                .quantity(2)
                .userId(String.valueOf(userId))
                .build();

        List<CartRequestDto> requestDtoList = List.of(cartRequestDto);

        // 메서드 실행
        cartService.CartToDb(userId, requestDtoList);

        // 결과 검증
        List<Cart> carts = cartRepository.findAll();
        assertFalse(carts.isEmpty());
        assertEquals(userId, carts.get(0).getUser().getUserId());
    }
}
