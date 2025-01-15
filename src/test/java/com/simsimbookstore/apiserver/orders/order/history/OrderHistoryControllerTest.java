package com.simsimbookstore.apiserver.orders.order.history;

import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
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

        // Page 객체를 생성
        Pageable pageable = PageRequest.of(0, 20); // 0부터 시작하는 페이지 번호
        Page<OrderHistoryResponseDto> mockPage = new PageImpl<>(content, pageable, 46);

        // PageResponse 객체를 생성
        PageResponse<OrderHistoryResponseDto> mockPageResponse = new PageResponse<>(
                mockPage.getContent(),
                1, // 클라이언트에 노출되는 1부터 시작하는 페이지 번호
                1, // 시작 페이지
                3, // 끝 페이지
                mockPage.getTotalPages(), // 전체 페이지 수
                mockPage.getTotalElements() // 총 데이터 수
        );

        // Mock 설정
        when(orderHistoryService.getOrderHistory(1L, pageable)).thenReturn(mockPageResponse);

        // When & Then: API 호출 및 검증
        mockMvc.perform(get("/api/shop/users/{userId}/orders", 1L)
                        .param("page", "1") // 클라이언트 요청은 page=1
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()) // 응답 데이터 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderNumber").value("ORD001"))
                .andExpect(jsonPath("$.data[0].orderDate").value("2025-01-01T19:00:00"))
                .andExpect(jsonPath("$.data[0].orderName").value("aaa"))
                .andExpect(jsonPath("$.data[0].orderAmount").value(49500.00))
                .andExpect(jsonPath("$.data[0].orderState").value("COMPLETED"))
                .andExpect(jsonPath("$.data[0].trackingNumber").isEmpty())
                .andExpect(jsonPath("$.data[0].orderUserName").value("test"))
                .andExpect(jsonPath("$.data[0].receiverName").value("Unknown Receiver"))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.startPage").value(1))
                .andExpect(jsonPath("$.endPage").value(3))
                .andExpect(jsonPath("$.totalPage").value(3))
                .andExpect(jsonPath("$.totalElements").value(46));
    }
}

