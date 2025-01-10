package com.simsimbookstore.apiserver.payment.controller;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeImpl;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import com.simsimbookstore.apiserver.payment.dto.PaymentMethodResponse;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.exception.PaymentMethodNotFoundException;
import com.simsimbookstore.apiserver.payment.exception.PaymentValidationFailException;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final RedisTemplate<String, Object> redisTemplate;

    private final String HASH_NAME = "Payment";

    // 사용자 정보 (amount, orderId) 임시 저장
    @PostMapping("/payment")
    public ResponseEntity<String> paymentInitiate(@RequestBody OrderFacadeRequestDto dto, HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        OrderFacadeResponseDto facadeResponseDto = orderFacade.createPrepareOrder(dto);

        // 존재하는 결제 방법인지 확인
        if (paymentService.checkPayMethod(facadeResponseDto)) {

            // 임시저장
            redisTemplate.opsForHash().put(HASH_NAME, "orderId", facadeResponseDto.getOrderNumber());
            redisTemplate.opsForHash().put(HASH_NAME, "totalAmount", facadeResponseDto.getTotalPrice());
            redisTemplate.opsForHash().put(HASH_NAME, "method", facadeResponseDto.getMethod());
            String result = paymentService.createPaymentRequest(facadeResponseDto);
            return ResponseEntity.ok(result);
        }

        throw new PaymentMethodNotFoundException("해당하는 결제방법은 존재하지 않습니다");
    }

    // Toss에게 받은 결제 인증 성공시 검증 후 결제 승인 요청 -> 결제 완료 -> DB 저장
    @GetMapping("/payment/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam String paymentKey,
                                            @RequestParam String orderId,
                                            @RequestParam BigDecimal amount) {

        String redisOrderId = (String) redisTemplate.opsForHash().get(HASH_NAME, "orderId");
        BigDecimal redisAmount = (BigDecimal) redisTemplate.opsForHash().get(HASH_NAME, "totalAmount");
        String userPayMethod = (String) redisTemplate.opsForHash().get(HASH_NAME, "method");

        PaymentMethodResponse paymentMethodResponse = paymentService.getPaymentMethodByName(userPayMethod);

        SuccessRequestDto successDto = new SuccessRequestDto(paymentKey, orderId, amount, paymentMethodResponse);

        ConfirmResponseDto confirmResponseDto;

        // 임시 저장값과 같은지 검증
        if ((Objects.equals(orderId, redisOrderId)) && (Objects.equals(amount, redisAmount))) {
            // 같으면 임시 저장 데이터 삭제
            redisTemplate.opsForHash().delete(HASH_NAME, "orderId");
            redisTemplate.opsForHash().delete(HASH_NAME, "totalAmount");

            // 결제 승인 요청
            confirmResponseDto = paymentService.confirm(successDto);
        } else {
            // 검증 실패 시 처리
            throw new PaymentValidationFailException("요청 orderId, amount 값과 결제 orderId, amount 값이 일치하지 않습니다.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(confirmResponseDto);
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
