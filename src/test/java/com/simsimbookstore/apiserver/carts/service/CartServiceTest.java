package com.simsimbookstore.apiserver.carts.service;

import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;
import com.simsimbookstore.apiserver.carts.repository.CartRepository;
import com.simsimbookstore.apiserver.exception.BadRequestException;

import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("장바구니에 담을 도서 조회 - 정상 케이스")
    void getBookForCart_Success() {

        CartResponseDto mockResponse = new CartResponseDto();
        mockResponse.setBookId(1L);
        mockResponse.setBookQuantity(10);
        mockResponse.setQuantity(3);

        when(cartRepository.getBookForCart(anyLong())).thenReturn(mockResponse);

        CartResponseDto result = cartService.getBookForCart("1", 3);

        assertNotNull(result);
        assertEquals(3, result.getQuantity());
        verify(cartRepository, times(1)).getBookForCart(anyLong());
    }

    @Test
    @DisplayName("장바구니에 담을 도서 조회 - 재고 부족 예외")
    void getBookForCart_InsufficientStock() {

        CartResponseDto mockResponse = new CartResponseDto();
        mockResponse.setBookId(1L);
        mockResponse.setBookQuantity(5);
        mockResponse.setQuantity(10);

        when(cartRepository.getBookForCart(anyLong())).thenReturn(mockResponse);


        assertThrows(BadRequestException.class, () -> cartService.getBookForCart("1", 10));
        verify(cartRepository, times(1)).getBookForCart(anyLong());
    }


}