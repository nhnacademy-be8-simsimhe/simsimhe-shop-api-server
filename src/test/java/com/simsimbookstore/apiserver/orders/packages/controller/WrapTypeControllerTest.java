package com.simsimbookstore.apiserver.orders.packages.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

class WrapTypeControllerTest {

    private MockMvc mockMvc;
    private WrapTypeService wrapTypeService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        wrapTypeService = mock(WrapTypeService.class);
        WrapTypeController wrapTypeController = new WrapTypeController(wrapTypeService);
        mockMvc = MockMvcBuilders.standaloneSetup(wrapTypeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllWrapTypes() throws Exception {
        List<WrapTypeResponseDto> responseDtos = List.of(
                WrapTypeResponseDto.builder()
                        .packageTypeId(1L)
                        .packageName("Premium Package")
                        .packagePrice(BigDecimal.valueOf(20000))
                        .isAvailable(false)
                        .build(),
                WrapTypeResponseDto.builder()
                        .packageTypeId(2L)
                        .packageName("normal package")
                        .packagePrice(BigDecimal.valueOf(500))
                        .isAvailable(true)
                        .build()
        );

        when(wrapTypeService.getAllWrapTypes()).thenReturn(responseDtos);

        mockMvc.perform(get("/api/admin/wrap-types")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(2))
                .andExpect(jsonPath("$[0].packageTypeId").value(1))
                .andExpect(jsonPath("$[0].packageName").value("Premium Package"))
                .andExpect(jsonPath("$[1].packageTypeId").value(2))
                .andExpect(jsonPath("$[1].packageName").value("normal package"));
    }

    @Test
    void testGetWrapTypeById() throws Exception {
        WrapTypeResponseDto responseDto = WrapTypeResponseDto.builder()
                .packageTypeId(1L)
                .packageName("Premium Package")
                .packagePrice(BigDecimal.valueOf(20000))
                .isAvailable(false)
                .build();

        when(wrapTypeService.getWrapTypeById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/admin/wrap-types/1")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("packageTypeId").value(1))
                .andExpect(jsonPath("packageName").value("Premium Package"))
                .andExpect(jsonPath("packagePrice").value(20000))
                .andExpect(jsonPath("isAvailable").value(false));
    }

    @Test
    void testCreateWrapType() throws Exception {
        WrapTypeRequestDto requestDto = new WrapTypeRequestDto();
        requestDto.setPackageName("Standard Package");
        requestDto.setPackagePrice(BigDecimal.valueOf(1000));
        requestDto.setIsAvailable(true);

        WrapTypeResponseDto responseDto = WrapTypeResponseDto.builder()
                .packageTypeId(3L)
                .packageName("Standard Package")
                .packagePrice(BigDecimal.valueOf(1000))
                .isAvailable(true)
                .build();

        when(wrapTypeService.createWrapType(any(WrapTypeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/wrap-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("packageTypeId").value(3))
                .andExpect(jsonPath("packageName").value("Standard Package"))
                .andExpect(jsonPath("packagePrice").value(1000))
                .andExpect(jsonPath("isAvailable").value(true));
    }

    @Test
    void testUpdateAvailability() throws Exception {
        WrapTypeResponseDto responseDto = WrapTypeResponseDto.builder()
                .packageTypeId(1L)
                .packageName("Premium Package")
                .packagePrice(BigDecimal.valueOf(20000))
                .isAvailable(true)
                .build();

        when(wrapTypeService.updateAvailability(1L, true)).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/wrap-types/1/availability")
                        .param("isAvailable", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // 응답 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("packageTypeId").value(1))
                .andExpect(jsonPath("isAvailable").value(true));
    }
}