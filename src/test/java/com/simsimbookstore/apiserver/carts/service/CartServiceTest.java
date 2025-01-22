package com.simsimbookstore.apiserver.carts.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.carts.dto.CartRequestDto;
import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;
import com.simsimbookstore.apiserver.carts.repository.CartRepository;
import com.simsimbookstore.apiserver.exception.BadRequestException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    private User mockUser;
    private Book mockBook;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .userId(1L)
                .userName("Test User")
                .build();
        mockBook = Book.builder()
                .bookId(1L)
                .title("Test Book")
                .isbn("123456789")
                .build();
    }


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

    @Test
    @DisplayName("Redis 장바구니 데이터를 DB에 저장 - 정상 케이스")
    void cartToDb_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        CartRequestDto requestDto = new CartRequestDto(1, "1", "1");
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(mockBook));
        // When
        cartService.cartToDb(userId, List.of(requestDto));
        // Then
        verify(cartRepository, times(1)).deleteByUserId(userId);
        verify(cartRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Redis 장바구니 데이터를 DB에 저장 - 사용자 정보 없음 예외")
    void cartToDb_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        CartRequestDto requestDto = new CartRequestDto(1, "1", "1");
        // When & Then
        assertThrows(NotFoundException.class, () -> cartService.cartToDb(userId, List.of(requestDto)));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Redis 장바구니 데이터를 DB에 저장 - 도서 정보 없음 예외")
    void cartToDb_BookNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        CartRequestDto requestDto = new CartRequestDto(1, "1", "1");
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        // When & Then
        assertThrows(NotFoundException.class, () -> cartService.cartToDb(userId, List.of(requestDto)));
        verify(bookRepository, times(1)).findById(anyLong());
    }


}