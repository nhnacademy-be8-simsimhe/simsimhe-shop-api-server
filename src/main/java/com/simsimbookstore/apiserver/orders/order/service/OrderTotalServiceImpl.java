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

        List<BookListResponseDto> bookOrderList = orderListService.toBookOrderList(requestDto.getBookList());

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal notPointUseTotal = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        BigDecimal discountedPrice = BigDecimal.ZERO;

        List<CouponUsageDto> couponUsageDtos = new ArrayList<>();
        Map<Long, BigDecimal> couponDiscountDetails = new HashMap<>();

        // 책 가격 및 포장 비용 계산
        for (BookListResponseDto book : bookOrderList) {
            BigDecimal bookOriginalTotal = book.getPrice().multiply(BigDecimal.valueOf(book.getQuantity()));
            originalPrice = originalPrice.add(bookOriginalTotal);
            BigDecimal bookTotal = bookOriginalTotal;

            // 포장 비용 계산
            if (requestDto.getPackagingOptions() != null) {
                TotalRequestDto.PackagingRequestDto packaging = requestDto.getPackagingOptions().get(book.getBookId());
                if (packaging != null && packaging.getPackageTypeId() != null) {
                    BigDecimal packagingCost = wrapTypeService.getWrapTypeById(packaging.getPackageTypeId()).getPackagePrice();
                    log.info("Packaging cost: BookId={}, TypeId={}, Quantity={}, Cost={}",
                            book.getBookId(), packaging.getPackageTypeId(), packaging.getQuantity(), packagingCost);
                    bookTotal = bookTotal.add(packagingCost.multiply(BigDecimal.valueOf(packaging.getQuantity())));
                }
            }

            if (requestDto.getCouponOptions() != null
                    && requestDto.getCouponOptions().containsKey(book.getBookId())) {
                log.info("쿠폰진입");
                Long couponId = requestDto.getCouponOptions().get(book.getBookId());
                try {
                    // 쿠폰 할인 금액 계산
                    DiscountAmountResponseDto discountDto = couponService.calDiscountAmount(
                            book.getBookId(),
                            book.getQuantity(),
                            couponId
                    );

                    BigDecimal discountAmount = discountDto.getDiscountAmount();

                    log.info("쿠폰 할인 금액 : {}", discountAmount);

                    String name = (String) couponService.getCouponById(couponId).getCouponTypeName();

                    // 책 금액(bookTotal)에서 할인액만큼 차감
                    bookTotal = bookTotal.subtract(discountAmount);
                    log.info("쿠폰 할인액 : {}", discountAmount);
                    // 전체 할인액 누적
                    log.info("쿠폰 합산");
                    CouponUsageDto usageDto = CouponUsageDto.builder()
                            .bookId(book.getBookId())
                            .couponName(name)
                            .couponId(couponId)
                            .discount(discountAmount)
                            .build();
                    couponUsageDtos.add(usageDto);
                    // 책별 할인액 저장
                    couponDiscountDetails.put(book.getBookId(), discountAmount);

                } catch (Exception e) {
                    // 쿠폰이 적용 불가능할 경우, 예외처리나 로그 남기기
                    // 예: throw e;  또는 log.warn(...)
                    log.warn("쿠폰 적용 에러: bookId={}, couponId={}, msg={}", book.getBookId(), couponId, e.getMessage());
                }
            }

            total = total.add(bookTotal);
        }
        BigDecimal deliveryPrice = calculateDeliveryPrice(originalPrice);
        // 배송비 추가
        total = total.add(deliveryPrice);
        notPointUseTotal = total;

        // 포인트 사용 검증
        pointHistoryService.validateUsePoints(requestDto.getUserId(), requestDto.getUsePoint());
        BigDecimal userPoints = pointHistoryService.getUserPoints(requestDto.getUserId());
        total = total.subtract(requestDto.getUsePoint());

        log.info("Final total: {}", total);
        return TotalResponseDto.builder()
                .total(total)
                .availablePoints(userPoints)
                .deliveryPrice(deliveryPrice)
                .originalPrice(originalPrice)
                .usePoint(requestDto.getUsePoint())
                .couponDiscountDetails(couponUsageDtos)
                .notPointUseTotal(notPointUseTotal)
                .build();
    }

    @Override
    public BigDecimal calculateDeliveryPrice(BigDecimal total) {
        DeliveryPolicy standardPolicy = deliveryPolicyService.getStandardPolicy();

        BigDecimal deliveryPrice = standardPolicy.getDeliveryPrice();

        if (total.compareTo(standardPolicy.getPolicyStandardPrice()) < 0) {
            total = total.add(standardPolicy.getDeliveryPrice());
            log.info("Total is below the standard price. Added delivery price: {}", standardPolicy.getDeliveryPrice());
        } else {
            deliveryPrice = BigDecimal.ZERO;
            log.info("Total exceeds the standard price. Delivery is free.");
        }

        return deliveryPrice;
    }

}
