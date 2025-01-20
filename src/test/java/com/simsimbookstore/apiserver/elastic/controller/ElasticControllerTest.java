package com.simsimbookstore.apiserver.elastic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.elastic.dto.SearchBookDto;
import com.simsimbookstore.apiserver.elastic.entity.SearchBook;
import com.simsimbookstore.apiserver.elastic.service.ElasticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElasticController.class)
@ExtendWith(MockitoExtension.class)
class ElasticControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ElasticService elasticService;

    private ObjectMapper objectMapper;


    @TestConfiguration
    static class TestConfig {

        @Bean
        public ElasticService elasticService() {
            return mock(ElasticService.class);
        }
    }


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("도서 단일 저장 성공 테스트")
    void saveData_Success() throws Exception {
        SearchBookDto searchBookDto = new SearchBookDto(1L, "Test Title", "Description", "Author", "image.jpg", null, "2024-01-01", 1000);

        doNothing().when(elasticService).createBook(any(SearchBook.class));

        mockMvc.perform(post("/api/shop/elastic/document")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchBookDto)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(elasticService, times(1)).createBook(any(SearchBook.class));
    }

    @Test
    @DisplayName("모든 도서 저장 성공 테스트")
    void saveAll_Success() throws Exception {
        doNothing().when(elasticService).saveAll("simsim.json");

        mockMvc.perform(post("/api/shop/elastic/document/save"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("all data is saved"));

        verify(elasticService, times(1)).saveAll("simsim.json");
    }

    @Test
    @DisplayName("도서 검색 성공 테스트")
    void getDatas_Success() throws Exception {
        String keyword = "Test";
        String sort = "popular";
        int page = 1;

        when(elasticService.searchBookByWord(keyword, sort, page)).thenReturn(null);

        mockMvc.perform(get("/api/shop/elastic/document")
                        .param("keyword", keyword)
                        .param("sort", sort)
                        .param("page", String.valueOf(page)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(elasticService, times(1)).searchBookByWord(keyword, sort, page);
    }

    @Test
    @DisplayName("도서 삭제 성공 테스트")
    void deleteData_Success() throws Exception {
        String id = "1";
        doNothing().when(elasticService).deleteBook(id);

        mockMvc.perform(delete("/api/shop/elastic/document/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(elasticService, times(1)).deleteBook(id);
    }


}