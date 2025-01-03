package com.simsimbookstore.apiserver.orders.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalResponseDto {
    BigDecimal total;
    BigDecimal deliveryPrice;
    BigDecimal originalPrice;
    BigDecimal usePoint;
    BigDecimal availablePoints;

    List<CouponUsageDto> couponDiscountDetails; // 각 책별 쿠폰 사용 정보
}
