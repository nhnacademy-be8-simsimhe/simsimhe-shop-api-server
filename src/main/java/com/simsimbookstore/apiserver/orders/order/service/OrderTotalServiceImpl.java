package com.simsimbookstore.apiserver.orders.order.service;

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

    @Override
    public TotalResponseDto calculateTotal(TotalRequestDto requestDto) {

        List<BookListResponseDto> bookOrderList = orderListService.toBookOrderList(requestDto.getBookList());

        BigDecimal total = BigDecimal.ZERO;
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

            total = total.add(bookTotal);
        }

        // 포인트 사용 검증
        pointHistoryService.validateUsePoints(requestDto.getUserId(), requestDto.getUsePoint());
        BigDecimal userPoints = pointHistoryService.getUserPoints(requestDto.getUserId());
        total = total.subtract(requestDto.getUsePoint());

        // 배송비 계산 (할인되지 않은 책 가격 기준)
        BigDecimal deliveryPrice = calculateDeliveryPrice(originalPrice);

        // 배송비 추가
        total = total.add(deliveryPrice);

        log.info("Final total: {}", total);
        return TotalResponseDto.builder()
                .total(total)
                .availablePoints(userPoints)
                .deliveryPrice(deliveryPrice)
                .originalPrice(originalPrice)
                .usePoint(requestDto.getUsePoint())
                .couponDiscountDetails(couponUsageDtos)
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
