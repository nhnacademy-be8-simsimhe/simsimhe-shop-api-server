package com.simsimbookstore.apiserver.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.payment.client.PaymentRestTemplate;
import com.simsimbookstore.apiserver.payment.dto.ConfirmSuccessResponseDto;
import com.simsimbookstore.apiserver.payment.dto.FailResponseDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRestTemplate paymentRestTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    // method DB에 있는지 확인
    public void checkPayMethod(OrderFacadeResponseDto facadeResponseDto) {
        paymentMethodRepository.findByPaymentMethod(facadeResponseDto.getMethod()).orElseThrow(() -> new NotFoundException("존재하지 않는 결제 방법입니다."));
    }

    // 결제 요청 객체 생성
    public String createPaymentRequest(OrderFacadeResponseDto facadeResponseDto) {
        String response = paymentRestTemplate.requestPayment(facadeResponseDto);

        ObjectMapper objectMapper = new ObjectMapper();

        String url;
        try {
            url = objectMapper.readTree(response).get("checkout").get("url").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }

        return url;
    }

    // 결제 승인 요청
    public ConfirmSuccessResponseDto confirm(SuccessRequestDto successDto) {
        ConfirmSuccessResponseDto response = paymentRestTemplate.confirm(successDto);
        savePayment(response);
        return response;
    }

    // 결제 완료된 객체 저장
    private void savePayment(ConfirmSuccessResponseDto confirmSuccessResponseDto) {
        PaymentStatus status = paymentStatusRepository.findByPaymentStatusName("SUCCESS").orElseThrow(() -> new NotFoundException("'SUCCESS'가 존재하지 않습니다."));
        Order order = orderRepository.findByOrderNumber(confirmSuccessResponseDto.getOrderId()).orElseThrow(() -> new NotFoundException("OrderNumber가 존재하지 않습니다."));

        // save
        Payment payment = new Payment(
                null,
                confirmSuccessResponseDto.getPaymentKey(),
                confirmSuccessResponseDto.getApprovedAt(),
                confirmSuccessResponseDto.getMethod(),
                status,
                order
        );

        paymentRepository.save(payment);
    }

    // 인증 실패
    public void failPayment(FailResponseDto failResponseDto) {
//        paymentRepository.savePaymentStatus(paymentStatus);
        Payment payment = new Payment();
//        payment.setPaymentStatusId(PaymentStatusName.FAIL);

//        return FailDto.builder()
//                .code(code)
//                .message(message)
//                .orderId(orderId)
//                .build();

        // code에 따라 달라지는 상황
        if (failResponseDto.getCode().equals("PAY_PROCESS_CANCELED")) {
            // 1. PAY_PROCESS_CANCELED : 구매자에 의한 취소 + orderId X -> 저장만 안되면 됨
        } else if (failResponseDto.getCode().equals("PAY_PROCESS_ABORTED")) {
            // 2. PAY_PROCESS_ABORTED : 오류 메시지 확인 필요
        } else if (failResponseDto.getCode().equals("REJECT_CARD_COMPANY")) {
            // 3. REJECT_CARD_COMPANY : 구매자의 카드 정보가 문제 -> 오류 메시지 확인 + 구매자에게 안내 필요
        }
    }



    // 환불을 위한 주문 번호로 paymentKey 조회
//    public String getPaymentKey(String orderId) {
//        String paymentKey = paymentRepository.findPaymentKeyByOrderId(orderId);
//        return paymentKey;
//    }
//
//
//    // 관리자 - toss에게 환불 요청
//    public void adminCanceled(String paymentKey, String cancelReason) {
//        String response = paymentRestTemplate.adminCanceled(paymentKey, cancelReason);
//    }
}
