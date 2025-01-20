package com.simsimbookstore.apiserver.payment.service;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.payment.dto.Cancel;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import com.simsimbookstore.apiserver.payment.dto.PaymentMethodResponse;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import com.simsimbookstore.apiserver.payment.exception.PaymentMethodNotFoundException;
import com.simsimbookstore.apiserver.payment.processor.PaymentProcessor;
import com.simsimbookstore.apiserver.payment.repository.PaymentCanceledRepository;
import com.simsimbookstore.apiserver.payment.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final List<PaymentProcessor> paymentProcessorList;
    private final PaymentMethodRepository paymentMethodRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final String HASH_NAME = "Payment";

    // 사용자가 선택한 결제 방법이 존재하는지 확인 후, 해당 processor에 맞는 결제 진행
    public PaymentProcessor getProcessor(String paymentMethod) {
        return paymentProcessorList.stream()
                .filter(paymentProcessor -> paymentProcessor.checkPayMethod(paymentMethod))
                .findFirst()
                .orElseThrow(() -> new PaymentMethodNotFoundException("해당하는 결제방법은 존재하지 않습니다"));
    }

    // 결제 요청 생성
    public String createPaymentRequest(OrderFacadeResponseDto orderFacadeResponseDto) {
        PaymentProcessor paymentProcessor = getProcessor(orderFacadeResponseDto.getMethod());
        return paymentProcessor.createPaymentRequest(orderFacadeResponseDto);
    }

    // 결제 승인 요청
    public ConfirmResponseDto confirm(String paymentKey, String orderId, BigDecimal amount) {
        SuccessRequestDto successDto = new SuccessRequestDto(paymentKey, orderId, amount, getPaymentMethodByName());

        PaymentProcessor paymentProcessor = getProcessor(successDto.getPaymentMethod().getPaymentMethod());
        return paymentProcessor.confirm(successDto);
    }

    // 사용자가 선택한 결제 수단
    public PaymentMethodResponse getPaymentMethodByName() {
        String userPayMethod = (String) redisTemplate.opsForHash().get(HASH_NAME, "method");

        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethod(userPayMethod).orElseThrow();
        return PaymentMethodResponse.changeEntityToDto(paymentMethod);
    }

    // 결제 취소
    public void canceledPayment(Cancel cancel) {
        String userPayMethod = (String) redisTemplate.opsForHash().get(HASH_NAME, "method");
        PaymentProcessor paymentProcessor = getProcessor(userPayMethod);
        paymentProcessor.canceledPayment(cancel);
    }
}
