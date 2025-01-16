package com.simsimbookstore.apiserver.books.contributor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.service.ContributorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ContributorController.class)
class ContributorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ContributorService contributorService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ContributorService contributorService() {
            return Mockito.mock(ContributorService.class);
        }
    }

    @Test
    @DisplayName("기여자 등록 api")
    void createContributor() throws Exception {
        ContributorRequestDto requestDto = ContributorRequestDto.builder().contributorName("임채환").contributorRole("지은이").build();
        ContributorResponseDto responseDto = ContributorResponseDto.builder().contributorId(1L)
                .contributorName("임채환")
                .contributorRole("지은이").build();

        Mockito.when(contributorService.createContributor(any(ContributorRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/contributors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contributorId").value(1L))
                .andExpect(jsonPath("$.contributorName").value("임채환"))
                .andExpect(jsonPath("$.contributorRole").value("지은이"));
        Mockito.verify(contributorService, Mockito.times(1)).createContributor(requestDto);
    }

    @Test
    @DisplayName("모든 기여자 조회 api 페이징처리 x")
    void findAllContributors() throws Exception {
        List<ContributorResponseDto> contributorResponseDtos = new ArrayList<>();
        ContributorResponseDto responseDto1 = ContributorResponseDto.builder().contributorId(1L)
                .contributorName("임채환")
                .contributorRole("지은이").build();
        ContributorResponseDto responseDto2 = ContributorResponseDto.builder().contributorId(1L)
                .contributorName("박신지")
                .contributorRole("지은이").build();
        contributorResponseDtos.add(responseDto1);
        contributorResponseDtos.add(responseDto2);

        Mockito.when(contributorService.getAllContributor()).thenReturn(contributorResponseDtos);

        mockMvc.perform(get("/api/admin/contributors/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contributorName").value("임채환"))
                .andExpect(jsonPath("$[1].contributorName").value("박신지"));
        Mockito.verify(contributorService, Mockito.times(1)).getAllContributor();
    }

    @Test
    @DisplayName("기여자 페이징 처리 조회 API 테스트")
    void getContributorPaging() throws Exception {
        // Mock 데이터 생성
        List<ContributorResponseDto> contributorResponseDtos = Arrays.asList(
                ContributorResponseDto.builder()
                        .contributorId(1L)
                        .contributorName("임채환")
                        .contributorRole("지은이")
                        .build(),
                ContributorResponseDto.builder()
                        .contributorId(2L)
                        .contributorName("박신지")
                        .contributorRole("지은이")
                        .build()
        );

        int page = 1;
        int size = 2;
        int totalElements = contributorResponseDtos.size();
        int totalPage = (int) Math.ceil((double) totalElements / size);

        // PageResponse 빌더로 Mock Response 생성
        PageResponse<ContributorResponseDto> pageResponse = PageResponse.<ContributorResponseDto>builder()
                .data(contributorResponseDtos)
                .totalPage(totalPage)
                .currentPage(page)
                .totalElements((long) totalElements)
                .build();

        // Mocking Service 메서드
        Mockito.when(contributorService.getAllContributors(any(Pageable.class))).thenReturn(pageResponse);

        // API 호출 및 검증
        mockMvc.perform(get("/api/admin/contributors")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].contributorId").value(1L))
                .andExpect(jsonPath("$.data[0].contributorName").value("임채환"))
                .andExpect(jsonPath("$.data[0].contributorRole").value("지은이"))
                .andExpect(jsonPath("$.data[1].contributorId").value(2L))
                .andExpect(jsonPath("$.data[1].contributorName").value("박신지"))
                .andExpect(jsonPath("$.data[1].contributorRole").value("지은이"))
                .andExpect(jsonPath("$.currentPage").value(page))
                .andExpect(jsonPath("$.totalPage").value(totalPage))
                .andExpect(jsonPath("$.totalElements").value(totalElements));
    }


    @Test
    @DisplayName("기여자 삭제 api")
    void deleteContributor() throws Exception {
        Mockito.doNothing().when(contributorService).deleteContributor(1L);

        mockMvc.perform(delete("/api/admin/contributors/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(contributorService, Mockito.times(1)).deleteContributor(1L);
    }

    @Test
    @DisplayName("기여자 단건 조회 api")
    void getContributor() throws Exception {
        Contributor contributor = Contributor.builder().contributorId(1L)
                .contributorName("임채환")
                .contributorRole("지은이").build();

        Mockito.when(contributorService.getContributer(1L)).thenReturn(contributor);

        mockMvc.perform(get("/api/admin/contributors/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contributorId").value(1L))
                .andExpect(jsonPath("$.contributorName").value("임채환"))
                .andExpect(jsonPath("$.contributorRole").value("지은이"));

        Mockito.verify(contributorService, Mockito.times(1)).getContributer(1L);

    }

    @Test
    @DisplayName("기여자 수정 api")
    void updateContributor() throws Exception {
        ContributorRequestDto requestDto = ContributorRequestDto.builder().contributorName("수정").contributorRole("지은이").build();
        ContributorResponseDto responseDto = ContributorResponseDto.builder().contributorId(1L)
                .contributorName("수정")
                .contributorRole("수정이").build();

        Mockito.when(contributorService.updateContributor(eq(1L), any(ContributorRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/admin/contributors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contributorId").value(1L))
                .andExpect(jsonPath("$.contributorName").value("수정"))
                .andExpect(jsonPath("$.contributorRole").value("수정이"));

    }

}