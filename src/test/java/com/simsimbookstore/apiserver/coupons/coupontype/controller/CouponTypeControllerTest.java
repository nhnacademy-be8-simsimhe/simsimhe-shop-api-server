package com.simsimbookstore.apiserver.coupons.coupontype.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeRequestDto;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeResponseDto;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import com.simsimbookstore.apiserver.coupons.coupontype.service.CouponTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CouponTypeControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CouponTypeService couponTypeService;
    @InjectMocks
    private CouponTypeController couponTypeController;

    private ObjectMapper objectMapper;

    private CouponTypeResponseDto couponTypeResponseDto1;
    private CouponTypeResponseDto couponTypeResponseDto2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponTypeController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        couponTypeResponseDto1 = CouponTypeResponseDto.builder()
                .couponTypeId(1L)
                .couponTypeName("Book Discount")
                .couponTypes(CouponTargetType.BOOK)
                .build();

        couponTypeResponseDto2 = CouponTypeResponseDto.builder()
                .couponTypeId(2L)
                .couponTypeName("Category Discount")
                .couponTypes(CouponTargetType.CATEGORY)
                .build();
    }

    @Test
    @DisplayName("GET /api/admin/couponTypes - 전체 쿠폰 타입 Page 조회")
    void getAllCouponType_ReturnPage() throws Exception {
        // Given
        Page<CouponTypeResponseDto> pageResult =
                new PageImpl<>(Arrays.asList(couponTypeResponseDto1, couponTypeResponseDto2),
                        PageRequest.of(0, 10),
                        2);
        when(couponTypeService.getAllCouponType(any())).thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/couponTypes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].couponTypeName").value("Book Discount"))
                .andExpect(jsonPath("$.content[1].couponTypeName").value("Category Discount"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(couponTypeService, times(1)).getAllCouponType(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/admin/couponTypes/{couponTypeId} - 특정 쿠폰 타입 조회")
    void getCouponType_ReturnSingle() throws Exception {
        // given
        long couponTypeId = 1L;
        when(couponTypeService.getCouponType(eq(couponTypeId)))
                .thenReturn(couponTypeResponseDto1);

        // when & then
        mockMvc.perform(get("/api/admin/couponTypes/{couponTypeId}", couponTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponTypeName").value("Book Discount"));

        verify(couponTypeService, times(1)).getCouponType(eq(couponTypeId));
    }

    @Test
    @DisplayName("GET /api/admin/couponTypes?couponPolicyId= - 정책 ID로 쿠폰 타입 조회")
    void getCouponTypeByPolicyId_ReturnPage() throws Exception {
        // given
        long couponPolicyId = 10L;
        Page<CouponTypeResponseDto> pageResult =
                new PageImpl<>(
                        Arrays.asList(couponTypeResponseDto2),
                        PageRequest.of(0, 10),
                        1
                );

        when(couponTypeService.getCouponByCouponPolicy(any(Pageable.class), eq(couponPolicyId)))
                .thenReturn(pageResult);

        // when & then
        mockMvc.perform(get("/api/admin/couponTypes")
                        .param("couponPolicyId", String.valueOf(couponPolicyId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].couponTypeId").value(2))
                .andExpect(jsonPath("$.content[0].couponTypeName").value("Category Discount"));

        verify(couponTypeService, times(1))
                .getCouponByCouponPolicy(any(Pageable.class), eq(couponPolicyId));
    }
    @Test
    @DisplayName("POST /api/admin/couponTypes - 쿠폰 타입 생성")
    void createCouponType_ReturnCreated() throws Exception {
        // given
        CouponTypeRequestDto requestDto = CouponTypeRequestDto.builder()
                .couponTypeName("New CouponType")
                .couponPolicyId(100L) // ★ 필수 필드
                .period(30)
                .stacking(false)
                .couponTargetType(CouponTargetType.BOOK) // ★ 필수 필드
                .build();

        CouponTypeResponseDto createdCouponType = CouponTypeResponseDto.builder()
                .couponTypeId(3L)
                .couponTypeName("New Coupon")
                .couponTypes(CouponTargetType.BOOK)
                .build();

        when(couponTypeService.createCouponType(any(CouponTypeRequestDto.class)))
                .thenReturn(createdCouponType);

        // when & then
        mockMvc.perform(post("/api/admin/couponTypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponTypeId").value(3))
                .andExpect(jsonPath("$.couponTypeName").value("New Coupon"));

        verify(couponTypeService, times(1)).createCouponType(any(CouponTypeRequestDto.class));
    }

    @Test
    @DisplayName("DELETE /api/admin/couponTypes/{couponTypeId} - 쿠폰 타입 삭제")
    void deleteCouponType_ReturnNoContent() throws Exception {
        // given
        long couponTypeId = 5L;
        doNothing().when(couponTypeService).deleteCouponType(couponTypeId);

        // when & then
        mockMvc.perform(delete("/api/admin/couponTypes/{couponTypeId}", couponTypeId))
                .andExpect(status().isNoContent());

        verify(couponTypeService, times(1)).deleteCouponType(couponTypeId);
    }

}