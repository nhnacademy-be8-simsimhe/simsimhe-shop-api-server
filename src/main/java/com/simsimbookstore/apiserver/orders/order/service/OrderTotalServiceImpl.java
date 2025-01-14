package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.coupons.coupon.dto.DiscountAmountResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryPolicyService;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.CouponUsageDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import com.simsimbookstore.apiserver.point.service.PointHistoryService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderTotalServiceImpl implements OrderTotalService{

    private final WrapTypeService wrapTypeService;
    private final OrderListService orderListService;
    private final DeliveryPolicyService deliveryPolicyService;
    private final PointHistoryService pointHistoryService;
    private final CouponService couponService;

    @Override
    public TotalResponseDto calculateTotal(TotalRequestDto requestDto) {
        log.info("requestDto = {}", requestDto);
        log.info("couponOptions = {}", requestDto.getCouponOptions());

        // 먼저 책 목록을 가져옵니다.
        List<BookListResponseDto> bookOrderList = orderListService.toBookOrderList(requestDto.getBookList());

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        List<CouponUsageDto> couponUsageDtos = new ArrayList<>();

        // 책 가격 + 포장 비용 등 계산
        for (BookListResponseDto book : bookOrderList) {

            // 1) 각 책의 원래 총액
            BigDecimal bookOriginalTotal = book.getPrice().multiply(BigDecimal.valueOf(book.getQuantity()));
            originalPrice = originalPrice.add(bookOriginalTotal);
            BigDecimal bookTotal = bookOriginalTotal;

            // 2) 포장 비용
            if (requestDto.getPackagingOptions() != null) {
                TotalRequestDto.PackagingRequestDto packaging = requestDto.getPackagingOptions().get(book.getBookId());
                if (packaging != null && packaging.getPackageTypeId() != null) {
                    BigDecimal packagingCost = wrapTypeService
                            .getWrapTypeById(packaging.getPackageTypeId())
                            .getPackagePrice();
                    bookTotal = bookTotal.add(packagingCost.multiply(BigDecimal.valueOf(packaging.getQuantity())));
                }
            }

            // 3) 쿠폰 할인: 회원만 적용 (userId != null)
            if (requestDto.getUserId() != null) {
                // 쿠폰 옵션이 있는지 확인
                if (requestDto.getCouponOptions() != null
                        && requestDto.getCouponOptions().containsKey(book.getBookId())) {

                    Long couponId = requestDto.getCouponOptions().get(book.getBookId());
                    try {
                        // 쿠폰 할인 계산
                        DiscountAmountResponseDto discountDto = couponService.calDiscountAmount(
                                book.getBookId(),
                                book.getQuantity(),
                                couponId
                        );
                        BigDecimal discountAmount = discountDto.getDiscountAmount();

                        // bookTotal에서 할인액 차감
                        bookTotal = bookTotal.subtract(discountAmount);

                        // CouponUsageDto 생성
                        String couponName = couponService.getCouponById(couponId).getCouponTypeName();
                        CouponUsageDto usageDto = CouponUsageDto.builder()
                                .bookId(book.getBookId())
                                .couponName(couponName)
                                .couponId(couponId)
                                .discount(discountAmount)
                                .build();
                        couponUsageDtos.add(usageDto);

                        log.info("쿠폰 적용 bookId={}, couponId={}, discount={}",
                                book.getBookId(), couponId, discountAmount);
                    } catch (Exception e) {
                        log.warn("쿠폰 적용 에러: bookId={}, couponId={}, msg={}",
                                book.getBookId(), couponId, e.getMessage());
                    }
                }
            } else {
                // userId == null → 비회원이면 쿠폰 로직 스킵
                // 필요하다면 로그만 남기기
                log.info("비회원 - 쿠폰 적용 스킵 for bookId={}", book.getBookId());
            }

            // 책별 계산 완료 → 전체 합계에 더함
            total = total.add(bookTotal);
        }

        // 4) 배송비 계산
        BigDecimal deliveryPrice = calculateDeliveryPrice(originalPrice);
        total = total.add(deliveryPrice);

        // "포인트 사용 전" 금액
        BigDecimal notPointUseTotal = total;

        // 5) 포인트 사용: 회원만 가능
        BigDecimal userPoints = BigDecimal.ZERO; // 기본값
        if (requestDto.getUserId() != null) {
            // 포인트 사용 가능여부 검증
            pointHistoryService.validateUsePoints(requestDto.getUserId(), requestDto.getUsePoint());
            // 현재 회원 포인트 조회
            userPoints = pointHistoryService.getUserPoints(requestDto.getUserId());
            // 총액에서 포인트 차감
            total = total.subtract(requestDto.getUsePoint());
        } else {
            // 비회원은 포인트 사용 X
            // 요청값을 0으로 강제 세팅(혹은 무시)
            requestDto.setUsePoint(BigDecimal.ZERO);
            log.info("비회원 - 포인트 로직 스킵");
        }

        // 최종 합계
        log.info("Final total: {}", total);

        return TotalResponseDto.builder()
                .total(total)
                .availablePoints(userPoints)  // 회원이면 실제 값, 비회원이면 0
                .deliveryPrice(deliveryPrice)
                .originalPrice(originalPrice)
                .usePoint(requestDto.getUsePoint())  // 비회원이면 0
                .couponDiscountDetails(couponUsageDtos)
                .notPointUseTotal(notPointUseTotal)
                .build();
    }

    @Override
    public BigDecimal calculateDeliveryPrice(BigDecimal total) {
        DeliveryPolicy standardPolicy = deliveryPolicyService.getStandardPolicy();
        BigDecimal deliveryPrice = standardPolicy.getDeliveryPrice();

        if (total.compareTo(standardPolicy.getPolicyStandardPrice()) < 0) {
            // 기준 금액 미만이면 배송비 추가
            log.info("Total is below the standard price. Delivery price: {}", deliveryPrice);
        } else {
            // 기준 금액 이상이면 무료배송
            deliveryPrice = BigDecimal.ZERO;
            log.info("Total exceeds the standard price. Delivery is free.");
        }
        return deliveryPrice;
    }
}