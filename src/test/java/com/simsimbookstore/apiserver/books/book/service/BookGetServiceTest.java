package com.simsimbookstore.apiserver.books.book.service;

import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.book.entity.Book;

import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.exception.NotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookGetServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookGetService bookGetService;



    /**
     * 책 수정 조회 테스트
     */
    @Test
    @DisplayName("책 수정을 위한 상세 조회 테스트 - 성공")
    void getUpdateBook_Success() {
        // Given
        Book book = createSampleBook();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // When
        BookResponseDto responseDto = bookGetService.getUpdateBook(1L);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo(book.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("책 수정을 위한 상세 조회 테스트 - 실패(책 없음)")
    void getUpdateBook_Fail_NotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> bookGetService.getUpdateBook(1L));
        verify(bookRepository, times(1)).findById(1L);
    }

    /**
     * 모든 도서 조회 테스트
     */
    @Test
    @DisplayName("모든 도서 조회 - 페이징 적용")
    void getAllBook() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<BookListResponse> pageResponse = new PageImpl<>(List.of(new BookListResponse()), pageable, 10);

        when(bookRepository.getAllBook(pageable)).thenReturn(pageResponse);

        // When
        PageResponse<BookListResponse> response = bookGetService.getAllBook(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(10);
        verify(bookRepository, times(1)).getAllBook(pageable);
    }

    /**
     * 최신 도서 8개 조회 테스트
     */
    @Test
    @DisplayName("최신 도서 8개 조회")
    void getNewBooks() {
        // Given
        when(bookRepository.getNewBookList()).thenReturn(List.of(new BookListResponse(), new BookListResponse()));

        // When
        List<BookListResponse> newBooks = bookGetService.getNewBooks();

        // Then
        assertThat(newBooks).isNotEmpty();
        verify(bookRepository, times(1)).getNewBookList();
    }

    /**
     * 책 상세 정보 조회 테스트
     */
    @Test
    @DisplayName("책 상세 정보 조회")
    void getBookDetail() {
        // Given
        when(bookRepository.getBookDetail(1L, 1L)).thenReturn(new BookResponseDto());

        // When
        BookResponseDto bookDetail = bookGetService.getBookDetail(1L, 1L);

        // Then
        assertThat(bookDetail).isNotNull();
        verify(bookRepository, times(1)).getBookDetail(1L, 1L);
    }

    /**
     * 주문 전 책 상태 확인 테스트
     */
    @Test
    @DisplayName("주문 전 책 상태 확인")
    void getBooksForCheck() {
        // Given
        when(bookRepository.getBooksForCheck(List.of(1L, 2L))).thenReturn(List.of(new BookListResponse()));

        // When
        List<BookListResponse> books = bookGetService.getBooksForCheck(List.of(1L, 2L));

        // Then
        assertThat(books).isNotEmpty();
        verify(bookRepository, times(1)).getBooksForCheck(List.of(1L, 2L));
    }

    /**
     * 좋아요한 책 목록 조회 테스트
     */
    @Test
    @DisplayName("좋아요한 책 목록 조회")
    void getUserLikeBook() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<BookListResponse> pageResponse = new PageImpl<>(List.of(new BookListResponse()), pageable, 5);

        when(bookRepository.getUserLikeBook(pageable, 1L)).thenReturn(pageResponse);

        // When
        PageResponse<BookListResponse> userLikedBooks = bookGetService.getUserLikeBook(1L, pageable);

        // Then
        assertThat(userLikedBooks).isNotNull();
        verify(bookRepository, times(1)).getUserLikeBook(pageable, 1L);
    }

    /**
     * 특정 카테고리 및 하위 카테고리 책 조회 테스트
     */
    @Test
    @DisplayName("특정 카테고리 및 하위 카테고리 책 조회")
    void getBooksByCategory() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<BookListResponse> pageResponse = new PageImpl<>(List.of(new BookListResponse()), pageable, 5);

        when(bookRepository.getBookListByCategory(1L, 1L, pageable)).thenReturn(pageResponse);

        // When
        PageResponse<BookListResponse> booksByCategory = bookGetService.getBooksByCategory(1L, 1L, pageable);

        // Then
        assertThat(booksByCategory).isNotNull();
        verify(bookRepository, times(1)).getBookListByCategory(1L, 1L, pageable);
    }

    /**
     * 특정 태그에 맞는 도서 조회 테스트
     */
    @Test
    @DisplayName("특정 태그에 맞는 도서 조회")
    void getBooksByTag() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<BookListResponse> pageResponse = new PageImpl<>(List.of(new BookListResponse()), pageable, 5);

        when(bookRepository.getBookListByTag(1L, 1L, pageable)).thenReturn(pageResponse);

        // When
        PageResponse<BookListResponse> booksByTag = bookGetService.getBooksByTag(1L, 1L, pageable);

        // Then
        assertThat(booksByTag).isNotNull();
        verify(bookRepository, times(1)).getBookListByTag(1L, 1L, pageable);
    }

    /**
     * 인기 도서 6개 조회 테스트
     */
    @Test
    @DisplayName("인기 도서 6개 조회")
    void getPopularityBook() {
        // Given
        when(bookRepository.getPopularityBook()).thenReturn(List.of(new BookListResponse()));

        // When
        List<BookListResponse> popularityBooks = bookGetService.getPopularityBook();

        // Then
        assertThat(popularityBooks).isNotEmpty();
        verify(bookRepository, times(1)).getPopularityBook();
    }

    /**
     * 특정 도서를 제외한 동일 카테고리 내 인기 도서 추천 기능 테스트
     */
    @Test
    @DisplayName("특정 도서를 제외한 동일 카테고리 내 인기 도서 추천 기능")
    void getRecommendBooks() {
        // Given
        when(bookRepository.getRecommendBook(List.of(1L, 2L), 1L)).thenReturn(List.of(new BookListResponse()));

        // When
        List<BookListResponse> recommendBooks = bookGetService.getRecommendBooks(List.of(1L, 2L), 1L);

        // Then
        assertThat(recommendBooks).isNotEmpty();
        verify(bookRepository, times(1)).getRecommendBook(List.of(1L, 2L), 1L);
    }

    private Book createSampleBook() {
        return Book.builder()
                .bookId(1L)
                .title("Sample Book")
                .price(BigDecimal.valueOf(100))
                .build();
    }
}
