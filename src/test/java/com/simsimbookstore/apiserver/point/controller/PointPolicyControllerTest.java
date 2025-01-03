package com.simsimbookstore.apiserver.point.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.simsimbookstore.apiserver.point.dto.PointPolicyRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.entity.PointPolicy;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.ArrayList;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

class PointPolicyControllerTest {

    private MockMvc mockMvc;

    private PointPolicyService pointPolicyService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        pointPolicyService = mock(PointPolicyService.class);
        PointPolicyController controller = new PointPolicyController(pointPolicyService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllPolicies_ReturnsEmptyList() throws Exception {
        when(pointPolicyService.getAllPolicies()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/admin/pointPolicies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(pointPolicyService, times(1)).getAllPolicies();
    }

    @Test
    void testGetAllPolicies_ReturnsSomePolicies() throws Exception {
        PointPolicyResponseDto policy1 = PointPolicyResponseDto.builder()
                .earningMethod(PointPolicy.EarningMethod.ORDER_STANDARD)
                .earningType(PointPolicy.EarningType.FIX)
                .description("Normal order reward")
                .build();

        PointPolicyResponseDto policy2 = PointPolicyResponseDto.builder()
                .earningMethod(PointPolicy.EarningMethod.SIGNUP)
                .earningType(PointPolicy.EarningType.FIX)
                .description("Signup reward")
                .build();

        when(pointPolicyService.getAllPolicies()).thenReturn(List.of(policy1, policy2));

        mockMvc.perform(get("/api/admin/pointPolicies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("Normal order reward")))
                .andExpect(jsonPath("$[1].description", is("Signup reward")));

        verify(pointPolicyService, times(1)).getAllPolicies();
    }

    @Test
    void testCreatePolicy_Success() throws Exception {
        PointPolicyRequestDto requestDto = PointPolicyRequestDto.builder()
                .earningMethod(PointPolicy.EarningMethod.SIGNUP)
                .earningType(PointPolicy.EarningType.FIX)
                .earningValue(BigDecimal.valueOf(100))
                .description("Signup policy")
                .isAvailable(true)
                .build();

        PointPolicyResponseDto responseDto = PointPolicyResponseDto.builder()
                .earningMethod(PointPolicy.EarningMethod.SIGNUP)
                .earningType(PointPolicy.EarningType.FIX)
                .description("Signup policy")
                .isAvailable(true)
                .build();

        when(pointPolicyService.createPolicy(any(PointPolicyRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/pointPolicies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Signup policy")));

        verify(pointPolicyService, times(1)).createPolicy(any(PointPolicyRequestDto.class));
    }

    @Test
    void testUpdatePolicy_Success() throws Exception {
        Long policyId = 1L;
        PointPolicyRequestDto requestDto = PointPolicyRequestDto.builder()
                .earningMethod(PointPolicy.EarningMethod.ORDER_STANDARD)
                .earningType(PointPolicy.EarningType.RATE)
                .earningValue(new BigDecimal("0.05"))
                .description("Normal order 5% reward")
                .isAvailable(true)
                .build();

        PointPolicyResponseDto responseDto = PointPolicyResponseDto.builder()
                .earningMethod(PointPolicy.EarningMethod.ORDER_STANDARD)
                .earningType(PointPolicy.EarningType.RATE)
                .description("Normal order 5% reward")
                .isAvailable(true)
                .build();

        when(pointPolicyService.updatePolicy(eq(policyId), any(PointPolicyRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/admin/pointPolicies/{policyId}", policyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Normal order 5% reward")));

        verify(pointPolicyService, times(1))
                .updatePolicy(eq(policyId), any(PointPolicyRequestDto.class));
    }

    @Test
    void testDeletePolicy_Success() throws Exception {
        Long policyId = 99L;

        mockMvc.perform(delete("/api/admin/pointPolicies/{policyId}", policyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(pointPolicyService, times(1)).deletePolicy(policyId);
    }
}
