package com.simsimbookstore.apiserver.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeImpl;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private OrderFacadeImpl orderFacade;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("orderName, totalAmount 데이터가 /api/payment 경로로 들어오는지 확인")
    void initiatePayment() throws Exception {

//        OrderFacadeRequestDto orderFacadeRequestDto
//                = new OrderFacadeRequestDto(
//
//        )
//                ;



//        String req = objectMapper.writeValueAsString(dd);

        //when
        mvc.perform(post("/api/payment")
//                        .content(req)
//                        .param("id","1")
                )
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.orderId").value(1))
//                .andExpect(jsonPath())
                .andExpect(content().string("Order information saved in session"));

    }

    @Test
    @DisplayName("paymentSuccess 테스트 - 정상동작")
    void orderFacadeRequestDtoTest() throws Exception {
        // deliveryRequestDto
        DeliveryRequestDto deliveryRequestDto = DeliveryRequestDto.builder()
                .deliveryState(Delivery.DeliveryState.READY)
                .deliveryReceiver("홍길동")
                .receiverPhoneNumber("01022223333")
                .postalCode("12345")
                .roadAddress("광주광역시 조선대")
                .detailedAddress("1층")
                .reference("빠른 배송 부탁드립니다")
                .build();

        // memberOrderRequestDto
        MemberOrderRequestDto memberOrderRequestDto = MemberOrderRequestDto.builder()
                .userId(1L)
                .deliveryId(null)
                .originalPrice(BigDecimal.valueOf(10000))
                .pointUse(BigDecimal.valueOf(0))
                .totalPrice(BigDecimal.valueOf(10000))
                .deliveryDate(LocalDate.of(2023,12,31))
                .orderEmail("hi@hi.com")
                .pointEarn(100)
                .deliveryPrice(BigDecimal.valueOf(0))
                .build();

        // orderBookRequestDto
        OrderBookRequestDto orderBookRequestDto = OrderBookRequestDto.builder()
                .bookId(1L)
                .couponId(null)
                .quantity(2)
                .salePrice(BigDecimal.valueOf(15000))
                .discountPrice(BigDecimal.valueOf(0))
                .orderBookState("READY")
                .couponDiscountRequestDto(null)
                .build();
        // orderFacadeRequestDto - List 형태여서 list에 추가
        List<OrderBookRequestDto> orderBookRequestDtoList = new ArrayList<>();
        orderBookRequestDtoList.add(orderBookRequestDto);

        // 위의 세가지를 받는 orderFacadeRequestDto
        OrderFacadeRequestDto orderFacadeRequestDto = new OrderFacadeRequestDto(deliveryRequestDto, memberOrderRequestDto, orderBookRequestDtoList);

        // @RequestBody : 역직렬화
        String requestBody = objectMapper.writeValueAsString(orderFacadeRequestDto);

        // OrderFacadeResponseDto stubbing 처리
        OrderFacadeResponseDto facadeResponseDto = mock(OrderFacadeResponseDto.class);
        when(orderFacade.createPrepareOrder(orderFacadeRequestDto))
                .thenReturn(OrderFacadeResponseDto.builder()
                        .orderNumber("20229-3kn")
                        .totalPrice(BigDecimal.valueOf(10000))
                        .orderName("JPA 프로그래밍 외 1권")
                        .email("hi@hi.com")
                        .phoneNumber("01022223333")
                        .build());

        OrderFacadeResponseDto facadeResponseDto1 = orderFacade.createPrepareOrder(orderFacadeRequestDto);

        String response = objectMapper.writeValueAsString(facadeResponseDto1);

        // response
        mvc.perform(post("/api/payment")
                        .content(response)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order information saved in session"));
    }

    @Test
    @DisplayName("session에 저장")
    void saveSession() {

    }

    @Test
    @DisplayName("orderName -> orderId로 jsonProperty 사용")
    void jsonProperty() {

    }

    @Test
    void paymentSuccess() {
        // 결제 요청
        // 결제 paymentKey, orderId, amount 값 응답 받고
        // 검증
        // session.getAttribute("orderId") 삭제
        // 결제 승인 요청 : restTemplate
        // 모든 요청에 성공하면 DB에 저장 : JpaRepository
    }

//    @Test
//    @DisplayName("/api/payment/success")
//    void paymentSuccess() {
//
//    }
}