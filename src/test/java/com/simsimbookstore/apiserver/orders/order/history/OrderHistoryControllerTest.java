package com.simsimbookstore.apiserver.orders.order.history;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.order.dto.OrderHistoryResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class OrderHistoryControllerTest {

    private MockMvc mockMvc;
    private OrderHistoryService orderHistoryService;

    @BeforeEach
    void setUp() {
        // Mock Service 생성
        orderHistoryService = Mockito.mock(OrderHistoryService.class);

        // PageableHandlerMethodArgumentResolver 설정
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        // Controller에 Mock 주입 및 MockMvc 설정
        OrderHistoryController orderHistoryController = new OrderHistoryController(orderHistoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(orderHistoryController)
                .setCustomArgumentResolvers(pageableResolver) // PageableResolver 추가
                .build();
    }


    @Test
    @DisplayName("GET /api/users/{userId}/orders - 주문 이력 조회 성공")
    void getOrderHistory_Success() throws Exception {
        // Given: Mock 데이터 준비
        List<OrderHistoryResponseDto> content = List.of(
                OrderHistoryResponseDto.builder()
                        .orderNumber("ORD001")
                        .orderDate(LocalDateTime.of(2025, 1, 1, 19, 0))
                        .orderName("aaa")
                        .orderAmount(BigDecimal.valueOf(49500.00))
                        .orderState(Order.OrderState.COMPLETED)
                        .trackingNumber(null)
                        .orderUserName("test")
                        .receiverName("Unknown Receiver")
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 20);
        Page<OrderHistoryResponseDto> mockPage = new PageImpl<>(content, pageable, 46);

        // Mock 설정
        when(orderHistoryService.getOrderHistory(anyLong(), any(Pageable.class))).thenReturn(mockPage);

        // When & Then: API 호출 및 응답 데이터 출력
        mockMvc.perform(get("/api/shop/users/{userId}/orders", 1L)
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()) // 응답 데이터 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD001"))
                .andExpect(jsonPath("$.content[0].orderDate").value("2025-01-01T19:00:00"))
                .andExpect(jsonPath("$.content[0].orderName").value("aaa"))
                .andExpect(jsonPath("$.content[0].orderAmount").value(49500.00))
                .andExpect(jsonPath("$.content[0].orderState").value("COMPLETED"))
                .andExpect(jsonPath("$.content[0].trackingNumber").isEmpty())
                .andExpect(jsonPath("$.content[0].orderUserName").value("test"))
                .andExpect(jsonPath("$.content[0].receiverName").value("Unknown Receiver"))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(46))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

}

