package com.simsimbookstore.apiserver.orders.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.order.dto.OrderAdminResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderBookStateUpdateRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderOrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderStateUpdateDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookAdminResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderAdminController.class)
class OrderAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberOrderService memberOrderService;

    @Autowired
    private OrderBookService orderBookService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MemberOrderService memberOrderService() {
            return Mockito.mock(MemberOrderService.class);
        }

        @Bean
        public OrderBookService orderBookService() {
            return Mockito.mock(OrderBookService.class);
        }
    }

    @Test
    @DisplayName("GET /api/admin/orders - 주문 목록 조회 성공 테스트")
    void findAllOrdersSuccess() throws Exception {
        // given
        OrderResponseDto orderDto = OrderResponseDto.builder()
                .orderId(100L)
                .orderNumber("ORD-100")
                .totalPrice(new BigDecimal("9999"))
                .build();

        // PageResponse에 넣을 목업 데이터
        PageResponse<OrderResponseDto> pageResponse = PageResponse.<OrderResponseDto>builder()
                .data(List.of(orderDto))
                .currentPage(1)
                .startPage(1)
                .endPage(1)
                .totalPage(1)
                .totalElements(1L)
                .build();

        // memberOrderService.getAllOrders(...) 호출 시 pageResponse 반환
        when(memberOrderService.getAllOrders(any(Pageable.class))).thenReturn(pageResponse);

        // when & then
        mockMvc.perform(
                        get("/api/admin/orders")
                                .param("page", "1")
                                .param("size", "15")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderId").value(100L))
                .andExpect(jsonPath("$.data[0].orderNumber").value("ORD-100"))
                .andExpect(jsonPath("$.data[0].totalPrice").value(9999));

        verify(memberOrderService, times(1)).getAllOrders(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/admin/orders/{id} - 주문 상세 조회 성공 테스트")
    void findByIdSuccess() throws Exception {
        // given
        OrderAdminResponseDto orderAdminResponse = OrderAdminResponseDto.builder()
                .orderId(1L)
                .orderNumber("ORD-1")
                .totalPrice(new BigDecimal("10000"))
                .orderState(null) // 예시
                .build();

        OrderBookAdminResponseDto bookAdminDto = OrderBookAdminResponseDto.builder()
                .orderBookId(10L)
                .bookTitle("Mock Book")
                .quantity(2)
                .salePrice(new BigDecimal("20000"))
                .discountPrice(new BigDecimal("15000"))
                .orderBookState(null) // 예시
                .build();

        OrderOrderBookResponseDto orderOrderBookResponse = OrderOrderBookResponseDto.builder()
                .orderResponseDto(orderAdminResponse)
                .orderBookAdminResponseDtos(List.of(bookAdminDto))
                .build();

        // eq(1L) 제거 → 직접 1L 전달
        when(memberOrderService.getOrderOrderBook(1L)).thenReturn(orderOrderBookResponse);

        // when & then
        mockMvc.perform(get("/api/admin/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderResponseDto.orderId").value(1L))
                .andExpect(jsonPath("$.orderResponseDto.orderNumber").value("ORD-1"))
                .andExpect(jsonPath("$.orderBookAdminResponseDtos[0].orderBookId").value(10L))
                .andExpect(jsonPath("$.orderBookAdminResponseDtos[0].bookTitle").value("Mock Book"));

        verify(memberOrderService, times(1)).getOrderOrderBook(1L);
    }

    @Test
    @DisplayName("POST /api/admin/orders/orderBook/{orderBookId}/state - 주문 도서 상태 업데이트 테스트")
    void updateOrderBookStateSuccess() throws Exception {
        // given
        OrderBookStateUpdateRequestDto requestDto =
                new OrderBookStateUpdateRequestDto(OrderBook.OrderBookState.COMPLETED);

        OrderBookResponseDto updatedOrderBook = OrderBookResponseDto.builder()
                .orderBookId(10L)
                .bookTitle("Mock Book")
                .orderBookState("COMPLETED")
                .build();

        // eq(...) 제거 후, 직접 값 전달
        when(orderBookService.updateOrderBook(10L, OrderBook.OrderBookState.COMPLETED))
                .thenReturn(updatedOrderBook);

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(
                        post("/api/admin/orders/orderBook/{orderBookId}/state", 10L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderBookId").value(10L))
                .andExpect(jsonPath("$.orderBookState").value("COMPLETED"));

        verify(orderBookService, times(1))
                .updateOrderBook(10L, OrderBook.OrderBookState.COMPLETED);
    }

    @Test
    @DisplayName("POST /api/admin/orders/order/{orderId}/state - 주문 상태 업데이트 테스트")
    void updateOrderStateSuccess() throws Exception {
        // given
        OrderStateUpdateDto updateDto = OrderStateUpdateDto.builder()
                .orderId(1L)
                .orderState(Order.OrderState.COMPLETED)
                .build();

        OrderAdminResponseDto updatedOrder = OrderAdminResponseDto.builder()
                .orderId(1L)
                .orderNumber("ORD-1")
                .totalPrice(new BigDecimal("99999"))
                .orderState(Order.OrderState.COMPLETED)
                .build();

        // ★ any(...) 사용
        when(memberOrderService.updateOrder(any(OrderStateUpdateDto.class)))
                .thenReturn(updatedOrder);

        String jsonBody = objectMapper.writeValueAsString(updateDto);

        // when & then
        mockMvc.perform(
                        post("/api/admin/orders/order/{orderId}/state", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.orderState").value("COMPLETED"));

        // 검증
        verify(memberOrderService, times(1)).updateOrder(any(OrderStateUpdateDto.class));
    }

}