package com.simsimbookstore.apiserver.orders.delivery.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryPolicyRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryPolicyService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(DeliveryPolicyController.class)
@ExtendWith(MockitoExtension.class)
class DeliveryPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryPolicyService deliveryPolicyService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public DeliveryPolicyService deliveryPolicyService() {
            return mock(DeliveryPolicyService.class);
        }
    }

    @Test
    @DisplayName("모든 배송 정책 조회 테스트")
    void getAllDeliveryPoliciesTest() throws Exception {
        List<DeliveryPolicy> policies = List.of(
                new DeliveryPolicy(1L, "Policy 1", BigDecimal.valueOf(1000), true),
                new DeliveryPolicy(2L, "Policy 2", BigDecimal.valueOf(2000), false)
        );
        when(deliveryPolicyService.findAll()).thenReturn(policies);

        mockMvc.perform(get("/api/admin/delivery-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].deliveryPolicyName").value("Policy 1"))
                .andExpect(jsonPath("$[1].deliveryPolicyName").value("Policy 2"));
    }

    @Test
    @DisplayName("새로운 배송 정책 생성 테스트")
    void createDeliveryPolicyTest() throws Exception {
        DeliveryPolicyRequestDto requestDto = new DeliveryPolicyRequestDto("Policy 1", BigDecimal.valueOf(1000), true);
        DeliveryPolicy savedPolicy = new DeliveryPolicy(1L, "Policy 1", BigDecimal.valueOf(1000), true);

        when(deliveryPolicyService.save(any(DeliveryPolicyRequestDto.class))).thenReturn(savedPolicy);

        mockMvc.perform(post("/api/admin/delivery-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"policyName\": \"Policy 1\", \"standardPrice\": 1000, \"standardPolicy\": true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deliveryPolicyId").value(1))
                .andExpect(jsonPath("$.deliveryPolicyName").value("Policy 1"))
                .andExpect(jsonPath("$.policyStandardPrice").value(1000))
                .andExpect(jsonPath("$.standardPolicy").value(true));
    }

    @Test
    @DisplayName("배송 정책 기준 상태 토글 테스트")
    void toggleStandardPolicyTest() throws Exception {
        doNothing().when(deliveryPolicyService).toggleStandardPolicy(1L);

        mockMvc.perform(post("/api/admin/delivery-policies/1/toggle"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("배송 정책 삭제 테스트")
    void deleteDeliveryPolicyTest() throws Exception {
        doNothing().when(deliveryPolicyService).deleteDeliveryPolicy(1L);

        mockMvc.perform(delete("/api/admin/delivery-policies/1"))
                .andExpect(status().isNoContent());
    }
}