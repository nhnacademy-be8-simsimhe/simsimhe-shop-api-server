package com.simsimbookstore.apiserver.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeImpl;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.payment.dto.FailResponseDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    String orderId;
    BigDecimal totalAmount;
    String paymentKey;

    @BeforeEach
    void data() {
        paymentKey = "tviva202412302247066Cbm6";
        orderId = "2222-1jjh";
        totalAmount = BigDecimal.valueOf(10000);
    }

    @Test
    @DisplayName("paymentInitiate 테스트")
    void paymentInitiateTest() throws Exception {
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

        // session에 임시 데이터 저장
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("orderId", orderId);
        session.setAttribute("amount", totalAmount);

        Assertions.assertEquals("2222-1jjh", session.getAttribute("orderId"));
        Assertions.assertEquals(totalAmount, session.getAttribute("amount"));

        // @RequestBody : 역직렬화
        String requestBody = objectMapper.writeValueAsString(orderFacadeRequestDto);

        // OrderFacadeResponseDto stubbing 처리
        OrderFacadeResponseDto facadeResponseDto = new OrderFacadeResponseDto("1213-ddd", "책 외 1권", "hi@hi.com", "01022223333", "홍길동", BigDecimal.valueOf(10000), "CARD");
        when(orderFacade.createPrepareOrder(any(OrderFacadeRequestDto.class))).thenReturn(facadeResponseDto);

        String url = "결제 url";
        when(paymentService.createPaymentRequest(facadeResponseDto)).thenReturn(url);

        // response
        mvc.perform(post("/api/payment")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(url));
    }

    @Test
    @DisplayName("paymentSuccess 테스트")
    void paymentSuccessTest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("orderId", orderId);
        session.setAttribute("totalAmount", totalAmount);

//        String expectedOrderId = (String) session.getAttribute("orderId");
//        BigDecimal expectedAmount = (BigDecimal) session.getAttribute("totalAmount");
//
//        // 임시 저장 값과 파라미터 값이 같은지 검증
//        Assertions.assertEquals(expectedOrderId, orderId);
//        Assertions.assertEquals(expectedAmount, totalAmount);

        mvc.perform(get("/api/payment/success")
                        .param("paymentKey", paymentKey)
                        .param("orderId", orderId)
                        .param("totalAmount", String.valueOf(totalAmount))
                        .session(session)
                )
                .andExpect(status().isCreated());

        // 검증 성공 시, session 임시 저장 값 삭제
        Assertions.assertNull(session.getAttribute("orderId"));
        Assertions.assertNull(session.getAttribute("totalAmount"));

        Mockito.verify(paymentService, Mockito.times(1)).confirm(Mockito.any(SuccessRequestDto.class));
//        Mockito.verify(paymentService, Mockito.times(1)).confirmPayment(Mockito.any());
    }

    @Test
    @DisplayName("검증 실패시 BAD_REQUEST 반환")
    void failValidationTest() throws Exception {
        String wrongOrderId = "1232jkj-d";

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("orderId", wrongOrderId);
        session.setAttribute("totalAmount", totalAmount);

        mvc.perform(get("/api/payment/success")
                .param("paymentKey", paymentKey)
                .param("orderId", orderId)
                .param("totalAmount", String.valueOf(totalAmount))
                .session(session))
                .andExpect(status().isBadRequest());

        Mockito.verify(paymentService, Mockito.never()).confirm(Mockito.any(SuccessRequestDto.class));
//        Mockito.verify(paymentService, Mockito.never()).confirmPayment(Mockito.any());
    }

    @Test
    @DisplayName("paymentSuccess 테스트 - 승인 실패")
    void paymentValidationFailTest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("orderId", orderId);
        session.setAttribute("totalAmount", totalAmount);

        mvc.perform(get("/api/payment/success")
                        .param("paymentKey", paymentKey)
                        .param("orderId", orderId)
                        .param("totalAmount", "293480")
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("결제 인증 실패 테스트")
    void paymentFail() throws Exception {
        String code = "400";
        String message = "결제 실패";

        mvc.perform(get("/api/payment/fail")
                        .param("code", code)
                        .param("message", message)
                        .param("orderId", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(message));

        Mockito.verify(paymentService, Mockito.times(1)).failPayment(any(FailResponseDto.class));
    }
}
