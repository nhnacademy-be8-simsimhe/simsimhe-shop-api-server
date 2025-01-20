package com.simsimbookstore.apiserver.books.book.controller;


import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.book.service.BookGetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookGetController.class)
class BookGetControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private BookGetService bookGetService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BookGetService bookGetService() {
            return Mockito.mock(BookGetService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(bookGetService);
    }

    /**
     * 도서 단건 조회 테스트 (수정을 위한 조회)
     */
    @Test
    @DisplayName("도서 수정 상세 조회 API 테스트")
    void getBookByIdForUpdate() throws Exception {
        // Given
        BookResponseDto responseDto = new BookResponseDto();
        when(bookGetService.getUpdateBook(1L)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/shop/books/1/update"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getUpdateBook(1L);
    }

    /**
     * 모든 도서 목록 조회 (페이징 적용)
     */
    @Test
    @DisplayName("모든 도서 조회 - 페이징 적용")
    void getAllBooks() throws Exception {
        // Given
        PageResponse<BookListResponse> mockResponse = new PageResponse<>();
        when(bookGetService.getAllBook(any())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/shop/books")
                        .param("page", "1")
                        .param("size", "30"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getAllBook(any());
    }

    /**
     * 최신 출판된 도서 8권 조회
     */
    @Test
    @DisplayName("최신 도서 8권 조회 API 테스트")
    void getNewBooks() throws Exception {

        when(bookGetService.getNewBooks()).thenReturn(List.of(new BookListResponse()));


        mockMvc.perform(get("/api/shop/books/new"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getNewBooks();
    }

    /**
     * 도서 상세 조회 API 테스트
     */
    @Test
    @DisplayName("도서 상세 조회 API 테스트")
    void getBookById() throws Exception {

        BookResponseDto responseDto = new BookResponseDto();
        when(bookGetService.getBookDetail(1L, 1L)).thenReturn(responseDto);


        mockMvc.perform(get("/api/shop/books/1")
                        .param("userId", "1"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getBookDetail(1L, 1L);
    }

    /**
     * 주문 전 재고 확인 API 테스트
     */
    @Test
    @DisplayName("주문 전 재고 확인 API 테스트")
    void getBooksForQuantityCheck() throws Exception {

        when(bookGetService.getBooksForCheck(anyList())).thenReturn(List.of(new BookListResponse()));


        mockMvc.perform(get("/api/shop/books/check")
                        .param("bookIdList", "1,2,3"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getBooksForCheck(anyList());
    }

    /**
     * 사용자가 좋아요한 책 조회
     */
    @Test
    @DisplayName("사용자가 좋아요한 책 조회 API 테스트")
    void getUserLikeBook() throws Exception {

        PageResponse<BookListResponse> mockResponse = new PageResponse<>();
        when(bookGetService.getUserLikeBook(anyLong(), any())).thenReturn(mockResponse);


        mockMvc.perform(get("/api/shop/books/like/user/1")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getUserLikeBook(anyLong(), any());
    }

    /**
     * 카테고리별 도서 조회 API 테스트
     */
    @Test
    @DisplayName("카테고리별 도서 조회 API 테스트")
    void getBooksByCategory() throws Exception {

        PageResponse<BookListResponse> mockResponse = new PageResponse<>();
        when(bookGetService.getBooksByCategory(nullable(Long.class), anyLong(), any())).thenReturn(mockResponse);


        mockMvc.perform(get("/api/shop/books/category/1")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getBooksByCategory(nullable(Long.class), anyLong(), any());
    }

    /**
     * 인기 도서 조회 API 테스트
     */
    @Test
    @DisplayName("인기 도서 조회 API 테스트")
    void getPopularityBook() throws Exception {
        // Given
        when(bookGetService.getPopularityBook()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/shop/books/popularity"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getPopularityBook();
    }

    /**
     * 추천 도서 조회 API 테스트
     */
    @Test
    @DisplayName("추천 도서 조회 API 테스트")
    void getRecommendBooks() throws Exception {
        // Given
        when(bookGetService.getRecommendBooks(anyList(), anyLong())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/shop/books/1/recommend")
                        .param("categoryIdList", "1,2,3"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getRecommendBooks(anyList(), anyLong());
    }

    @Test
    @DisplayName("특정 태그에 속한 도서 조회 API 테스트")
    void getBooksByTag() throws Exception {
        // Given
        PageResponse<BookListResponse> mockResponse = new PageResponse<>();
        when(bookGetService.getBooksByTag(nullable(Long.class), anyLong(), any())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/shop/books/tag/1")
                        .param("page", "1")
                        .param("size", "16"))
                .andExpect(status().isOk());

        verify(bookGetService, times(1)).getBooksByTag(nullable(Long.class), anyLong(), any());
    }

}
