package com.simsimbookstore.apiserver.payment.controller;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeImpl;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.RetryOrderRequestDto;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import com.simsimbookstore.apiserver.payment.dto.PaymentMethodResponse;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.exception.InvalidPaymentDataException;
import com.simsimbookstore.apiserver.payment.exception.PaymentMethodNotFoundException;
import com.simsimbookstore.apiserver.payment.exception.PaymentValidationFailException;
import com.simsimbookstore.apiserver.payment.processor.PaymentProcessor;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;

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
    public ResponseEntity<?> paymentSuccess(@RequestParam String paymentKey,
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

//    // 사용자/관리자의 환불 요청 (결제 취소)
//    @PostMapping("/payment/cancel")
//    public ResponseEntity<Cancel> canceledPayment(@RequestParam String cancelReason,
//                                                  @RequestParam String orderId,
//                                                  UserRole userRole) { // 사용자일 때, 관리자일 때 ){  // 멱등키 : @RequestHeader("idempotentId") String idempotentId,
//        Cancel cancel = new Cancel();
//        cancel.setCancelReason(cancelReason);
//        cancel.setOrderId(orderId);
//
//        if (userRole.getRole().getRoleName().equals(ADMIN)) {
//            String paymentKey = paymentService.getPaymentKey(cancel.getOrderId());
//            paymentService.adminCanceled(paymentKey, cancel.getCancelReason());
//        } else if (userRole.getRole().getRoleName().equals(USER)) {
//            // 관리자 페이지에 환불 요청
//        }
//        return ResponseEntity.ok(cancel);
//    }
}
