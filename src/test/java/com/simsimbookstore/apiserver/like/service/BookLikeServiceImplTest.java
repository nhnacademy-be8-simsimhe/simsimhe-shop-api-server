package com.simsimbookstore.apiserver.like.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.like.dto.BookLikeRequestDto;
import com.simsimbookstore.apiserver.like.dto.BookLikeResponseDto;
import com.simsimbookstore.apiserver.like.entity.BookLike;
import com.simsimbookstore.apiserver.like.repository.BookLikeRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookLikeServiceImplTest {

    @InjectMocks
    private BookLikeServiceImpl bookLikeService;

    @Mock
    private BookLikeRepository bookLikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

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
    @DisplayName("회원이 도서에 좋아요 누르기")
    void testSetBookLike_LikeBook() {
        // Arrange
        BookLikeRequestDto requestDto = new BookLikeRequestDto(mockUser.getUserId(), mockBook.getBookId());

        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(mockBook.getBookId())).thenReturn(Optional.of(mockBook));

        //빈값을 반환한다고 가정,좋아요가 없다고 가정(Optional.empty() 반환).
        when(bookLikeRepository.findBookLike(mockBook.getBookId(), mockUser.getUserId())).thenReturn(Optional.empty());


        BookLike savedBookLike = BookLike.builder()
                .book(mockBook)
                .user(mockUser)
                .build();

        when(bookLikeRepository.save(any(BookLike.class))).thenReturn(savedBookLike);

        // Act
        BookLikeResponseDto responseDto = bookLikeService.setBookLike(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertTrue(responseDto.isLiked());
        assertEquals("123456789", responseDto.getIsbn());
        assertEquals("Test User", responseDto.getUserName());
        assertTrue(responseDto.isLiked());

        verify(bookLikeRepository, times(1)).save(any(BookLike.class));
    }

    @Test
    @DisplayName("이미 좋아요가 눌러져 있으면 좋아요 취소하기")
    void testSetBookLike_UnlikeBook() {
        // Arrange
        BookLikeRequestDto requestDto = new BookLikeRequestDto(mockUser.getUserId(), mockBook.getBookId());

        BookLike existingBookLike = BookLike.builder()
                .bookLikeId(1L)
                .book(mockBook)
                .user(mockUser)
                .build();

        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(mockBook.getBookId())).thenReturn(Optional.of(mockBook));
        when(bookLikeRepository.findBookLike(mockBook.getBookId(), mockUser.getUserId())).thenReturn(Optional.of(existingBookLike));

        doNothing().when(bookLikeRepository).deleteById(existingBookLike.getBookLikeId());

        // Act
        BookLikeResponseDto responseDto = bookLikeService.setBookLike(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertFalse(responseDto.isLiked());

        verify(bookLikeRepository, times(1)).deleteById(existingBookLike.getBookLikeId());
    }

    @Test
    @DisplayName("회원이없으면 에러")
    void testSetBookLike_UserNotFound() {
        // Arrange
        BookLikeRequestDto requestDto = new BookLikeRequestDto(999L, mockBook.getBookId());

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookLikeService.setBookLike(requestDto));
        verify(bookLikeRepository, never()).save(any());
    }

    @Test
    @DisplayName("도서가없으면 에러")
    void testSetBookLike_BookNotFound() {
        // Arrange
        BookLikeRequestDto requestDto = new BookLikeRequestDto(mockUser.getUserId(), 999L);

        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookLikeService.setBookLike(requestDto));
        verify(bookLikeRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자가 누른 좋아요의 총 개수를 반환")
    void testGetUserLikeNum() {
        // Arrange
        Long userId = 1L;
        when(bookLikeRepository.getUserLikeNum(userId)).thenReturn(5L);

        // Act
        Long likeCount = bookLikeService.getUserLikeNum(userId);

        // Assert
        assertNotNull(likeCount);
        assertEquals(5L, likeCount);

        verify(bookLikeRepository, times(1)).getUserLikeNum(userId);
    }
}
