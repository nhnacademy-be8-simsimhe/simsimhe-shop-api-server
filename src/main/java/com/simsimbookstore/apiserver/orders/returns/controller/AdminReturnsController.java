package com.simsimbookstore.apiserver.orders.returns.controller;

import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsResponseDto;
import com.simsimbookstore.apiserver.orders.returns.service.AdminReturnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminReturnsController {
    private final AdminReturnsService adminReturnsService;

    @GetMapping("/orders/refund")
    public ResponseEntity<Page<ReturnsResponseDto>> canceledList(
            @PageableDefault(size = 10, sort = "returnDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReturnsResponseDto> returnsResponseDto = adminReturnsService.findReturnList(pageable);

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