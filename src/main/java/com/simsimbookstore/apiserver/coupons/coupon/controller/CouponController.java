package com.simsimbookstore.apiserver.coupons.coupon.controller;

import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.DiscountAmountResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.IssueCouponsRequestDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class CouponController {
    private final CouponService couponService;

    /**
     * 특정 쿠폰(couponId) 하나를 응답한다.
     * @param couponId
     * @return 쿠폰
     */
    @GetMapping("/shop/coupons/{couponId}")
    public ResponseEntity<CouponResponseDto> getCoupon(@PathVariable Long couponId) {
        CouponResponseDto couponById = couponService.getCouponById(couponId);
        return ResponseEntity.status(HttpStatus.OK).body(couponById);
    }

    /**
     * 유저가 가진 쿠폰 중 특정(couponTypeId) 쿠폰 타입인 미사용 쿠폰을 응답한다.
     * @param userId
     * @param couponTypeId
     * @return 미사용된 쿠폰
     */
    @GetMapping(value = "/shop/users/{userId}/coupons/unused",params = "couponTypeId")
    public ResponseEntity<CouponResponseDto> getUnusedCouponByCouponType(@PathVariable Long userId,
                                                                       @RequestParam Long couponTypeId) {
        CouponResponseDto unusedCoupon = couponService.getUnusedCouponByCouponType(userId, couponTypeId);
        return ResponseEntity.status(HttpStatus.OK).body(unusedCoupon);
    }

    /**
     * 유저가 가진 쿠폰을 Page로 응답한다.
     * 만료, 미사용, 사용된 쿠폰 상관없이 가지고 와서 응답한다.
     * @param userId
     * @param sortField 쿠폰 정렬기준 (null일 경우 발급일(issueDate)를 기준으로 정렬함)
     * @param pageable
     * @return 유저의 쿠폰 Page
     */
    @GetMapping("/shop/users/{userId}/coupons")
    public ResponseEntity<Page<CouponResponseDto>> getCoupons(@PathVariable Long userId,
                                                        @RequestParam(required = false) String sortField,
                                                        Pageable pageable) {
        Page<CouponResponseDto> coupons = couponService.getCoupons(setPageable(pageable, sortField), userId);
        return ResponseEntity.status(HttpStatus.OK).body(coupons);
    }

    /**
     * 유저의 쿠폰 중 미사용된 쿠폰을 Page로 응답한다..
     * @param userId
     * @param sortField 쿠폰 정렬기준 (null일 경우 발급일(issueDate)를 기준으로 정렬함)
     * @param pageable
     * @return 유저의 미사용 쿠폰 Page
     */
    @GetMapping(value ="/shop/users/{userId}/coupons/unused", params = {"!bookId", "!couponTypeId"})
    public ResponseEntity<Page<CouponResponseDto>> getUnusedCoupons(@PathVariable Long userId,
                                                                    @RequestParam(required = false) String sortField,
                                                                    Pageable pageable) {
        Page<CouponResponseDto> unusedCoupons = couponService.getUnusedCoupons(setPageable(pageable, sortField), userId);
        return ResponseEntity.status(HttpStatus.OK).body(unusedCoupons);
    }

    /**
     * 유저가 가지고 있는 쿠폰 중 특정 책(bookId)에 적용 가능한 쿠폰을 Page로 응답한다.
     * @param userId
     * @param bookId
     * @return 적용가능한 쿠폰 Page
     */
    @GetMapping(value = "/shop/users/{userId}/coupons/unused",params = "bookId")
    public ResponseEntity<List<CouponResponseDto>> getEligibleCouponsToBook(@PathVariable Long userId,
                                                                            @RequestParam Long bookId) {
        List<CouponResponseDto> eligibleCoupons = couponService.getEligibleCoupons(userId, bookId);
        return ResponseEntity.status(HttpStatus.OK).body(eligibleCoupons);

    }

    /**
     * 유저들에게 쿠폰을 발행한다.
     * @param requestDto 여러 유저id, 쿠폰 타입 id
     * @return 발행된 쿠폰들
     */
    @PostMapping("/admin/coupons/issue")
    public ResponseEntity<Map<String,List<Long>>> issueCoupons(@Valid @RequestBody IssueCouponsRequestDto requestDto) {
        List<Long> issueCouponIds = couponService.issueCoupons(requestDto.getUserIds(), requestDto.getCouponTypeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("couponIds",issueCouponIds));
    }

    /**
     * 유저의 쿠폰을 만료시킵니다.
     * @param userId
     * @param couponId
     * @return 만료된 쿠폰
     */
    @PostMapping("/admin/users/{userId}/coupons/expired")
    public ResponseEntity<CouponResponseDto> expiredCoupon(@PathVariable Long userId,
                                                           @RequestParam Long couponId) {
        CouponResponseDto expireCoupon = couponService.expireCoupon(userId, couponId);
        return ResponseEntity.status(HttpStatus.OK).body(expireCoupon);
    }

    /**
     * 쿠폰을 사용합니다.
     * @param userId
     * @param couponId
     * @return 사용한 쿠폰
     */
    @PostMapping("/shop/users/{userId}/coupons/use")
    public ResponseEntity<CouponResponseDto> useCoupon(@PathVariable Long userId,
                                                       @RequestParam Long couponId) {
        CouponResponseDto useCoupon = couponService.useCoupon(userId, couponId);
        return ResponseEntity.status(HttpStatus.OK).body(useCoupon);
    }

    /**
     * 유저의 쿠폰을 삭제합니다.
     * @param userId
     * @param couponId
     * @return
     */
    @DeleteMapping("/admin/users/{userId}/coupons/{couponId}")
    public ResponseEntity deleteCoupon(@PathVariable  Long userId,
                                       @PathVariable Long couponId) {
        couponService.deleteCoupon(userId, couponId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 쿠폰의 할인액을 계산합니다.
     * @param couponId
     * @param bookId
     * @param quantity
     * @return
     */
    @GetMapping("/shop/coupons/{couponId}/calculate")
    public ResponseEntity<DiscountAmountResponseDto> calDiscountAmount(@PathVariable Long couponId,
                                                                       @RequestParam Long bookId,
                                                                       @RequestParam @PositiveOrZero(message = "수량은 0 이상이어야 합니다.") Integer quantity) {
        DiscountAmountResponseDto discountAmountResponseDto = couponService.calDiscountAmount(bookId, quantity, couponId);
        return ResponseEntity.status(HttpStatus.OK).body(discountAmountResponseDto);
    }

    private Pageable setPageable(Pageable pageable, String sortField) {
        String sortBy = (StringUtils.isEmpty(sortField) && StringUtils.isBlank(sortField) ? "issueDate" : sortField);
        return PageRequest.of(
                pageable.getPageNumber(),
                10, //페이지 사이즈를 10으로 고정
                Sort.by(Sort.Direction.ASC, sortBy)
        );
    }
}
