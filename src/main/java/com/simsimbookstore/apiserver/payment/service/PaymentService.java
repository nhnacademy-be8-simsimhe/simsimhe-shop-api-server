package com.simsimbookstore.apiserver.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.payment.client.PaymentRestTemplate;
import com.simsimbookstore.apiserver.payment.dto.ConfirmSuccessResponseDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRestTemplate paymentRestTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;


    // method DB에 있는지 확인
    public void checkPayMethod(OrderFacadeResponseDto facadeResponseDto) {
        paymentMethodRepository.findByPaymentMethod(facadeResponseDto.getMethod())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 결제 방법입니다."));
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
        ConfirmSuccessResponseDto response = paymentRestTemplate.confirm(successDto);   //성공
        savePayment(response);
        return response;
    }

    // 결제 완료된 객체 저장
    private void savePayment(ConfirmSuccessResponseDto confirmSuccessResponseDto) {
        PaymentStatus status = paymentStatusRepository.findByPaymentStatusName("SUCCESS")
                .orElseThrow(() -> new NotFoundException("SUCCESS 상태가 존재하지 않습니다."));
        Order order = orderRepository.findByOrderNumber(confirmSuccessResponseDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 OrderNumber 입니다."));

        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethod(confirmSuccessResponseDto.getMethod())
                .orElseThrow(() -> new NotFoundException("해당 결제 수단이 존재하지 않습니다."));

        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(confirmSuccessResponseDto.getApprovedAt());
        LocalDateTime dateTime = zonedDateTime.withZoneSameInstant(seoulZone).toLocalDateTime();

        // Payment 객체 생성 (빌더 패턴 사용)
        Payment payment = Payment.builder()
                .paymentKey(confirmSuccessResponseDto.getPaymentKey())
                .paymentDate(dateTime)
                .paymentStatus(status)
                .order(order)
                .paymentMethod(paymentMethod)
                .build();

        // Payment 저장
        paymentRepository.save(payment);
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
