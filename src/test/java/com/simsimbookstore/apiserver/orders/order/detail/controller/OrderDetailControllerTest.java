package com.simsimbookstore.apiserver.orders.order.detail.controller;

import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailInfoDto;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailProduct;
import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailResponseDto;
import com.simsimbookstore.apiserver.orders.order.detail.service.OrderDetailService;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderDetailController.class)
@Import(OrderDetailControllerTest.TestConfig.class)
class OrderDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderDetailService orderDetailService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderDetailService orderDetailService() {
            return Mockito.mock(OrderDetailService.class);
        }
    }

    @Test
    @DisplayName("일반 주문 상세 조회 성공 테스트")
    void testOrderDetailHistory_Success() throws Exception {
        // 더미 데이터 생성
        OrderDetailInfoDto infoDto = new OrderDetailInfoDto(
                "ORDER123",
                LocalDateTime.now(),
                Order.OrderState.IN_DELIVERY,
                "홍길동",
                "010-1234-5678",
                "test@example.com",
                100,
                BigDecimal.TEN,
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(35000),
                1L,
                "수령인",
                "010-9999-8888",
                123456,
                "12345",
                "서울시 종로구",
                "1-1",
                "참고사항",
                Delivery.DeliveryState.IN_PROGRESS,
                "카드"
        );
        List<OrderDetailProduct> productList = Collections.emptyList();
        OrderDetailResponseDto responseDto = new OrderDetailResponseDto(infoDto, productList);

        when(orderDetailService.getOrderDetail(1L, "ORDER123")).thenReturn(responseDto);

        mockMvc.perform(get("/api/shop/users/1/orders/ORDER123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderDetailInfoDto").exists())
                .andExpect(jsonPath("$.orderDetailProductList").isArray());

        verify(orderDetailService).getOrderDetail(1L, "ORDER123");
    }

    @Test
    @DisplayName("게스트 주문 상세 조회 성공 테스트")
    void testGuestOrderDetail_Success() throws Exception {
        // 더미 데이터 생성
        OrderDetailInfoDto infoDto = new OrderDetailInfoDto(
                "ORDER456",
                LocalDateTime.now(),
                Order.OrderState.IN_DELIVERY,
                "홍길동",
                "010-1234-5678",
                "guest@example.com",
                100,
                BigDecimal.TEN,
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(35000),
                1L,
                "수령인",
                "010-9999-8888",
                123456,
                "12345",
                "서울시 종로구",
                "1-1",
                "참고사항",
                Delivery.DeliveryState.IN_PROGRESS,
                "카드"
        );
        List<OrderDetailProduct> productList = Collections.emptyList();
        OrderDetailResponseDto responseDto = new OrderDetailResponseDto(infoDto, productList);

        when(orderDetailService.getGuestOrderDetail("ORDER456", "guest@example.com")).thenReturn(responseDto);

        mockMvc.perform(get("/api/shop/users/guest-orders")
                        .param("orderNumber", "ORDER456")
                        .param("email", "guest@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderDetailInfoDto").exists())
                .andExpect(jsonPath("$.orderDetailProductList").isArray());

        verify(orderDetailService).getGuestOrderDetail("ORDER456", "guest@example.com");
    }
}