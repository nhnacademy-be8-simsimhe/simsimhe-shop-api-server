package com.simsimbookstore.apiserver.orders.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryPolicyService;
import com.simsimbookstore.apiserver.orders.facade.OrderFacade;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.OrderListService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
@Import(OrderControllerTest.MockConfig.class) // TestConfiguration이 있는 내부 클래스를 Import
class OrderControllerTest {

    private static final String API_ORDER_URL = "/api/shop/order";
    private static final String API_ORDER_TOTAL_URL = "/api/shop/order/total";
    private static final String API_ORDER_PREPARE_URL = "/api/shop/order/prepare";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // MockBean으로 선언하여 스프링 컨텍스트 내에서 Mock 객체 주입
    @TestConfiguration
    static class MockConfig {
        @Bean
        public DeliveryPolicyService deliveryPolicyService() {
            return mock(DeliveryPolicyService.class);
        }

        @Bean
        public OrderTotalService orderTotalService() {
            return mock(OrderTotalService.class);
        }

        @Bean
        public OrderFacade orderFacade() {
            return mock(OrderFacade.class);
        }

        @Bean
        public AddressService addressService() {
            return mock(AddressService.class);
        }

        @Bean
        public OrderListService orderListService() {
            return mock(OrderListService.class);
        }

        @Bean
        public WrapTypeService wrapTypeService() {
            return mock(WrapTypeService.class);
        }

        // ObjectMapper 주입 (테스트에서 DTO <-> JSON 직렬화/역직렬화 용도)
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    // 실제 서비스 레이어는 @MockBean이 아닌 @Autowired로 받기 위해 리팩토링 시 설정이 필요하지만
    // 여기서는 테스트만 살펴보므로 @Autowired로 모킹된 Bean을 가져옵니다.
    @Autowired
    private OrderListService orderListService;

    @Autowired
    private OrderTotalService orderTotalService;

    @Autowired
    private OrderFacade orderFacade;

    @Test
    @DisplayName("POST 바디로 책 목록을 받아서 책 주문 리스트를 정상적으로 반환한다.")
    void getOrderPage_Success() throws Exception {
        // given
        List<BookListRequestDto> bookListRequestDto = List.of(
                new BookListRequestDto(1L, 2),
                new BookListRequestDto(2L, 1)
        );

        List<BookListResponseDto> bookListResponseDto = List.of(
                BookListResponseDto.builder().bookId(1L).price(BigDecimal.valueOf(10000)).quantity(2).build(),
                BookListResponseDto.builder().bookId(2L).price(BigDecimal.valueOf(20000)).quantity(1).build()
        );

        // Mock 설정: 특정 객체 대신 anyList() 사용
        when(orderListService.toBookOrderList(anyList())).thenReturn(bookListResponseDto);

        // JSON 본문
        String requestBody = objectMapper.writeValueAsString(bookListRequestDto);

        // when & then
        mockMvc.perform(
                        post(API_ORDER_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].price").value(10000))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[1].bookId").value(2))
                .andExpect(jsonPath("$[1].price").value(20000))
                .andExpect(jsonPath("$[1].quantity").value(1));

        verify(orderListService, times(1)).toBookOrderList(anyList());
    }



    @Test
    @DisplayName("POST로 총합 계산 요청 시 TotalResponseDto를 반환한다.")
    void calculateTotal_Success() throws Exception {
        // given
        TotalRequestDto requestDto = TotalRequestDto.builder()
                .userId(1L)
                .bookList(List.of(new BookListRequestDto(1L, 2)))
                .usePoint(BigDecimal.valueOf(5000))
                .build();

        TotalResponseDto totalResponseDto = TotalResponseDto.builder()
                .total(BigDecimal.valueOf(20000))
                .availablePoints(BigDecimal.valueOf(10000))
                .deliveryPrice(BigDecimal.valueOf(3000))
                .originalPrice(BigDecimal.valueOf(25000))
                .usePoint(BigDecimal.valueOf(5000))
                .build();

        when(orderTotalService.calculateTotal(any(TotalRequestDto.class)))
                .thenReturn(totalResponseDto);

        String requestContent = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(
                        post(API_ORDER_TOTAL_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(20000))
                .andExpect(jsonPath("$.availablePoints").value(10000))
                .andExpect(jsonPath("$.deliveryPrice").value(3000))
                .andExpect(jsonPath("$.originalPrice").value(25000))
                .andExpect(jsonPath("$.usePoint").value(5000));

        verify(orderTotalService, times(1)).calculateTotal(any(TotalRequestDto.class));
    }

    @Test
    @DisplayName("주문 준비(Prepare) 요청 시 OrderFacadeResponseDto를 반환한다.")
    void createPrepareOrder_Success() throws Exception {
        // given
        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto();
        deliveryRequestDto.setDeliveryState(Delivery.DeliveryState.READY);

        MemberOrderRequestDto memberOrderRequestDto = new MemberOrderRequestDto();
        memberOrderRequestDto.setUserId(1L);
        memberOrderRequestDto.setTotalPrice(BigDecimal.valueOf(20000));

        OrderBookRequestDto orderBookRequestDto = OrderBookRequestDto.builder()
                .bookId(1L)
                .quantity(2)
                .salePrice(BigDecimal.valueOf(10000))
                .build();
        String method = "CARD";
        OrderFacadeRequestDto facadeRequestDto = new OrderFacadeRequestDto(
                deliveryRequestDto,
                memberOrderRequestDto,
                List.of(orderBookRequestDto),
                method
        );

        OrderFacadeResponseDto facadeResponseDto = OrderFacadeResponseDto.builder()
                .orderNumber("ORDER12345")
                .totalPrice(BigDecimal.valueOf(20000))
                .orderName("Book 1")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .build();

        // Mocking
        when(orderFacade.createPrepareOrder(any(OrderFacadeRequestDto.class)))
                .thenReturn(facadeResponseDto);

        // JSON 변환
        String requestJson = objectMapper.writeValueAsString(facadeRequestDto);

        // when & then
        mockMvc.perform(
                        post(API_ORDER_PREPARE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORDER12345"))
                .andExpect(jsonPath("$.totalPrice").value(20000))
                .andExpect(jsonPath("$.orderName").value("Book 1"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));

        verify(orderFacade, times(1)).createPrepareOrder(any(OrderFacadeRequestDto.class));
    }
}


