package com.simsimbookstore.apiserver.payment.processor.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.exception.OrderNotFoundException;
import com.simsimbookstore.apiserver.orders.order.repository.OrderRepository;
import com.simsimbookstore.apiserver.payment.client.PaymentRestTemplate;
import com.simsimbookstore.apiserver.payment.dto.Cancel;
import com.simsimbookstore.apiserver.payment.dto.CanceledResponseDto;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentCanceled;
import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import com.simsimbookstore.apiserver.payment.exception.PaymentAlreadyCanceled;
import com.simsimbookstore.apiserver.payment.exception.PaymentNotExistException;
import com.simsimbookstore.apiserver.payment.exception.PaymentValidationFailException;
import com.simsimbookstore.apiserver.payment.processor.PaymentProcessor;
import com.simsimbookstore.apiserver.payment.repository.PaymentCanceledRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentProcessor implements PaymentProcessor {
    private final PaymentRestTemplate paymentRestTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentCanceledRepository paymentCanceledRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final String HASH_NAME = "Payment";

    // method DB에 있는지 확인
    @Override
    public boolean checkPayMethod(String paymentMethod) {
        return paymentMethodRepository.existsByPaymentMethod(paymentMethod);
    }

    // 결제 요청 객체 생성
    @Override
    public String createPaymentRequest(OrderFacadeResponseDto facadeResponseDto) {
        // 임시 저장
        redisTemplate.opsForHash().put(HASH_NAME, "orderId", facadeResponseDto.getOrderNumber());
        redisTemplate.opsForHash().put(HASH_NAME, "totalAmount", facadeResponseDto.getTotalPrice());
        redisTemplate.opsForHash().put(HASH_NAME, "method", facadeResponseDto.getMethod());

        String response = paymentRestTemplate.requestPayment(facadeResponseDto);
        ObjectMapper objectMapper = new ObjectMapper();

        String url;  // 결제창
        try {
            url = objectMapper.readTree(response).get("checkout").get("url").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }

        return url;
    }

    public boolean validation(SuccessRequestDto successDto) {  // SuccessRequestDto successRequestDto
        // redis에 임시 저장해둔 값과 같은지 검증
        String redisOrderId = (String) redisTemplate.opsForHash().get(HASH_NAME, "orderId");
        BigDecimal redisAmount = (BigDecimal) redisTemplate.opsForHash().get(HASH_NAME, "totalAmount");

        if ((Objects.equals(successDto.getOrderId(), redisOrderId))
                && (Objects.equals(successDto.getAmount(), redisAmount))) {
            // 같으면 임시 저장 데이터 삭제 > 달라도 삭제가 되야할듯??
            redisTemplate.opsForHash().delete(HASH_NAME, "orderId");
            redisTemplate.opsForHash().delete(HASH_NAME, "totalAmount");
            return true;
        }
        return false;
    }

    // 결제 승인 요청
    @Override
    public ConfirmResponseDto confirm(SuccessRequestDto successDto) { //SuccessRequestDto successDto
        if (validation(successDto)) {
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
        } else {
            throw new PaymentValidationFailException("요청 orderId, amount 값과 결제 orderId, amount 값이 일치하지 않습니다.");
        }
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

    // 환불을 위한 주문 번호로 paymentKey 조회
    @Transactional(readOnly = true)
    public String getPaymentKey(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> new OrderNotFoundException("주문이 존재하지 않습니다."));
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new PaymentNotExistException("결제가 존재하지 않습니다."));
        return payment.getPaymentKey();
    }

    // 결제 취소
    @Override
    @Transactional
    public void canceledPayment(Cancel cancel) {
        String orderNumber = cancel.getOrderNumber();
        String paymentKey = getPaymentKey(orderNumber);
        ResponseEntity<CanceledResponseDto> canceledResponse = paymentRestTemplate.canceled(paymentKey, cancel.getCanceledReason());  //CanceledResponseDto 화면에 보여줄거면 필요
        canceledSaveAndPaymentUpdateState(canceledResponse.getBody(), paymentKey);

        // 이미 취소된 건에 대한 처리 필요
        if (canceledResponse.getStatusCode().is4xxClientError()) {
            throw new PaymentAlreadyCanceled("이미 취소가 처리된 결제입니다.");
        }
    }

    public void canceledSaveAndPaymentUpdateState(CanceledResponseDto canceledResponseDto, String paymentKey) {
        Payment payment = paymentRepository.findPaymentByPaymentKey(paymentKey)
                .orElseThrow(() -> new PaymentNotExistException("결제 내역이 존재하지 않습니다."));
        PaymentStatus status = paymentStatusRepository.findByPaymentStatusName("CANCEL")
                .orElseThrow(() -> new NotFoundException("CANCEL 상태가 존재하지 않습니다."));

        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(canceledResponseDto.getCanceledAt());
        LocalDateTime dateTime = zonedDateTime.withZoneSameInstant(seoulZone).toLocalDateTime();

        // 결제 취소 저장
        PaymentCanceled canceled = PaymentCanceled.builder()
                .paymentCanceledReason(canceledResponseDto.getPaymentCanceledReason())
                .paymentCanceledTransactionKey(canceledResponseDto.getPaymentCanceledTransactionKey())
                .canceledAt(dateTime)
                .payment(payment)
                .build();

        paymentCanceledRepository.save(canceled);

        // 취소에 따른 payment status 상태 변경
        payment.setPaymentStatus(status);
    }
}