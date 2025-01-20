package com.simsimbookstore.apiserver.books.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.category.dto.CategoryRequestDto;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.service.CategoryService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CategoryService categoryService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CategoryService categoryService() {
            return Mockito.mock(CategoryService.class);
        }
    }

    private CategoryResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = CategoryResponseDto.builder()
                .categoryId(1L)
                .categoryName("테스트 카테고리")
                .build();
    }

    @Test
    @DisplayName("카테고리 등록")
    void saveCategory() throws Exception {
        CategoryRequestDto requestDto = CategoryRequestDto.builder().categoryName("테스트 카테고리").build();
        Mockito.when(categoryService.createCategory(any(CategoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("테스트 카테고리"));
    }

    @Test
    @DisplayName("모든 카테고리 조회 - 데이터 있음")
    void getAllCategories() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/admin/categories/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1L))
                .andExpect(jsonPath("$[0].categoryName").value("테스트 카테고리"));
    }

    @Test
    @DisplayName("모든 카테고리 조회 - 데이터 없음")
    void getAllCategories_Empty() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/categories/list"))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("단일 카테고리 조회")
    void getCategoryById() throws Exception {
        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/admin/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("테스트 카테고리"));
    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/admin/categories/1"))
                .andExpect(status().isNoContent());
    }
}
