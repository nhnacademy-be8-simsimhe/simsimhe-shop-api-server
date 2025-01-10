package com.simsimbookstore.apiserver.coupons.coupon.mapper;

import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.FixCouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.RateCouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;

import java.util.Objects;

public class CouponMapper {
    public static CouponResponseDto toResponse(Coupon coupon) {
        if (Objects.isNull(coupon)) {
            throw new IllegalArgumentException("coupon is null");
        }

        CouponType couponType = coupon.getCouponType();
        CouponPolicy couponPolicy = couponType.getCouponPolicy();
        CouponDetails couponDetails = extractCouponDetails(couponType);

        return switch (couponPolicy.getDiscountType()) {
            case FIX -> toFixCouponResponse(coupon, couponType, couponPolicy, couponDetails);
            case RATE -> toRateCouponResponse(coupon, couponType, couponPolicy, couponDetails);
        };
    }

    private static CouponDetails extractCouponDetails(CouponType couponType) {
        if (couponType instanceof BookCoupon bookCoupon) {
            return new CouponDetails(CouponTargetType.BOOK, bookCoupon.getBook().getBookId());
        } else if (couponType instanceof CategoryCoupon categoryCoupon) {
            return new CouponDetails(CouponTargetType.CATEGORY, categoryCoupon.getCategory().getCategoryId());
        } else if (couponType instanceof AllCoupon) {
            return new CouponDetails(CouponTargetType.ALL, null);
        } else {
            throw new IllegalArgumentException("Unknown CouponType: " + couponType.getClass().getSimpleName());
        }
    }

    private static FixCouponResponseDto toFixCouponResponse(Coupon coupon, CouponType couponType, CouponPolicy couponPolicy,CouponDetails couponDetails) {

        return FixCouponResponseDto.builder()
                .couponId(coupon.getCouponId())
                .issueDate(coupon.getIssueDate())
                .deadline(coupon.getDeadline())
                .couponTypeName(couponType.getCouponTypeName())
                .isStacking(couponType.isStacking())
                .couponStatus(coupon.getCouponStatus())
                .couponTargetType(couponDetails.couponTargetType())
                .couponTargetId(couponDetails.targetId())
                .discountPrice(couponPolicy.getDiscountPrice())
                .minOrderAmount(couponPolicy.getMinOrderAmount())
                .build();
    }

    private static RateCouponResponseDto toRateCouponResponse(Coupon coupon, CouponType couponType, CouponPolicy couponPolicy, CouponDetails couponDetails) {

        return RateCouponResponseDto.builder()
                .couponId(coupon.getCouponId())
                .issueDate(coupon.getIssueDate())
                .deadline(coupon.getDeadline())
                .couponTypeName(couponType.getCouponTypeName())
                .isStacking(couponType.isStacking())
                .couponStatus(coupon.getCouponStatus())
                .couponTargetType(couponDetails.couponTargetType())
                .couponTargetId(couponDetails.targetId())
                .discountRate(couponPolicy.getDiscountRate())
                .maxDiscountAmount(couponPolicy.getMaxDiscountAmount())
                .minOrderAmount(couponPolicy.getMinOrderAmount())
                .build();
    }


    private static class CouponDetails {
        private final CouponTargetType couponTargetType;
        private final Long targetId;

        private CouponDetails(CouponTargetType couponTargetType, Long targetId) {
            this.couponTargetType = couponTargetType;
            this.targetId = targetId;
        }
        public CouponTargetType couponTargetType() {
            return couponTargetType;
        }

        public Long targetId() {
            return targetId;
        }

    }
}
