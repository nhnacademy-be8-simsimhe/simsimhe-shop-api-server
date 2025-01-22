package com.simsimbookstore.apiserver.orders.returns.controller;

import com.simsimbookstore.apiserver.orders.returns.dto.ReturnsRequestDto;
import com.simsimbookstore.apiserver.orders.returns.service.ReturnsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ReturnsController {
    private final ReturnsService returnsService;

    @PostMapping("/users/{userId}/orders/{orderNumber}/return")
    public ResponseEntity<Void> totalReturn(@PathVariable Long userId,
                                      @PathVariable String orderNumber,
                                            @RequestBody ReturnsRequestDto returnsRequestDto) {
        returnsService.createReturn(returnsRequestDto);
        return ResponseEntity.ok().build();
    }
}
