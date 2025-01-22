package com.simsimbookstore.apiserver.orders.packages.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class WrapTypeOrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WrapTypeService wrapTypeService;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        // 컨트롤러 수동 생성 및 MockMvc 설정
        WrapTypeOrderController wrapTypeOrderController = new WrapTypeOrderController(wrapTypeService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(wrapTypeOrderController).build();
    }

    @Test
    void testFindAll() throws Exception {
        // given: 서비스가 반환할 가짜 데이터 생성
        WrapTypeResponseDto dto1 = WrapTypeResponseDto.builder()
                .packageTypeId(1L)
                .packageName("Bubble Wrap")
                .packagePrice(new BigDecimal("5.99"))
                .isAvailable(true)
                .build();

        WrapTypeResponseDto dto2 = WrapTypeResponseDto.builder()
                .packageTypeId(2L)
                .packageName("Foam Wrap")
                .packagePrice(new BigDecimal("3.49"))
                .isAvailable(true)
                .build();

        List<WrapTypeResponseDto> mockResponse = Arrays.asList(dto1, dto2);

        when(wrapTypeService.getAllWarpTypeIsAvailable()).thenReturn(mockResponse);

        // when & then: GET 요청 테스트 및 결과 검증
        mockMvc.perform(get("/api/shop/wrap-types")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // 반환된 JSON 배열 크기 검증
                .andExpect(jsonPath("$.length()").value(mockResponse.size()))
                // 첫 번째 요소 검증
                .andExpect(jsonPath("$[0].packageTypeId").value(1))
                .andExpect(jsonPath("$[0].packageName").value("Bubble Wrap"))
                .andExpect(jsonPath("$[0].packagePrice").value(5.99))
                .andExpect(jsonPath("$[0].isAvailable").value(true))
                // 두 번째 요소 검증
                .andExpect(jsonPath("$[1].packageTypeId").value(2))
                .andExpect(jsonPath("$[1].packageName").value("Foam Wrap"))
                .andExpect(jsonPath("$[1].packagePrice").value(3.49))
                .andExpect(jsonPath("$[1].isAvailable").value(true));
    }
}