package com.simsimbookstore.apiserver.payment.processor;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.payment.dto.Cancel;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import org.springframework.http.ResponseEntity;

public interface PaymentProcessor {
    // 존재하는 결제 방법인지 확인
    boolean checkPayMethod(String paymentMethod);

    // 결제 요청 생성, 결제 창 띄워주기
    String createPaymentRequest(OrderFacadeResponseDto orderFacadeResponseDto);

    // 결제 승인
    ConfirmResponseDto confirm(SuccessRequestDto successRequestDto);

    // 결제 취소
    void canceledPayment(Cancel cancel);
}
