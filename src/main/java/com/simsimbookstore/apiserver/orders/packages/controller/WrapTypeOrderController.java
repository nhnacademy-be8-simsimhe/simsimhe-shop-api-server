package com.simsimbookstore.apiserver.orders.packages.controller;


import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/wrap-types")
public class WrapTypeOrderController {

    private final WrapTypeService wrapTypeService;

    @GetMapping
    public ResponseEntity<List<WrapTypeResponseDto>> findAll() {
        List<WrapTypeResponseDto> allWarp = wrapTypeService.getAllWarpTypeIsAvailable();
        return ResponseEntity.ok(allWarp);
    }



}
