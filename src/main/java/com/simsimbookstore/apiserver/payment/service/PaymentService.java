package com.simsimbookstore.apiserver.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.payment.client.PaymentRestTemplate;
import com.simsimbookstore.apiserver.payment.dto.ConfirmSuccessResponseDto;
import com.simsimbookstore.apiserver.payment.dto.FailResponseDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.entity.Payment;
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

    // 결제 승인 요청
    public ConfirmSuccessResponseDto confirm(SuccessRequestDto successDto) throws URISyntaxException {
        String response = paymentRestTemplate.confirm(successDto);
        // response = 응답 객체
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ConfirmSuccessResponseDto confirmSuccessResponseDto = objectMapper.readValue(response, ConfirmSuccessResponseDto.class);
            System.out.println(confirmSuccessResponseDto.toString());

            return confirmSuccessResponseDto;

            // 승인 요청 중 실패
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPayment(ConfirmSuccessResponseDto confirmSuccessResponseDto) {
        // DTO > entity

        // save
        Payment payment = new Payment(
                null,
                confirmSuccessResponseDto.getPaymentKey(),
                confirmSuccessResponseDto.getApprovedAt(),
                confirmSuccessResponseDto.getPaymentMethod(),
                paymentStatusRepository.findById(0L).orElseThrow(() -> new NotFoundException("'SUCCESS'가 존재하지 않습니다.")),
                orderRepository.findByOrderNumber(confirmSuccessResponseDto.getOrderId()).orElseThrow(() -> new NotFoundException("OrderNumber가 존재하지 않습니다."))
        );

        paymentRepository.save(payment);

        // entity > DTO
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
    }


    // 환불을 위한 주문 번호로 paymentKey 조회
//    public String getPaymentKey(String orderId) {
//        String paymentKey = paymentRepository.findPaymentKeyByOrder(orderId);
//        return paymentKey;
//    }
//
//
//    // 관리자 - toss에게 환불 요청
//    public void adminCanceled(String paymentKey, String cancelReason) {
//        String response = paymentRestTemplate.adminCanceled(paymentKey, cancelReason);
//    }
}
