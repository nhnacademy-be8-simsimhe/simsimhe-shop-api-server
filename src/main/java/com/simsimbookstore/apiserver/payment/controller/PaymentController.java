package com.simsimbookstore.apiserver.payment.controller;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeImpl;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.payment.dto.Cancel;
import com.simsimbookstore.apiserver.orders.order.dto.RetryOrderRequestDto;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import com.simsimbookstore.apiserver.payment.exception.InvalidPaymentDataException;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop")
public class PaymentController {

    private final OrderFacadeImpl orderFacade;
    private final PaymentService paymentService;

    // 사용자 정보 (amount, orderId) 임시 저장
    @PostMapping("/payment")
    public ResponseEntity<String> paymentInitiate(@Valid @RequestBody OrderFacadeRequestDto dto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidPaymentDataException("필드의 값이 잘못되었습니다.");
        }
        OrderFacadeResponseDto facadeResponseDto = orderFacade.createPrepareOrder(dto);
        String result = paymentService.createPaymentRequest(facadeResponseDto);
        return ResponseEntity.ok(result);
    }

    // Toss에게 받은 결제 인증 성공시 검증 후 결제 승인 요청 -> 결제 완료 -> DB 저장
    @GetMapping("/payment/success")
    public ResponseEntity<ConfirmResponseDto> paymentSuccess(@RequestParam String paymentKey,
                                            @RequestParam String orderId,
                                            @RequestParam BigDecimal amount) {
        // 결제 승인 요청
        ConfirmResponseDto confirmResponseDto = paymentService.confirm(paymentKey, orderId, amount);
            
      //재고소모,쿠폰사용,포인트적립소모
        orderFacade.completeOrder(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(confirmResponseDto);
    }

    @PostMapping("/payment/retry")
    public ResponseEntity<String> paymentRetry(@RequestBody RetryOrderRequestDto dto) {
        OrderFacadeResponseDto responseDto = orderFacade.retryOrder(dto);
        String result = paymentService.createPaymentRequest(responseDto);
        return ResponseEntity.ok(result);
    } 

    // 비회원의 환불 요청 및 회원의 결제 취소
    @PostMapping("/payment/canceled/{orderNumber}")
    public ResponseEntity<?> canceledPayment(@PathVariable String orderNumber,
                                             @RequestParam Long userId,
                                             @RequestParam String canceledReason) {  //orderId = orderNumber  // 멱등키 : @RequestHeader("idempotentId") String idempotentId,
        Cancel cancel = new Cancel();
        cancel.setCanceledReason(canceledReason);
        cancel.setOrderNumber(orderNumber);
        paymentService.canceledPayment(cancel);

        return ResponseEntity.ok(cancel);
    }
}
