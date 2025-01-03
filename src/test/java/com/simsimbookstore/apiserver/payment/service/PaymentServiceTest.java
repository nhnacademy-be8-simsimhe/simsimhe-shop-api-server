package com.simsimbookstore.apiserver.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.payment.client.PaymentRestTemplate;
import com.simsimbookstore.apiserver.payment.dto.ConfirmSuccessResponseDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentStatusRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentRestTemplate paymentRestTemplate;

    @Mock
    private PaymentStatusRepository paymentStatusRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentService paymentService;

    BigDecimal amount;

    @BeforeEach
    void before() {
        amount = BigDecimal.valueOf(20000);
    }

    @Test
    @DisplayName("결제 방법 있는지 확인 > Repository에서 test하는게 맞는 것 같음")
    void checkPayMethodTest() {

        Mockito.when
                (paymentMethodRepository.findByPaymentMethod(Mockito.anyString()))
                .thenThrow(NotFoundException.class);

        OrderFacadeResponseDto orderFacadeResponseDto = OrderFacadeResponseDto.builder()
                .orderNumber("orderId")
                .orderName("티셔츠")
                .email("hi@hi.com")
                .phoneNumber("01011112222")
                .totalPrice(amount)
                .method("CARD")
                .userName("홍길동")
                .build();

        Assertions.assertThrows(
                NotFoundException.class, () ->
                        paymentService.checkPayMethod(orderFacadeResponseDto));
    }

    @Test
    @DisplayName("결제 요청 url 반환 테스트")
    void createPaymentRequestTest() {
        // given
        OrderFacadeResponseDto orderFacadeResponseDto = OrderFacadeResponseDto.builder()
                .orderNumber("orderId")
                .orderName("티셔츠")
                .email("hi@hi.com")
                .phoneNumber("01011112222")
                .totalPrice(amount)
                .method("CARD")
                .userName("홍길동")
                .build();

        // when
        when(paymentRestTemplate.requestPayment(orderFacadeResponseDto)).thenReturn(
                        "{\n" +
                        "  \"mId\" : \"tvivarepublica\",\n" +
                        "  \"lastTransactionKey\" : null,\n" +
                        "  \"paymentKey\" : \"tviva20250102170032lFRs8\",\n" +
                        "  \"orderId\" : \"MC4xNTQyMjY2MTAyMjA4\",\n" +
                        "  \"orderName\" : \"티셔츠 외 1건\",\n" +
                        "  \"taxExemptionAmount\" : 0,\n" +
                        "  \"status\" : \"READY\",\n" +
                        "  \"requestedAt\" : \"2025-01-02T17:00:32+09:00\",\n" +
                        "  \"approvedAt\" : null,\n" +
                        "  \"useEscrow\" : null,\n" +
                        "  \"cultureExpense\" : false,\n" +
                        "  \"card\" : null,\n" +
                        "  \"virtualAccount\" : null,\n" +
                        "  \"transfer\" : null,\n" +
                        "  \"mobilePhone\" : null,\n" +
                        "  \"giftCertificate\" : null,\n" +
                        "  \"cashReceipt\" : null,\n" +
                        "  \"cashReceipts\" : null,\n" +
                        "  \"discount\" : null,\n" +
                        "  \"cancels\" : null,\n" +
                        "  \"secret\" : \"ps_ALnQvDd2VJLXXXY5bGQa8Mj7X41m\",\n" +
                        "  \"type\" : \"NORMAL\",\n" +
                        "  \"easyPay\" : null,\n" +
                        "  \"country\" : \"KR\",\n" +
                        "  \"failure\" : null,\n" +
                        "  \"isPartialCancelable\" : true,\n" +
                        "  \"receipt\" : null,\n" +
                        "  \"checkout\" : {\n" +
                        "    \"url\" : \"https://payment-gateway-sandbox.tosspayments.com/link/payment?urlToken=checkout-url-df00fde203e245c58048fefb4437eaaed9c30be4d1f34c84b5bd5c6e2c5c9dbd&sessionCreationApiVersion=V3&gtid=a25010205bf5b0cbcf14a3e837417a7d958746a&flowMode=DEFAULT\"\n" +
                        "  },\n" +
                        "  \"currency\" : \"KRW\",\n" +
                        "  \"totalAmount\" : 17500,\n" +
                        "  \"balanceAmount\" : 17500,\n" +
                        "  \"suppliedAmount\" : 15909,\n" +
                        "  \"vat\" : 1591,\n" +
                        "  \"taxFreeAmount\" : 0,\n" +
                        "  \"method\" : null,\n" +
                        "  \"version\" : \"2022-11-16\",\n" +
                        "  \"metadata\" : null\n" +
                        "}"
        );

        String url = paymentService.createPaymentRequest(orderFacadeResponseDto);
        String expectedUrl = "https://payment-gateway-sandbox.tosspayments.com/link/payment?urlToken=checkout-url-df00fde203e245c58048fefb4437eaaed9c30be4d1f34c84b5bd5c6e2c5c9dbd&sessionCreationApiVersion=V3&gtid=a25010205bf5b0cbcf14a3e837417a7d958746a&flowMode=DEFAULT";

        //then
        assertEquals(expectedUrl, url);
    }

    @Test
    @DisplayName("인증된 결제에 대한 승인 절차 성공 테스트")
    void confirm() {
        //given
        SuccessRequestDto successRequestDto = new SuccessRequestDto("paymentKey", "orderId", amount);
        ConfirmSuccessResponseDto response = new ConfirmSuccessResponseDto("orderId", "paymentKey", amount, "CARD", LocalDateTime.now());
        PaymentStatus status = new PaymentStatus(1L, "SUCCESS");
        Order order = new Order(1L, null, null, "orderId", null, null, null, null, null, null, null, null, null, null);

        when(paymentRestTemplate.confirm(successRequestDto)).thenReturn(response);
        when(paymentStatusRepository.findByPaymentStatusName("SUCCESS")).thenReturn(Optional.of(status));
        when(orderRepository.findByOrderNumber(successRequestDto.getOrderId())).thenReturn(Optional.of(order));

        //when
        ConfirmSuccessResponseDto actual = paymentService.confirm(successRequestDto);

        //then
        assertNotNull(actual);
        assertAll(
                "",
                ()-> assertEquals(amount, actual.getTotalAmount()),
                ()-> assertEquals(successRequestDto.getOrderId(), actual.getOrderId()),
                ()-> assertEquals(successRequestDto.getPaymentKey(), actual.getPaymentKey())
        );
    }

//    @Test
//    @DisplayName("성공한 결제에 대해 승인 절차를 진행")
//    void createPaymentTest() {
//        paymentService.confirmPayment();

//        Assertions.assertEquals("vkkdi-lsk3", payment.getPaymentKey());
//        // 사용자가 선택한 결제 방법 (methodToss) > method DB에 존재하는 방법인지 확인 후, 결제
//        Assertions.assertEquals("CARD", payment.getPaymentMethodToss());
//        Assertions.assertEquals(status.getPaymentStatusName(), payment.getPaymentStatus().getPaymentStatusName());
//    }

//    @Test
//    @DisplayName("status repository not found success")
//    void notFoundSuccessStatus() {
//        PaymentStatus paymentStatus = new PaymentStatus();
//        paymentStatusRepository.save(paymentStatus);
//
//        Assertions.assertThrows(NotFoundException.class, () -> paymentStatusRepository.findByPaymentStatusName("SUCCESS"));
//    }

    @Test
    void failPayment() {
    }
}