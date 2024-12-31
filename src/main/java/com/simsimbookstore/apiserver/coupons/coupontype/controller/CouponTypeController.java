package com.simsimbookstore.apiserver.coupons.coupontype.controller;

import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeResponseDto;
import com.simsimbookstore.apiserver.coupons.coupontype.service.CouponTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/admin")
public class CouponTypeController {
    private final CouponTypeService couponTypeService;

    @GetMapping("/couponType")
    public ResponseEntity<Page<CouponTypeResponseDto>> getAllCouponType(Pageable pageable) {
        Page<CouponTypeResponseDto> couponTypePage = couponTypeService.getAllCouponType(setPageable(pageable));
        return ResponseEntity.status(HttpStatus.OK).body(couponTypePage);
    }

    public ResponseEntity<Coupon>

    private Pageable setPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                10 // 페이지 크기를 10으로 고정
        );
    }
}
