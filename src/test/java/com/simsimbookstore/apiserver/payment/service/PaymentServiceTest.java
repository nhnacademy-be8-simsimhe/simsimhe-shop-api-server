package com.simsimbookstore.apiserver.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.payment.controller.PaymentController;
import com.simsimbookstore.apiserver.payment.dto.ConfirmSuccessResponseDto;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

//    @Autowired
//    ObjectMapper mapper;

    @Test
    @DisplayName("결제 승인 응답 객체 dto에 입력...?????")
    void confirm() {
    }

    @Test
    @DisplayName("저장..?")
    void createPayment() {
        ConfirmSuccessResponseDto confirmSuccessResponseDto = new ConfirmSuccessResponseDto("20240903-0002", "dhflkhj39", "19500", "CARD", 2024-12-24T02:34:22);

    }

    @Test
    @DisplayName("??Jpa?? orderId로 paymentKey 찾기")
    void getPaymentKey() {
    }
}