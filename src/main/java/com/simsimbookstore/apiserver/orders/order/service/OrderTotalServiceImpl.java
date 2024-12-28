package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import com.simsimbookstore.apiserver.orders.delivery.entity.DeliveryPolicy;
import com.simsimbookstore.apiserver.orders.delivery.service.DeliveryPolicyService;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import java.math.BigDecimal;
import java.util.List;
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
    //private final CouponService couponService;

    @Override
    public BigDecimal calculateTotal(TotalRequestDto requestDto) {
        List<BookListResponseDto> bookOrderList = orderListService.toBookOrderList(requestDto.getBookList());
        BigDecimal total = BigDecimal.ZERO;

        for (BookListResponseDto book : bookOrderList) {
            BigDecimal bookTotal = book.getPrice().multiply(BigDecimal.valueOf(book.getQuantity()));

            // 쿠폰 할인
//            Long couponId = requestDto.getCouponOptions().get(book.getBookId());
//            if (couponId != null) {
//                BigDecimal discount = couponService.getDiscountAmount(couponId, bookTotal);
//                log.info("Coupon applied: BookId={}, CouponId={}, Discount={}", book.getBookId(), couponId, discount);
//                bookTotal = bookTotal.subtract(discount);
//            }

            // 포장 비용
            TotalRequestDto.PackagingRequestDto packaging = requestDto.getPackagingOptions().get(book.getBookId());
            if (packaging != null && packaging.getPackageTypeId() != null) {
                BigDecimal packagingCost = wrapTypeService.getWrapTypeById(packaging.getPackageTypeId()).getPackagePrice();
                log.info("Packaging cost: BookId={}, TypeId={}, Quantity={}, Cost={}",
                        book.getBookId(), packaging.getPackageTypeId(), packaging.getQuantity(), packagingCost);
                bookTotal = bookTotal.add(packagingCost.multiply(BigDecimal.valueOf(packaging.getQuantity())));
            }

            total = total.add(bookTotal);
        }

        DeliveryPolicy standardPolicy = deliveryPolicyService.getStandardPolicy();
         if (total.compareTo(standardPolicy.getPolicyStandardPrice()) < 0) {
            total = total.add(standardPolicy.getDeliveryPrice());
            log.info("Total is below the standard price. Added delivery price: {}", standardPolicy.getDeliveryPrice());
        } else {
            log.info("Total exceeds the standard price. Delivery is free.");
        }

        log.info("Final total: {}", total);
        return total;
    }

}
