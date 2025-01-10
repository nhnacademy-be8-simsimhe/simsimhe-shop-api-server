package com.simsimbookstore.apiserver.coupons.coupontype.mapper;

import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeRequestDto;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeResponseDto;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;

import java.util.Objects;

public class CouponTypeMapper {
    public static CouponTypeResponseDto toResponse(CouponType couponType) {
        CouponTypeResponseDto responseDto = CouponTypeResponseDto.builder()
                .couponTypeId(couponType.getCouponTypeId())
                .couponTypeName(couponType.getCouponTypeName())
                .period(couponType.getPeriod())
                .deadline(couponType.getDeadline())
                .couponPolicyId(couponType.getCouponPolicy().getCouponPolicyId())
                .build();

        if (couponType instanceof BookCoupon) {
            responseDto.setCouponTypes(CouponTargetType.BOOK);
            responseDto.setCouponTargetId(((BookCoupon) couponType).getBook().getBookId());
            responseDto.setCouponTargetName(((BookCoupon) couponType).getBook().getTitle());
        } else if (couponType instanceof  CategoryCoupon) {
            responseDto.setCouponTypes(CouponTargetType.CATEGORY);
            responseDto.setCouponTargetId(((CategoryCoupon) couponType).getCategory().getCategoryId());
            responseDto.setCouponTargetName(((CategoryCoupon) couponType).getCategory().getCategoryName());
        } else {
            responseDto.setCouponTypes(CouponTargetType.ALL);
        }

        return responseDto;
    }

    public static CouponType toCouponType(CouponTypeRequestDto requestDto) {
        if (requestDto.getCouponTargetType() == CouponTargetType.BOOK) {
            BookCoupon bookCoupon = BookCoupon.builder()
                    .couponTypeName(requestDto.getCouponTypeName())
                    .stacking(requestDto.getStacking())
                    .build();
            return setPeriodOrDeadLine(bookCoupon, requestDto);
        } else if (requestDto.getCouponTargetType() == CouponTargetType.CATEGORY) {
            CategoryCoupon categoryCoupon = CategoryCoupon.builder()
                    .couponTypeName(requestDto.getCouponTypeName())
                    .stacking(requestDto.getStacking())
                    .build();
            return setPeriodOrDeadLine(categoryCoupon, requestDto);
        } else {
            AllCoupon allCoupon = AllCoupon.builder()
                    .couponTypeName(requestDto.getCouponTypeName())
                    .stacking(requestDto.getStacking())
                    .build();
            return setPeriodOrDeadLine(allCoupon, requestDto);
        }

    }

    private static CouponType setPeriodOrDeadLine(CouponType couponType, CouponTypeRequestDto requestDto) {
        if (Objects.isNull(requestDto.getPeriod())) {
            couponType.setDeadline(requestDto.getDeadline());
        } else {
            couponType.setPeriod(requestDto.getPeriod());
        }
        return couponType;
    }
}
