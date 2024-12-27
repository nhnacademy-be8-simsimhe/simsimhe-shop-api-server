package com.simsimbookstore.apiserver.payment.controller;

import com.netflix.discovery.converters.Auto;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    @MockBean
    PaymentService paymentService;

    @Test
    @DisplayName("orderName, totalAmount 데이터가 /api/payment 경로로 들어오는지 확인")
    void initiatePayment() {

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
}