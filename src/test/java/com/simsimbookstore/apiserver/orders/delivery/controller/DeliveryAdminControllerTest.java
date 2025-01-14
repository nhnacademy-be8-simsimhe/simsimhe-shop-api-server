package com.simsimbookstore.apiserver.orders.delivery.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryDetailResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryResponseDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryAdminController.class)
class DeliveryAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DeliveryService deliveryService() {
            return Mockito.mock(DeliveryService.class);
        }
    }

    @Autowired
    private DeliveryService deliveryService;

    @Test
    @DisplayName("ID로 배송 조회 성공 테스트")
    void findByIdSuccess() throws Exception {
        DeliveryDetailResponseDto responseDto = DeliveryDetailResponseDto.builder()
                .deliveryId(1L)
                .deliveryReceiver("홍길동")
                .deliveryState("READY")
                .build();

        Mockito.when(deliveryService.getDeliveryById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/admin/deliveries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(1L))
                .andExpect(jsonPath("$.deliveryReceiver").value("홍길동"))
                .andExpect(jsonPath("$.deliveryState").value("READY"));

        Mockito.verify(deliveryService, Mockito.times(1)).getDeliveryById(1L);
    }

    @Test
    @DisplayName("배송 상태 수정 성공 테스트")
    void updateStateSuccess() throws Exception {
        DeliveryResponseDto responseDto = DeliveryResponseDto.builder()
                .deliveryId(1L)
                .deliveryState("IN_PROGRESS")
                .build();

        Mockito.when(deliveryService.updateDeliveryState(1L, Delivery.DeliveryState.IN_PROGRESS))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/deliveries/1/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newState\": \"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(1L))
                .andExpect(jsonPath("$.deliveryState").value("IN_PROGRESS"));

        Mockito.verify(deliveryService, Mockito.times(1))
                .updateDeliveryState(1L, Delivery.DeliveryState.IN_PROGRESS);
    }

    @Test
    @DisplayName("전체 배송 조회 성공 테스트")
    void findAllDeliveriesSuccess() throws Exception {
        PageResponse<DeliveryResponseDto> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(1L);
        pageResponse.setData(List.of(DeliveryResponseDto.builder()
                .deliveryId(1L)
                .deliveryReceiver("홍길동")
                .deliveryState("READY")
                .build()));

        Mockito.when(deliveryService.getAllDelivery(Mockito.any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/admin/deliveries")
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.data[0].deliveryReceiver").value("홍길동"));

        Mockito.verify(deliveryService, Mockito.times(1)).getAllDelivery(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("트래킹 번호 업데이트 성공 테스트")
    void updateTrackingNumberSuccess() throws Exception {
        DeliveryResponseDto responseDto = DeliveryResponseDto.builder()
                .deliveryId(1L)
                .trackingNumber(67890)
                .deliveryState("IN_PROGRESS")
                .build();

        Mockito.when(deliveryService.updateTrackingNumber(1L, 67890)).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/deliveries/1/tracking-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trackingNumber\": 67890}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(1L))
                .andExpect(jsonPath("$.trackingNumber").value(67890));

        Mockito.verify(deliveryService, Mockito.times(1)).updateTrackingNumber(1L, 67890);
    }
}
