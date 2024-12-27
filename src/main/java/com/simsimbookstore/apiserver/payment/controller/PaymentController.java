package com.simsimbookstore.apiserver.payment.controller;

import com.simsimbookstore.apiserver.payment.dto.ConfirmSuccessResponseDto;
import com.simsimbookstore.apiserver.payment.dto.FailResponseDto;
import com.simsimbookstore.apiserver.payment.dto.RequestOrderValueDto;
import com.simsimbookstore.apiserver.payment.dto.SuccessRequestDto;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;
//    private final AesUtil aesUtil;

    // 사용자 정보 (amount, orderId) 임시 저장
    @PostMapping("/payment")
    public ResponseEntity<?> initiatePayment(@RequestBody RequestOrderValueDto orderValue, HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        // Long orderId -> String orderId로 변환해서 session에 저장
        session.setAttribute("orderId", orderValue.getOrderId()); // orderValue 필드명 - javascript fetch에서 보내는 데이터의 이름이랑 같아야 함 (json 역직렬화 규칙)
        session.setAttribute("amount", orderValue.getTotalAmount());

        return ResponseEntity.ok("Order information saved in session");
    }

    // Toss에게 받은 결제 인증 성공시 검증 후 결제 승인 요청 -> 결제 완료 -> DB 저장
    @GetMapping("/payment/success")
    public ResponseEntity<SuccessRequestDto> paymentSuccess(@RequestParam String paymentKey,
                                                            @RequestParam String orderId,
                                                            @RequestParam Double amount,
                                                            HttpServletRequest request) {
        SuccessRequestDto successDto = new SuccessRequestDto(paymentKey, orderId, amount);
        HttpSession session = request.getSession();

        // 임시 저장값과 같은지 검증
        if (Objects.equals(orderId, (String) session.getAttribute("orderId"))) {
            if (Objects.equals(amount, (Double) session.getAttribute("amount"))) {
                // 같으면 세션 삭제
                session.removeAttribute(orderId);
                try {
                    // 결제 승인 요청
                    ConfirmSuccessResponseDto confirmSuccessResponseDto = paymentService.confirm(successDto);
                    // 결제 승인 요청까지 완료되면 DB에 저장
                    paymentService.createPayment(confirmSuccessResponseDto);
                } catch (URISyntaxException e) {
                    // 승인 실패
                    paymentFail("INVALID_REQUEST", "잘못된 요청입니다.", orderId);
                }
            }
        } else {
            // 검증 실패 시 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 결제 인증 실패
    @GetMapping("/payment/fail")
    public ResponseEntity<String> paymentFail(@RequestParam String code,
                                              @RequestParam String message,
                                              @RequestParam String orderId) {
        FailResponseDto failDto = new FailResponseDto(code, message, orderId);
        // 결제 중단 처리 + 결제 실패
        paymentService.failPayment(failDto);

        return ResponseEntity.status(Integer.parseInt(code)).body(message);

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
