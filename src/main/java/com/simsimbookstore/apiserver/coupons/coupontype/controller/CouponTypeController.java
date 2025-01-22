package com.simsimbookstore.apiserver.coupons.coupontype.controller;

import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeRequestDto;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeResponseDto;
import com.simsimbookstore.apiserver.coupons.coupontype.service.CouponTypeService;
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
public class CouponTypeController {
    private final CouponTypeService couponTypeService;

    /**
     * 모든 쿠폰 타입을 가져온다.
     * @param pageable
     * @return
     */
    @GetMapping("/couponTypes")
    public ResponseEntity<Page<CouponTypeResponseDto>> getAllCouponType(Pageable pageable) {
        Page<CouponTypeResponseDto> couponTypePage = couponTypeService.getAllCouponType(setPageable(pageable));
        return ResponseEntity.status(HttpStatus.OK).body(couponTypePage);
    }

    /**
     * 특정 쿠폰 타입(couponTypeId)을 가져온다.
     * @param couponTypeId
     * @return
     */
    @GetMapping("/couponTypes/{couponTypeId}")
    public ResponseEntity<CouponTypeResponseDto> getCouponType(@PathVariable Long couponTypeId) {
        CouponTypeResponseDto couponType = couponTypeService.getCouponType(couponTypeId);
        return ResponseEntity.status(HttpStatus.OK).body(couponType);
    }

    /**
     * 특정 쿠폰 정책(couponPolicyId)과 연결된 쿠폰 타입들을 가져온다.
     * @param couponPolicyId
     * @param pageable
     * @return
     */
    @GetMapping(value = "/couponTypes", params = "couponPolicyId")
    public ResponseEntity<Page<CouponTypeResponseDto>> getCouponTypeByPolicyId(@RequestParam Long couponPolicyId,
                                                                               Pageable pageable) {
        Page<CouponTypeResponseDto> couponByCouponPolicy = couponTypeService.getCouponByCouponPolicy(setPageable(pageable), couponPolicyId);
        return ResponseEntity.status(HttpStatus.OK).body(couponByCouponPolicy);
    }

    /**
     * 쿠폰 타입을 생성한다.
     * @param requestDto
     * @return
     */
    @PostMapping("/couponTypes")
    public ResponseEntity<CouponTypeResponseDto> createCouponType(@Valid @RequestBody CouponTypeRequestDto requestDto) {
        CouponTypeResponseDto couponType = couponTypeService.createCouponType(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponType);
    }

    /**
     * 특정 쿠폰 타입(couponTypeId)를 삭제한다.
     * 만약 이미 회원들에게 발급된 쿠폰 타입이면 삭제할 수 없다.
     * @param couponTypeId
     * @return
     */
    @DeleteMapping("/couponTypes/{couponTypeId}")
    public ResponseEntity deleteCouponType(@PathVariable Long couponTypeId) {
        couponTypeService.deleteCouponType(couponTypeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Pageable setPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                10 // 페이지 크기를 10으로 고정
        );
    }
}
