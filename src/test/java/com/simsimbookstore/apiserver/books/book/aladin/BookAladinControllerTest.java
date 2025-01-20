package com.simsimbookstore.apiserver.books.book.aladin;

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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(BookAladinController.class)
class BookAladinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AladinApiService aladinApiService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AladinApiService aladinApiService() {
            return Mockito.mock(AladinApiService.class);
        }
    }

    @Test
    @DisplayName("알라딘 API 도서 정보 정상 호출 테스트")
    void fetchBooks_Success() throws Exception {
        // Given
        doNothing().when(aladinApiService).fetchAndSaveBestsellerBooks();

        // When & Then
        mockMvc.perform(get("/api/books/aladin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Books have been fetched and saved successfully."));


    }

    @Test
    @DisplayName("알라딘 API 호출 실패 테스트")
    void fetchBooks_Failure() throws Exception {
        // Given
        doThrow(new RuntimeException("API 호출 오류")).when(aladinApiService).fetchAndSaveBestsellerBooks();

        // When & Then
        mockMvc.perform(get("/api/books/aladin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 응답 상태는 200이지만 에러 메시지를 반환해야 함
                .andExpect(content().string("Error occurred: API 호출 오류"));

        verify(aladinApiService, times(1)).fetchAndSaveBestsellerBooks();
    }


}