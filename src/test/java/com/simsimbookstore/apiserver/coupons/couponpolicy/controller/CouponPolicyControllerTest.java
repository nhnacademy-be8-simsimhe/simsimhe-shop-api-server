package com.simsimbookstore.apiserver.coupons.couponpolicy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyRequestDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyResponseDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.couponpolicy.service.CouponPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CouponPolicyControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CouponPolicyService couponPolicyService;
    @InjectMocks
    private CouponPolicyController couponPolicyController;

    private ObjectMapper objectMapper;

    private CouponPolicyResponseDto responseDto1;
    private CouponPolicyResponseDto responseDto2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponPolicyController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();

        responseDto1 = CouponPolicyResponseDto.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Fix Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(BigDecimal.valueOf(1000))
                .minOrderAMount(BigDecimal.valueOf(5000))
                .policyDescription("Fix Policy description")
                .build();

        responseDto2 = CouponPolicyResponseDto.builder()
                .couponPolicyId(2L)
                .couponPolicyName("Rate Policy")
                .discountType(DisCountType.RATE)
                .discountRate(BigDecimal.TEN)
                .minOrderAMount(BigDecimal.valueOf(5000))
                .maxDiscountAmount(BigDecimal.valueOf(10000))
                .policyDescription("Rate Policy description")
                .build();
    }

    @Test
    @DisplayName("GET /api/admin/couponPolicies")
    void getAllCouponPolicy() throws Exception {
        Page<CouponPolicyResponseDto> pageResult = new PageImpl<>(Arrays.asList(responseDto1, responseDto2), PageRequest.of(0, 10), 2);

        when(couponPolicyService.getAllCouponPolicy(any(Pageable.class)))
                .thenReturn(pageResult);
        mockMvc.perform(get("/api/admin/couponPolicies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].couponPolicyId").value(1L))
                .andExpect(jsonPath("$.content[0].couponPolicyName").value("Fix Policy"))
                .andExpect(jsonPath("$.content[1].couponPolicyId").value(2L))
                .andExpect(jsonPath("$.content[1].couponPolicyName").value("Rate Policy"));

        verify(couponPolicyService, times(1)).getAllCouponPolicy(any(Pageable.class));

    }
    @Test
    @DisplayName("GET /api/admin/couponPolicies/{couponPolicyId} - 단일 쿠폰정책 조회 성공")
    void getCouponPolicy_success() throws Exception {
        // given
        Long policyId = 1L;
        when(couponPolicyService.getCouponPolicy(policyId)).thenReturn(responseDto1);

        // when & then
        mockMvc.perform(get("/api/admin/couponPolicies/{couponPolicyId}", policyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyId").value(1L))
                .andExpect(jsonPath("$.couponPolicyName").value("Fix Policy"));

        verify(couponPolicyService, times(1)).getCouponPolicy(policyId);
    }

//    @Test
//    @DisplayName("GET /api/admin/couponPolicies/{couponPolicyId} - 존재하지 않는 쿠폰정책 조회 시 404 처리")
//    void getCouponPolicy_notFound() throws Exception {
//        // given
//        Long invalidId = 999L;
//        // 서비스에서 NotFoundException 등을 던진다고 가정
//        when(couponPolicyService.getCouponPolicy(invalidId))
//                .thenThrow(new RuntimeException("쿠폰정책(id:999)이 존재하지 않습니다.")); // 예시
//
//        // when & then
//        mockMvc.perform(get("/api/admin/couponPolicies/{couponPolicyId}", invalidId))
//                .andExpect(status().is4xxClientError());
//        // 실제로는 ControllerAdvice 설정이 있으면 404, 400, 500 등 원하는 상태로 매핑
//
//        verify(couponPolicyService, times(1)).getCouponPolicy(invalidId);
//    }

    @Test
    @DisplayName("POST /api/admin/couponPolicies - 쿠폰정책 생성 성공")
    void createCouponPolicy() throws Exception {
        // given
        // 요청 바디로 들어올 DTO (유효성 검증 대상)
        CouponPolicyRequestDto requestDto = CouponPolicyRequestDto.builder()
                .couponPolicyName("New Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(BigDecimal.valueOf(2000))
                .minOrderAmount(BigDecimal.valueOf(5000))
                .policyDescription("New Policy Description")
                .build();

        // 서비스가 반환할 응답 DTO
        CouponPolicyResponseDto createdResponse = CouponPolicyResponseDto.builder()
                .couponPolicyId(3L)
                .couponPolicyName("New Policy")
                .discountType(DisCountType.FIX)
                .discountPrice(BigDecimal.valueOf(2000))
                .minOrderAMount(BigDecimal.valueOf(5000))
                .policyDescription("New Policy Description")
                .build();

        when(couponPolicyService.createCouponPolicy(any())).thenReturn(createdResponse);

        // when & then
        mockMvc.perform(post("/api/admin/couponPolicies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponPolicyId").value(3L))
                .andExpect(jsonPath("$.couponPolicyName").value("New Policy"))
                .andExpect(jsonPath("$.discountPrice").value(2000));

        verify(couponPolicyService, times(1)).createCouponPolicy(any());
    }

    @Test
    @DisplayName("POST /api/admin/couponPolicies - 유효성 검사 실패 시 400 반환")
    void createCouponPolicy_validationFail() throws Exception {
        // given
        // 예: policyName이 null 이거나 discountType이 null 등
        CouponPolicyRequestDto invalidRequestDto = CouponPolicyRequestDto.builder()
                .discountPrice(BigDecimal.valueOf(500))
                .build(); // discountType, couponPolicyName 등 누락

        // when & then
        mockMvc.perform(post("/api/admin/couponPolicies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());
        // Bean Validation 실패 -> 400 Bad Request

        verify(couponPolicyService, never()).createCouponPolicy(any());
    }

    @Test
    @DisplayName("DELETE /api/admin/couponPolicies/{couponPolicyId} - 쿠폰정책 삭제 성공")
    void deleteCouponPolicy() throws Exception {
        // given
        Long policyId = 1L;
        doNothing().when(couponPolicyService).deleteCouponPolicy(policyId);

        // when & then
        mockMvc.perform(delete("/api/admin/couponPolicies/{couponPolicyId}", policyId))
                .andExpect(status().isNoContent());

        verify(couponPolicyService, times(1)).deleteCouponPolicy(policyId);
    }

//    @Test
//    @DisplayName("DELETE /api/admin/couponPolicies/{couponPolicyId} - 존재하지 않는 쿠폰정책이면 예외 처리(예: 404)")
//    void deleteCouponPolicy_notFound() throws Exception {
//        // given
//        Long invalidId = 999L;
//        doThrow(new RuntimeException("쿠폰정책(id:999)이 존재하지 않습니다."))
//                .when(couponPolicyService).deleteCouponPolicy(invalidId);
//
//        // when & then
//        mockMvc.perform(delete("/api/admin/couponPolicies/{couponPolicyId}", invalidId))
//                .andExpect(status().is4xxClientError());
//        // 실제 예외 상황에서 어떤 상태코드(404/400/500 등)를 반환할지는
//        // @ControllerAdvice, 예외처리 로직에 따라 달라집니다.
//
//        verify(couponPolicyService, times(1)).deleteCouponPolicy(invalidId);
//    }
}