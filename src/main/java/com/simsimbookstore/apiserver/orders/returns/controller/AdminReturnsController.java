package com.simsimbookstore.apiserver.orders.returns.controller;

import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.returns.service.AdminReturnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminReturnsController {
    private final AdminReturnsService adminReturnsService;

    // userId, orderNumber, refundedAt, price, 상세/승인/반려 버튼
    // pageable 필요
    @GetMapping("/orders/refund")
    public ResponseEntity<List<ReturnsResponseDto>> canceledList() {
        List<ReturnsResponseDto> returnsResponseDto = adminReturnsService.findReturnList();

        return ResponseEntity.ok(returnsResponseDto);
    }

    @PostMapping("/orders/refund/{orderBookId}/confirm")
    public ResponseEntity<Void> confirmReturn(@PathVariable Long orderBookId) {
        adminReturnsService.confirmReturn(orderBookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/refund/{orderBookId}/reject")
    public ResponseEntity<Void> rejectReturn(@PathVariable Long orderBookId) {
        adminReturnsService.rejectReturn(orderBookId);
        return ResponseEntity.ok().build();
    }
}
