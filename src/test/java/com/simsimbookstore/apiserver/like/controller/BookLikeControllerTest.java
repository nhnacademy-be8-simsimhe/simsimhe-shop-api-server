package com.simsimbookstore.apiserver.like.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.like.dto.BookLikeRequestDto;
import com.simsimbookstore.apiserver.like.dto.BookLikeResponseDto;
import com.simsimbookstore.apiserver.like.service.BookLikeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookLikeController.class)
class BookLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookLikeService bookLikeService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BookLikeService bookLikeService() {
            return Mockito.mock(BookLikeService.class);
        }
    }

    @Test
    @DisplayName("좋아요를 설정 취소하는 api")
    void setBookLike() throws Exception {
        BookLikeRequestDto requestDto = BookLikeRequestDto.builder().bookId(1L)
                .userId(1L).build();

        BookLikeResponseDto responseDto = BookLikeResponseDto.builder()
                .userName("임채환")
                .isbn("1234567891234")
                .isLiked(true).build();

        Mockito.when(bookLikeService.setBookLike(any(BookLikeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/shop/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("임채환"))
                .andExpect(jsonPath("$.isbn").value("1234567891234"))
                .andExpect(jsonPath("$.liked").value(true));
    }

    @Test
    @DisplayName("좋아요 누른 총 개수 API 테스트")
    void getUserLikesNum() throws Exception {

        Long userId = 1L;
        Long userLikeCount = 5L;

        Mockito.when(bookLikeService.getUserLikeNum(userId)).thenReturn(userLikeCount);


        mockMvc.perform(get("/api/shop/likes/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userLikeCount));
    }

}