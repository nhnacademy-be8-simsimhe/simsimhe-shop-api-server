package com.simsimbookstore.apiserver.books.book.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.book.dto.BookGiftResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookRequestDto;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.BookStatusResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.service.BookManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(BookManagementController.class)
class BookManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookManagementService bookManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BookManagementService bookManagementService() {
            return Mockito.mock(BookManagementService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(bookManagementService);
    }

    @Test
    @DisplayName("도서 등록 API 테스트")
    void createBook_Success() throws Exception {
        // Given
        BookRequestDto requestDto = BookRequestDto.builder()
                .title("테스트 도서")
                .description("테스트 설명")
                .isbn("1234567890123")
                .price(BigDecimal.valueOf(10000))
                .saleprice(BigDecimal.valueOf(9000))
                .publisher("테스트 출판사")
                .quantity(10)
                .pages(100)
                .publicationDate(LocalDate.now())
                .bookIndex("test")
                .build();

        BookResponseDto responseDto = BookResponseDto.builder()
                .bookId(1L)
                .title("테스트 도서")
                .isbn("1234567890123")
                .price(BigDecimal.valueOf(10000))
                .saleprice(BigDecimal.valueOf(9000))
                .build();

        when(bookManagementService.registerBook(any(BookRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/admin/shop/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 도서"))
                .andExpect(jsonPath("$.isbn").value("1234567890123"));

        verify(bookManagementService, times(1)).registerBook(any(BookRequestDto.class));
    }

    @Test
    @DisplayName("도서 수정 API 테스트")
    void updateBook_Success() throws Exception {
        // Given
        BookRequestDto requestDto = BookRequestDto.builder()
                .title("수정된 도서 제목")
                .description("수정된 설명")
                .isbn("1234567890123")
                .price(BigDecimal.valueOf(10000))
                .saleprice(BigDecimal.valueOf(9000))
                .publisher("테스트 출판사")
                .quantity(10)
                .pages(100)
                .publicationDate(LocalDate.now())
                .bookIndex("test")
                .build();

        BookResponseDto responseDto = BookResponseDto.builder()
                .bookId(1L)
                .title("수정된 도서 제목")
                .isbn("1234567890123")
                .price(BigDecimal.valueOf(10000))
                .saleprice(BigDecimal.valueOf(9000))
                .build();

        when(bookManagementService.updateBook(eq(1L), any(BookRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/admin/shop/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1L))
                .andExpect(jsonPath("$.title").value("수정된 도서 제목"))
                .andExpect(jsonPath("$.isbn").value("1234567890123"));

        verify(bookManagementService, times(1)).updateBook(eq(1L), any(BookRequestDto.class));
    }

    @Test
    @DisplayName("도서 상태 수정 API 테스트")
    void modifyBookStatus_Success() throws Exception {
        // Given
        BookRequestDto requestDto = BookRequestDto.builder()
                .title("테스트 도서")
                .description("테스트 설명")
                .isbn("1234567890123")
                .price(BigDecimal.valueOf(10000))
                .saleprice(BigDecimal.valueOf(9000))
                .publisher("테스트 출판사")
                .quantity(10)
                .pages(100)
                .publicationDate(LocalDate.now())
                .bookIndex("test")
                .build();

        BookStatusResponseDto responseDto = BookStatusResponseDto.builder().bookStatus(BookStatus.ONSALE).build();

        when(bookManagementService.modifyBookStatus(eq(1L), any(BookRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/admin/shop/books/status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookStatus").value("ONSALE"));

        verify(bookManagementService, times(1)).modifyBookStatus(eq(1L), any(BookRequestDto.class));
    }

    @Test
    @DisplayName("도서 수량 수정 API 테스트")
    void modifyBookQuantity_Success() throws Exception {
        // Given
        when(bookManagementService.modifyQuantity(1L, 5)).thenReturn(15);

        // When & Then
        mockMvc.perform(put("/api/admin/shop/books/quantity/1")
                        .param("quantity", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));

        verify(bookManagementService, times(1)).modifyQuantity(1L, 5);
    }

    @Test
    @DisplayName("도서 선물 포장 수정 API 테스트")
    void modifyGift_Success() throws Exception {
        BookRequestDto requestDto = BookRequestDto.builder()
                .title("테스트 도서")
                .description("테스트 설명")
                .isbn("1234567890123")
                .price(BigDecimal.valueOf(10000))
                .saleprice(BigDecimal.valueOf(9000))
                .publisher("테스트 출판사")
                .quantity(10)
                .pages(100)
                .publicationDate(LocalDate.now())
                .bookIndex("test")
                .build();
        BookGiftResponse responseDto = BookGiftResponse.builder().giftPackaging(true).build();

        when(bookManagementService.modifyBookGift(eq(1L), any(BookRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/admin/shop/books/gift/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.giftPackaging").value(true));

        verify(bookManagementService, times(1)).modifyBookGift(eq(1L), any(BookRequestDto.class));
    }


}