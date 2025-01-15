package com.simsimbookstore.apiserver.books.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.dto.TagRequestDto;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.books.tag.service.TagService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(SpringExtension.class)
@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TagService tagService() {
            return Mockito.mock(TagService.class);
        }
    }

    @Autowired
    private TagService tagService;

    @Test
    @DisplayName("태그 생성 API")
    void createTag() throws Exception {
        TagRequestDto requestDto = TagRequestDto.builder().tagName("test").build();
        TagResponseDto responseDto = TagResponseDto.builder().tagId(1L).tagName("test").build();

        Mockito.when(tagService.createTag(any(TagRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.tagName").value("test"));
    }

    @Test
    @DisplayName("모든 태그 조회 api")
    void findAllTag() throws Exception {
        List<TagResponseDto> tags = new ArrayList<>();
        TagResponseDto responseDto1 = TagResponseDto.builder().tagId(1L).tagName("test1").build();
        TagResponseDto responseDto2 = TagResponseDto.builder().tagId(2L).tagName("test2").build();

        tags.add(responseDto1);
        tags.add(responseDto2);

        Mockito.when(tagService.getAlltag()).thenReturn(tags);

        mockMvc.perform(get("/api/admin/tags/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tagId").value(1L))
                .andExpect(jsonPath("$[0].tagName").value("test1"));
    }

    @Test
    @DisplayName("태그 단일 조회 API 테스트")
    void getTagById() throws Exception {
        Tag tag = Tag.builder().tagId(1L).tagName("test").build();

        // 서비스 계층의 `getTag` 메서드 모킹
        Mockito.when(tagService.getTag(1L)).thenReturn(tag);

        // API 호출 및 검증
        mockMvc.perform(get("/api/admin/tags/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.tagName").value("test"));

        // 서비스 메서드 호출 검증
        Mockito.verify(tagService, Mockito.times(1)).getTag(1L);
    }


    @Test
    @DisplayName("태그 삭제 api")
    void deleteTag() throws Exception {
        Mockito.doNothing().when(tagService).deleteTag(1L);

        mockMvc.perform(delete("/api/admin/tags/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(tagService, Mockito.times(1)).deleteTag(1L);
    }

    @Test
    @DisplayName("태그 수정 api")
    void updateTag() throws Exception {
        TagRequestDto requestDto = TagRequestDto.builder().tagName("updateTag").build();
        TagResponseDto responseDto = TagResponseDto.builder().tagId(1L).tagName("updateTag").build();

        Mockito.when(tagService.updateTag(eq(1L), any(TagRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/admin/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.tagName").value("updateTag"));
    }


}