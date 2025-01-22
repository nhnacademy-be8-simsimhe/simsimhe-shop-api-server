package com.simsimbookstore.apiserver.coupons.couponpolicy.controller;

import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyRequestDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyResponseDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.service.CouponPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/admin")
public class CouponPolicyController {
    private final CouponPolicyService couponPolicyService;

    /**
     * 모든 쿠폰 정책을 Page로 가지고온다.
     * @param pageable
     * @return
     */
    @GetMapping("/couponPolicies")
    public ResponseEntity<Page<CouponPolicyResponseDto>> getAllCouponPolicy(Pageable pageable) {
        Page<CouponPolicyResponseDto> responseDtos = couponPolicyService.getAllCouponPolicy(setPageable(pageable));
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    /**
     * 쿠폰 정책을 하나 가지고 온다.
     * @param couponPolicyId
     * @return
     */
    @GetMapping("/couponPolicies/{couponPolicyId}")
    public ResponseEntity<CouponPolicyResponseDto> getCouponPolicy(@PathVariable Long couponPolicyId) {
        CouponPolicyResponseDto responseDto = couponPolicyService.getCouponPolicy(couponPolicyId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/couponPolicies")
    public ResponseEntity<CouponPolicyResponseDto> createCouponPolicy(@Valid @RequestBody CouponPolicyRequestDto requestDto) {
        CouponPolicyResponseDto responseDto = couponPolicyService.createCouponPolicy(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/couponPolicies/{couponPolicyId}")
    public ResponseEntity deleteCouponPolicy(@PathVariable Long couponPolicyId) {
        couponPolicyService.deleteCouponPolicy(couponPolicyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Pageable setPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                10 // 페이지 크기를 10으로 고정
        );
    }
}

