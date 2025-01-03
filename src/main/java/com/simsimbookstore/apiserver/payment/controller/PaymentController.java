package com.simsimbookstore.apiserver.payment.controller;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeImpl;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.payment.dto.*;
import com.simsimbookstore.apiserver.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final OrderFacadeImpl orderFacade;
    private final PaymentService paymentService;
//    private final AesUtil aesUtil;

    // 사용자 정보 (amount, orderId) 임시 저장
    @PostMapping("/payment")
    public ResponseEntity<String> paymentInitiate(@RequestBody OrderFacadeRequestDto dto, HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        OrderFacadeResponseDto facadeResponseDto = orderFacade.createPrepareOrder(dto);

        // 존재하는 결제 방법인지 확인
        paymentService.checkPayMethod(facadeResponseDto);

        session.setAttribute("orderId", facadeResponseDto.getOrderNumber());
        session.setAttribute("totalAmount", facadeResponseDto.getTotalPrice());

        String result = paymentService.createPaymentRequest(facadeResponseDto);

        return ResponseEntity.ok(result);
    }

    // Toss에게 받은 결제 인증 성공시 검증 후 결제 승인 요청 -> 결제 완료 -> DB 저장
    @GetMapping("/payment/success")
    public ResponseEntity<SuccessRequestDto> paymentSuccess(@RequestParam String paymentKey,
                                                            @RequestParam String orderId,
                                                            @RequestParam BigDecimal totalAmount,
                                                            HttpServletRequest request) {
        SuccessRequestDto successDto = new SuccessRequestDto(paymentKey, orderId, totalAmount);
        HttpSession session = request.getSession();

        // 임시 저장값과 같은지 검증
        if ((Objects.equals(orderId, (String) session.getAttribute("orderId"))) && (Objects.equals(totalAmount, (BigDecimal) session.getAttribute("totalAmount")))) {
                // 같으면 세션 삭제
                session.removeAttribute("orderId");
                session.removeAttribute("totalAmount");
                    // 결제 승인 요청
                    ConfirmSuccessResponseDto confirmSuccessResponseDto = paymentService.confirm(successDto);
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

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
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
