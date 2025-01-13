package com.simsimbookstore.apiserver.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.payment.client.PaymentRestTemplate;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import com.simsimbookstore.apiserver.payment.dto.PaymentMethodResponse;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public boolean checkPayMethod(OrderFacadeResponseDto facadeResponseDto) {
        return paymentMethodRepository.existsByPaymentMethod(facadeResponseDto.getMethod());
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
    public ConfirmResponseDto confirm(SuccessRequestDto successDto) {
        ResponseEntity<ConfirmResponseDto> response = paymentRestTemplate.confirm(successDto);

        boolean isSuccess = response.getStatusCode().is2xxSuccessful();

        // 승인 성공
        if (isSuccess) {
            savePayment(response.getBody(), PaymentMethod.createPaymentMethod(successDto.getPaymentMethod()));
        }
        // 승인 실패
        else {
            PaymentStatus status = paymentStatusRepository.findByPaymentStatusName("FAIL")
                    .orElseThrow(() -> new NotFoundException("FAIL 상태가 존재하지 않습니다."));

            Order order = orderRepository.findByOrderNumber((response.getBody()).getOrderId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 OrderNumber 입니다."));

            Payment payment = Payment.builder()
                    .paymentKey(successDto.getPaymentKey())
                    .paymentStatus(status)
                    .paymentDate(LocalDateTime.now())
                    .errorCode(response.getBody().getCode())
                    .errorMessage(response.getBody().getMessage())
                    .paymentMethod(PaymentMethod.createPaymentMethod(successDto.getPaymentMethod()))
                    .order(order)
                    .build();

            paymentRepository.save(payment);
        }

        return response.getBody();
    }

    // 결제 완료된 객체 저장
    private void savePayment(ConfirmResponseDto confirmResponseDto, PaymentMethod userMethod) {
        PaymentStatus status = paymentStatusRepository.findByPaymentStatusName("SUCCESS")
                .orElseThrow(() -> new NotFoundException("SUCCESS 상태가 존재하지 않습니다."));
        Order order = orderRepository.findByOrderNumber(confirmResponseDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 OrderNumber 입니다."));

        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(confirmResponseDto.getApprovedAt());
        LocalDateTime dateTime = zonedDateTime.withZoneSameInstant(seoulZone).toLocalDateTime();

        // Payment 객체 생성 (빌더 패턴 사용)
        Payment payment = Payment.builder()
                .paymentKey(confirmResponseDto.getPaymentKey())
                .paymentDate(dateTime)
                .paymentStatus(status)
                .order(order)
                .paymentMethod(userMethod)
                .tossReturnMethod(confirmResponseDto.getTossReturnMethod())  // toss가 반환해주는 결제 방법 저장
                .build();

        // Payment 저장
        paymentRepository.save(payment);
    }

    public PaymentMethodResponse getPaymentMethodByName(String name){
        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethod(name).orElseThrow();

        return PaymentMethodResponse.changeEntityToDto(paymentMethod);
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
